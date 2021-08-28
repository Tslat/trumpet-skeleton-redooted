package com.jamieswhiteshirt.trumpetskeleton;

import com.jamieswhiteshirt.trumpetskeleton.entities.TrumpetSkeletonEntity;
import com.jamieswhiteshirt.trumpetskeleton.register.Entities;
import com.jamieswhiteshirt.trumpetskeleton.register.Items;
import com.jamieswhiteshirt.trumpetskeleton.register.SoundEvents;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Mod(TrumpetSkeleton.MOD_ID)
public class TrumpetSkeleton {
    public static final String MOD_ID = "trumpetskeleton";
    private static final Logger LOGGER = LogManager.getLogger("TrumpetSkeleton");

    public TrumpetSkeleton() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::setupCommon);
        modEventBus.addListener(this::setupClient);
        modEventBus.addListener(this::setupItemColours);
        modEventBus.addListener(this::setupStats);
        MinecraftForge.EVENT_BUS.addListener(this::setupSpawns);

        Entities.REGISTER.register(modEventBus);
        Items.REGISTER.register(modEventBus);
        SoundEvents.REGISTER.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CONFIG);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void setupClient(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(
                Entities.TRUMPET_SKELETON_ENTITY.get(),
                SkeletonRenderer::new
        );
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void setupItemColours(final ColorHandlerEvent.Item event) {
        event.getItemColors().register(
                (itemColor, itemsIn) -> Items.TRUMPET_SKELETON_SPAWN_EGG.get().getColor(itemsIn),
                Items.TRUMPET_SKELETON_SPAWN_EGG.get()
        );
    }

    @SubscribeEvent
    public void setupCommon(final FMLCommonSetupEvent event) {
        EntitySpawnPlacementRegistry.register(
                Entities.TRUMPET_SKELETON_ENTITY.get(),
                EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                TrumpetSkeletonEntity::checkMobSpawnRules
        );

        try {
            Map<EntityType<?>, SoundEvent> parrotImitationsMap = ObfuscationReflectionHelper.getPrivateValue(ParrotEntity.class, null, "field_192017_bK");

            parrotImitationsMap.put(Entities.TRUMPET_SKELETON_ENTITY.get(), SoundEvents.PARROT_DOOT.get());
        }
        catch (Exception ex) {
            LOGGER.error("Failed to integrate parrot imitation sound", ex);
        }
    }

    @SubscribeEvent
    public void setupStats(EntityAttributeCreationEvent ev) {
        ev.put(Entities.TRUMPET_SKELETON_ENTITY.get(), AbstractSkeletonEntity.createAttributes().build());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void setupSpawns(final BiomeLoadingEvent ev) {
        double relativeWeight = Config.RELATIVE_SPAWN_WEIGHT.get();

        if (relativeWeight > 0) {
            int skeletonWeight = 0;

            for (MobSpawnInfo.Spawners spawner : ev.getSpawns().getSpawner(EntityClassification.MONSTER)) {
                if (spawner.type == EntityType.SKELETON)
                    skeletonWeight += spawner.weight;
            }

            if (skeletonWeight > 0) {
               ev.getSpawns().addSpawn(
                       EntityClassification.MONSTER,
                       new MobSpawnInfo.Spawners(Entities.TRUMPET_SKELETON_ENTITY.get(),
                               (int) Math.ceil(skeletonWeight * relativeWeight),
                               1,
                               1));
            }
        }
        else {
            LOGGER.info("Trumpet skeletons have been configured not to spawn; not registering spawn entries.");
        }
    }
}

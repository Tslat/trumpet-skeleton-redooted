package com.jamieswhiteshirt.trumpetskeleton;

import com.jamieswhiteshirt.trumpetskeleton.entities.TrumpetSkeletonEntity;
import com.jamieswhiteshirt.trumpetskeleton.register.Entities;
import com.jamieswhiteshirt.trumpetskeleton.register.Items;
import com.jamieswhiteshirt.trumpetskeleton.register.SoundEvents;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
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

@Mod(TrumpetSkeleton.MOD_ID)
public class TrumpetSkeleton {
    public static final String MOD_ID = "trumpetskeleton";
    private static final Logger LOGGER = LogManager.getLogger("TrumpetSkeleton");

    public TrumpetSkeleton() {
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.register(this);
        eventBus.register(Entities.class);

        Entities.REGISTER.register(eventBus);
        Items.REGISTER.register(eventBus);
        SoundEvents.REGISTER.register(eventBus);

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
    public void setupCommon(final FMLCommonSetupEvent event) throws NoSuchFieldException, IllegalAccessException {
        addSpawns();

        // Apparently, this is the recommended way to do this. Now using reflection correctly.
        Field f = ObfuscationReflectionHelper.findField(ParrotEntity.class, "field_192017_bK");

        try {
            // Get a reference to the underlying HashMap
            HashMap<EntityType<?>, SoundEvent> imitationSound = (HashMap<EntityType<?>, SoundEvent>) f.get(ParrotEntity.class);

            // Add our sound event to the map
            imitationSound.put(Entities.TRUMPET_SKELETON_ENTITY.get(), SoundEvents.PARROT_DOOT.get());
        } catch (IllegalAccessException e) {
            // If it didn't work, we should log the problem and then re-throw the exception.
            LOGGER.error("Failed to set up parrot imitation sound", e);
            throw e;
        }
    }

    private void addSpawns() {
        DeferredWorkQueue.runLater(() -> {  // Forge does this multithreaded, we'd get an exception otherwise
            double relativeWeight = Config.RELATIVE_SPAWN_WEIGHT.get();

            if (relativeWeight > 0) {
                for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
                    int skeletonWeight = 0;

                    for (Biome.SpawnListEntry entry : biome.getSpawns(EntityClassification.MONSTER)) {
                        if (entry.entityType == EntityType.SKELETON) {
                            skeletonWeight += entry.itemWeight;
                        }
                    }

                    if (skeletonWeight > 0) {
                        int computedWeight = (int) Math.ceil(skeletonWeight * relativeWeight);

                        LOGGER.debug("Computed weight for biome " + biome.getRegistryName() + " is " + computedWeight);

                        biome.getSpawns(EntityClassification.MONSTER).add(
                                new Biome.SpawnListEntry(
                                        Entities.TRUMPET_SKELETON_ENTITY.get(),
                                        (int) Math.ceil(skeletonWeight * relativeWeight),
                                        1,
                                        1
                                )
                        );
                    }
                }
            } else {
                LOGGER.info("Trumpet skeletons have been configured not to spawn; not registering spawn entries.");
            }

            EntitySpawnPlacementRegistry.register(
                    Entities.TRUMPET_SKELETON_ENTITY.get(),
                    EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                    TrumpetSkeletonEntity::canMonsterSpawn
            );
        });
    }
}

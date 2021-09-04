package net.tslat.trumpetskeleton;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.tslat.trumpetskeleton.client.ClientOps;
import net.tslat.trumpetskeleton.register.Entities;
import net.tslat.trumpetskeleton.register.Items;
import net.tslat.trumpetskeleton.register.SoundEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod("trumpetskeleton")
public class TrumpetSkeleton {
    public static final String MOD_ID = "trumpetskeleton";
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public TrumpetSkeleton() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(ClientOps::setupClient);
        });
        modEventBus.addListener(this::setupCommon);
        modEventBus.addListener(Entities::setupStats);
        MinecraftForge.EVENT_BUS.addListener(this::setupSpawns);

        Entities.REGISTRY.register(modEventBus);
        Items.REGISTRY.register(modEventBus);
        SoundEvents.REGISTRY.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CONFIG);
    }

    @SubscribeEvent
    public void setupCommon(final FMLCommonSetupEvent event) {
        SpawnPlacements.register(
                Entities.TRUMPET_SKELETON.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                net.tslat.trumpetskeleton.entities.TrumpetSkeleton::checkMobSpawnRules
        );

        try {
            Map<EntityType<?>, SoundEvent> parrotImitationsMap = ObfuscationReflectionHelper.getPrivateValue(Parrot.class, null, "f_29358_");

            parrotImitationsMap.put(Entities.TRUMPET_SKELETON.get(), SoundEvents.PARROT_DOOT.get());
        }
        catch (Exception ex) {
            LOGGER.error("Failed to integrate parrot imitation sound", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void setupSpawns(final BiomeLoadingEvent ev) {
        double relativeWeight = Config.RELATIVE_SPAWN_WEIGHT.get();

        if (relativeWeight > 0) {
            int skeletonWeight = 0;

            for (MobSpawnSettings.SpawnerData spawner : ev.getSpawns().getSpawner(MobCategory.MONSTER)) {
                if (spawner.type == EntityType.SKELETON)
                    skeletonWeight += spawner.getWeight().asInt();
            }

            if (skeletonWeight > 0)
                ev.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(Entities.TRUMPET_SKELETON.get(), (int) Math.ceil(skeletonWeight * relativeWeight), 1, 1));
        }
        else {
            LOGGER.info("Trumpet skeletons have been configured not to spawn; not registering spawn entries.");
        }
    }
}

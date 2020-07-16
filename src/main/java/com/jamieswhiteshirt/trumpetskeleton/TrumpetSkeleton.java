package com.jamieswhiteshirt.trumpetskeleton;

import com.jamieswhiteshirt.trumpetskeleton.entities.TrumpetSkeletonEntity;
import com.jamieswhiteshirt.trumpetskeleton.items.TrumpetItem;
import com.jamieswhiteshirt.trumpetskeleton.register.Entities;
import com.jamieswhiteshirt.trumpetskeleton.register.Items;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
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
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "trumpetskeleton";
    public static final String VERSION = "1.12-1.0.2.1";

    public TrumpetSkeleton() {
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.register(this);

        Entities.REGISTER.register(eventBus);
        Items.REGISTER.register(eventBus);
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
                (itemColor, itemsIn) -> Items.TRUMPET_SEKELETON_SPAWN_EGG.get().getColor(itemsIn),
                Items.TRUMPET_SEKELETON_SPAWN_EGG.get()
        );
    }

    @SubscribeEvent
    public void onActiveItemUseTick(final LivingEntityUseItemEvent.Tick event) {
        ItemStack stack = event.getItem();

        if (stack.getItem() == Items.TRUMPET_ITEM.get()) {
            if (event.getDuration() == stack.getUseDuration() - 10) {
                LivingEntity user = event.getEntityLiving();
                World world = user.world;

                user.playSound(
                        TrumpetItem.trumpetSound,
                        1.0F,
                        0.9F + world.rand.nextFloat() * 0.2F
                );

                TrumpetItem.scare(world, user);
                stack.damageItem(1, user, (entity) -> entity.sendBreakAnimation(user.getActiveHand()));
            } else if (event.getDuration() <= stack.getUseDuration() - 15) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlaySoundAtEntity(final PlaySoundAtEntityEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;

            if (player.getActiveItemStack().getItem() == Items.TRUMPET_ITEM.get()) {
                if (event.getSound() == SoundEvents.ENTITY_GENERIC_EAT) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void setupCommon(final FMLCommonSetupEvent event) throws NoSuchFieldException, IllegalAccessException {
        addSpawns();

        // Apparently, this is the recommended way to do this. Go figure.
        Field f;

        try {
            // Attempt to grab a reference to the IMITATION_SOUND_EVENTS map and make it available.
            f = ParrotEntity.class.getDeclaredField("field_192017_bK");
        } catch (NoSuchFieldException e) {
            // We may be in a development environment
            f = ParrotEntity.class.getDeclaredField("IMITATION_SOUND_EVENTS");
        }

        try {
            f.setAccessible(true);  // Bypass `private` access modifier.

            // Get a reference to the underlying HashMap
            HashMap<EntityType<?>, SoundEvent> imitationSound = (HashMap<EntityType<?>, SoundEvent>) f.get(ParrotEntity.class);

            // Add our sound event to the map
            imitationSound.put(Entities.TRUMPET_SKELETON_ENTITY.get(), TrumpetSkeletonEntity.parrotSound);
        } catch (IllegalAccessException e) {
            // If it didn't work, we should log the problem and then re-throw the exception.
            LOGGER.error("Failed to set up parrot imitation sound", e);
            throw e;
        }
    }

    private void addSpawns() {
        DeferredWorkQueue.runLater(() -> {
            for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
                for (Biome.SpawnListEntry entry : biome.getSpawns(EntityClassification.MONSTER)) {
                    if (entry.entityType == EntityType.SKELETON) {
                        biome.getSpawns(EntityClassification.MONSTER).add(
                                new Biome.SpawnListEntry(
                                        Entities.TRUMPET_SKELETON_ENTITY.get(),
                                        entry.itemWeight / 4,
                                        entry.minGroupCount,
                                        entry.maxGroupCount
                                )
                        );

                        break;
                    }
                }
            }
        });
    }
}

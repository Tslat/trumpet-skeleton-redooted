package com.jamieswhiteshirt.trumpetskeleton.register;

import com.jamieswhiteshirt.trumpetskeleton.TrumpetSkeleton;
import com.jamieswhiteshirt.trumpetskeleton.entities.TrumpetSkeletonEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Entities {
    public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, TrumpetSkeleton.MOD_ID);

    public static final RegistryObject<EntityType<TrumpetSkeletonEntity>> TRUMPET_SKELETON_ENTITY = REGISTER.register(
            "trumpet_skeleton",

            () -> EntityType.Builder
                    .create(TrumpetSkeletonEntity::new, EntityClassification.MONSTER)
                    .size(0.6F, 1.99F)
                    .func_233606_a_(8)  // Unknown, but part of the vanilla skeleton definition
                    .build("trumpet_skeleton")
    );


    @SubscribeEvent
    public static void registerEntities(final FMLCommonSetupEvent event) {
        GlobalEntityTypeAttributes.put(
                TRUMPET_SKELETON_ENTITY.get(),

                GlobalEntityTypeAttributes.func_233835_a_(EntityType.SKELETON)  // get
        );
    }
}

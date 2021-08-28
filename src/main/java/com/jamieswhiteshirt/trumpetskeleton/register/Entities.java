package com.jamieswhiteshirt.trumpetskeleton.register;

import com.jamieswhiteshirt.trumpetskeleton.TrumpetSkeleton;
import com.jamieswhiteshirt.trumpetskeleton.entities.TrumpetSkeletonEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Entities {
    public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, TrumpetSkeleton.MOD_ID);

    public static final RegistryObject<EntityType<TrumpetSkeletonEntity>> TRUMPET_SKELETON_ENTITY = REGISTER.register(
            "trumpet_skeleton",

            () -> EntityType.Builder
                    .of(TrumpetSkeletonEntity::new, EntityClassification.MONSTER)
                    .sized(0.6F, 1.99F)
                    .build("trumpet_skeleton")
    );
}

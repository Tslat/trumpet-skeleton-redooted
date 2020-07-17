package com.jamieswhiteshirt.trumpetskeleton.register;

import com.jamieswhiteshirt.trumpetskeleton.TrumpetSkeleton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundEvents {
    public static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TrumpetSkeleton.MOD_ID);

    public static final RegistryObject<SoundEvent> PARROT_DOOT = REGISTER.register(
            "entity.parrot.imitate.trumpet_skeleton",
            () -> new SoundEvent(new ResourceLocation(TrumpetSkeleton.MOD_ID, "entity.parrot.imitate.trumpet_skeleton"))
    );

    public static final RegistryObject<SoundEvent> SKELETON_DOOT = REGISTER.register(
            "entity.trumpet_skeleton.ambient",
            () -> new SoundEvent(new ResourceLocation(TrumpetSkeleton.MOD_ID, "entity.trumpet_skeleton.ambient"))
    );

    public static final RegistryObject<SoundEvent> TRUMPET_DOOT = REGISTER.register(
            "item.trumpet.use",
            () -> new SoundEvent(new ResourceLocation(TrumpetSkeleton.MOD_ID, "item.trumpet.use"))
    );
}

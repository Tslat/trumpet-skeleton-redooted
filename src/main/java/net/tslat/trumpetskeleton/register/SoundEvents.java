package net.tslat.trumpetskeleton.register;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tslat.trumpetskeleton.TrumpetSkeleton;

public class SoundEvents {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TrumpetSkeleton.MOD_ID);

    public static final RegistryObject<SoundEvent> PARROT_DOOT = register("trumpet_skeleton_parrot_imitation", "entity.parrot.imitate.trumpet_skeleton");
    public static final RegistryObject<SoundEvent> SKELETON_DOOT = register("trumpet_skeleton_ambient", "entity.trumpet_skeleton.ambient");
    public static final RegistryObject<SoundEvent> TRUMPET_DOOT = register("trumpet_use", "item.trumpet.use");


    private static RegistryObject<SoundEvent> register(String id, String path) {
        return REGISTRY.register(id, () -> new SoundEvent(new ResourceLocation(TrumpetSkeleton.MOD_ID, path)));
    }
}

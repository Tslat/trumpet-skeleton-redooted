package com.jamieswhiteshirt.trumpetskeleton.register;

import com.jamieswhiteshirt.trumpetskeleton.TrumpetSkeleton;
import com.jamieswhiteshirt.trumpetskeleton.items.TrumpetItem;
import com.jamieswhiteshirt.trumpetskeleton.items.SupplierSpawnEggItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Items {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, TrumpetSkeleton.MOD_ID);

    public static final RegistryObject<TrumpetItem> TRUMPET_ITEM = REGISTER.register(
            "trumpet",

            () -> new TrumpetItem(
                    new Item.Properties()
                            .group(ItemGroup.MISC)
                            .maxDamage(200)
            )
    );

    public static final RegistryObject<SpawnEggItem> TRUMPET_SEKELETON_SPAWN_EGG = REGISTER.register(
            "trumpet_skeleton_spawn_egg",

            () -> new SupplierSpawnEggItem(
                    Entities.TRUMPET_SKELETON_ENTITY,
                    0xC1C1C1,
                    0xFCFC00,

                    new Item.Properties().group(ItemGroup.MISC)
            )
    );
}

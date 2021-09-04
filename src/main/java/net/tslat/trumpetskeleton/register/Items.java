package net.tslat.trumpetskeleton.register;

import net.minecraft.world.item.Item;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tslat.trumpetskeleton.TrumpetSkeleton;
import net.tslat.trumpetskeleton.items.TrumpetItem;

public class Items {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, TrumpetSkeleton.MOD_ID);

    public static final RegistryObject<TrumpetItem> TRUMPET = REGISTRY.register("trumpet", TrumpetItem::new);
}

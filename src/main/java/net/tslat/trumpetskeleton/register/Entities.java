package net.tslat.trumpetskeleton.register;

import net.minecraft.SharedConstants;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tslat.trumpetskeleton.TrumpetSkeleton;

public class Entities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, TrumpetSkeleton.MOD_ID);

    public static final RegistryObject<EntityType<net.tslat.trumpetskeleton.entities.TrumpetSkeleton>> TRUMPET_SKELETON = register("trumpet_skeleton",
            net.tslat.trumpetskeleton.entities.TrumpetSkeleton::new, 0.6f, 1.99f, 0xC1C1C1, 0xFCFC00);

    // Helper method to register egg alongside entity
    private static <T extends Mob> RegistryObject<EntityType<T>> register(String id, EntityType.EntityFactory<T> factory, float width, float height, int primaryEggColour, int secondaryEggColour) {
        EntityType.Builder<T> typeBuilder = EntityType.Builder.of(factory, MobCategory.MONSTER).sized(width, height).clientTrackingRange(8);

        boolean dataFixers = SharedConstants.CHECK_DATA_FIXER_SCHEMA; // Disable the pointless warnings on registration. Thanks Forge.
        SharedConstants.CHECK_DATA_FIXER_SCHEMA = false;

        EntityType<T> entityType = typeBuilder.build(id);

        SharedConstants.CHECK_DATA_FIXER_SCHEMA = dataFixers;

        if (primaryEggColour != -1)
            Items.REGISTRY.register(id + "_spawn_egg", () -> new SpawnEggItem(entityType, primaryEggColour, secondaryEggColour, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

        return REGISTRY.register(id, () -> entityType);
    }

    @SubscribeEvent
    public static void setupStats(final EntityAttributeCreationEvent ev) {
        ev.put(TRUMPET_SKELETON.get(), AbstractSkeleton.createAttributes().build());
    }
}

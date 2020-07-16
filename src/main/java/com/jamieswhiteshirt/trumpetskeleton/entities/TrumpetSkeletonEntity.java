package com.jamieswhiteshirt.trumpetskeleton.entities;

import com.jamieswhiteshirt.trumpetskeleton.TrumpetSkeleton;
import com.jamieswhiteshirt.trumpetskeleton.entities.goals.TrumpetAttackGoal;
import com.jamieswhiteshirt.trumpetskeleton.register.Items;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class TrumpetSkeletonEntity extends SkeletonEntity {
    private static final SoundEvent skeletonSound = new SoundEvent(new ResourceLocation(TrumpetSkeleton.MOD_ID, "entity.trumpet_skeleton.ambient"));
    public static final SoundEvent parrotSound = new SoundEvent(new ResourceLocation(TrumpetSkeleton.MOD_ID, "entity.parrot.imitate.trumpet_skeleton"));

    public TrumpetSkeletonEntity(EntityType<? extends SkeletonEntity> p_i50194_1_, World p_i50194_2_) {
        super(p_i50194_1_, p_i50194_2_);
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        super.setEquipmentBasedOnDifficulty(difficulty);

        setActiveHand(Hand.MAIN_HAND);
        setItemStackToSlot(
                EquipmentSlotType.MAINHAND,
                new ItemStack(Items.TRUMPET_ITEM.get())
        );
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return skeletonSound;
    }

    @Override
    public void playAmbientSound() {
        if (!isAggressive()) super.playAmbientSound();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(4, new TrumpetAttackGoal<>(this, 1, 40, 6));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.2, false));
    }
}

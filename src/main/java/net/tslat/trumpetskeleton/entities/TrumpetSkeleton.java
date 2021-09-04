package net.tslat.trumpetskeleton.entities;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.tslat.trumpetskeleton.entities.goals.TrumpetAttackGoal;
import net.tslat.trumpetskeleton.register.Items;
import net.tslat.trumpetskeleton.register.SoundEvents;

public class TrumpetSkeleton extends Skeleton {
    private final TrumpetAttackGoal<TrumpetSkeleton> trumpetAttackGoal = new TrumpetAttackGoal<TrumpetSkeleton>(this, 1, 40, 6);
    private final MeleeAttackGoal meleeAttackGoal = new MeleeAttackGoal(this, 1.2, false) {
        @Override
        public void start() {
            super.start();

            setAggressive(true);
        }

        @Override
        public void stop() {
            super.stop();

            setAggressive(false);
        }
    };

    public TrumpetSkeleton(EntityType<? extends Skeleton> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(difficulty);

        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRUMPET.get()));
        startUsingItem(InteractionHand.MAIN_HAND);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_DOOT.get();
    }

    @Override
    public void playAmbientSound() {
        if (!isAggressive())
            super.playAmbientSound();
    }

    @Override
    public void reassessWeaponGoal() {
        if (this.level != null && !this.level.isClientSide()) {
            goalSelector.removeGoal(meleeAttackGoal);
            goalSelector.removeGoal(trumpetAttackGoal);

            if (isHolding(Items.TRUMPET.get())) {
                int attackInterval = 40;

                if (level.getDifficulty() != Difficulty.HARD)
                    attackInterval = 80;

                trumpetAttackGoal.setAttackInterval(attackInterval);
                goalSelector.addGoal(4, trumpetAttackGoal);
            }
            else {
                goalSelector.addGoal(4, meleeAttackGoal);
            }
        }
    }
}

package com.jamieswhiteshirt.trumpetskeleton.entities.goals;

import com.jamieswhiteshirt.trumpetskeleton.register.Items;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;

import java.util.EnumSet;

public class TrumpetAttackGoal<T extends MonsterEntity> extends Goal {
    private final T actor;
    private final double speed;
    private int attackInterval;
    private final float squaredRange;

    private int cooldown = -1;
    private int seeCounter;
    private boolean strafeLeft;
    private boolean strafeBack;
    private int strafeChangeTimer = -1;

    public TrumpetAttackGoal(T actor, double speed, int attackInterval, float range) {
        this.actor = actor;
        this.speed = speed;
        this.attackInterval = attackInterval;
        this.squaredRange = range * range;

        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public void setAttackInterval(int attackInterval) {
        this.attackInterval = attackInterval;
    }

    @Override
    public boolean shouldExecute() {
        return actor.getAttackTarget() != null && isHoldingTrumpet();
    }

    protected boolean isHoldingTrumpet() {
        return actor.isHolding(Items.TRUMPET_ITEM.get());
    }

    @Override
    public boolean shouldContinueExecuting() {
        return (shouldExecute() || !actor.getNavigator().noPath()) && isHoldingTrumpet();
    }

    @Override
    public void startExecuting() {
        super.startExecuting();

        actor.setAggroed(true);
    }

    @Override
    public void resetTask() {
        super.resetTask();
        actor.setAggroed(false);
        actor.resetActiveHand();

        seeCounter = 0;
        cooldown = -1;
    }

    @Override
    public void tick() {
        LivingEntity target = actor.getAttackTarget();

        if (target == null) return;

        double squaredDistance = actor.getDistanceSq(target);
        boolean canSeeTarget = actor.canEntityBeSeen(target);
        boolean bool2 = seeCounter > 0;

        if (canSeeTarget != bool2) {
            seeCounter = 0;
        }

        if (canSeeTarget) seeCounter += 1;
        else seeCounter -= 1;

        if (squaredDistance <= squaredRange && seeCounter >= 20) {
            actor.getNavigator().clearPath();
            strafeChangeTimer += 1;
        } else {
            actor.getNavigator().tryMoveToEntityLiving(target, speed);
            strafeChangeTimer -= 1;
        }

        if (strafeChangeTimer >= 20) {
            if (actor.world.rand.nextFloat() < 0.3) {
                strafeLeft = !strafeLeft;
            }

            if (actor.world.rand.nextFloat() < 0.3) {
                strafeBack = !strafeBack;
            }

            strafeChangeTimer = 0;
        }

        if (strafeChangeTimer > -1) {
            if (squaredDistance > squaredRange * 0.75) {
                strafeBack = false;
            } else if (squaredDistance < squaredRange * 0.25) {
                strafeBack = true;
            }

            actor.getMoveHelper().strafe(
                    strafeBack ? -0.5f : 0.5f,
                    strafeLeft ? 0.5f : -0.5f
            );

            actor.getLookController().setLookPositionWithEntity(target, 30, 30);
        }

        if (actor.isHandActive()) {
            if (!canSeeTarget && seeCounter < -60) {
                actor.resetActiveHand();
            }
        } else if (--cooldown <= 0 && seeCounter >= -60) {
            actor.setActiveHand(ProjectileHelper.getHandWith(actor, Items.TRUMPET_ITEM.get()));
            cooldown = actor.world.rand.nextInt(attackInterval);
        }
    }
}

package com.jamieswhiteshirt.trumpetskeleton.items;

import com.jamieswhiteshirt.trumpetskeleton.TrumpetSkeleton;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.List;

public class TrumpetItem extends Item {
    public static final SoundEvent trumpetSound = new SoundEvent(new ResourceLocation(TrumpetSkeleton.MOD_ID, "item.trumpet.use"));

    public TrumpetItem(Properties properties) {
        super(properties);
    }

    public static void scare(World world, LivingEntity user) {
        if (!world.isRemote) {
            List<LivingEntity> spooked = world.getEntitiesWithinAABB(
                    LivingEntity.class,
                    user.getBoundingBox().grow(10.0)
            );

            for (LivingEntity entity : spooked) {
                if (entity == user) continue;

                double deltaX = entity.getPosX() - user.getPosX() + world.rand.nextDouble() - world.rand.nextDouble();
                double deltaZ = entity.getPosZ() - user.getPosZ() + world.rand.nextDouble() - world.rand.nextDouble();

                double distance = Math.sqrt((deltaX * deltaX) + (deltaZ * deltaZ));

                entity.velocityChanged = true;
                entity.setRevengeTarget(user);

                entity.addVelocity(
                        0.5 * deltaX / distance,
                        5 / (10 + distance),
                        0.5 + deltaZ / distance
                );
            }
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 55;
    }

    @Override
    public SoundEvent getDrinkSound() {
        return null;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        super.onUsingTick(stack, player, count);

        int useTime = getUseDuration(stack) - count;

        if (useTime == 10) {
            player.playSound(TrumpetItem.trumpetSound, 1, 0.9F + player.world.rand.nextFloat() * 0.2F);
            TrumpetItem.scare(player.world, player);
            stack.damageItem(1, player, (entity) -> entity.sendBreakAnimation(entity.getActiveHand()));
        } else if (useTime >= 15) {
            player.stopActiveHand();
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.setActiveHand(handIn);
        return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
    }
}

package net.tslat.trumpetskeleton.items;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.tslat.trumpetskeleton.register.SoundEvents;

import java.util.List;

public class TrumpetItem extends Item {
    public TrumpetItem() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_MISC).durability(200));
    }

    private void scare(Level level, LivingEntity user) {
        if (!level.isClientSide()) {
            List<LivingEntity> spooked = level.getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(10.0));

            for (LivingEntity entity : spooked) {
                if (entity == user)
                    continue;

                double deltaX = entity.getX() - user.getX() + level.random.nextDouble() - level.random.nextDouble();
                double deltaZ = entity.getZ() - user.getZ() + level.random.nextDouble() - level.random.nextDouble();

                double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

                entity.hurtMarked = true;
                entity.setLastHurtByMob(user);
                entity.push(0.5 * deltaX / distance, 5 / (10 + distance), 0.5 + deltaZ / distance);
            }
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 55;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return null;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        super.onUsingTick(stack, player, count);

        int useTime = getUseDuration(stack) - count;

        if (useTime == 10) {
            player.playSound(SoundEvents.TRUMPET_DOOT.get(), 1, 0.9F + player.level.random.nextFloat() * 0.2F);
            scare(player.level, player);
            stack.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(entity.getUsedItemHand()));
        }
        else if (useTime >= 15) {
            player.stopUsingItem();
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}

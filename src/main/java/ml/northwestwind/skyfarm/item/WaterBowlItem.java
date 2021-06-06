package ml.northwestwind.skyfarm.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class WaterBowlItem extends Item {
    public WaterBowlItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClientSide) return super.use(world, player, hand);
        return fillBowl(world, player, hand);
    }

    private ActionResult<ItemStack> fillBowl(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockRayTraceResult result = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
        if (!result.getType().equals(RayTraceResult.Type.BLOCK)) return ActionResult.pass(stack);
        BlockState state = world.getBlockState(result.getBlockPos());
        if (state.getBlock() instanceof IBucketPickupHandler && !(state.getBlock() instanceof CauldronBlock)) {
            if (state.getFluidState().is(FluidTags.WATER)) {
                Item waterBowl = ForgeRegistries.ITEMS.getValue(new ResourceLocation("botania", "water_bowl"));
                ItemStack itemstack = new ItemStack(waterBowl);
                boolean flag = player.inventory.add(itemstack);
                if (flag && itemstack.isEmpty()) {
                    itemstack.setCount(1);
                    ItemEntity itementity1 = player.drop(itemstack, false);
                    if (itementity1 != null) itementity1.makeFakeItem();
                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundCategory.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    player.inventoryMenu.broadcastChanges();
                } else {
                    ItemEntity itementity = player.drop(itemstack, false);
                    if (itementity != null) {
                        itementity.setNoPickUpDelay();
                        itementity.setOwner(player.getUUID());
                    }
                }
                stack.shrink(1);
                if (!stack.isEmpty()) return ActionResult.success(stack);
            }
        }
        return ActionResult.pass(stack);
    }
}

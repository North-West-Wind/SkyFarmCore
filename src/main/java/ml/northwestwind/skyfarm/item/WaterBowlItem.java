package ml.northwestwind.skyfarm.item;

import ml.northwestwind.skyfarm.events.RegistryEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class WaterBowlItem extends Item {
    private final boolean isEmpty;

    public WaterBowlItem(Properties properties, boolean isEmpty) {
        super(properties);
        this.isEmpty = isEmpty;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (isEmpty) return fillBowl(world, player, hand);
        ItemStack stack = player.getItemInHand(hand);
        BlockRayTraceResult result = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.ANY);
        if (!result.getType().equals(RayTraceResult.Type.BLOCK)) return ActionResult.pass(stack);
        BlockState state = world.getBlockState(result.getBlockPos());
        if (state.getBlock() instanceof CauldronBlock) {
            int amount = state.getValue(BlockStateProperties.LEVEL_CAULDRON);
            if (amount >= 3) return ActionResult.pass(stack);
            world.setBlockAndUpdate(result.getBlockPos(), state.setValue(BlockStateProperties.LEVEL_CAULDRON, Math.min(amount + 1, 3)));
            player.setItemInHand(hand, new ItemStack(Items.BOWL));
            return ActionResult.success(stack);
        }
        return super.use(world, player, hand);
    }

    private ActionResult<ItemStack> fillBowl(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockRayTraceResult result = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
        if (!result.getType().equals(RayTraceResult.Type.BLOCK)) return ActionResult.pass(stack);
        BlockState state = world.getBlockState(result.getBlockPos());
        if (state.getBlock() instanceof IBucketPickupHandler && !(state.getBlock() instanceof CauldronBlock)) {
            if (state.getFluidState().is(FluidTags.WATER)) {
                stack.shrink(1);
                player.addItem(new ItemStack(RegistryEvents.Items.WATER_BOWL));
                return ActionResult.success(stack);
            }
        }
        return ActionResult.fail(stack);
    }
}

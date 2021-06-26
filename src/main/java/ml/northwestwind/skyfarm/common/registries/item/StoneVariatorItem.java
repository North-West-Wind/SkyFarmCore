package ml.northwestwind.skyfarm.common.registries.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class StoneVariatorItem extends TooltipItem implements IVanishable {
    public StoneVariatorItem(Properties properties, String registryName) {
        super(properties, registryName);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null) return super.useOn(context);
        BlockPos pos = context.getClickedPos();
        World world = context.getLevel();
        BlockState state = world.getBlockState(pos);
        List<Block> blocks = BlockTags.BASE_STONE_OVERWORLD.getValues().stream().filter(block -> !block.equals(state.getBlock())).collect(Collectors.toList());
        if (state.is(BlockTags.BASE_STONE_OVERWORLD)) {
            if (!world.getFluidState(pos.below()).is(FluidTags.LAVA)) {
                world.playSound(null, pos, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 1, 1);
                if (world.isClientSide) player.displayClientMessage(new TranslationTextComponent("usage.skyfarm.stone_variator"), true);
                return ActionResultType.PASS;
            }
            world.playSound(null, pos, SoundEvents.STONE_BREAK, SoundCategory.BLOCKS, 1, 1);
            if (!world.isClientSide) world.setBlockAndUpdate(pos, blocks.get(world.getRandom().nextInt(blocks.size())).defaultBlockState());
            ItemStack stack = player.getItemInHand(context.getHand());
            stack.hurtAndBreak(1, player, playerEntity -> playerEntity.broadcastBreakEvent(context.getHand()));
            return ActionResultType.SUCCESS;
        }
        return super.useOn(context);
    }
}

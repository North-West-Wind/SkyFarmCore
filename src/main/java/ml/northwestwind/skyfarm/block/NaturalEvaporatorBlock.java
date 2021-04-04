package ml.northwestwind.skyfarm.block;

import ml.northwestwind.skyfarm.tile.NaturalEvaporatorTileEntity;
import ml.northwestwind.skyfarm.tile.renderer.NaturalEvaporatorRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class NaturalEvaporatorBlock extends Block {
    private static final VoxelShape SHAPE = Stream.of(
            Block.box(4, 14, 1, 5, 15, 15),
            Block.box(7.5, 14, 1, 8.5, 15, 15),
            Block.box(11, 14, 1, 12, 15, 15),
            Block.box(1, 13.99, 11, 15, 14.99, 12),
            Block.box(1, 13.99, 7.5, 15, 14.99, 8.5),
            Block.box(1, 13.99, 4, 15, 14.99, 5),
            Block.box(14, 0, 0, 16, 11, 2),
            Block.box(14, 0, 14, 16, 11, 16),
            Block.box(0, 0, 14, 2, 11, 16),
            Block.box(0, 0, 0, 2, 11, 2),
            Block.box(1, 10, 1, 15, 12, 15),
            Block.box(15, 11, 0, 16, 16, 15),
            Block.box(0, 11, 1, 1, 16, 16),
            Block.box(1, 11, 15, 16, 16, 16),
            Block.box(0, 11, 0, 15, 16, 1)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    public NaturalEvaporatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        NaturalEvaporatorTileEntity tile = (NaturalEvaporatorTileEntity) world.getBlockEntity(pos);
        if (tile == null) return ActionResultType.FAIL;
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty()) {
            if (tile.takeItem(player)) player.playSound(SoundEvents.ITEM_PICKUP, 1, 10);
        } else if (tile.addItem(stack)) {
            player.playSound(SoundEvents.ITEM_PICKUP, 1, 1);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return SHAPE;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new NaturalEvaporatorTileEntity();
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random rng) {
        NaturalEvaporatorTileEntity tile = (NaturalEvaporatorTileEntity) world.getBlockEntity(pos);
        if (tile == null) return;
        if (rng.nextInt(20) == 0 && tile.isEvaporating(0)) {
            double d0 = (double) pos.getX() + (rng.nextDouble() * 0.5 - 0.25) + 0.5D;
            double d1 = (double) pos.getY() + 1.0D;
            double d2 = (double) pos.getZ() + (rng.nextDouble() * 0.5 - 0.25) + 0.5D;
            world.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            world.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 0.2F + rng.nextFloat() * 0.2F, 0.9F + rng.nextFloat() * 0.15F, false);
        }
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
        NaturalEvaporatorTileEntity tile = (NaturalEvaporatorTileEntity) world.getBlockEntity(pos);
        if (tile != null) {
            IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().get();
            for (int i = 0; i < handler.getSlots(); i++) world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),handler.getStackInSlot(i)));
        }
        super.spawnAfterBreak(state, world, pos, stack);
    }
}

package ml.northwestwind.skyfarm.common.registries.block;

import ml.northwestwind.skyfarm.common.registries.tile.VoidGeneratorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class VoidGeneratorBlock extends Block {
    public VoidGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new VoidGeneratorTileEntity();
    }
}

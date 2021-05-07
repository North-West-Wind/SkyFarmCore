package ml.northwestwind.skyfarm.mixin;

import mekanism.api.chemical.gas.IGasTank;
import mekanism.common.lib.math.voxel.VoxelCuboid;
import mekanism.common.lib.multiblock.MultiblockData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(value = MultiblockData.class, remap = false)
public abstract class MixinMultiblockData {
    @Shadow(remap = false) @Final protected List<IGasTank> gasTanks;

    @Shadow(remap = false) public abstract VoxelCuboid getBounds();

    @Shadow(remap = false) public abstract boolean isFormed();
}

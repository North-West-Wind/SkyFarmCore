package ml.northwestwind.skyfarm.container;

import ml.northwestwind.skyfarm.events.RegistryEvents;
import ml.northwestwind.skyfarm.tile.ParaboxTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class ParaboxContainer extends Container {
    private final BlockPos pos;
    private ParaboxTileEntity tile = null;
    public ParaboxContainer(@Nullable ContainerType<?> type, int id) {
        this(type, id, BlockPos.ZERO);
    }

    public ParaboxContainer(@Nullable ContainerType<?> type, int id, BlockPos pos) {
        super(type, id);
        this.pos = pos;
    }

    public ParaboxContainer setTile(ParaboxTileEntity tile) {
        this.tile = tile;
        return this;
    }

    @Nullable
    public ParaboxTileEntity getTile() {
        return tile;
    }

    public ParaboxContainer(int id) {
        this(RegistryEvents.ContainerTypes.PARABOX, id);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > 64;
    }
}

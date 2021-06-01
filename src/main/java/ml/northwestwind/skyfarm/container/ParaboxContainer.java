package ml.northwestwind.skyfarm.container;

import ml.northwestwind.skyfarm.events.RegistryEvents;
import ml.northwestwind.skyfarm.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.packet.message.CCloseParaboxPacket;
import ml.northwestwind.skyfarm.tile.ParaboxTileEntity;
import ml.northwestwind.skyfarm.world.data.SkyblockData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Objects;

public class ParaboxContainer extends Container {
    public ParaboxTileEntity tile;

    public ParaboxContainer(int id, PlayerInventory inv, PacketBuffer data) {
        this(id, getTileEntity(inv, data));
    }

    public ParaboxContainer(int id, ParaboxTileEntity tile) {
        super(RegistryEvents.ContainerTypes.PARABOX, id);
        this.tile = tile;
    }

    private static ParaboxTileEntity getTileEntity(PlayerInventory inv, PacketBuffer data) {
        Objects.requireNonNull(inv, "playerInv cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final TileEntity tileAtPos = inv.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof ParaboxTileEntity) return (ParaboxTileEntity) tileAtPos;
        throw new IllegalStateException("TileEntity is not correct " + tileAtPos);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        BlockPos pos = tile.getBlockPos();
        return player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64;
    }

    @Override
    protected void clearContainer(PlayerEntity player, World world, IInventory inv) {
        super.clearContainer(player, world, inv);
        if (!world.isClientSide) {
            SkyblockData data = SkyblockData.get((ServerWorld) world);
            data.setUsingParabox(false);
            data.setDirty();
        } else SkyFarmPacketHandler.INSTANCE.sendToServer(new CCloseParaboxPacket());
    }

    public boolean isLooping() {
        return tile.isWorldInLoop();
    }

    public boolean isBackingUp() {
        return tile.isBackingUp();
    }
}

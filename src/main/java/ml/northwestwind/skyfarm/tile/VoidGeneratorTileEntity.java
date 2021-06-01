package ml.northwestwind.skyfarm.tile;

import ml.northwestwind.skyfarm.events.RegistryEvents;
import ml.northwestwind.skyfarm.tile.handler.VoidGeneratorEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VoidGeneratorTileEntity extends TileEntity implements ITickableTileEntity {
    private final VoidGeneratorEnergyStorage energyStorage = new VoidGeneratorEnergyStorage();
    private static final int INCREMENT = (int) Math.pow(2, 24);

    public VoidGeneratorTileEntity(TileEntityType<?> type) {
        super(type);
    }

    public VoidGeneratorTileEntity() {
        this(RegistryEvents.TileEntityTypes.VOID_GENERATOR);
    }

    private boolean shouldWork() {
        if (level == null) return false;
        BlockPos pos = getBlockPos().below();
        while (pos.getY() >= 0) {
            if (!level.getBlockState(pos).isAir()) return false;
            pos = pos.below();
        }
        return true;
    }

    @Override
    public void tick() {
        if (energyStorage.getEnergyStored() < Integer.MAX_VALUE && shouldWork()) {
            if (Integer.MAX_VALUE - energyStorage.getEnergyStored() < INCREMENT) energyStorage.setEnergyStored(Integer.MAX_VALUE);
            else energyStorage.setEnergyStored(energyStorage.getEnergyStored() + INCREMENT);
            setChanged();
            if (level != null) level.sendBlockUpdated(getBlockPos(), RegistryEvents.Blocks.VOID_GENERATOR.defaultBlockState(), RegistryEvents.Blocks.VOID_GENERATOR.defaultBlockState(), 3);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        energyStorage.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        energyStorage.readFromNBT(nbt);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.save(nbt);
        return new SUpdateTileEntityPacket(getBlockPos(), 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        if (level != null) this.load(level.getBlockState(pkt.getPos()), pkt.getTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = new CompoundNBT();
        this.save(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.load(state, tag);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityEnergy.ENERGY)) return LazyOptional.of(() -> (T) energyStorage);
        return super.getCapability(cap, side);
    }
}

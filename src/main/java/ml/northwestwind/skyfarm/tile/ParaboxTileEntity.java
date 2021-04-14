package ml.northwestwind.skyfarm.tile;

import ml.northwestwind.skyfarm.container.ParaboxContainer;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import ml.northwestwind.skyfarm.misc.ParaboxEnergyStorage;
import ml.northwestwind.skyfarm.world.data.SkyblockData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParaboxTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
    private final ParaboxEnergyStorage energyStorage = new ParaboxEnergyStorage();
    private double ticksLeft = 12000;
    private int paraboxLevel;

    public ParaboxTileEntity(TileEntityType<?> type) {
        super(type);
    }

    public ParaboxTileEntity() {
        this(RegistryEvents.TileEntityTypes.PARABOX);
    }

    public boolean isWorldInLoop() {
        if (!(level instanceof ServerWorld)) return false;
        SkyblockData data = SkyblockData.get((ServerWorld) level);
        return data.isInLoop();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap.equals(CapabilityEnergy.ENERGY) && isWorldInLoop()) return LazyOptional.of(() -> (T) energyStorage);
        return super.getCapability(cap);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        energyStorage.writeToNBT(nbt);
        nbt.putDouble("ticks", ticksLeft);
        return super.save(nbt);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        energyStorage.readFromNBT(nbt);
        ticksLeft = nbt.getDouble("ticks");
        super.load(state, nbt);
    }

    @Override
    public void tick() {
        if (level == null) return;
        if (ticksLeft <= 0 && isWorldInLoop()) {
            ticksLeft = 12000;
            addPoint();
            addParaboxLevel();
            setChanged();
        } else if (isWorldInLoop()) {
            double percentage = energyStorage.drainAll();
            ticksLeft -= percentage;
            setChanged();
        } else if (ticksLeft != 12000 && !level.isClientSide) {
            ticksLeft = 12000;
            setChanged();
        }
        if (!level.isClientSide) {
            int lvl = getParaboxLevel();
            if (lvl > paraboxLevel) {
                paraboxLevel = lvl;
                setChanged();
            }
        }
    }

    private void addPoint() {
        if (level == null || level.isClientSide) return;
        ServerWorld world = (ServerWorld) level;
        SkyblockData data = SkyblockData.get(world);
        data.addPoint(1);
        world.getServer().getPlayerList().broadcastAll(new STitlePacket(STitlePacket.Type.TIMES, new TranslationTextComponent("points.gain", data.getPoint()), 5, 60, 5));
    }

    private int getParaboxLevel() {
        if (level == null || level.isClientSide) return 0;
        ServerWorld world = (ServerWorld) level;
        SkyblockData data = SkyblockData.get(world);
        return data.getParaboxLevel();
    }

    private void addParaboxLevel() {
        if (level == null || level.isClientSide) return;
        ServerWorld world = (ServerWorld) level;
        SkyblockData data = SkyblockData.get(world);
        data.setParaboxLevel(++paraboxLevel);
        energyStorage.setNewMax((int) Math.pow(2, paraboxLevel) * 2048);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("block.skyfarm.parabox");
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ParaboxContainer(id);
    }
}

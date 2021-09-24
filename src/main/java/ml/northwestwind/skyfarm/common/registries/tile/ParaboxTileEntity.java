package ml.northwestwind.skyfarm.common.registries.tile;

import ml.northwestwind.skyfarm.common.registries.container.ParaboxContainer;
import ml.northwestwind.skyfarm.common.registries.tile.handler.ParaboxEnergyStorage;
import ml.northwestwind.skyfarm.common.registries.tile.handler.ParaboxItemHandler;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import ml.northwestwind.skyfarm.misc.backup.Backups;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParaboxTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
    private final ParaboxEnergyStorage energyStorage = new ParaboxEnergyStorage();
    private final ParaboxItemHandler inventory = new ParaboxItemHandler(1);
    private final boolean empowered;
    private double ticksLeft, efficiency;
    private int paraboxLevel, energy;
    private boolean isInLoop, isBackingUp;
    private ItemStack wantingItem = ItemStack.EMPTY;

    public ParaboxTileEntity(TileEntityType<?> type, boolean empowered) {
        super(type);
        this.empowered = empowered;
        this.ticksLeft = empowered ? 12000 : 18000;
    }

    public ParaboxTileEntity(boolean empowered) {
        this(empowered ? RegistryEvents.TileEntityTypes.POWERBOX : RegistryEvents.TileEntityTypes.PARABOX, empowered);
    }

    public boolean isWorldInLoop() {
        if (!(level instanceof ServerWorld)) return isInLoop;
        SkyblockData data = SkyblockData.get((ServerWorld) level);
        isInLoop = data.isInLoop();
        return isInLoop;
    }

    public boolean isBackingUp() {
        if (!(level instanceof ServerWorld)) return isBackingUp;
        isBackingUp = Backups.INSTANCE.doingBackup.isRunning();
        return isBackingUp;
    }

    public boolean isUsingThis() {
        if (!(level instanceof ServerWorld)) return false;
        SkyblockData data = SkyblockData.get((ServerWorld) level);
        return data.isUsing(this.getBlockPos());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap.equals(CapabilityEnergy.ENERGY) && isWorldInLoop() && isUsingThis()) return LazyOptional.of(() -> (T) energyStorage);
        else if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) && isUsingThis() && isWorldInLoop()) return LazyOptional.of(() -> (T) inventory);
        return super.getCapability(cap, side);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        energyStorage.writeToNBT(nbt);
        nbt.putDouble("ticks", ticksLeft);
        ItemStackHelper.saveAllItems(nbt, this.inventory.toNonNullList());
        return nbt;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        energyStorage.readFromNBT(nbt);
        ticksLeft = nbt.getDouble("ticks");
        NonNullList<ItemStack> inv = NonNullList.withSize(this.inventory.getSlots(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbt, inv);
        this.inventory.setNonNullList(inv);
    }

    @Override
    public void tick() {
        if (level == null) return;
        boolean dirty = false;
        if (ticksLeft <= 0 && isWorldInLoop()) {
            addPoint();
            addParaboxLevel();
            if (empowered) ticksLeft = 12000 + paraboxLevel * 1200;
            else ticksLeft = 18000;
            dirty = true;
        } else if (isWorldInLoop()) {
            if (wantingItem.isEmpty() && level instanceof ServerWorld) {
                ServerWorld world = (ServerWorld) level;
                wantingItem = new ItemStack(SkyblockData.getWantingItem());
                if (wantingItem.isEmpty()) wantingItem = new ItemStack(SkyblockData.generateItem(world));
            }
            energy = energyStorage.getEnergyStored();
            double percentage = energyStorage.drainAll();
            efficiency = percentage;
            ticksLeft -= percentage;
            if (!inventory.isEmpty()) {
                inventory.clear();
                ticksLeft -= 1200;
                if (level instanceof ServerWorld) {
                    ServerWorld world = (ServerWorld) level;
                    world.getServer().getPlayerList().broadcastMessage(new TranslationTextComponent("parabox.item").setStyle(Style.EMPTY.applyFormat(TextFormatting.GOLD)), ChatType.SYSTEM, Util.NIL_UUID);
                    wantingItem = new ItemStack(SkyblockData.generateItem(world));
                }
            }
            dirty = true;
        } else if (ticksLeft != 12000 && !level.isClientSide) {
            if (empowered) ticksLeft = 12000 + paraboxLevel * 1200;
            else ticksLeft = 18000;
            energy = 0;
            dirty = true;
        }
        if (!level.isClientSide) {
            int lvl = getParaboxLevel();
            if (lvl != paraboxLevel) paraboxLevel = lvl;
        }
        if (dirty) {
            setChanged();
            if (level != null) level.sendBlockUpdated(getBlockPos(), (empowered ? RegistryEvents.Blocks.POWERBOX : RegistryEvents.Blocks.PARABOX).defaultBlockState(), (empowered ? RegistryEvents.Blocks.POWERBOX : RegistryEvents.Blocks.PARABOX).defaultBlockState(), 3);
        }
    }

    private void addPoint() {
        if (level == null || level.isClientSide) return;
        int points = empowered ? paraboxLevel + 3 : 1;
        ServerWorld world = (ServerWorld) level;
        SkyblockData data = SkyblockData.get(world);
        data.addGlobalPoint(points);
        data.setDirty();
        world.getServer().getPlayerList().broadcastMessage(new TranslationTextComponent("points.gain", points).setStyle(Style.EMPTY.applyFormat(TextFormatting.GOLD)), ChatType.SYSTEM, Util.NIL_UUID);
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
        data.setDirty();
        energyStorage.setNewMax((paraboxLevel + 1) * 4096);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("block.skyfarm.parabox");
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ParaboxContainer(id, this);
    }

    public CompoundNBT saveAdditional(CompoundNBT nbt) {
        nbt.putInt("GuiEnergy", energy);
        nbt.putDouble("efficiency", efficiency);
        nbt.putInt("level", paraboxLevel);
        nbt.putBoolean("looping", isWorldInLoop());
        nbt.putBoolean("backingUp", isBackingUp());
        if (!wantingItem.isEmpty()) nbt.putString("wantingItem", wantingItem.getItem().getRegistryName().toString());
        return nbt;
    }

    public void loadAdditional(CompoundNBT nbt) {
        energy = nbt.getInt("GuiEnergy");
        efficiency = nbt.getDouble("efficiency");
        paraboxLevel = nbt.getInt("level");
        isInLoop = nbt.getBoolean("looping");
        isBackingUp = nbt.getBoolean("backingUp");
        String id = nbt.getString("wantingItem");
        if (!id.isEmpty()) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
            if (item != null) wantingItem = new ItemStack(item);
        }
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.save(nbt);
        this.saveAdditional(nbt);
        return new SUpdateTileEntityPacket(this.getBlockPos(), 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        if (level != null) this.load(level.getBlockState(pkt.getPos()), nbt);
        this.loadAdditional(nbt);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
        this.load(state, nbt);
        this.loadAdditional(nbt);
    }

    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = new CompoundNBT();
        this.save(nbt);
        this.saveAdditional(nbt);
        return nbt;
    }

    public double getTicksLeft() {
        return ticksLeft;
    }

    public ParaboxEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public double getEfficiency() {
        return efficiency;
    }

    public int paraboxLevel() {
        return paraboxLevel;
    }

    @Nonnull
    public ItemStack getWantingItem() {
        return wantingItem;
    }

    public int getEnergy() {
        return energy;
    }
}

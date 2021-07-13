package ml.northwestwind.skyfarm.common.registries.tile;

import ml.northwestwind.skyfarm.common.recipes.EvaporatingRecipe;
import ml.northwestwind.skyfarm.common.registries.tile.handler.SkyFarmItemHandler;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class NaturalEvaporatorTileEntity extends TileEntity implements ITickableTileEntity, IClearable, IInventory {
    private final SkyFarmItemHandler inventory;
    private ItemStack stackLastTick;
    private EvaporatingRecipe recipe;
    private int tick = 0;

    public NaturalEvaporatorTileEntity(TileEntityType<?> type) {
        super(type);
        inventory = new SkyFarmItemHandler(1);
    }

    public NaturalEvaporatorTileEntity() {
        this(RegistryEvents.TileEntityTypes.NATURAL_EVAPORATOR);
    }

    @Override
    public void tick() {
        if (!canWork()) return;
        boolean dirty = false;

        if (this.level != null && !this.level.isClientSide) {
            ItemStack stack = inventory.getStackInSlot(0);
            if (!stack.equals(stackLastTick)) {
                recipe = getRecipe(stack);
                stackLastTick = stack;
            }
            if (recipe == null) {
                tick = 0;
                return;
            }
            if (tick < recipe.getTick()) {
                tick += 1;
            } else {
                ItemStack output = recipe.getResultItem();
                if (this.level.random.nextDouble() < recipe.getChance()) inventory.setStackInSlot(0, output);
                else inventory.setStackInSlot(0, ItemStack.EMPTY);
                tick = 0;
            }
            dirty = true;
        }

        if (dirty) {
            this.setChanged();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
        }
    }

    public boolean isEvaporating(int index) {
        ItemStack stack = inventory.getStackInSlot(index);
        EvaporatingRecipe recipe = getRecipe(stack);
        return recipe != null && canWork();
    }

    private boolean canWork() {
        if (!this.hasLevel()) return false;
        World world = getLevel();
        return !world.isNight() || world.dimension().equals(World.NETHER);
    }

    @Override
    public int getContainerSize() {
        return inventory.getSize();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    public boolean addItem(ItemStack stack) {
        if (stack.isEmpty() || level == null) return false;
        ItemStack stackInSlot = inventory.getStackInSlot(0);
        if (stackInSlot.isEmpty()) {
            inventory.insertItem(0, stack.split(1), false);
            setChanged();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
            return true;
        }
        return false;
    }

    public boolean takeItem(PlayerEntity player) {
        if (level == null) return false;
        ItemStack stackInSlot = inventory.getStackInSlot(0);
        if (!stackInSlot.isEmpty()) {
            player.addItem(removeItem(0, 1));
            setChanged();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
            return true;
        }
        return false;
    }

    @Override
    public ItemStack getItem(int index) {
        return inventory.getStackInSlot(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return inventory.decrStackSize(index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return inventory.decrStackSize(index, 64);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        inventory.insertItem(index, stack, false);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        inventory.deserializeNBT(compound.getCompound("inventory"));
        tick = compound.getInt("progression");
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        compound.put("inventory", inventory.serializeNBT());
        compound.putInt("progression", tick);
        return compound;
    }

    @Override
    public void clearContent() {
        inventory.clear();
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.save(nbt);

        return new SUpdateTileEntityPacket(this.getBlockPos(), 1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.load(RegistryEvents.Blocks.NATURAL_EVAPORATOR.defaultBlockState(), pkt.getTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.load(state, tag);
    }

    public NonNullList<ItemStack> getItems() {
        return inventory.toNonNullList();
    }

    @Nullable
    private EvaporatingRecipe getRecipe(ItemStack stack) {
        if (stack == null || this.level == null || this.level.getServer() == null) {
            return null;
        }

        List<IRecipe<IInventory>> recipes = this.level.getServer().getRecipeManager().getAllRecipesFor(RegistryEvents.Recipes.EVAPORATING.getType());
        for (IRecipe<?> iRecipe : recipes) {
            EvaporatingRecipe recipe = (EvaporatingRecipe) iRecipe;
            SkyFarmItemHandler fakeInv = new SkyFarmItemHandler(1, stack);
            if (recipe.matches(new RecipeWrapper(fakeInv), this.level)) {
                return recipe;
            }
        }

        return null;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) return LazyOptional.of(() -> (T) inventory);
        return super.getCapability(cap, side);
    }
}

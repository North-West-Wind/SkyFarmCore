package ml.northwestwind.skyfarm.tile.handler;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class SkyFarmItemHandler extends ItemStackHandler {
    private final int size;

    public SkyFarmItemHandler(int size, ItemStack... stacks) {
        super(size);
        this.size = size;
        for (int index = 0; index < stacks.length; index++) {
            this.stacks.set(index, stacks[index]);
        }
    }

    public void clear() {
        for (int index = 0; index < this.getSlots(); index++) {
            this.stacks.set(index, ItemStack.EMPTY);
            this.onContentsChanged(index);
        }
    }

    public boolean isEmpty() {
        for (ItemStack stack : this.stacks) {
            if (stack.isEmpty() || stack.getItem() == Items.AIR) {
                return true;
            }
        }
        return false;
    }

    public ItemStack decrStackSize(int index, int count) {
        return extractItem(index, count, false);
    }

    public void removeStackFromSlot(int index) {
        this.stacks.set(index, ItemStack.EMPTY);
        this.onContentsChanged(index);
    }

    public NonNullList<ItemStack> toNonNullList() {
        NonNullList<ItemStack> items = NonNullList.create();
        items.addAll(this.stacks);
        return items;
    }

    public void setNonNullList(NonNullList<ItemStack> items) {
        if (items.size() == 0)
            return;
        if (items.size() != this.getSlots())
            throw new IndexOutOfBoundsException("NonNullList must be same size as ItemStackHandler!");
        for (int index = 0; index < items.size(); index++) {
            this.stacks.set(index, items.get(index));
        }
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return this.stacks.toString();
    }
}

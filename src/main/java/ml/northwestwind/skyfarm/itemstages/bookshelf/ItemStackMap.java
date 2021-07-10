package ml.northwestwind.skyfarm.itemstages.bookshelf;

import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

// Copied from old Bookshelf
public class ItemStackMap<T> extends HashMap<ItemStack, T> {
    private final IStackComparator comparator;

    public ItemStackMap(IStackComparator comparator) {
        this.comparator = comparator;
    }

    @Override
    public boolean containsKey (Object key) {
        for (final Map.Entry<ItemStack, T> entry : this.entrySet())
            if (this.comparator.isValid(entry.getKey(), key))
                return true;
        return super.containsKey(key);
    }

    @Override
    public T remove (Object key) {
        this.entrySet().removeIf(entry -> this.comparator.isValid(entry.getKey(), key));
        return super.remove(key);
    }

    @Override
    public T get (Object key) {
        for (final Map.Entry<ItemStack, T> entry : this.entrySet())
            if (this.comparator.isValid(entry.getKey(), key))
                return entry.getValue();
        return super.get(key);
    }

    public interface IStackComparator {
        boolean isValid (ItemStack first, Object second);
    }
}

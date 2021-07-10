package ml.northwestwind.skyfarm.itemstages.bookshelf;

import net.darkhax.bookshelf.Bookshelf;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public final class StackUtils {
    private StackUtils() {
        throw new IllegalAccessError("Utility class");
    }

    public static boolean areStacksSimilar (@Nonnull ItemStack firstStack, @Nonnull ItemStack secondStack) {
        return firstStack.getItem() == secondStack.getItem();
    }

    @Deprecated
    public static ItemStack getStackFromState (AbstractBlock.AbstractBlockState state, int size) {
        return new ItemStack(state.getBlock(), size);
    }

    @Deprecated
    public static AbstractBlock.AbstractBlockState getStateFromStack (ItemStack stack) {
        final Block block = Block.byItem(stack.getItem());
        return block.defaultBlockState();
    }

    public static CompoundNBT getTagCleanly (ItemStack stack) {
        return stack.getOrCreateTag();
    }

    public static ItemStack[] getAllItems (Item item) {
        return findVariations(item).toArray(new ItemStack[0]);
    }

    public static NonNullList<ItemStack> findVariations (Item item) {

        final NonNullList<ItemStack> items = NonNullList.create();

        for (final ItemGroup tab : item.getCreativeTabs()) {
            if (tab == null) {
                if (item == Items.ENCHANTED_BOOK) {
                    item.fillItemCategory(ItemGroup.TAB_SEARCH, items);
                }
                items.add(new ItemStack(item));
            } else {
                final NonNullList<ItemStack> subItems = NonNullList.create();
                try {
                    item.fillItemCategory(tab, subItems);
                } catch (final Exception e) {
                    Bookshelf.LOG.error("Caught the following exception while getting sub items for {}. It should be reported to that mod's author.", item.getRegistryName().toString());
                    Bookshelf.LOG.catching(e);
                }
                items.addAll(subItems);
            }
        }
        return items;
    }

    public static String getStackIdentifier (ItemStack stack) {
        return stack != null && !stack.isEmpty() ? stack.getItem().getRegistryName().toString() : "minecraft:air";
    }
}
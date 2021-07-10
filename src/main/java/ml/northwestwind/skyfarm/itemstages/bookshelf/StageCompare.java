package ml.northwestwind.skyfarm.itemstages.bookshelf;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;

public class StageCompare implements ItemStackMap.IStackComparator {
    public static final ItemStackMap.IStackComparator INSTANCE = new StageCompare();

    @Override
    public boolean isValid (ItemStack entryStack, Object second) {
        if (second instanceof ItemStack) {
            final ItemStack stack = (ItemStack) second;
            final CompoundNBT first = StackUtils.getTagCleanly(entryStack);
            final CompoundNBT two = StackUtils.getTagCleanly(stack);
            return ((!this.isTagEmpty(stack) && arePartiallySimilar(first, two)) || (this.isTagEmpty(stack) && this.isTagEmpty(entryStack))) && StackUtils.areStacksSimilar(stack, entryStack);
        }
        return false;
    }

    private boolean isTagEmpty (ItemStack stack) {
        return !stack.hasTag() || stack.getTag().isEmpty();
    }

    public static boolean arePartiallySimilar (INBT one, INBT two) {
        if (one == null) return false;
        if (two == null) return true;
        else if (one instanceof CompoundNBT && two instanceof CompoundNBT) {
            final CompoundNBT tagOne = (CompoundNBT) one;
            final CompoundNBT tagTwo = (CompoundNBT) two;

            for (final String key : tagTwo.getAllKeys()) {
                if (!arePartiallySimilar(tagOne.get(key), tagTwo.get(key))) return false;
            }
            return true;
        }
        else if (one instanceof ListNBT && two instanceof ListNBT) {
            final ListNBT listOne = (ListNBT) one;
            final ListNBT listTwo = (ListNBT) two;
            for (INBT inbt : listTwo) {
                boolean similar = false;
                for (INBT value : listOne) {
                    if (arePartiallySimilar(value, inbt)) {
                        similar = true;
                        break;
                    }
                }
                if (!similar) return false;
            }
            return true;
        }
        return two.equals(one);
    }
}

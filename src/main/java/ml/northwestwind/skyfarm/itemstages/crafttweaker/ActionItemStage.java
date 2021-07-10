package ml.northwestwind.skyfarm.itemstages.crafttweaker;

import com.blamejared.crafttweaker.api.actions.IAction;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.impl.helper.CraftTweakerHelper;
import ml.northwestwind.skyfarm.itemstages.bookshelf.StackUtils;
import net.darkhax.bookshelf.Bookshelf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.StringJoiner;

public abstract class ActionItemStage implements IAction {
    private ItemStack[] restrictions;
    public ActionItemStage(Item item) {
        this.restrictions = StackUtils.getAllItems(item);
        if (this.restrictions.length == 0) {
            this.restrictions = new ItemStack[] { new ItemStack(item) };
        }
    }

    public ActionItemStage(IIngredient restricted) {
        this.restrictions = CraftTweakerHelper.getItemStacks(restricted.getItems());
    }

    protected ItemStack[] getRestrictedItems () {
        return this.restrictions;
    }

    private String describeStack (ItemStack stack) {
        return String.format("%s - %s:%s", stack.getDisplayName(), StackUtils.getStackIdentifier(stack), stack.getOrCreateTag());
    }

    protected String describeRestrictedStacks () {
        if (this.restrictions.length == 1) return this.describeStack(this.restrictions[0]);
        final StringJoiner joiner = new StringJoiner(Bookshelf.NEW_LINE);
        for (final ItemStack stack : this.getRestrictedItems()) joiner.add(this.describeStack(stack));
        return this.getRestrictedItems().length + " entries: " + Bookshelf.NEW_LINE + joiner;
    }

    protected void validate () {
        if (this.restrictions.length == 0) throw new IllegalArgumentException("No items or blocks found for this entry!");
        for (final ItemStack stack : this.restrictions)
            if (stack.isEmpty())
                throw new IllegalArgumentException("Entry contains an empty/air stack!");
    }
}

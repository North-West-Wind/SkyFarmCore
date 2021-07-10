package ml.northwestwind.skyfarm.itemstages.crafttweaker;

import com.blamejared.crafttweaker.api.item.IIngredient;
import ml.northwestwind.skyfarm.itemstages.ItemStages;
import net.minecraft.item.ItemStack;

public class ActionRemoveRestriction extends ActionItemStage {
    public ActionRemoveRestriction(IIngredient restricted) {
        super(restricted);
    }

    @Override
    public void apply() {
        this.validate();
        for (final ItemStack stack : this.getRestrictedItems())
            ItemStages.ITEM_STAGES.remove(stack);
    }

    @Override
    public String describe() {
        return "Removing item stage for " + this.describeRestrictedStacks();
    }
}

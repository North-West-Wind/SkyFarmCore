package ml.northwestwind.skyfarm.common.recipes;

import ml.northwestwind.skyfarm.events.RegistryEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class CompactBrickRecipe extends AbstractCompactBrickRecipe {
    public CompactBrickRecipe(ResourceLocation id, Ingredient input, ItemStack output, double chance) {
        super(RegistryEvents.Recipes.COMPACT_BRICK.getType(), id, input, output, chance);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RegistryEvents.Recipes.COMPACT_BRICK.getSerializer();
    }

    @Override
    public ItemStack getToastSymbol() {
        return RegistryEvents.Items.COMPACT_BRICK.getDefaultInstance();
    }
}

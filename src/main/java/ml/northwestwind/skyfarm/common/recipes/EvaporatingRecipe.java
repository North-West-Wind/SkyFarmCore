package ml.northwestwind.skyfarm.common.recipes;

import mcp.MethodsReturnNonnullByDefault;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

@MethodsReturnNonnullByDefault
public class EvaporatingRecipe extends AbstractEvaporatingRecipe {

    public EvaporatingRecipe(ResourceLocation id, Ingredient input, ItemStack output, int tick, double chance) {
        super(RegistryEvents.Recipes.EVAPORATING.getType(), id, input, output, tick, chance);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RegistryEvents.Recipes.EVAPORATING.getSerializer();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(RegistryEvents.Blocks.NATURAL_EVAPORATOR);
    }
}

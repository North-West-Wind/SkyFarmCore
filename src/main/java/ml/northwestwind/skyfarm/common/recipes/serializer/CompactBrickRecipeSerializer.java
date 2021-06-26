package ml.northwestwind.skyfarm.common.recipes.serializer;

import com.google.gson.JsonObject;
import ml.northwestwind.skyfarm.common.recipes.AbstractCompactBrickRecipe;
import ml.northwestwind.skyfarm.common.recipes.CompactBrickRecipe;
import ml.northwestwind.skyfarm.common.recipes.holders.RecipeHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class CompactBrickRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CompactBrickRecipe> {
    @Override
    public CompactBrickRecipe fromJson(ResourceLocation id, JsonObject json) {
        ItemStack output = CraftingHelper.getItemStack(JSONUtils.getAsJsonObject(json, "output"), true);
        Ingredient input = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "input"));
        double chance = JSONUtils.getAsFloat(json, "chance", 1);

        CompactBrickRecipe recipe = new CompactBrickRecipe(id, input, output, chance);
        RecipeHolder.addRecipes(AbstractCompactBrickRecipe.RECIPE_TYPE_ID, recipe);
        return recipe;
    }

    @Override
    public CompactBrickRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
        ItemStack output = buffer.readItem();
        Ingredient input = Ingredient.fromNetwork(buffer);
        double chance = buffer.readDouble();

        CompactBrickRecipe recipe = new CompactBrickRecipe(recipeId, input, output, chance);
        RecipeHolder.addRecipes(AbstractCompactBrickRecipe.RECIPE_TYPE_ID, recipe);
        return recipe;
    }

    @Override
    public void toNetwork(PacketBuffer buffer, CompactBrickRecipe recipe) {
        buffer.writeItem(recipe.getResultItem());
        recipe.getIngredients().get(0).toNetwork(buffer);
        buffer.writeDouble(recipe.getChance());
    }
}

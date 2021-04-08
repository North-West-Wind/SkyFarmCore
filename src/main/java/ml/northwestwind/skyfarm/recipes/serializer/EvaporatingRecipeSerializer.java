package ml.northwestwind.skyfarm.recipes.serializer;

import com.google.gson.JsonObject;
import ml.northwestwind.skyfarm.recipes.EvaporatingRecipe;
import ml.northwestwind.skyfarm.recipes.holders.EvaporatingRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class EvaporatingRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<EvaporatingRecipe> {
    @Override
    public EvaporatingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        ItemStack output = CraftingHelper.getItemStack(JSONUtils.getAsJsonObject(json, "output"), true);
        Ingredient input = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "input"));
        int tick = JSONUtils.getAsInt(json, "evaporateTime");
        double chance = JSONUtils.getAsFloat(json, "chance", 1);

        EvaporatingRecipe recipe = new EvaporatingRecipe(recipeId, input, output, tick, chance);
        EvaporatingRecipes.addRecipes(recipe);
        return recipe;
    }

    @Override
    public EvaporatingRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
        ItemStack output = buffer.readItem();
        Ingredient input = Ingredient.fromNetwork(buffer);
        int tick = buffer.readInt();
        double chance = buffer.readDouble();

        EvaporatingRecipe recipe = new EvaporatingRecipe(recipeId, input, output, tick, chance);
        EvaporatingRecipes.addRecipes(recipe);
        return recipe;
    }

    @Override
    public void toNetwork(PacketBuffer buffer, EvaporatingRecipe recipe) {
        buffer.writeDouble(recipe.getChance());
        buffer.writeInt(recipe.getTick());
        Ingredient input = recipe.getIngredients().get(0);
        input.toNetwork(buffer);

        buffer.writeItemStack(recipe.getResultItem(), false);
    }
}

package ml.northwestwind.skyfarm.recipes;

import ml.northwestwind.skyfarm.SkyFarm;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;

public interface IEvaporatingRecipe extends IRecipe<RecipeWrapper> {
    ResourceLocation RECIPE_TYPE_ID = new ResourceLocation(SkyFarm.MOD_ID, "evaporating");

    @Nonnull
    @Override
    default IRecipeType<?> getType() {
        return Registry.RECIPE_TYPE.getOptional(RECIPE_TYPE_ID).get();
    }

    @Override
    default boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    Ingredient getInput();
}

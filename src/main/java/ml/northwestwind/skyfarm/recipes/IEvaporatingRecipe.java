package ml.northwestwind.skyfarm.recipes;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;

public interface IEvaporatingRecipe extends IRecipe<RecipeWrapper> {
    ResourceLocation RECIPE_TYPE_ID = new ResourceLocation(SkyFarm.MOD_ID, "evaporating");

    @Nonnull
    @Override
    default IRecipeType<?> getType() {
        return RegistryEvents.RecipeType.EVAPORATING.getType();
    }

    @Override
    default boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    Ingredient getInput();

    int getTick();
}

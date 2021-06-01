package ml.northwestwind.skyfarm.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import ml.northwestwind.skyfarm.jei.category.CompactBrickCategory;
import ml.northwestwind.skyfarm.jei.category.EvaporatingCategory;
import ml.northwestwind.skyfarm.recipes.AbstractCompactBrickRecipe;
import ml.northwestwind.skyfarm.recipes.AbstractEvaporatingRecipe;
import ml.northwestwind.skyfarm.recipes.holders.RecipeHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class SkyFarmJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(SkyFarm.MOD_ID, "main");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new EvaporatingCategory(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(new CompactBrickCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        registry.addRecipes(RecipeHolder.getRecipes(AbstractEvaporatingRecipe.RECIPE_TYPE_ID), EvaporatingCategory.UID);
        registry.addRecipes(RecipeHolder.getRecipes(AbstractCompactBrickRecipe.RECIPE_TYPE_ID), CompactBrickCategory.UID);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(RegistryEvents.Blocks.NATURAL_EVAPORATOR), EvaporatingCategory.UID);
        registry.addRecipeCatalyst(RegistryEvents.Items.COMPACT_BRICK.getDefaultInstance(), CompactBrickCategory.UID);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registry) {
        ISubtypeInterpreter interpreter = stack -> {
            String type = stack.getOrCreateTag().getString("Type");
            if (!SkyFarm.BEE_TYPES.containsKey(type)) return ISubtypeInterpreter.NONE;
            return stack.getItem().getName(stack).getString();
        };

        registry.registerSubtypeInterpreter(RegistryEvents.Items.MUTATION_POLLEN, interpreter);
    }
}

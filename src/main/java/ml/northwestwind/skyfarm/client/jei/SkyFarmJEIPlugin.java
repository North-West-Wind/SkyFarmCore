package ml.northwestwind.skyfarm.client.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.client.jei.category.CompactBrickCategory;
import ml.northwestwind.skyfarm.client.jei.category.EvaporatingCategory;
import ml.northwestwind.skyfarm.common.recipes.CompactBrickRecipe;
import ml.northwestwind.skyfarm.common.recipes.EvaporatingRecipe;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import net.minecraft.client.Minecraft;
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
        registry.addRecipes(Minecraft.getInstance().getConnection().getRecipeManager().getAllRecipesFor(RegistryEvents.Recipes.EVAPORATING.getType()), EvaporatingRecipe.RECIPE_TYPE_ID);
        registry.addRecipes(Minecraft.getInstance().getConnection().getRecipeManager().getAllRecipesFor(RegistryEvents.Recipes.COMPACT_BRICK.getType()), CompactBrickRecipe.RECIPE_TYPE_ID);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(RegistryEvents.Blocks.NATURAL_EVAPORATOR), EvaporatingRecipe.RECIPE_TYPE_ID);
        registry.addRecipeCatalyst(RegistryEvents.Items.COMPACT_BRICK.getDefaultInstance(), CompactBrickRecipe.RECIPE_TYPE_ID);
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

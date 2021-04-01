package ml.northwestwind.skyfarm.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import ml.northwestwind.skyfarm.jei.category.EvaporatingCategory;
import ml.northwestwind.skyfarm.recipes.holders.EvaporatingRecipes;
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
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        registry.addRecipes(EvaporatingRecipes.getRecipes(), EvaporatingCategory.UID);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(RegistryEvents.Blocks.NATURAL_EVAPORATOR), EvaporatingCategory.UID);
    }
}

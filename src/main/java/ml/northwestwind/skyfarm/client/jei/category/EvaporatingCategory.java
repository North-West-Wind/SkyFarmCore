package ml.northwestwind.skyfarm.client.jei.category;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.client.jei.builder.JEIIngredientStackListBuilder;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import ml.northwestwind.skyfarm.common.recipes.AbstractEvaporatingRecipe;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EvaporatingCategory implements IRecipeCategory<AbstractEvaporatingRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(SkyFarm.MOD_ID, "evaporating");
    private final IDrawable background;
    private final String localizedName;
    private final IDrawable icon;

    public EvaporatingCategory(IGuiHelper guiHelper) {
        localizedName = I18n.get("recipe.skyfarm.evaporating");
        background = guiHelper.createDrawable(new ResourceLocation(SkyFarm.MOD_ID, "textures/gui/background/natural_evaporator_background.png"), 0, 0, 108, 64);
        icon = guiHelper.createDrawableIngredient(new ItemStack(RegistryEvents.Blocks.NATURAL_EVAPORATOR));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends AbstractEvaporatingRecipe> getRecipeClass() {
        return AbstractEvaporatingRecipe.class;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(AbstractEvaporatingRecipe recipe, IIngredients iIngredients) {
        iIngredients.setInputLists(VanillaTypes.ITEM, JEIIngredientStackListBuilder.make(recipe.getInput()).build());
        iIngredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AbstractEvaporatingRecipe recipe, IIngredients ingredients) {
        IGuiIngredientGroup<ItemStack> group = recipeLayout.getItemStacks();

        group.init(0, true, 16, 15);
        group.set(0, Arrays.asList(recipe.getInput().getItems()));

        group.init(1, false, 70, 15);
        group.set(1, recipe.getResultItem());

        group.init(2, true, 16, 31);
        group.set(2, new ItemStack(RegistryEvents.Blocks.NATURAL_EVAPORATOR));
    }
}

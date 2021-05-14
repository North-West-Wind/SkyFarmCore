package ml.northwestwind.skyfarm.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import ml.northwestwind.skyfarm.jei.builder.JEIIngredientStackListBuilder;
import ml.northwestwind.skyfarm.recipes.AbstractCompactBrickRecipe;
import ml.northwestwind.skyfarm.recipes.CompactBrickRecipe;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class CompactBrickCategory implements IRecipeCategory<AbstractCompactBrickRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(SkyFarm.MOD_ID, "compact_brick");
    private final IDrawable background;
    private final String localizedName;
    private final IDrawable icon;

    public CompactBrickCategory(IGuiHelper guiHelper) {
        localizedName = I18n.get("recipe.skyfarm.compact_brick");
        background = guiHelper.createDrawable(new ResourceLocation(SkyFarm.MOD_ID, "textures/gui/background/compact_brick_background.png"), 0, 0, 116, 64);
        icon = guiHelper.createDrawableIngredient(new ItemStack(RegistryEvents.Items.COMPACT_BRICK));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends AbstractCompactBrickRecipe> getRecipeClass() {
        return CompactBrickRecipe.class;
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
    public void setIngredients(AbstractCompactBrickRecipe recipe, IIngredients iIngredients) {
        iIngredients.setInputLists(VanillaTypes.ITEM, JEIIngredientStackListBuilder.make(recipe.getInput()).build());
        iIngredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AbstractCompactBrickRecipe recipe, IIngredients ingredients) {
        IGuiIngredientGroup<ItemStack> group = recipeLayout.getItemStacks();

        group.init(0, true, 20, 15);
        group.set(0, Arrays.asList(recipe.getInput().getItems()));

        group.init(1, false, 40, 15);
        group.set(1, recipe.getResultItem());

        group.init(2, true, 10, 15);
        group.set(2, RegistryEvents.Items.COMPACT_BRICK.getDefaultInstance());
    }
}

package ml.northwestwind.skyfarm.recipes.holders;

import com.google.common.collect.Lists;
import ml.northwestwind.skyfarm.recipes.CompactBrickRecipe;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CompactBrickRecipes {
    private static final List<CompactBrickRecipe> recipes = Lists.newArrayList();

    public static void addRecipes(CompactBrickRecipe ...recipe) {
        recipes.addAll(Arrays.asList(recipe));
    }

    public static List<CompactBrickRecipe> getRecipes() {
        return recipes;
    }
}

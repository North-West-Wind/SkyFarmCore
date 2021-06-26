package ml.northwestwind.skyfarm.common.recipes.holders;

import com.google.common.collect.Lists;
import ml.northwestwind.skyfarm.common.recipes.EvaporatingRecipe;

import java.util.Arrays;
import java.util.List;

public class EvaporatingRecipes {
    private static final List<EvaporatingRecipe> recipes = Lists.newArrayList();

    public static void addRecipes(EvaporatingRecipe ...recipe) {
        recipes.addAll(Arrays.asList(recipe));
    }

    public static List<EvaporatingRecipe> getRecipes() {
        return recipes;
    }
}

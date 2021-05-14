package ml.northwestwind.skyfarm.recipes.holders;

import com.google.common.collect.Maps;
import ml.northwestwind.skyfarm.SkyFarm;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RecipeHolder {
    private static final Map<ResourceLocation, List<? extends IRecipe<?>>> RECIPE_HOLDER = Maps.newHashMap();

    public static <R extends IRecipe<?>> void addRecipes(ResourceLocation rl, R...recipe) {
        if (!RECIPE_HOLDER.containsKey(rl)) return;
        List<R> list = (List<R>) RECIPE_HOLDER.get(rl);
        list.addAll(Arrays.asList(recipe));
        RECIPE_HOLDER.put(rl, list);
        Arrays.stream(recipe).forEach((r) -> SkyFarm.LOGGER.info(String.format("Added recipe %s to recipe holder %s", r.getId(), rl.toString())));
    }

    public static <R extends IRecipe<?>> List<R> getRecipes(ResourceLocation rl) {
        return (List<R>) RECIPE_HOLDER.get(rl);
    }
}

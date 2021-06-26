package ml.northwestwind.skyfarm.common.recipes.holders;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;

public class RecipeHolder {
    private static final Map<ResourceLocation, List<? extends IRecipe<?>>> RECIPE_HOLDER = Maps.newHashMap();

    public static <R extends IRecipe<?>> void addRecipes(ResourceLocation rl, R...recipe) {
        List<R> list;
        if (!RECIPE_HOLDER.containsKey(rl)) list = Lists.newArrayList();
        else list = (List<R>) RECIPE_HOLDER.get(rl);
        for (R r : recipe) {
            list.stream().filter((re) -> re.getId().equals(r.getId())).findFirst().ifPresent(list::remove);
            list.add(r);
        }
        RECIPE_HOLDER.put(rl, list);
    }

    public static <R extends IRecipe<?>> List<R> getRecipes(ResourceLocation rl) {
        return (List<R>) RECIPE_HOLDER.get(rl);
    }
}

package ml.northwestwind.skyfarm.client.jei.builder;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Thank you Immersive Engineering
public class JEIIngredientStackListBuilder
{
    private final List<List<ItemStack>> list;

    private JEIIngredientStackListBuilder()
    {
        this.list = new ArrayList<>();
    }

    public static JEIIngredientStackListBuilder make(Ingredient... ingredientStacks)
    {
        JEIIngredientStackListBuilder builder = new JEIIngredientStackListBuilder();
        builder.add(ingredientStacks);
        return builder;
    }

    public JEIIngredientStackListBuilder add(Ingredient... ingredientStacks)
    {
        for(Ingredient ingr : ingredientStacks)
            this.list.add(Arrays.asList(ingr.getItems()));
        return this;
    }

    public List<List<ItemStack>> build()
    {
        return this.list;
    }
}
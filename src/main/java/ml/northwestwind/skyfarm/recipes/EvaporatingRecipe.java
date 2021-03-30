package ml.northwestwind.skyfarm.recipes;

import ml.northwestwind.skyfarm.events.RegistryEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class EvaporatingRecipe implements IEvaporatingRecipe {
    private final ResourceLocation id;
    private final Ingredient input;
    private final ItemStack output;
    private final int tick;

    public EvaporatingRecipe(ResourceLocation id, Ingredient input, ItemStack output, int tick) {
        this.id = id;
        this.output = output;
        this.input = input;
        this.tick = tick;
    }

    @Override
    public boolean matches(RecipeWrapper inv, World worldIn) {
        return this.input.test(inv.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeWrapper inv) {
        return this.output.copy();
    }

    @Override
    public ItemStack getResultItem() {
        return this.output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RegistryEvents.SkyFarmRecipeSerializers.EVAPORATING_SERIALIZER;
    }

    @Override
    public Ingredient getInput() {
        return this.input;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(null, this.input);
    }

    public int getTick() {
        return tick;
    }
}

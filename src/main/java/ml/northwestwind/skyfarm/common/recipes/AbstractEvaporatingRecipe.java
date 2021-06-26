package ml.northwestwind.skyfarm.common.recipes;

import ml.northwestwind.skyfarm.SkyFarm;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class AbstractEvaporatingRecipe implements IRecipe<IInventory> {
    public static final ResourceLocation RECIPE_TYPE_ID = new ResourceLocation(SkyFarm.MOD_ID, "evaporating");
    protected final IRecipeType<?> type;
    protected final ResourceLocation id;
    protected final Ingredient input;
    protected final ItemStack output;
    protected final int tick;
    protected final double chance;

    public AbstractEvaporatingRecipe(IRecipeType<?> type, ResourceLocation id, Ingredient input, ItemStack output, int tick, double chance) {
        this.type = type;
        this.id = id;
        this.output = output;
        this.input = input;
        this.tick = tick;
        this.chance = chance;
    }

    @Override
    public boolean matches(IInventory inv, World world) {
        return this.input.test(inv.getItem(0));
    }

    @Override
    public ItemStack assemble(IInventory inv) {
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
    public IRecipeType<?> getType() {
        return type;
    }

    @Override
    public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
        return true;
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.input);
        return nonnulllist;
    }

    public int getTick() {
        return tick;
    }

    public double getChance() {
        return chance;
    }

    public Ingredient getInput() {
        return input;
    };
}

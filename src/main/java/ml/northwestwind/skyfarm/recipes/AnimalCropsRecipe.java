package ml.northwestwind.skyfarm.recipes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class AnimalCropsRecipe extends SpecialRecipe {
    public AnimalCropsRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInventory inv) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int a, int b) {
        return true;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return null;
    }
}

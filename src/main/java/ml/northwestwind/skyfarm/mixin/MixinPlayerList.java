package ml.northwestwind.skyfarm.mixin;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.stream.Collectors;

@Mixin(PlayerList.class)
public class MixinPlayerList {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/crafting/RecipeManager;getRecipes()Ljava/util/Collection;"), method = "placeNewPlayer")
    public Collection<IRecipe<?>> getRecipes(RecipeManager recipeManager) {
        return recipeManager.recipes.values().stream().flatMap((map) -> map.values().stream()).collect(Collectors.toSet());
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/crafting/RecipeManager;getRecipes()Ljava/util/Collection;"), method = "reloadResources")
    public Collection<IRecipe<?>> getRecipesAgain(RecipeManager recipeManager) {
        return recipeManager.recipes.values().stream().flatMap((map) -> map.values().stream()).collect(Collectors.toSet());
    }
}

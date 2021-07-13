package ml.northwestwind.skyfarm.mixin;

import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import ml.northwestwind.skyfarm.itemstages.ItemStages;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(RecipeManager.class)
public class MixinRecipeManager {
    @Inject(at = @At("RETURN"), method = "byType", cancellable = true)
    public <C extends IInventory, T extends IRecipe<C>> void byType(IRecipeType<T> type, CallbackInfoReturnable<Map<ResourceLocation, IRecipe<C>>> cir) {
        cir.setReturnValue(typeRecipeFilter(cir.getReturnValue()));
    }

    @Inject(at = @At("RETURN"), method = "getRecipes", cancellable = true)
    public void getRecipes(CallbackInfoReturnable<Collection<IRecipe<?>>> cir) {
        cir.setReturnValue(allRecipeFilter(cir.getReturnValue()));
    }

    @Unique
    private <C extends IInventory> Map<ResourceLocation, IRecipe<C>> typeRecipeFilter(Map<ResourceLocation, IRecipe<C>> map) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return map;
        ServerWorld overworld = server.overworld();
        if (overworld == null || overworld.isClientSide) return map;
        SkyblockData data = SkyblockData.get(overworld);
        return map.entrySet().stream().filter(entry -> {
            ItemStack stack = entry.getValue().getResultItem();
            final String stage = ItemStages.getStage(stack);
            return stage == null || data.hasStage(stage);
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Unique
    private Collection<IRecipe<?>> allRecipeFilter(Collection<IRecipe<?>> collection) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return collection;
        ServerWorld overworld = server.overworld();
        if (overworld == null || overworld.isClientSide) return collection;
        SkyblockData data = SkyblockData.get(overworld);
        return collection.stream().filter(recipe -> {
            ItemStack stack = recipe.getResultItem();
            final String stage = ItemStages.getStage(stack);
            return stage == null || data.hasStage(stage);
        }).collect(Collectors.toSet());
    }
}

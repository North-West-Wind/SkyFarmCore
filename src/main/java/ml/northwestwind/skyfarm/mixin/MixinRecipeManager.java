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
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        ServerWorld overworld = server.overworld();
        if (overworld == null || overworld.isClientSide) return;
        SkyblockData data = SkyblockData.get(overworld);
        cir.setReturnValue(cir.getReturnValue().entrySet().stream().filter(entry -> {
            ItemStack stack = entry.getValue().getResultItem();
            final String stage = ItemStages.getStage(stack);
            return stage == null || data.hasStage(stage);
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @Inject(at = @At("RETURN"), method = "getRecipes", cancellable = true)
    public void getRecipes(CallbackInfoReturnable<Collection<IRecipe<?>>> cir) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        ServerWorld overworld = server.overworld();
        if (overworld == null || overworld.isClientSide) return;
        SkyblockData data = SkyblockData.get(overworld);
        cir.setReturnValue(cir.getReturnValue().stream().filter(recipe -> {
            ItemStack stack = recipe.getResultItem();
            final String stage = ItemStages.getStage(stack);
            return stage == null || data.hasStage(stage);
        }).collect(Collectors.toSet()));
    }
}

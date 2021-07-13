package ml.northwestwind.skyfarm.itemstages.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.itemstages.ItemStages;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.GameStageSaveHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JeiPlugin
public class PluginItemStages implements IModPlugin {
    public static IIngredientManager ingredientManager;
    public static IRecipeRegistration recipeRegistry;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(SkyFarm.MOD_ID, "itemstages");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ingredientManager = registration.getIngredientManager();
        recipeRegistry = registration;
    }

    @OnlyIn(Dist.CLIENT)
    public static void syncHiddenItems(PlayerEntity player) {
        if (ingredientManager == null) {
            ItemStages.LOG.info("Cannot sync yet because JEI didn't give IngredientManager yet");
            return;
        }
        if (player != null && player.level.isClientSide) {
            Minecraft minecraft = Minecraft.getInstance();
            if (!minecraft.isSameThread()) {
                minecraft.submitAsync(() -> syncHiddenItems(player));
                return;
            }
            ItemStages.LOG.info("Syncing {} items with JEI!.", ItemStages.ITEM_STAGES.size());
            final long time = System.currentTimeMillis();

            final Collection<ItemStack> itemBlacklist = new ArrayList<>();
            final Collection<ItemStack> itemWhitelist = new ArrayList<>();
            for (final String key : ItemStages.SORTED_STAGES.keySet()) {
                final List<ItemStack> entries = ItemStages.SORTED_STAGES.get(key);
                if (GameStageHelper.hasStage(player, GameStageSaveHandler.getClientData(), key)) {
                    for (ItemStack stack : entries) {
                        NonNullList<ItemStack> list = NonNullList.create();
                        stack.getItem().fillItemCategory(ItemGroup.TAB_SEARCH, list);
                        itemWhitelist.addAll(list);
                    }
                } else {
                    for (ItemStack stack : entries) {
                        NonNullList<ItemStack> list = NonNullList.create();
                        stack.getItem().fillItemCategory(ItemGroup.TAB_SEARCH, list);
                        itemBlacklist.addAll(list);
                    }
                }
            }
            if (!itemBlacklist.isEmpty()) ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM, itemBlacklist);
            if (!itemWhitelist.isEmpty()) ingredientManager.addIngredientsAtRuntime(VanillaTypes.ITEM, itemWhitelist);
            ItemStages.LOG.info("Finished JEI Sync, took " + (System.currentTimeMillis() - time) + "ms. " + itemBlacklist.size() + " hidden, " + itemWhitelist.size() + " shown.");
        }
    }
}

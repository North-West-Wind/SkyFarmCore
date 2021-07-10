package ml.northwestwind.skyfarm.itemstages.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import ml.northwestwind.skyfarm.itemstages.ItemStages;
import net.minecraft.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.ItemStages")
public class ItemStagesCrT {
    @ZenCodeType.Method
    public static void addItemStage(String stage, IIngredient input) {

        CraftTweakerAPI.apply(new ActionAddItemRestriction(stage, input));
    }

    @ZenCodeType.Method
    public static void removeItemStage(IIngredient input) {

        CraftTweakerAPI.apply(new ActionRemoveRestriction(input));
    }

    @ZenCodeType.Method
    public static void stageModItems(String stage, String modid) {
        ForgeRegistries.ITEMS.getValues().forEach(item -> {
            if (item == null || item == Items.AIR || !item.getRegistryName().getNamespace().equals(modid)) return;
            CraftTweakerAPI.apply(new ActionAddItemRestriction(stage, item));
            ItemStages.LOG.info("Added {} to stage {}", item.getRegistryName(), stage);
        });
    }
}

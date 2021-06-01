package ml.northwestwind.skyfarm.item;

import ml.northwestwind.skyfarm.SkyFarm;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ShifterItem extends ArmorItem {
    private final String registryName;

    public ShifterItem(IArmorMaterial material, EquipmentSlotType slot, Properties properties, String registryName) {
        super(material, slot, properties);
        this.registryName = registryName;
        setRegistryName(SkyFarm.MOD_ID, registryName);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltips, ITooltipFlag flag) {
        tooltips.add(new TranslationTextComponent("tooltip.skyfarm.shifter."+registryName, new TranslationTextComponent("key.crouch").withStyle(TextFormatting.AQUA)).withStyle(TextFormatting.GRAY));
        tooltips.add(new TranslationTextComponent("tooltip.skyfarm.shifter.usage."+registryName, new TranslationTextComponent("key.crouch").withStyle(TextFormatting.AQUA)).withStyle(TextFormatting.GRAY));
    }
}

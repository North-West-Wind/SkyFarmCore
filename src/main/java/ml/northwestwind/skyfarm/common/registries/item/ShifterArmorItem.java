package ml.northwestwind.skyfarm.common.registries.item;

import ml.northwestwind.skyfarm.SkyFarm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.List;

public class ShifterArmorItem extends ArmorItem {
    private final String registryName, modid;

    public ShifterArmorItem(IArmorMaterial material, EquipmentSlotType slot, Properties properties, String registryName, String modid) {
        super(material, slot, properties);
        this.registryName = registryName;
        this.modid = modid;
        setRegistryName(SkyFarm.MOD_ID, registryName);
    }

    public ShifterArmorItem(IArmorMaterial material, EquipmentSlotType slot, Properties properties, String registryName) {
        this(material, slot, properties, registryName, "minecraft");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltips, ITooltipFlag flag) {
        if (!ModList.get().isLoaded(modid)) tooltips.add(new TranslationTextComponent("tooltip.skyfarm.shifter.missing", new StringTextComponent(modid).withStyle(TextFormatting.GOLD)).withStyle(TextFormatting.RED));
        else {
            IFormattableTextComponent text = (IFormattableTextComponent) Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage();
            tooltips.add(new TranslationTextComponent("tooltip.skyfarm.shifter."+registryName, text.withStyle(TextFormatting.AQUA)).withStyle(TextFormatting.GRAY));
            tooltips.add(new TranslationTextComponent("tooltip.skyfarm.shifter.usage."+registryName, text.withStyle(TextFormatting.AQUA)).withStyle(TextFormatting.GRAY));
        }
    }
}

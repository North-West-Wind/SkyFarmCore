package ml.northwestwind.skyfarm.item;

import ml.northwestwind.skyfarm.SkyFarm;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class TooltipItem extends Item {
    private final String registryName;
    public TooltipItem(Properties properties, String registryName) {
        super(properties);
        setRegistryName(SkyFarm.MOD_ID, registryName);
        this.registryName = registryName;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltips, ITooltipFlag flag) {
        tooltips.add(new TranslationTextComponent("tooltip.skyfarm."+registryName).setStyle(Style.EMPTY.applyFormat(TextFormatting.GRAY)));
    }
}

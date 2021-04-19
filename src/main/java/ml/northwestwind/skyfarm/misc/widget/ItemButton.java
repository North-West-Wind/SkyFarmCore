package ml.northwestwind.skyfarm.misc.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

public class ItemButton extends Button {
    protected final Item item;

    public ItemButton(int x, int y, int width, int height, IPressable onPress, Item item) {
        this(x, y, width, height, onPress, NO_TOOLTIP, item);
    }

    public ItemButton(int x, int y, int width, int height, IPressable onPress, ITooltip tooltip, Item item) {
        super(x, y, width, height, new StringTextComponent(""), onPress, tooltip);
        this.item = item;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getItemRenderer().renderGuiItem(new ItemStack(item), this.x + this.width / 2 - 8, this.y + this.height / 2 - 8);
    }
}

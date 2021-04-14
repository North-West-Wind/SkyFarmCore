package ml.northwestwind.skyfarm.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import ml.northwestwind.skyfarm.container.ParaboxContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;

public class ParaboxScreen extends ContainerScreen<ParaboxContainer> {
    private static final ResourceLocation DEMO_BG = new ResourceLocation("minecraft", "textures/gui/demo_background.png");
    private Button activate, loop;

    public ParaboxScreen(ParaboxContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    protected void init() {
        this.imageWidth = 248;
        this.imageHeight = 166;
        super.init();

        activate = new Button(width / 2 - 55, this.topPos + this.imageHeight - 30, 50, 20, new TranslationTextComponent("button.parabox.activate"), button -> {
            if (minecraft != null && minecraft.player != null) {
                minecraft.player.displayClientMessage(new TranslationTextComponent("mods.skyfarm.missing", "GameStages"), true);
                onClose();
                return;
            }

        }, (button, matrixStack, mouseX, mouseY) -> renderWrappedToolTip(matrixStack, Lists.newArrayList(new TranslationTextComponent("tooltip.parabox.activate")), mouseX, mouseY, Minecraft.getInstance().font));
        loop = new Button(width / 2 + 5, this.topPos + this.imageHeight - 30, 50, 20, new TranslationTextComponent("button.parabox.loop.on"), button -> {

        }, (button, matrixStack, mouseX, mouseY) -> renderWrappedToolTip(matrixStack, Lists.newArrayList(new TranslationTextComponent("tooltip.parabox.loop")), mouseX, mouseY, Minecraft.getInstance().font));
        loop.active = false;
        if (!ModList.get().isLoaded("gamestages")) {
            if (minecraft != null && minecraft.player != null) minecraft.player.displayClientMessage(new TranslationTextComponent("mods.skyfarm.missing", "GameStages"), true);
            activate.active = false;
        }

        addButton(activate);
        addButton(loop);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        if (this.minecraft == null) return;
        this.minecraft.textureManager.bind(DEMO_BG);
        blit(matrixStack, width / 2 - this.imageWidth / 2, height / 2 - this.imageHeight / 2, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        fill(matrixStack, this.leftPos + 10, this.topPos + 14, this.leftPos + this.imageWidth - 10, this.topPos + this.imageHeight - 40, 0);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title, this.titleLabelX, this.titleLabelY, 0x404040);
    }
}

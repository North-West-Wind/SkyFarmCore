package ml.northwestwind.skyfarm.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.container.ParaboxContainer;
import ml.northwestwind.skyfarm.misc.Utils;
import ml.northwestwind.skyfarm.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.packet.message.CVoteActivateParaboxPacket;
import ml.northwestwind.skyfarm.packet.message.CCloseParaboxPacket;
import ml.northwestwind.skyfarm.packet.message.CVoteDeactivateParaboxPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;

public class ParaboxScreen extends ContainerScreen<ParaboxContainer> {
    private static final ResourceLocation DEMO_BG = new ResourceLocation("minecraft", "textures/gui/demo_background.png");
    private static final ResourceLocation BLACK_DOT = new ResourceLocation(SkyFarm.MOD_ID, "textures/gui/black_dot.png");
    private Button activate, loop;
    private boolean inLoop, oldLoop, backup;

    public ParaboxScreen(ParaboxContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.inLoop = container.isLooping();
        this.backup = !container.isBackingUp();
    }

    @Override
    protected void init() {
        this.imageWidth = 248;
        this.imageHeight = 166;
        super.init();
        if (!isGameStageLoaded()) return;

        activate = new Button(width / 2 - 85, this.topPos + this.imageHeight - 30, 80, 20, new TranslationTextComponent("button.parabox."+ (inLoop ? "de" : "") +"activate"), button -> {
            if (!isGameStageLoaded()) return;
            if (!inLoop) SkyFarmPacketHandler.INSTANCE.sendToServer(new CVoteActivateParaboxPacket(true, this.menu.tile.getBlockPos()));
            else SkyFarmPacketHandler.INSTANCE.sendToServer(new CVoteDeactivateParaboxPacket(true, true));
        }, (button, matrixStack, mouseX, mouseY) -> renderWrappedToolTip(matrixStack, Lists.newArrayList(new TranslationTextComponent("tooltip.parabox."+ (inLoop ? "de" : "") +"activate")), mouseX, mouseY, Minecraft.getInstance().font));
        loop = new Button(width / 2 + 5, this.topPos + this.imageHeight - 30, 80, 20, new TranslationTextComponent("button.parabox.loop."+ (inLoop ? "on" : "off")), button -> {
            if (!isGameStageLoaded()) return;
            if (inLoop) SkyFarmPacketHandler.INSTANCE.sendToServer(new CVoteDeactivateParaboxPacket(false, true));
        }, (button, matrixStack, mouseX, mouseY) -> renderWrappedToolTip(matrixStack, Lists.newArrayList(new TranslationTextComponent("tooltip.parabox.loop."+ (inLoop ? "on" : "off"))), mouseX, mouseY, Minecraft.getInstance().font));
        if (!inLoop) loop.active = false;
        oldLoop = inLoop;

        addButton(activate);
        addButton(loop);
    }

    @Override
    public void tick() {
        super.tick();
        if (oldLoop != inLoop) {
            activate.setMessage(new TranslationTextComponent("button.parabox." + (inLoop ? "de" : "") + "activate"));
            loop.setMessage(new TranslationTextComponent("button.parabox.loop." + (inLoop ? "on" : "off")));
            oldLoop = inLoop;
        }
        activate.active = backup;
        loop.active = this.menu.tile.paraboxLevel() > 0 && inLoop;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        int x = width / 2 - (this.imageWidth - 20) / 2 + 4;
        int y = height / 2 - (this.imageHeight - 40) / 2 + 4;
        if (!inLoop) {
            this.font.draw(matrixStack, new TranslationTextComponent("screen.parabox.off"), x, y, 0xFFFFFF);
            return;
        } else if (!backup) {
            this.font.draw(matrixStack, new TranslationTextComponent("screen.parabox.backup"), x, y, 0xFFFFFF);
            return;
        }
        this.font.draw(matrixStack, new TranslationTextComponent("screen.parabox.receiving", this.menu.tile.getEnergy()), x, y, 0xFFFFFF);
        y += 2 + this.font.lineHeight;
        this.font.draw(matrixStack, new TranslationTextComponent("screen.parabox.max", this.menu.tile.getEnergyStorage().getMaxEnergyStored()), x, y, 0xFFFFFF);
        y += 2 + this.font.lineHeight;
        this.font.draw(matrixStack, new TranslationTextComponent("screen.parabox.duration", Utils.formatDuration((long) this.menu.tile.getTicksLeft())), x, y, 0xFFFFFF);
        y += 2 + this.font.lineHeight;
        this.font.draw(matrixStack, new TranslationTextComponent("screen.parabox.efficiency", ((int) (this.menu.tile.getEfficiency() * 100)) + "%"), x, y, 0xFFFFFF);
        y += 2 + this.font.lineHeight;
        if (this.menu.tile.getWantingItem() != null) this.font.draw(matrixStack, new TranslationTextComponent("screen.parabox.item", this.menu.tile.getWantingItem().getDefaultInstance().getDisplayName()), x, y, 0xFFFFFF);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        renderBackground(matrixStack);
        if (this.minecraft == null) return;
        this.minecraft.textureManager.bind(DEMO_BG);
        blit(matrixStack, width / 2 - this.imageWidth / 2, height / 2 - this.imageHeight / 2, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        this.minecraft.textureManager.bind(BLACK_DOT);
        blit(matrixStack, width / 2 - (this.imageWidth - 20) / 2, height / 2 - (this.imageHeight - 40) / 2, 0, 0, this.imageWidth - 20, this.imageHeight - 60, 1, 1);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title, this.titleLabelX, this.titleLabelY, 0x404040);
    }

    private boolean isGameStageLoaded() {
        if (!ModList.get().isLoaded("gamestages")) {
            if (minecraft != null && minecraft.player != null)
                minecraft.player.displayClientMessage(new TranslationTextComponent("mods.skyfarm.missing", "GameStages"), true);
            onClose();
            return false;
        }
        return true;
    }

    @Override
    public void onClose() {
        super.onClose();
        SkyFarmPacketHandler.INSTANCE.sendToServer(new CCloseParaboxPacket());
    }

    public void setBackup(boolean backup) {
        this.backup = backup;
    }

    public void setInLoop(boolean inLoop) {
        this.inLoop = inLoop;
    }
}

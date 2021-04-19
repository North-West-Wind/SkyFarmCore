package ml.northwestwind.skyfarm.misc.widget;

import com.google.common.collect.Lists;
import ml.northwestwind.skyfarm.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.packet.message.CAddStagePacket;
import ml.northwestwind.skyfarm.screen.GameStageScreen;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.GameStageSaveHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.apache.commons.lang3.tuple.Triple;

public class StageButton extends ItemButton {
    private final String stage;
    private final int point;

    public StageButton(int x, int y, int width, int height, int parentWidth, int parentHeight, Triple<String, Item, Integer> triple) {
        super(x, y, width, height, button -> {
            if (GameStageScreen.points < triple.getRight()) return;
            SkyFarmPacketHandler.INSTANCE.sendToServer(new CAddStagePacket(triple.getLeft()));
            button.active = false;
        }, (button, matrixStack, mouseX, mouseY) -> {
            Minecraft minecraft = Minecraft.getInstance();
            GuiUtils.drawHoveringText(matrixStack, Lists.newArrayList(
                    new TranslationTextComponent("stages.skyfarm." + triple.getLeft() + ".title").setStyle(Style.EMPTY.applyFormat(TextFormatting.GOLD)),
                    new StringTextComponent("\n\n"),
                    new TranslationTextComponent("stages.skyfarm." + triple.getLeft() + ".description").setStyle(Style.EMPTY.applyFormat(TextFormatting.GRAY)),
                    new StringTextComponent("\n\n"),
                    minecraft.player != null && GameStageHelper.hasStage(minecraft.player, GameStageSaveHandler.getClientData(), triple.getLeft()) ?
                            new TranslationTextComponent("stages.skyfarm.known").setStyle(Style.EMPTY.applyFormat(TextFormatting.AQUA)) :
                            new TranslationTextComponent("stages.skyfarm.points", triple.getRight()).setStyle(Style.EMPTY.applyFormat(GameStageScreen.points < triple.getRight() ? TextFormatting.RED : TextFormatting.GREEN))
            ), mouseX, mouseY, parentWidth, parentHeight, -1, minecraft.font);
        }, triple.getMiddle());
        this.stage = triple.getLeft();
        this.point = triple.getRight();
    }

    public void tick() {
        this.active = GameStageScreen.points >= point && !GameStageHelper.hasStage(Minecraft.getInstance().player, GameStageSaveHandler.getClientData(), stage);
    }
}

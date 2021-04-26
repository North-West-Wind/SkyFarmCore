package ml.northwestwind.skyfarm.misc.widget;

import com.google.common.collect.Lists;
import ml.northwestwind.skyfarm.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.packet.message.CAddStagePacket;
import ml.northwestwind.skyfarm.screen.GameStageScreen;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.GameStageSaveHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public class StageButton extends ItemButton {
    private final String stage;
    private final int point;
    private final List<String> required;

    public StageButton(int x, int y, int width, int height, int parentWidth, int parentHeight, String stage, Triple<Item, Integer, List<String>> triple) {
        super(x, y, width, height, button -> {
            if (GameStageScreen.points < triple.getMiddle()) return;
            SkyFarmPacketHandler.INSTANCE.sendToServer(new CAddStagePacket(stage));
            button.active = false;
        }, (button, matrixStack, mouseX, mouseY) -> {
            Minecraft minecraft = Minecraft.getInstance();
            List<ITextComponent> tooltip = Lists.newArrayList(
                    new TranslationTextComponent("stages.skyfarm." + stage + ".title").setStyle(Style.EMPTY.applyFormat(TextFormatting.GOLD)),
                    new StringTextComponent(""),
                    new TranslationTextComponent("stages.skyfarm." + stage + ".description").setStyle(Style.EMPTY.applyFormat(TextFormatting.GRAY)),
                    new StringTextComponent(""));
            addTooltip(tooltip, stage, triple.getRight(), triple.getMiddle());
            GuiUtils.drawHoveringText(matrixStack, tooltip, mouseX, mouseY, parentWidth, parentHeight, -1, minecraft.font);
        }, triple.getLeft());
        this.stage = stage;
        this.point = triple.getMiddle();
        this.required = triple.getRight();
        this.active = false;
    }
    
    private static void addTooltip(List<ITextComponent> tooltip, String stage, List<String> required, int point) {
        Minecraft minecraft = Minecraft.getInstance();
        if (GameStageHelper.hasStage(minecraft.player, GameStageSaveHandler.getClientData(), stage)) tooltip.add(new TranslationTextComponent("stages.skyfarm.known").setStyle(Style.EMPTY.applyFormat(TextFormatting.AQUA)));
        else {
            for (String s : required) {
                boolean hasStage = GameStageHelper.hasStage(minecraft.player, GameStageSaveHandler.getClientData(), s);
                tooltip.add(new TranslationTextComponent("stages.skyfarm.require", new TranslationTextComponent("stages.skyfarm."+s+".title").getString()).setStyle(Style.EMPTY.applyFormat(hasStage ? TextFormatting.GREEN : TextFormatting.RED)));
            }
            boolean hasPoints = GameStageScreen.points >= point;
            tooltip.add(new TranslationTextComponent("stages.skyfarm.points", point).setStyle(Style.EMPTY.applyFormat(hasPoints ? TextFormatting.GREEN : TextFormatting.RED)));
        }
    }
    
    private boolean isClickable() {
        boolean noStage = !GameStageHelper.hasStage(Minecraft.getInstance().player, GameStageSaveHandler.getClientData(), stage);
        boolean hasPoints = GameStageScreen.points >= point;
        boolean hasRequired = true;
        for (String s : required) if (!GameStageHelper.hasStage(Minecraft.getInstance().player, GameStageSaveHandler.getClientData(), s)) {
            hasRequired = false;
            break;
        }
        return noStage && hasPoints && hasRequired;
    }

    public void tick() {
        this.active = isClickable();
    }
}

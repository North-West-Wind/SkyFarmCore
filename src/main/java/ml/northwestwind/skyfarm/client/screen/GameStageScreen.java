package ml.northwestwind.skyfarm.client.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.client.widget.StageButton;
import ml.northwestwind.skyfarm.common.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.common.packet.message.CAddStagePacket;
import ml.northwestwind.skyfarm.common.packet.message.CSyncPointsPacket;
import ml.northwestwind.skyfarm.misc.Utils;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class GameStageScreen extends Screen {
    public static final ResourceLocation WHITE_DOT = new ResourceLocation(SkyFarm.MOD_ID, "textures/gui/white_dot.png");
    public long points = 0;

    public GameStageScreen() {
        super(new TranslationTextComponent("screen.gamestage"));
    }

    public static Triple<Item, Integer, List<String>> getTriple(String stage) {
        return CAddStagePacket.STAGES.get(stage);
    }

    private void addStageButton(double x, double y, String stage, int w, int h) {
        if (!GameStageHelper.isStageKnown(stage)) return;
        double widthBy16 = ((double) this.width) / 16;
        double heightBy9 = ((double) this.height) / 9;
        addButton(new StageButton((int) (widthBy16 * x + widthBy16 / 2 - w / 2), (int) (heightBy9 * (y+1) + heightBy9 / 2 - h / 2), w, h, width, height, stage, getTriple(stage)));
    }

    private void addStageButton(double x, double y, String stage) {
        addStageButton(x, y, stage, 20, 20);
    }

    @Override
    protected void init() {
        if (this.points != 0) this.points = 0;
        SkyFarm.LOGGER.info("Sending synchronizing points");
        SkyFarmPacketHandler.INSTANCE.sendToServer(new CSyncPointsPacket());
        addStageButton(0, 0, "prudentium");
        addStageButton(1, 0, "tertium");
        addStageButton(2, 0, "imperium");
        addStageButton(3, 0, "supremium");
        addStageButton(4, 0, "insanium");

        addStageButton(0, 1, "sky_slime_grass_seeds");
        addStageButton(1, 1, "ender_slime_grass_seeds");
        addStageButton(2, 1, "blood_slime_grass_seeds");

        addStageButton(3, 1, "vehicle");
        addStageButton(4, 1, "simpleplanes");

        addStageButton(7.5, 3.5, "botanypots");

        addStageButton(11, 0, "mob_grinding_utils");
        addStageButton(12, 0, "darkutils");
        addStageButton(11, 1, "illuminati_pet");
        addStageButton(12, 1, "void_miner");

        addStageButton(14, 0, "mekasuit");
        addStageButton(15, 0, "mekatool");
        addStageButton(14, 1, "ironjetpacks");
        addStageButton(15, 1, "cgm");

        addStageButton(15, 7, "projecte");
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        drawCenteredString(matrixStack, font, title, width / 2, (height / 9) / 2 - font.lineHeight / 2, 0xFFFFFF);
        drawCenteredString(matrixStack, font, new TranslationTextComponent("stages.skyfarm.points", points), width / 2, (height / 9) / 2 - font.lineHeight / 2 + font.lineHeight + 2, 0xFFFFFF);
        if (this.minecraft != null) {
            double widthBy16 = ((double) this.width) / 16;
            double heightBy9 = ((double) this.height) / 9;
            this.minecraft.getTextureManager().bind(WHITE_DOT);
            blit(matrixStack, (int) (widthBy16 / 2), (int) (heightBy9 + heightBy9 / 2 - 1), 0, 0, (int) (widthBy16 * 4), 2, 1, 1);
            blit(matrixStack, (int) (widthBy16 / 2), (int) (heightBy9 * 2 + heightBy9 / 2 - 1), 0, 0, (int) (widthBy16 * 2), 2, 1, 1);
            blit(matrixStack, (int) (widthBy16 * 3 + widthBy16 / 2), (int) (heightBy9 * 2 + heightBy9 / 2 - 1), 0, 0, (int) (widthBy16), 2, 1, 1);
        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        for (Widget button : this.buttons) if (button instanceof StageButton) ((StageButton) button).tick();
    }
}

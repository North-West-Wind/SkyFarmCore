package ml.northwestwind.skyfarm.client.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.client.widget.StageButton;
import ml.northwestwind.skyfarm.common.packet.SkyFarmPacketHandler;
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
    public static final Map<String, Triple<Item, Integer, List<String>>> STAGES = Maps.newHashMap();
    public static final List<String> EMPTY_STRING_LIST = Lists.newArrayList();
    public long points = 0;

    public GameStageScreen() {
        super(new TranslationTextComponent("screen.gamestage"));
        STAGES.clear();
        STAGES.put("mob_grinding_utils", new ImmutableTriple<>(Utils.getItemByModAndName("mob_grinding_utils", "fan"), 1, EMPTY_STRING_LIST));
        STAGES.put("botanypots", new ImmutableTriple<>(Utils.getItemByModAndName("botanypots", "botany_pot"), 1, EMPTY_STRING_LIST));
        STAGES.put("darkutils", new ImmutableTriple<>(Utils.getItemByModAndName("darkutils", "vector_plate"), 1, EMPTY_STRING_LIST));
        STAGES.put("prudentium", new ImmutableTriple<>(Utils.getItemByModAndName("mysticalagriculture", "prudentium_essence"), 1, EMPTY_STRING_LIST));
        STAGES.put("tertium", new ImmutableTriple<>(Utils.getItemByModAndName("mysticalagriculture", "tertium_essence"), 1, Lists.newArrayList("prudentium")));
        STAGES.put("imperium", new ImmutableTriple<>(Utils.getItemByModAndName("mysticalagriculture", "imperium_essence"), 1, Lists.newArrayList("tertium")));
        STAGES.put("supremium", new ImmutableTriple<>(Utils.getItemByModAndName("mysticalagriculture", "supremium_essence"), 1, Lists.newArrayList("imperium")));
        STAGES.put("insanium", new ImmutableTriple<>(Utils.getItemByModAndName("mysticalagradditions", "insanium_essence"), 1, Lists.newArrayList("supremium")));
        STAGES.put("ironjetpacks", new ImmutableTriple<>(Utils.getItemByModAndName("ironjetpacks", "emerald_jetpack"), 2, EMPTY_STRING_LIST));
        STAGES.put("cgm", new ImmutableTriple<>(Utils.getItemByModAndName("cgm", "mini_gun"), 1, EMPTY_STRING_LIST));
        STAGES.put("vehicle", new ImmutableTriple<>(Utils.getItemByModAndName("vehicle", "standard_wheel"), 1, EMPTY_STRING_LIST));
        STAGES.put("sky_slime_grass_seeds", new ImmutableTriple<>(Utils.getItemByModAndName("tconstruct", "sky_slime_grass_seeds"), 1, EMPTY_STRING_LIST));
        STAGES.put("ender_slime_grass_seeds", new ImmutableTriple<>(Utils.getItemByModAndName("tconstruct", "ender_slime_grass_seeds"), 2, Lists.newArrayList("sky_slime_grass_seeds")));
        STAGES.put("blood_slime_grass_seeds", new ImmutableTriple<>(Utils.getItemByModAndName("tconstruct", "blood_slime_grass_seeds"), 3, Lists.newArrayList("ender_slime_grass_seeds")));
        STAGES.put("projecte", new ImmutableTriple<>(Utils.getItemByModAndName("projecte", "philosophers_stone"), 100, EMPTY_STRING_LIST));
        STAGES.put("simpleplanes", new ImmutableTriple<>(Utils.getItemByModAndName("simpleplanes", "plane"), 3, Lists.newArrayList("vehicle")));
        STAGES.put("mekasuit", new ImmutableTriple<>(Utils.getItemByModAndName("mekanism", "mekasuit_bodyarmor"), 5, EMPTY_STRING_LIST));
        STAGES.put("mekatool", new ImmutableTriple<>(Utils.getItemByModAndName("mekanism", "meka_tool"), 5, EMPTY_STRING_LIST));
        STAGES.put("illuminati_pet", new ImmutableTriple<>(Utils.getItemByModAndName("inventorypets", "pet_illuminati"), 4, EMPTY_STRING_LIST));
        STAGES.put("void_miner", new ImmutableTriple<>(Utils.getItemByModAndName("envirotech", "xerothium_void_miner_ccu"), 10, EMPTY_STRING_LIST));
    }

    public static Triple<Item, Integer, List<String>> getTriple(String stage) {
        return STAGES.get(stage);
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

package ml.northwestwind.skyfarm.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.misc.Utils;
import ml.northwestwind.skyfarm.misc.widget.ItemButton;
import ml.northwestwind.skyfarm.misc.widget.StageButton;
import ml.northwestwind.skyfarm.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.packet.message.CAddStagePacket;
import ml.northwestwind.skyfarm.packet.message.DSyncPointsPacket;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.GameStageSaveHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class GameStageScreen extends Screen {
    public static final ResourceLocation WHITE_DOT = new ResourceLocation(SkyFarm.MOD_ID, "textures/gui/white_dot.png");
    public static final Map<String, Triple<Item, Integer, List<String>>> STAGES = Maps.newHashMap();
    public static final List<String> EMPTY_STRING_LIST = Lists.newArrayList();
    public static long points;

    public GameStageScreen() {
        super(new TranslationTextComponent("screen.gamestage"));
        STAGES.clear();
        STAGES.put("mob_grinding_utils", new ImmutableTriple<>(Utils.getByModAndName("mob_grinding_utils", "fan"), 1, EMPTY_STRING_LIST));
        STAGES.put("botanypots", new ImmutableTriple<>(Utils.getByModAndName("botanypots", "botany_pot"), 1, EMPTY_STRING_LIST));
        STAGES.put("darkutils", new ImmutableTriple<>(Utils.getByModAndName("darkutils", "vector_plate"), 1, EMPTY_STRING_LIST));
        STAGES.put("prudentium", new ImmutableTriple<>(Utils.getByModAndName("mysticalagriculture", "prudentium_essence"), 2, EMPTY_STRING_LIST));
        STAGES.put("tertium", new ImmutableTriple<>(Utils.getByModAndName("mysticalagriculture", "tertium_essence"), 3, Lists.newArrayList("prudentium")));
        STAGES.put("imperium", new ImmutableTriple<>(Utils.getByModAndName("mysticalagriculture", "imperium_essence"), 4, Lists.newArrayList("tertium")));
        STAGES.put("supremium", new ImmutableTriple<>(Utils.getByModAndName("mysticalagriculture", "supremium_essence"), 5, Lists.newArrayList("imperium")));
        STAGES.put("insanium", new ImmutableTriple<>(Utils.getByModAndName("mysticalagradditions", "insanium_essence"), 6, Lists.newArrayList("supremium")));
        STAGES.put("ironjetpacks", new ImmutableTriple<>(Utils.getByModAndName("ironjetpacks", "emerald_jetpack"), 2, EMPTY_STRING_LIST));
        STAGES.put("cgm", new ImmutableTriple<>(Utils.getByModAndName("cgm", "mini_gun"), 1, EMPTY_STRING_LIST));
        STAGES.put("vehicle", new ImmutableTriple<>(Utils.getByModAndName("vehicle", "standard_wheel"), 1, EMPTY_STRING_LIST));
        STAGES.put("ender_slime_grass_seeds", new ImmutableTriple<>(Utils.getByModAndName("tconstruct", "ender_slime_grass_seeds"), 1, EMPTY_STRING_LIST));
        STAGES.put("blood_slime_grass_seeds", new ImmutableTriple<>(Utils.getByModAndName("tconstruct", "blood_slime_grass_seeds"), 2, Lists.newArrayList("ender_slime_grass_seeds")));
        STAGES.put("projecte", new ImmutableTriple<>(Utils.getByModAndName("projecte", "philosophers_stone"), 100, EMPTY_STRING_LIST));
        STAGES.put("simpleplanes", new ImmutableTriple<>(Utils.getByModAndName("simpleplanes", "plane"), 3, Lists.newArrayList("vehicle")));
        STAGES.put("mekasuit", new ImmutableTriple<>(Utils.getByModAndName("mekanism", "mekasuit_bodyarmor"), 5, EMPTY_STRING_LIST));
        STAGES.put("mekatool", new ImmutableTriple<>(Utils.getByModAndName("mekanism", "meka_tool"), 5, EMPTY_STRING_LIST));
        STAGES.put("illuminati_pet", new ImmutableTriple<>(Utils.getByModAndName("inventorypets", "pet_illuminati"), 4, EMPTY_STRING_LIST));
        STAGES.put("void_miner", new ImmutableTriple<>(Utils.getByModAndName("envirotech", "xerothium_void_miner_ccu"), 10, EMPTY_STRING_LIST));
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
        SkyFarmPacketHandler.INSTANCE.sendToServer(new DSyncPointsPacket());
        addStageButton(0, 0, "prudentium");
        addStageButton(1, 0, "tertium");
        addStageButton(2, 0, "imperium");
        addStageButton(3, 0, "supremium");
        addStageButton(4, 0, "insanium");

        addStageButton(0, 1, "ender_slime_grass_seeds");
        addStageButton(1, 1, "blood_slime_grass_seeds");

        addStageButton(2, 1, "vehicle");
        addStageButton(3, 1, "simpleplanes");

        addStageButton(7.5, 3.5, "botanypots", 40, 40);

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
            blit(matrixStack, (int) (widthBy16 / 2), (int) (heightBy9 * 2 + heightBy9 / 2 - 1), 0, 0, (int) (widthBy16), 2, 1, 1);
            blit(matrixStack, (int) (widthBy16 * 2 + widthBy16 / 2), (int) (heightBy9 * 2 + heightBy9 / 2 - 1), 0, 0, (int) (widthBy16), 2, 1, 1);
        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        for (Widget button : this.buttons) if (button instanceof StageButton) ((StageButton) button).tick();
    }
}

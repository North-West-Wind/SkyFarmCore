package ml.northwestwind.skyfarm.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
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
    public static final Map<String, Triple<Item, Integer, List<String>>> STAGES = Maps.newHashMap();
    public static final List<String> EMPTY_STRING_LIST = Lists.newArrayList();
    public static long points;

    public GameStageScreen() {
        super(new TranslationTextComponent("screen.gamestage"));
        STAGES.clear();
        STAGES.put("mob_grinding_utils", new ImmutableTriple<>(Utils.getByModAndName("mob_grinding_utils", "fan"), 1, EMPTY_STRING_LIST));
        STAGES.put("botanypots", new ImmutableTriple<>(Utils.getByModAndName("botanypots", "botany_pots"), 1, EMPTY_STRING_LIST));
        STAGES.put("darkutils", new ImmutableTriple<>(Utils.getByModAndName("darkutils", "vector_plate"), 1, EMPTY_STRING_LIST));
        STAGES.put("inferium_farmland", new ImmutableTriple<>(Utils.getByModAndName("mysticalagriculture", "inferium_farmland"), 1, EMPTY_STRING_LIST));
        STAGES.put("prudentium_farmland", new ImmutableTriple<>(Utils.getByModAndName("mysticalagriculture", "prudentium_farmland"), 2, Lists.newArrayList("inferium_farmland")));
        STAGES.put("tertium_farmland", new ImmutableTriple<>(Utils.getByModAndName("mysticalagriculture", "tertium_farmland"), 3, Lists.newArrayList("prudentium_farmland")));
        STAGES.put("imperium_farmland", new ImmutableTriple<>(Utils.getByModAndName("mysticalagriculture", "imperium_farmland"), 4, Lists.newArrayList("tertium_farmland")));
        STAGES.put("supremium_farmland", new ImmutableTriple<>(Utils.getByModAndName("mysticalagriculture", "supremium_farmland"), 5, Lists.newArrayList("imperium_farmland")));
        STAGES.put("insanium_farmland", new ImmutableTriple<>(Utils.getByModAndName("mysticalagradditions", "insanium_farmland"), 6, Lists.newArrayList("supremium_farmland")));
        STAGES.put("ironjetpacks", new ImmutableTriple<>(Utils.getByModAndName("ironjetpacks", "emerald_jetpack"), 2, EMPTY_STRING_LIST));
        STAGES.put("cgm", new ImmutableTriple<>(Utils.getByModAndName("cgm", "mini_gun"), 1, EMPTY_STRING_LIST));
        STAGES.put("vehicle", new ImmutableTriple<>(Utils.getByModAndName("vehicle", "standard_wheel"), 1, EMPTY_STRING_LIST));
        STAGES.put("projecte", new ImmutableTriple<>(Utils.getByModAndName("projecte", "philosophers_stone"), 100, EMPTY_STRING_LIST));
        STAGES.put("simpleplanes", new ImmutableTriple<>(Utils.getByModAndName("simpleplanes", "plane"), 3, Lists.newArrayList("vehicle")));
        STAGES.put("mekasuit", new ImmutableTriple<>(Utils.getByModAndName("mekanism", "mekasuit_bodyarmor"), 5, EMPTY_STRING_LIST));
        STAGES.put("mekatool", new ImmutableTriple<>(Utils.getByModAndName("mekanism", "meka_tool"), 5, EMPTY_STRING_LIST));
        STAGES.put("illuminati_pet", new ImmutableTriple<>(Utils.getByModAndName("inventorypets", "illuminati_pet"), 4, EMPTY_STRING_LIST));
        STAGES.put("void_miner", new ImmutableTriple<>(Utils.getByModAndName("envirotech", "xerothium_void_miner_ccu"), 10, EMPTY_STRING_LIST));
    }

    public static Triple<Item, Integer, List<String>> getTriple(String stage) {
        return STAGES.get(stage);
    }

    @Override
    protected void init() {
        Set<String> stages = GameStageHelper.getKnownStages();
        SkyFarmPacketHandler.INSTANCE.sendToServer(new DSyncPointsPacket());

        int x = 0, y = 0;
        double widthBy16 = ((double) this.width) / 16;
        double heightBy9 = ((double) this.height) / 9;
        for (String stage : stages) {
            Triple<Item, Integer, List<String>> triple = getTriple(stage);
            StageButton button = new StageButton((int) (widthBy16 * x + widthBy16 / 2 - 10), (int) (heightBy9 * (y + 1) + heightBy9 / 2 - 10), 20, 20, width, height, stage, triple);
            if (this.minecraft != null && this.minecraft.player != null && GameStageHelper.hasStage(this.minecraft.player, GameStageSaveHandler.getClientData(), stage))
                button.active = false;
            addButton(button);
            x++;
            if (x >= 16) {
                x = 0;
                y++;
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        drawCenteredString(matrixStack, font, title, width / 2, (height / 9) / 2 - font.lineHeight / 2, 0xFFFFFF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        for (Widget button : this.buttons) if (button instanceof StageButton) ((StageButton) button).tick();
    }
}

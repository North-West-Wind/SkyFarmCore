package ml.northwestwind.skyfarm.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
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
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class GameStageScreen extends Screen {
    public static final List<Triple<String, Item, Integer>> STAGES = Lists.newArrayList();
    public static long points;

    public GameStageScreen() {
        super(new TranslationTextComponent("screen.gamestage"));
        /*STAGE_ITEMS.put("placeholder", Items.GRASS_BLOCK);
        STAGE_ITEMS.put("test1", Items.STONE);
        STAGE_ITEMS.put("test2", Items.STICK);
        STAGE_ITEMS.put("test3", Items.PISTON);
        STAGE_ITEMS.put("test4", Items.EGG);
        STAGE_ITEMS.put("test5", Items.SEA_LANTERN);
        STAGE_ITEMS.put("test6", Items.OAK_BOAT);
        STAGE_ITEMS.put("test7", Items.SPRUCE_DOOR);
        STAGE_ITEMS.put("test8", Items.ACACIA_BUTTON);
        STAGE_ITEMS.put("test9", Items.DARK_OAK_FENCE);
        STAGE_ITEMS.put("test10", Items.JUNGLE_LEAVES);
        STAGE_ITEMS.put("test11", Items.ANDESITE);
        STAGE_ITEMS.put("test12", Items.GRANITE_SLAB);
        STAGE_ITEMS.put("test13", Items.DIORITE_STAIRS);
        STAGE_ITEMS.put("test14", Items.GRASS);
        STAGE_ITEMS.put("test15", Items.PACKED_ICE);
        STAGE_ITEMS.put("test16", Items.IRON_AXE);
        STAGE_ITEMS.put("test17", Items.DIAMOND);
        STAGE_ITEMS.put("test18", Items.GOLD_BLOCK);*/
    }

    public static Triple<String, Item, Integer> getTriple(String stage) {
        return STAGES.stream().filter(triple -> triple.getLeft().equals(stage)).findAny().orElse(new ImmutableTriple<>(stage, Items.AIR, 0));
    }

    @Override
    protected void init() {
        Set<String> stages = GameStageHelper.getKnownStages();
        SkyFarmPacketHandler.INSTANCE.sendToServer(new DSyncPointsPacket());

        int x = 0, y = 0;
        double widthBy16 = ((double) this.width) / 16;
        double heightBy9 = ((double) this.height) / 9;
        for (String stage : stages) {
            Triple<String, Item, Integer> triple = getTriple(stage);
            StageButton button = new StageButton((int) (widthBy16 * x + widthBy16 / 2 - 10), (int) (heightBy9 * (y + 1) + heightBy9 / 2 - 10), 20, 20, width, height, triple);
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

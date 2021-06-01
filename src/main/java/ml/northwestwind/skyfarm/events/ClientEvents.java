package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.misc.KeyBindings;
import ml.northwestwind.skyfarm.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.packet.message.CPlayerGrowPacket;
import ml.northwestwind.skyfarm.screen.GameStageScreen;
import ml.northwestwind.skyfarm.screen.VoteScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void keyInput(final InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.keyShift.consumeClick() && event.getAction() == GLFW.GLFW_PRESS) {
            SkyFarmPacketHandler.INSTANCE.sendToServer(new CPlayerGrowPacket());
        } else if (KeyBindings.stageMenu.consumeClick() && event.getAction() == GLFW.GLFW_PRESS) {
            if (mc.screen instanceof GameStageScreen) mc.setScreen(null);
            else {
                if (!ModList.get().isLoaded("gamestages")) {
                    if (mc.player == null) return;
                    mc.player.displayClientMessage(new TranslationTextComponent("mods.skyfarm.missing", "GameStage"), true);
                    return;
                }
                mc.setScreen(new GameStageScreen());
            }
        } else if (KeyBindings.voteMenu.consumeClick() && event.getAction() == GLFW.GLFW_PRESS) {
            if (mc.screen instanceof VoteScreen) mc.setScreen(null);
            else mc.setScreen(new VoteScreen());
        }
    }

    @SubscribeEvent
    public static void entityJoinWorld(final EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof ClientPlayerEntity)) return;
        ClientPlayerEntity player = (ClientPlayerEntity) event.getEntity();
        if (Minecraft.getInstance().player == null || !player.getUUID().equals(Minecraft.getInstance().player.getUUID())) return;
        Minecraft.getInstance().gui.handleChat(
                ChatType.SYSTEM,
                new TranslationTextComponent(
                        "tip.skyfarm.stageMenu",
                        ((IFormattableTextComponent) KeyBindings.stageMenu.getTranslatedKeyMessage()).withStyle(TextFormatting.AQUA)),
                Util.NIL_UUID);
    }
}

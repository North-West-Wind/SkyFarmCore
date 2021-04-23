package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.misc.KeyBindings;
import ml.northwestwind.skyfarm.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.packet.message.CPlayerGrowPacket;
import ml.northwestwind.skyfarm.screen.GameStageScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID)
public class KeyEvents {
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
        }
    }
}

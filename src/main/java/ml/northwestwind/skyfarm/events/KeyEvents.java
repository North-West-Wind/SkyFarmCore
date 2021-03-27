package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.packet.message.CPlayerGrowPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID)
public class KeyEvents {
    @SubscribeEvent
    public static void keyInput(final InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.keyShift.consumeClick()) {
            SkyFarmPacketHandler.INSTANCE.sendToServer(new CPlayerGrowPacket());
        }
    }
}

package ml.northwestwind.skyfarm.packet.message;

import ml.northwestwind.skyfarm.packet.IPacket;
import ml.northwestwind.skyfarm.screen.ParaboxScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.NetworkEvent;

public class SActivateParaboxPacket implements IPacket {
    @Override
    public void handle(NetworkEvent.Context ctx) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.screen instanceof ParaboxScreen)) return;
        ParaboxScreen screen = (ParaboxScreen) minecraft.screen;
        screen.setInLoop(true);
        screen.setBackedUp(false);
    }
}

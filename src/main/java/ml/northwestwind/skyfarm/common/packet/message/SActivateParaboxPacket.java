package ml.northwestwind.skyfarm.common.packet.message;

import ml.northwestwind.skyfarm.common.packet.IPacket;
import ml.northwestwind.skyfarm.client.screen.ParaboxScreen;
import net.minecraftforge.fml.network.NetworkEvent;

public class SActivateParaboxPacket implements IPacket {
    @Override
    public void handle(NetworkEvent.Context ctx) {
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        if (!(minecraft.screen instanceof ParaboxScreen)) return;
        ParaboxScreen screen = (ParaboxScreen) minecraft.screen;
        screen.setInLoop(true);
        screen.setBackedUp(false);
    }
}

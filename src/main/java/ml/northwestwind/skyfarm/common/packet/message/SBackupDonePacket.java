package ml.northwestwind.skyfarm.common.packet.message;

import ml.northwestwind.skyfarm.common.packet.IPacket;
import ml.northwestwind.skyfarm.client.screen.ParaboxScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.NetworkEvent;

public class SBackupDonePacket implements IPacket {
    @Override
    public void handle(NetworkEvent.Context ctx) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.screen instanceof ParaboxScreen)) return;
        ParaboxScreen screen = (ParaboxScreen) minecraft.screen;
        screen.setBackedUp(true);
        screen.setInLoop(true);
    }
}

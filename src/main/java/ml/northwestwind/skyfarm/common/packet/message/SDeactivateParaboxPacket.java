package ml.northwestwind.skyfarm.common.packet.message;

import ml.northwestwind.skyfarm.common.packet.IPacket;
import ml.northwestwind.skyfarm.client.screen.ParaboxScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

public class SDeactivateParaboxPacket implements IPacket {
    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(NetworkEvent.Context ctx) {
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        if (!(minecraft.screen instanceof ParaboxScreen)) return;
        ParaboxScreen screen = (ParaboxScreen) minecraft.screen;
        screen.setInLoop(false);
        screen.setBackedUp(true);
    }
}

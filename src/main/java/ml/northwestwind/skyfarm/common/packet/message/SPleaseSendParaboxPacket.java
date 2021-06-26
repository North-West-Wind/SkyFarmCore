package ml.northwestwind.skyfarm.common.packet.message;

import ml.northwestwind.skyfarm.common.packet.IPacket;
import ml.northwestwind.skyfarm.common.packet.SkyFarmPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

public class SPleaseSendParaboxPacket implements IPacket {
    private final boolean activate, votedFor;

    public SPleaseSendParaboxPacket(boolean activate, boolean votedFor) {
        this.activate = activate;
        this.votedFor = votedFor;
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return;
        if (activate) SkyFarmPacketHandler.INSTANCE.sendToServer(new CVoteActivateParaboxPacket(votedFor));
        else SkyFarmPacketHandler.INSTANCE.sendToServer(new CVoteDeactivateParaboxPacket(votedFor));
    }
}

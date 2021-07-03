package ml.northwestwind.skyfarm.common.packet.message;

import ml.northwestwind.skyfarm.common.packet.IPacket;
import ml.northwestwind.skyfarm.common.packet.SkyFarmPacketHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

public class SPleaseSendParaboxPacket implements IPacket {
    private final boolean activate, votedFor;

    public SPleaseSendParaboxPacket(boolean activate, boolean votedFor) {
        this.activate = activate;
        this.votedFor = votedFor;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(NetworkEvent.Context ctx) {
        PlayerEntity player = net.minecraft.client.Minecraft.getInstance().player;
        if (player == null) return;
        if (activate) SkyFarmPacketHandler.INSTANCE.sendToServer(new CVoteActivateParaboxPacket(votedFor));
        else SkyFarmPacketHandler.INSTANCE.sendToServer(new CVoteDeactivateParaboxPacket(votedFor));
    }
}

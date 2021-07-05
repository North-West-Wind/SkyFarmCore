package ml.northwestwind.skyfarm.common.packet.message;

import ml.northwestwind.skyfarm.common.packet.IPacket;
import ml.northwestwind.skyfarm.common.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class CSyncVotePacket implements IPacket {
    @Override
    public void handle(NetworkEvent.Context ctx) {
        ServerPlayerEntity player = ctx.getSender();
        if (player == null) return;
        SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SSyncVotePacket(SkyblockData.votingStatus, SkyblockData.voted.contains(player.getUUID())));
    }
}

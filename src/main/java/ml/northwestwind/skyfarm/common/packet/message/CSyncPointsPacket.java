package ml.northwestwind.skyfarm.common.packet.message;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.common.packet.IPacket;
import ml.northwestwind.skyfarm.common.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class CSyncPointsPacket implements IPacket {
    @Override
    public void handle(NetworkEvent.Context ctx) {
        SkyFarm.LOGGER.info("Received client points syncing packet!");
        ServerPlayerEntity player = ctx.getSender();
        if (player == null) return;
        SkyblockData data = SkyblockData.get(player.getLevel());
        SkyFarm.LOGGER.info("Replying to packet!");
        SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(ctx::getSender), new SSyncPointsPacket(data.getPoint()));
    }
}

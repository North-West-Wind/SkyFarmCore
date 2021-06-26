package ml.northwestwind.skyfarm.common.packet.message;

import ml.northwestwind.skyfarm.common.packet.IDoubleSidedPacket;
import ml.northwestwind.skyfarm.common.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.client.screen.GameStageScreen;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class DSyncPointsPacket implements IDoubleSidedPacket {
    private final long points;
    public DSyncPointsPacket(long points) {
        this.points = points;
    }

    public DSyncPointsPacket() {
        this(0);
    }

    @Override
    public void handleClient(NetworkEvent.Context ctx) {
        GameStageScreen.points = points;
    }

    @Override
    public void handleServer(NetworkEvent.Context ctx) {
        ServerPlayerEntity player = ctx.getSender();
        if (player == null) return;
        SkyblockData data = SkyblockData.get(player.getLevel());
        SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(ctx::getSender), new DSyncPointsPacket(data.getPoint()));
    }

    public static void serverSyncAll(MinecraftServer server) {
        SkyblockData data = SkyblockData.get(server.overworld());
        SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new DSyncPointsPacket(data.getPoint()));
    }
}

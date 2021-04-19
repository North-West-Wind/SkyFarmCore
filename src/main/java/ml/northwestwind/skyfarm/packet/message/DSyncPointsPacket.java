package ml.northwestwind.skyfarm.packet.message;

import ml.northwestwind.skyfarm.packet.IDoubleSidedPacket;
import ml.northwestwind.skyfarm.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.screen.GameStageScreen;
import ml.northwestwind.skyfarm.world.data.SkyblockData;
import net.minecraft.entity.player.ServerPlayerEntity;
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
}

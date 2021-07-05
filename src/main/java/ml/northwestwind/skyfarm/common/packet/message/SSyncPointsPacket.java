package ml.northwestwind.skyfarm.common.packet.message;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.common.packet.IPacket;
import ml.northwestwind.skyfarm.common.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class SSyncPointsPacket implements IPacket {
    private final long points;
    public SSyncPointsPacket(long points) {
        this.points = points;
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        SkyFarm.LOGGER.info("Now syncing points...");
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft.screen instanceof ml.northwestwind.skyfarm.client.screen.GameStageScreen) ((ml.northwestwind.skyfarm.client.screen.GameStageScreen) minecraft.screen).points = points;
    }

    public static void serverSyncAll(MinecraftServer server) {
        SkyblockData data = SkyblockData.get(server.overworld());
        SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SSyncPointsPacket(data.getPoint()));
    }
}

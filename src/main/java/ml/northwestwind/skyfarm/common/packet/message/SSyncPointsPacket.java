package ml.northwestwind.skyfarm.common.packet.message;

import ml.northwestwind.skyfarm.common.packet.IPacket;
import ml.northwestwind.skyfarm.common.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import ml.northwestwind.skyfarm.config.SkyFarmConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Collection;
import java.util.UUID;

public class SSyncPointsPacket implements IPacket {
    private final long points;
    public SSyncPointsPacket(long points) {
        this.points = points;
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft.screen instanceof ml.northwestwind.skyfarm.client.screen.GameStageScreen) ((ml.northwestwind.skyfarm.client.screen.GameStageScreen) minecraft.screen).points = points;
    }

    public static void serverSyncAll(MinecraftServer server, String team) {
        SkyblockData data = SkyblockData.get(server.overworld());
        if (SkyFarmConfig.GLOBAL_STAGE.get()) SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SSyncPointsPacket(data.getGlobalPoint()));
        else if (team != null && data.hasTeam(team)) {
            Collection<UUID> uuids = data.getTeamPlayers(team);
            uuids.forEach(uuid -> {
                ServerPlayerEntity player = server.getPlayerList().getPlayer(uuid);
                if (player != null) SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SSyncPointsPacket(data.getTeamPoint(team)));
            });
        }
    }
}

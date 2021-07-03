package ml.northwestwind.skyfarm.common.packet.message;

import ml.northwestwind.skyfarm.common.packet.IDoubleSidedPacket;
import ml.northwestwind.skyfarm.common.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.client.screen.VoteScreen;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class DSyncVotePacket implements IDoubleSidedPacket {
    private final int statusId;
    private final boolean hasVoted;

    public DSyncVotePacket(SkyblockData.VotingStatus status, boolean hasVoted) {
        this.statusId = status.getId();
        this.hasVoted = hasVoted;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleClient(NetworkEvent.Context ctx) {
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        if (!(minecraft.screen instanceof VoteScreen)) return;
        ((VoteScreen) minecraft.screen).syncFromPacket(SkyblockData.VotingStatus.getFromID(statusId), hasVoted);
    }

    @Override
    public void handleServer(NetworkEvent.Context ctx) {
        ServerPlayerEntity player = ctx.getSender();
        if (player == null) return;
        SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new DSyncVotePacket(SkyblockData.votingStatus, SkyblockData.voted.contains(player.getUUID())));
    }
}

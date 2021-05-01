package ml.northwestwind.skyfarm.packet.message;

import ml.northwestwind.skyfarm.packet.IDoubleSidedPacket;
import ml.northwestwind.skyfarm.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.screen.VoteScreen;
import ml.northwestwind.skyfarm.world.data.SkyblockData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class DSyncVotePacket implements IDoubleSidedPacket {
    private final int statusId;
    private final boolean hasVoted;

    public DSyncVotePacket(SkyblockData.VotingStatus status, boolean hasVoted) {
        this.statusId = status.getId();
        this.hasVoted = hasVoted;
    }

    @Override
    public void handleClient(NetworkEvent.Context ctx) {
        Minecraft minecraft = Minecraft.getInstance();
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

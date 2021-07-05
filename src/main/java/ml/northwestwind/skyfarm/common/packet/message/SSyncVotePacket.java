package ml.northwestwind.skyfarm.common.packet.message;

import ml.northwestwind.skyfarm.client.screen.VoteScreen;
import ml.northwestwind.skyfarm.common.packet.IPacket;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import net.minecraftforge.fml.network.NetworkEvent;

public class SSyncVotePacket implements IPacket {
    private final int statusId;
    private final boolean hasVoted;
    public SSyncVotePacket(SkyblockData.VotingStatus status, boolean hasVoted) {
        this.statusId = status.getId();
        this.hasVoted = hasVoted;
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        if (!(minecraft.screen instanceof VoteScreen)) return;
        ((VoteScreen) minecraft.screen).syncFromPacket(SkyblockData.VotingStatus.getFromID(statusId), hasVoted);
    }
}

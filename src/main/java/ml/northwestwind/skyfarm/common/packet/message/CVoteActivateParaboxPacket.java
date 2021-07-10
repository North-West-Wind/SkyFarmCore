package ml.northwestwind.skyfarm.common.packet.message;

import ml.northwestwind.skyfarm.common.packet.IPacket;
import ml.northwestwind.skyfarm.common.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import ml.northwestwind.skyfarm.misc.backup.Backups;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class CVoteActivateParaboxPacket implements IPacket {
    private final boolean votedFor;
    private final int x, y, z;
    public CVoteActivateParaboxPacket(boolean votedFor, BlockPos pos) {
        this.votedFor = votedFor;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public CVoteActivateParaboxPacket(boolean votedFor) {
        this(votedFor, BlockPos.ZERO);
    }

    private BlockPos getPos() {
        return new BlockPos(x, y, z);
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ServerPlayerEntity player = ctx.getSender();
        if (player == null) return;
        SkyblockData data = SkyblockData.get((ServerWorld) player.level);
        if (data.isInLoop() || player.getServer() == null) return;
        if (!SkyblockData.isVoting) startVoting(ctx);
        else if (votedFor) {
            if (SkyblockData.voted.contains(player.getUUID())) return;
            SkyblockData.voted.add(player.getUUID());
            if (player.getServer().getPlayerList().getPlayerCount() == SkyblockData.voted.size()) putInLoop(player.getServer());
        } else SkyblockData.cancelVote(player.getServer(), "deny");
    }

    private void startVoting(NetworkEvent.Context ctx) {
        ServerPlayerEntity player = ctx.getSender();
        if (player == null || player.getServer() == null) return;
        SkyblockData data = SkyblockData.get((ServerWorld) player.level);
        if (!getPos().equals(BlockPos.ZERO)) data.setParaboxPos(getPos());
        data.setDirty();
        if (player.getServer().getPlayerList().getPlayerCount() < 2) {
            player.sendMessage(new TranslationTextComponent("parabox.vote.activate", player.getName().getString()).setStyle(Style.EMPTY.applyFormat(TextFormatting.AQUA)), ChatType.SYSTEM, Util.NIL_UUID);
            putInLoop(player.getServer());
        } else {
            IFormattableTextComponent component = (IFormattableTextComponent) StringTextComponent.EMPTY;
            ITextComponent voteActivate = new TranslationTextComponent("parabox.vote.activate", player.getName().getString()).setStyle(Style.EMPTY.applyFormats(TextFormatting.AQUA));
            component.append(voteActivate);
            component.append("\n");
            component.append(new TranslationTextComponent("parabox.vote.openGui").setStyle(Style.EMPTY.applyFormats(TextFormatting.YELLOW, TextFormatting.BOLD)));
            for (ServerPlayerEntity s : player.getServer().getPlayerList().getPlayers()) {
                if (s.getUUID().equals(player.getUUID())) s.connection.send(new SChatPacket(voteActivate, ChatType.SYSTEM, Util.NIL_UUID));
                else s.connection.send(new SChatPacket(component, ChatType.SYSTEM, Util.NIL_UUID));
            }
            SkyblockData.startVoting(player.getServer(), SkyblockData.VotingStatus.ACTIVATE);
            SkyblockData.voted.add(player.getUUID());
        }
    }

    private void putInLoop(MinecraftServer server) {
        server.getPlayerList().broadcastMessage(new TranslationTextComponent("parabox.activate", x, y, z).setStyle(Style.EMPTY.applyFormat(TextFormatting.GREEN)), ChatType.SYSTEM, Util.NIL_UUID);
        SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SActivateParaboxPacket());
        SkyblockData.endVoting();
        SkyblockData data = SkyblockData.get(server.overworld());
        data.setInLoop(true);
        data.setDirty();
        Backups.INSTANCE.run(server);
    }
}

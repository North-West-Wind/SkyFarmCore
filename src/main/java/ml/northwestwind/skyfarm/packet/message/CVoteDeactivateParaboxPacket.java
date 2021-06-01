package ml.northwestwind.skyfarm.packet.message;

import com.google.common.collect.ImmutableList;
import ml.northwestwind.skyfarm.packet.IPacket;
import ml.northwestwind.skyfarm.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.world.data.SkyblockData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class CVoteDeactivateParaboxPacket implements IPacket {
    private final boolean votedFor, forced, isFirst;
    public CVoteDeactivateParaboxPacket(boolean forced, boolean votedFor, boolean isFirst) {
        this.votedFor = votedFor;
        this.forced = isFirst && forced;
        this.isFirst = isFirst;
    }

    public CVoteDeactivateParaboxPacket(boolean forced, boolean votedFor) {
        this(forced, votedFor, true);
    }

    public CVoteDeactivateParaboxPacket(boolean votedFor) {
        this(false, votedFor, false);
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ServerPlayerEntity player = ctx.getSender();
        if (player == null) return;
        if (isFirst) SkyblockData.forced = forced;
        SkyblockData data = SkyblockData.get(player.getLevel());
        if (!data.isInLoop() || player.getServer() == null) return;
        if (!SkyblockData.isVoting) startVoting(ctx);
        else if (votedFor) {
            if (SkyblockData.voted.contains(player.getUUID())) return;
            SkyblockData.voted.add(player.getUUID());
            if (player.getServer().getPlayerList().getPlayerCount() == SkyblockData.voted.size()) {
                BlockPos pos = data.getParaboxPos();
                player.getServer().getPlayerList().broadcastMessage(new TranslationTextComponent("parabox.deactivate", pos.getX(), pos.getY(), pos.getZ()).setStyle(Style.EMPTY.applyFormat(TextFormatting.GREEN)), ChatType.SYSTEM, Util.NIL_UUID);
                if (SkyblockData.forced) exitLoopForced(player);
                else exitLoop(player);
                SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SDeactivateParaboxPacket());
            }
        } else SkyblockData.cancelVote(player.getServer(), "deny");
    }

    private void startVoting(NetworkEvent.Context ctx) {
        ServerPlayerEntity player = ctx.getSender();
        if (player == null || player.getServer() == null) return;
        if (player.getServer().getPlayerList().getPlayerCount() < 2) {
            player.sendMessage(new TranslationTextComponent("parabox.vote.deactivate", player.getName().getString()).setStyle(Style.EMPTY.applyFormat(TextFormatting.AQUA)), ChatType.SYSTEM, Util.NIL_UUID);
            SkyblockData data = SkyblockData.get(player.getLevel());
            BlockPos pos = data.getParaboxPos();
            player.getServer().getPlayerList().broadcastMessage(new TranslationTextComponent("parabox.deactivate", pos.getX(), pos.getY(), pos.getZ()).setStyle(Style.EMPTY.applyFormat(TextFormatting.GREEN)), ChatType.SYSTEM, Util.NIL_UUID);
            if (SkyblockData.forced) exitLoopForced(player);
            else exitLoop(player);
            SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SDeactivateParaboxPacket());
        } else {
            IFormattableTextComponent component = (IFormattableTextComponent) StringTextComponent.EMPTY;
            ITextComponent voteDeactivate = new TranslationTextComponent("parabox.vote.deactivate", player.getName().getString()).setStyle(Style.EMPTY.applyFormats(TextFormatting.AQUA));
            component.append(voteDeactivate);
            component.append("\n");
            component.append(new TranslationTextComponent("parabox.vote.openGui").setStyle(Style.EMPTY.applyFormats(TextFormatting.YELLOW, TextFormatting.BOLD)));
            for (ServerPlayerEntity s : player.getServer().getPlayerList().getPlayers()) {
                if (s.getUUID().equals(player.getUUID())) s.connection.send(new SChatPacket(voteDeactivate, ChatType.SYSTEM, Util.NIL_UUID));
                else s.connection.send(new SChatPacket(component, ChatType.SYSTEM, Util.NIL_UUID));
            }
            SkyblockData.startVoting(player.getServer(), SkyblockData.VotingStatus.DEACTIVATE);
            SkyblockData.voted.add(player.getUUID());
        }
    }

    private void exitLoop(ServerPlayerEntity player) {
        SkyblockData.endVoting();
        if (player.getServer() == null) return;
        SkyblockData data = SkyblockData.get(player.getLevel());
        SkyblockData.shouldRestore = true;
        data.setInLoop(false);
        data.setParaboxLevel(0);
        ImmutableList<ServerPlayerEntity> players = ImmutableList.copyOf(player.getServer().getPlayerList().getPlayers());
        data.setDirty();
        player.getServer().saveAllChunks(true, true, true);
        for (ServerPlayerEntity p : players) try {
            p.connection.disconnect(new TranslationTextComponent("parabox.disconnect"));
        } catch (Exception ignored) { }
    }

    private void exitLoopForced(ServerPlayerEntity player) {
        SkyblockData.endVoting();
        if (player.getServer() == null) return;
        SkyblockData data = SkyblockData.get(player.getLevel());
        data.setInLoop(false);
        data.setParaboxLevel(0);
        data.setDirty();
    }
}

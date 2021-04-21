package ml.northwestwind.skyfarm.packet.message;

import com.google.common.collect.ImmutableList;
import ml.northwestwind.skyfarm.misc.backup.Backups;
import ml.northwestwind.skyfarm.packet.IPacket;
import ml.northwestwind.skyfarm.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.world.data.SkyblockData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;

public class CVoteDeactivateParaboxPacket implements IPacket {
    private final boolean votedFor;
    public static Thread thread;
    public CVoteDeactivateParaboxPacket(boolean forced, boolean votedFor, boolean isFirst) {
        this.votedFor = votedFor;
        if (isFirst) SkyblockData.forced = forced;
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
        SkyblockData data = SkyblockData.get(player.getLevel());
        if (!data.isInLoop() || player.getServer() == null) return;
        if (!SkyblockData.isVoting) startVoting(ctx);
        else if (votedFor) {
            SkyblockData.votedFor++;
            if (player.getServer().getPlayerList().getPlayerCount() == SkyblockData.votedFor) {
                BlockPos pos = data.getParaboxPos();
                player.getServer().getPlayerList().broadcastMessage(new TranslationTextComponent("parabox.deactivate", pos.getX(), pos.getY(), pos.getZ()).setStyle(Style.EMPTY.applyFormat(TextFormatting.GREEN)), ChatType.SYSTEM, Util.NIL_UUID);
                if (SkyblockData.forced) exitLoopForced(player);
                else exitLoop(player);
                SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SDeactivateParaboxPacket());
            }
        } else {
            SkyblockData.isVoting = false;
            thread.stop();
            player.getServer().getPlayerList().broadcastMessage(new TranslationTextComponent("parabox.vote.deny").setStyle(Style.EMPTY.applyFormat(TextFormatting.RED)), ChatType.SYSTEM, Util.NIL_UUID);
        }
    }

    private void startVoting(NetworkEvent.Context ctx) {
        ServerPlayerEntity player = ctx.getSender();
        if (player == null || player.getServer() == null) return;
        if (!player.getServer().isDedicatedServer()) {
            player.sendMessage(new TranslationTextComponent("parabox.vote.deactivate", player.getName().getString()).setStyle(Style.EMPTY.applyFormat(TextFormatting.AQUA)), ChatType.SYSTEM, Util.NIL_UUID);
            SkyblockData data = SkyblockData.get(player.getLevel());
            BlockPos pos = data.getParaboxPos();
            player.getServer().getPlayerList().broadcastMessage(new TranslationTextComponent("parabox.deactivate", pos.getX(), pos.getY(), pos.getZ()).setStyle(Style.EMPTY.applyFormat(TextFormatting.GREEN)), ChatType.SYSTEM, Util.NIL_UUID);

            if (SkyblockData.forced) exitLoopForced(player);
            else exitLoop(player);
            SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SDeactivateParaboxPacket());
        } else {
            IFormattableTextComponent component = (IFormattableTextComponent) StringTextComponent.EMPTY;
            component.append(new TranslationTextComponent("parabox.vote.deactivate", player.getName().getString()).setStyle(Style.EMPTY.applyFormats(TextFormatting.BOLD)));
            component.append("\n");
            component.append(new TranslationTextComponent("parabox.vote.deactivate.yes").setStyle(Style.EMPTY.applyFormat(TextFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "vote paraboxDeactivate true"))));
            component.append(" ");
            component.append(new TranslationTextComponent("parabox.vote.deactivate.no").setStyle(Style.EMPTY.applyFormat(TextFormatting.RED).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "vote paraboxDeactivate false"))));
            player.getServer().getPlayerList().broadcastMessage(component, ChatType.SYSTEM, Util.NIL_UUID);
            SkyblockData.isVoting = true;
            thread = new Thread(() -> {
                try {
                    Thread.sleep(60000);
                    if (SkyblockData.isVoting) {
                        SkyblockData.isVoting = false;
                        player.getServer().getPlayerList().broadcastMessage(new TranslationTextComponent("parabox.vote.timeout").setStyle(Style.EMPTY.applyFormat(TextFormatting.RED)), ChatType.SYSTEM, Util.NIL_UUID);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }

    private void exitLoop(ServerPlayerEntity player) {
        SkyblockData.isVoting = false;
        if (player.getServer() == null) return;
        SkyblockData data = SkyblockData.get(player.getLevel());
        SkyblockData.shouldRestore = true;
        data.setInLoop(false);
        data.setParaboxLevel(0);
        ImmutableList<ServerPlayerEntity> players = ImmutableList.copyOf(player.getServer().getPlayerList().getPlayers());
        //for (ServerPlayerEntity p : players) data.addPlayerData(p);
        data.setDirty();
        player.getServer().saveAllChunks(true, true, true);
        for (ServerPlayerEntity p : players) try {
            p.connection.disconnect(new TranslationTextComponent("parabox.disconnect"));
        } catch (Exception ignored) { }
    }

    private void exitLoopForced(ServerPlayerEntity player) {
        SkyblockData.isVoting = false;
        if (player.getServer() == null) return;
        SkyblockData data = SkyblockData.get(player.getLevel());
        data.setInLoop(false);
        data.setParaboxLevel(0);
        data.setDirty();
    }
}

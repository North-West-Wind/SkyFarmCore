package ml.northwestwind.skyfarm.packet.message;

import ml.northwestwind.skyfarm.misc.backup.Backups;
import ml.northwestwind.skyfarm.packet.IPacket;
import ml.northwestwind.skyfarm.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.world.data.SkyblockData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class CVoteActivateParaboxPacket implements IPacket {
    private final boolean votedFor;
    private final int x, y, z;
    public static Thread thread;
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
            SkyblockData.votedFor++;
            if (player.getServer().getPlayerList().getPlayerCount() == SkyblockData.votedFor) {
                player.getServer().getPlayerList().broadcastMessage(new TranslationTextComponent("parabox.activate", x, y, z).setStyle(Style.EMPTY.applyFormat(TextFormatting.GREEN)), ChatType.SYSTEM, Util.NIL_UUID);
                SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SActivateParaboxPacket());
                putInLoop(player.getServer());
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
        SkyblockData data = SkyblockData.get((ServerWorld) player.level);
        if (!getPos().equals(BlockPos.ZERO)) data.setParaboxPos(getPos());
        data.setDirty();
        if (!player.getServer().isDedicatedServer()) {
            player.sendMessage(new TranslationTextComponent("parabox.vote.activate", player.getName().getString()).setStyle(Style.EMPTY.applyFormat(TextFormatting.AQUA)), ChatType.SYSTEM, Util.NIL_UUID);
            player.getServer().getPlayerList().broadcastMessage(new TranslationTextComponent("parabox.activate", x, y, z).setStyle(Style.EMPTY.applyFormat(TextFormatting.GREEN)), ChatType.SYSTEM, Util.NIL_UUID);
            SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SActivateParaboxPacket());
            putInLoop(player.getServer());
        } else {
            IFormattableTextComponent component = (IFormattableTextComponent) StringTextComponent.EMPTY;
            component.append(new TranslationTextComponent("parabox.vote.activate", player.getName().getString()).setStyle(Style.EMPTY.applyFormats(TextFormatting.BOLD)));
            component.append("\n");
            component.append(new TranslationTextComponent("parabox.vote.activate.yes").setStyle(Style.EMPTY.applyFormat(TextFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "vote paraboxActivate true"))));
            component.append(" ");
            component.append(new TranslationTextComponent("parabox.vote.activate.no").setStyle(Style.EMPTY.applyFormat(TextFormatting.RED).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "vote paraboxActivate false"))));
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

    private void putInLoop(MinecraftServer server) {
        SkyblockData.isVoting = false;
        Backups.INSTANCE.run(server);
    }
}

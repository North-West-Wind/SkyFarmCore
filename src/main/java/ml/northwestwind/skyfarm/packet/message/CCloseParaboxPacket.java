package ml.northwestwind.skyfarm.packet.message;

import ml.northwestwind.skyfarm.packet.IPacket;
import ml.northwestwind.skyfarm.world.data.SkyblockData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class CCloseParaboxPacket implements IPacket {
    @Override
    public void handle(NetworkEvent.Context ctx) {
        ServerPlayerEntity player = ctx.getSender();
        if (player == null) return;
        SkyblockData data = SkyblockData.get((ServerWorld) player.level);
        data.setUsingParabox(false);
        data.setDirty();
    }
}

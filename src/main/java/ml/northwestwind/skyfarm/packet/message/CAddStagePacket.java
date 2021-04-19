package ml.northwestwind.skyfarm.packet.message;

import ml.northwestwind.skyfarm.packet.IPacket;
import ml.northwestwind.skyfarm.world.data.SkyblockData;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

public class CAddStagePacket implements IPacket {
    private final String stage;
    public CAddStagePacket(String stage) {
        this.stage = stage;
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ServerPlayerEntity player = ctx.getSender();
        if (player == null || player.getServer() == null) return;
        if (!GameStageHelper.isStageKnown(stage)) return;
        for (ServerPlayerEntity p : player.getServer().getPlayerList().getPlayers()) GameStageHelper.addStage(p, stage);
        SkyblockData data = SkyblockData.get(player.getLevel());
        data.addStage(stage);
        data.setPoint(data.getPoint() - 1);
    }
}

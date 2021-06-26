package ml.northwestwind.skyfarm.common.packet;

import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public interface IDoubleSidedPacket extends IPacket {
    @Override
    default void handle(NetworkEvent.Context ctx) {
        if (ctx.getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)) handleClient(ctx);
        else if (ctx.getDirection().equals(NetworkDirection.PLAY_TO_SERVER)) handleServer(ctx);
    }

    void handleClient(NetworkEvent.Context ctx);
    void handleServer(NetworkEvent.Context ctx);
}

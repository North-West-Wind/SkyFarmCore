package ml.northwestwind.skyfarm.common.packet;

import net.minecraftforge.fml.network.NetworkEvent;

import java.io.Serializable;

public interface IPacket extends Serializable {
    void handle(NetworkEvent.Context ctx);
}

package ml.northwestwind.skyfarm.common.packet;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.common.packet.message.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

public class SkyFarmPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SkyFarm.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int i = 0;

    public static void registerPackets() {
        registerMessage(CPlayerGrowPacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerMessage(CVoteActivateParaboxPacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerMessage(CVoteDeactivateParaboxPacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerMessage(CCloseParaboxPacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerMessage(SBackupDonePacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerMessage(SActivateParaboxPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerMessage(SDeactivateParaboxPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerMessage(CAddStagePacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerMessage(CSyncPointsPacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerMessage(SSyncPointsPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerMessage(CSyncVotePacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerMessage(SSyncVotePacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerMessage(SPleaseSendParaboxPacket.class, NetworkDirection.PLAY_TO_CLIENT);
    }

    private static <MSG extends IPacket> void registerMessage(Class<MSG> clazz, NetworkDirection direction) {
        INSTANCE.registerMessage(i++, clazz, (msg, buffer) -> buffer.writeByteArray(PacketCodec.encode(msg)), buffer -> (MSG) PacketCodec.decode(buffer.readByteArray()), (msg, ctx) -> ctx.get().enqueueWork(() -> {
            msg.handle(ctx.get());
            ctx.get().setPacketHandled(true);
        }), Optional.of(direction));
    }
}

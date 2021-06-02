package ml.northwestwind.skyfarm.packet;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.packet.message.*;
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
        registerMessage(SLaunchPlayerExplosionPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerMessage(CVoteActivateParaboxPacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerMessage(CVoteDeactivateParaboxPacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerMessage(CCloseParaboxPacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerMessage(SBackupDonePacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerMessage(SActivateParaboxPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerMessage(SDeactivateParaboxPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerMessage(CAddStagePacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerMessage(DSyncPointsPacket.class);
        registerMessage(DSyncVotePacket.class);
        registerMessage(SPleaseSendParaboxPacket.class, NetworkDirection.PLAY_TO_CLIENT);
    }

    private static <MSG extends IDoubleSidedPacket> void registerMessage(Class<MSG> clazz) {
        INSTANCE.registerMessage(i++, clazz, (msg, buffer) -> buffer.writeByteArray(PacketCodec.encode(msg)), buffer -> (MSG) PacketCodec.decode(buffer.readByteArray()), (msg, ctx) -> {
            ctx.get().enqueueWork(() -> msg.handle(ctx.get()));
            ctx.get().setPacketHandled(true);
        });
    }

    private static <MSG extends IPacket> void registerMessage(Class<MSG> clazz, NetworkDirection direction) {
        INSTANCE.registerMessage(i++, clazz, (msg, buffer) -> buffer.writeByteArray(PacketCodec.encode(msg)), buffer -> (MSG) PacketCodec.decode(buffer.readByteArray()), (msg, ctx) -> {
            ctx.get().enqueueWork(() -> msg.handle(ctx.get()));
            ctx.get().setPacketHandled(true);
        }, Optional.of(direction));
    }
}

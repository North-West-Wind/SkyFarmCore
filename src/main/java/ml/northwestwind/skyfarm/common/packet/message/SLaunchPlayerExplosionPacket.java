package ml.northwestwind.skyfarm.common.packet.message;

import ml.northwestwind.skyfarm.misc.NoDamageExplosion;
import ml.northwestwind.skyfarm.misc.serializable.RegistryKeySerializable;
import ml.northwestwind.skyfarm.common.packet.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SLaunchPlayerExplosionPacket implements IPacket {
    private final int x, y, z;
    private final RegistryKeySerializable<World> worldKey;
    public SLaunchPlayerExplosionPacket(BlockPos pos, RegistryKey<World> registryKey) {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();

        worldKey = new RegistryKeySerializable<>(registryKey);
    }

    private BlockPos getPos() {
        return new BlockPos(x, y, z);
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return;
        if (!player.level.dimension().equals(worldKey.toRegistryKey())) return;
        NoDamageExplosion explosion = new NoDamageExplosion(player.level, getPos(), 3, Explosion.Mode.NONE);
        explosion.explode();
        explosion.finalizeExplosion(true);
    }
}

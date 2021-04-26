package ml.northwestwind.skyfarm.misc.teleporter;

import ml.northwestwind.skyfarm.misc.Utils;
import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public class VoidTeleporter implements ITeleporter {
    private final boolean toVoid;
    private final double factor;
    public VoidTeleporter(boolean toVoid) {
        this(toVoid, 1);
    }

    public VoidTeleporter(boolean toVoid, double factor) {
        this.toVoid = toVoid;
        this.factor = factor;
    }

    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerWorld destWorld, Function<ServerWorld, PortalInfo> defaultPortalInfo) {
        if (destWorld.dimension().equals(World.OVERWORLD) && entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            Optional<BlockPos> optional = living.getSleepingPos();
            return optional.map(blockPos -> new PortalInfo(Utils.blockPosToVector3d(blockPos), Vector3d.ZERO, entity.yRot, entity.xRot)).orElseGet(() -> new PortalInfo(Vector3d.ZERO.add(0, 64, 0), Vector3d.ZERO, entity.yRot, entity.xRot));
        }
        WorldBorder border = destWorld.getWorldBorder();
        double minX = Math.max(-2.9999872E7D, border.getMinX() + 16.0D);
        double minZ = Math.max(-2.9999872E7D, border.getMinZ() + 16.0D);
        double maxX = Math.min(2.9999872E7D, border.getMaxX() - 16.0D);
        double maxZ = Math.min(2.9999872E7D, border.getMaxZ() - 16.0D);
        Vector3d pos = new Vector3d(MathHelper.clamp(entity.getX() * factor, minX, maxX), toVoid ? 316 : -60, MathHelper.clamp(entity.getZ() * factor, minZ, maxZ));
        return new PortalInfo(pos, entity.getDeltaMovement(), entity.yRot, entity.xRot);
    }
}

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

public class HorizontalTeleporter implements ITeleporter {
    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerWorld destWorld, Function<ServerWorld, PortalInfo> defaultPortalInfo) {
        if (destWorld.dimension().equals(World.OVERWORLD) && entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            Optional<BlockPos> optional = living.getSleepingPos();
            return optional.map(blockPos -> new PortalInfo(Utils.blockPosToVector3d(blockPos), entity.getDeltaMovement(), entity.yRot, entity.xRot)).orElseGet(() -> new PortalInfo(Vector3d.ZERO.add(0, 64, 0), entity.getDeltaMovement(), entity.yRot, entity.xRot));
        }
        WorldBorder border = destWorld.getWorldBorder();
        double minX = Math.max(-2.9999872E7D, border.getMinX() + 16.0D);
        double minZ = Math.max(-2.9999872E7D, border.getMinZ() + 16.0D);
        double maxX = Math.min(2.9999872E7D, border.getMaxX() - 16.0D);
        double maxZ = Math.min(2.9999872E7D, border.getMaxZ() - 16.0D);
        double coordinateDifference = DimensionType.getTeleportationScale(entity.level.dimensionType(), destWorld.dimensionType());
        Vector3d pos = new Vector3d(MathHelper.clamp(entity.getX() * coordinateDifference, minX, maxX), entity.getY(), MathHelper.clamp(entity.getZ() * coordinateDifference, minZ, maxZ));
        return new PortalInfo(pos, entity.getDeltaMovement(), entity.yRot, entity.xRot);
    }
}

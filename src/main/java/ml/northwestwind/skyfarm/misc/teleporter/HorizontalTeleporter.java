package ml.northwestwind.skyfarm.misc.teleporter;

import ml.northwestwind.skyfarm.misc.Utils;
import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
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
        return new PortalInfo(entity.position(), entity.getDeltaMovement(), entity.yRot, entity.xRot);
    }
}

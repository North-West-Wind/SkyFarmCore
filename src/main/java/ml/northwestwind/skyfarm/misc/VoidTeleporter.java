package ml.northwestwind.skyfarm.misc;

import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
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
        if (toVoid) return new PortalInfo(entity.position().multiply(factor, 0, factor).add(0, 316, 0), Vector3d.ZERO, entity.yRot, entity.xRot);
        else return new PortalInfo(entity.position().multiply(factor, 0, factor).add(0, -60, 0), Vector3d.ZERO, entity.yRot, entity.xRot);
    }
}

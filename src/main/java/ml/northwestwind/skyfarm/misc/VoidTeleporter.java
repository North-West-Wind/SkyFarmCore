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
    public VoidTeleporter(boolean toVoid) {
        this.toVoid = toVoid;
    }

    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerWorld destWorld, Function<ServerWorld, PortalInfo> defaultPortalInfo) {
        if (toVoid) return new PortalInfo(entity.position().multiply(1, 0, 1).add(0, 316, 0), Vector3d.ZERO, entity.yRot, entity.xRot);
        else return new PortalInfo(entity.position().multiply(1, 0, 1), Vector3d.ZERO, entity.yRot, entity.xRot);
    }
}

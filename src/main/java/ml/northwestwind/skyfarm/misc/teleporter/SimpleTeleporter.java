package ml.northwestwind.skyfarm.misc.teleporter;

import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.function.Function;

public class SimpleTeleporter implements ITeleporter {
    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerWorld destWorld, Function<ServerWorld, PortalInfo> defaultPortalInfo) {
        return new PortalInfo(entity.position(), entity.getDeltaMovement(), entity.yRot, entity.xRot);
    }
}

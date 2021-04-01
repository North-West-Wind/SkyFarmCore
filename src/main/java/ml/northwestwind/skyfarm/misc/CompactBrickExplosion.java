package ml.northwestwind.skyfarm.misc;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CompactBrickExplosion extends Explosion {
    public CompactBrickExplosion(World world, @Nullable Entity entity, @Nullable DamageSource source, @Nullable ExplosionContext context, BlockPos pos, float radius, Mode mode) {
        super(world, entity, source, context, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, radius, false, mode);
    }

    @Override
    public void explode() { }
}

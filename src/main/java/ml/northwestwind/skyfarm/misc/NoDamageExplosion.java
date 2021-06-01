package ml.northwestwind.skyfarm.misc;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class NoDamageExplosion extends Explosion {
    public NoDamageExplosion(World world, BlockPos pos, float radius, Mode mode) {
        super(world, null, null, null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, radius, false, mode);
    }

    @Override
    public void explode() { }
}

package ml.northwestwind.skyfarm.packet.message;

import ml.northwestwind.skyfarm.packet.IPacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SPlayerGrowPacket implements IPacket {
    double x, y, z;

    public SPlayerGrowPacket(Vector3d pos) {
        x = pos.x;
        y = pos.y;
        z = pos.z;
    }

    public Vector3d getPos() {
        return new Vector3d(x, y, z);
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) return;
        World world = player.getCommandSenderWorld();
        Vector3d pos = getPos();
        BlockPos blockPos = new BlockPos(pos);
        for (BlockPos blockPos1 : BlockPos.betweenClosed(blockPos.offset(-5, -5, -5), blockPos.offset(5, 5, 5))) {
            BlockState state = world.getBlockState(blockPos1);
            Block block = state.getBlock();
            if ((block instanceof SaplingBlock || block instanceof CropsBlock) && state.isRandomlyTicking()) {
                Vector3d dir = pos.vectorTo(new Vector3d(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ()));
                world.addParticle(ParticleTypes.HAPPY_VILLAGER.getType(), pos.x(), pos.y(), pos.z(), dir.x(), dir.y(), dir.z());
            }
        }
    }
}

package ml.northwestwind.skyfarm.packet.message;

import ml.northwestwind.skyfarm.packet.IPacket;
import ml.northwestwind.skyfarm.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.world.SkyblockChunkGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class CPlayerGrowPacket implements IPacket {
    @Override
    public void handle(NetworkEvent.Context ctx) {
        PlayerEntity player = ctx.getSender();
        if (player == null) return;
        World world = player.getCommandSenderWorld();
        if (SkyblockChunkGenerator.isWorldSkyblock(world)) return;
        Vector3d pos = player.position();
        BlockPos blockPos = player.blockPosition();
        for (BlockPos blockPos1 : BlockPos.betweenClosed(blockPos.offset(-5, -5, -5), blockPos.offset(5, 5, 5))) {
            BlockState state = world.getBlockState(blockPos1);
            Block block = state.getBlock();
            if ((block instanceof SaplingBlock || block instanceof CropsBlock) && state.isRandomlyTicking()) {
                state.randomTick((ServerWorld) world, blockPos1, player.getRandom());
            }
        }
        SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SPlayerGrowPacket(pos));
    }
}

package ml.northwestwind.skyfarm.packet.message;

import ml.northwestwind.skyfarm.packet.IPacket;
import ml.northwestwind.skyfarm.world.generators.SkyblockChunkGenerator;
import net.minecraft.block.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

// Thank you TwerkItMeal
public class CPlayerGrowPacket implements IPacket {
    @Override
    public void handle(NetworkEvent.Context ctx) {
        ServerPlayerEntity player = ctx.getSender();
        if (player == null) return;
        World world = player.getCommandSenderWorld();
        if (!SkyblockChunkGenerator.isWorldSkyblock((ServerWorld) world)) return;
        BlockPos blockPos = player.blockPosition();
        for (BlockPos blockPos1 : BlockPos.betweenClosed(blockPos.offset(-5, -5, -5), blockPos.offset(5, 5, 5))) {
            BlockState state = world.getBlockState(blockPos1);
            Block block = state.getBlock();
            if ((block instanceof SaplingBlock || block instanceof CropsBlock) && state.isRandomlyTicking()) {
                if (state.hasProperty(CropsBlock.AGE)) {
                    int growth = state.getValue(CropsBlock.AGE);
                    if (player.getRandom().nextInt(20) == 0) world.setBlockAndUpdate(blockPos1, state.setValue(CropsBlock.AGE, growth < 7 ? growth + 1 : 7));
                } else {
                    if (player.getRandom().nextInt(3) == 0) BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), world, blockPos1, player);
                }
                ((ServerWorld)world).sendParticles(player, ParticleTypes.HAPPY_VILLAGER, false, blockPos1.getX() + player.getRandom().nextDouble(), blockPos1.getY() + player.getRandom().nextDouble(), blockPos1.getZ() + player.getRandom().nextDouble(), 10, 0, 0, 0, 3);
            }
        }
    }
}

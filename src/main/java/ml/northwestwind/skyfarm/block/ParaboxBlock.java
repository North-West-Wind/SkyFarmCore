package ml.northwestwind.skyfarm.block;

import ml.northwestwind.skyfarm.misc.backup.Backups;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ParaboxBlock extends Block {
    public ParaboxBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if (!world.isClientSide) {
            Backups.INSTANCE.run(((ServerWorld) world).getServer());
        }
        return ActionResultType.SUCCESS;
    }
}

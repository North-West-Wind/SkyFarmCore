package ml.northwestwind.skyfarm.block;

import ml.northwestwind.skyfarm.tile.ParaboxTileEntity;
import ml.northwestwind.skyfarm.world.data.SkyblockData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class ParaboxBlock extends Block {
    public ParaboxBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if (!ModList.get().isLoaded("gamestages")) {
            player.displayClientMessage(new TranslationTextComponent("mods.skyfarm.missing", "GameStages"), true);
            return ActionResultType.CONSUME;
        }
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        } else {
            this.openContainer(world, pos, player);
            return ActionResultType.CONSUME;
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ParaboxTileEntity();
    }

    protected void openContainer(World world, BlockPos pos, PlayerEntity player) {
        TileEntity tileentity = world.getBlockEntity(pos);
        if (tileentity instanceof ParaboxTileEntity) {
            SkyblockData data = SkyblockData.get((ServerWorld) world);
            if (data.isUsingParabox()) player.displayClientMessage(new TranslationTextComponent("usage.skyfarm.parabox"), true);
            else {
                ParaboxTileEntity parabox = (ParaboxTileEntity) tileentity;
                NetworkHooks.openGui((ServerPlayerEntity) player, parabox, packetBuffer -> packetBuffer.writeBlockPos(pos));
                data.setUsingParabox(true);
                data.setDirty();
            }
        }
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean idk) {
        super.onRemove(state, world, pos, newState, idk);
        if (!world.isClientSide) {
            SkyblockData data = SkyblockData.get((ServerWorld) world);
            if (!data.isInLoop() || !data.isUsing(pos)) return;
            data.setInLoop(false);
            data.setDirty();
            ((ServerWorld) world).getServer().getPlayerList().broadcastMessage(new TranslationTextComponent("parabox.broken", pos.getX(), pos.getY(), pos.getZ()).setStyle(Style.EMPTY.applyFormat(TextFormatting.RED)), ChatType.SYSTEM, null);
        }
    }
}

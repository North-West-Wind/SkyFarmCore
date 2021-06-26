package ml.northwestwind.skyfarm.common.registries.item;

import ml.northwestwind.skyfarm.misc.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.ModList;

import java.util.function.Predicate;

public class DragonSummonerItem extends TooltipItem {
    public DragonSummonerItem(Properties properties, String registryName) {
        super(properties, registryName);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if (!ModList.get().isLoaded("iceandfire")) return super.useOn(context);
        Direction direction = context.getClickedFace();
        if (!direction.equals(Direction.UP)) return super.useOn(context);
        BlockPos pos = context.getClickedPos();
        World world = context.getLevel();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = context.getItemInHand();
        if (state.is(Blocks.NETHERRACK)) {
            if (!world.getBlockState(pos.above()).isAir()) return super.useOn(context);
            world.setBlock(pos.above(), Blocks.FIRE.defaultBlockState(), 3);
            stack.shrink(1);
            if (!world.isClientSide) summonDragon(Utils.DragonType.FIRE, pos, (ServerWorld) world, blockState -> blockState.is(Blocks.NETHERRACK));
            return ActionResultType.SUCCESS;
        }
        if (state.is(BlockTags.ICE)) {
            if (!world.getBlockState(pos.above()).isAir()) return super.useOn(context);
            world.setBlock(pos.above(), Blocks.SNOW.defaultBlockState(), 3);
            stack.shrink(1);
            if (!world.isClientSide) summonDragon(Utils.DragonType.ICE, pos, (ServerWorld) world, blockState -> blockState.is(BlockTags.ICE));
            return ActionResultType.SUCCESS;
        }
        if (state.is(Blocks.CAULDRON)) {
            if (!world.getBlockState(pos.above()).isAir()) return super.useOn(context);
            if (!world.isThundering()) return super.useOn(context);
            stack.shrink(1);
            if (!world.isClientSide) summonDragon(Utils.DragonType.LIGHTNING, pos, (ServerWorld) world, blockState -> blockState.is(Blocks.CAULDRON));
            return ActionResultType.SUCCESS;
        }
        return super.useOn(context);
    }

    private void summonDragon(Utils.DragonType type, BlockPos pos, ServerWorld world, Predicate<BlockState> predicate) {
        new Thread(() -> {
            world.getServer().getPlayerList().broadcastMessage(new TranslationTextComponent("dragon.summoning", new TranslationTextComponent("entity.iceandfire."+type.getName()+"_dragon").getString()).setStyle(Style.EMPTY.applyFormat(TextFormatting.GOLD)), ChatType.SYSTEM, Util.NIL_UUID);
            try {
                Thread.sleep(5000);
            } catch (Exception ignored) { }
            world.getServer().getPlayerList().broadcastMessage(new TranslationTextComponent("dragon.summoned", new TranslationTextComponent("entity.iceandfire."+type.getName()+"_dragon").getString()).setStyle(Style.EMPTY.applyFormat(TextFormatting.RED)), ChatType.SYSTEM, Util.NIL_UUID);
            if (!predicate.test(world.getBlockState(pos))) return;
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            if (type.equals(Utils.DragonType.LIGHTNING)) {
                LightningBoltEntity entity = new LightningBoltEntity(EntityType.LIGHTNING_BOLT, world);
                entity.setPos(pos.getX(), pos.getY(), pos.getZ());
                world.addFreshEntity(entity);
            }
            Utils.summonDragon(type, world, pos);
        }).start();
    }
}

package ml.northwestwind.skyfarm.events;


import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.world.SkyblockChunkGenerator;
import ml.northwestwind.skyfarm.world.SkyblockData;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID)
public class SkyblockEvents {
    @SubscribeEvent
    public static void playerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        World world = player.getCommandSenderWorld();
        if (SkyblockChunkGenerator.isWorldSkyblock(world)) {
            SkyblockData data = SkyblockData.get((ServerWorld) world);
            if (!data.isWorldGenerated()) {
                generateIsland(world);
                data.setWorldGenerated(true);
            }
            if (data.isFirstSpawn(player.getUUID())) {
                player.teleportTo(0.5, 64, 0.5);
                player.setSleepingPos(new BlockPos(0.5, 64, 0.5));
                data.playerJoin(player);
            }
            ((ServerWorld) world).getDataStorage().set(data);
        }
    }

    @SubscribeEvent
    public static void playerRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        PlayerEntity player = event.getPlayer();
        World world = player.getCommandSenderWorld();
        if (SkyblockChunkGenerator.isWorldSkyblock(world)) {
            if (player.getSleepingPos().isPresent()) return;
            player.teleportTo(0.5, 64, 0.5);
            player.setSleepingPos(new BlockPos(0.5, 64, 0.5));
        }
    }

    private static void generateIsland(World world) {
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                BlockPos pos = new BlockPos(i, 63, j);
                world.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState());
            }
        }
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                BlockPos pos = new BlockPos(i, 62, j);
                world.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
            }
        }
        world.setBlockAndUpdate(new BlockPos(0, 63, 0), Blocks.WATER.defaultBlockState());
        world.setBlockAndUpdate(new BlockPos(1, 63, 0), Blocks.FARMLAND.defaultBlockState());
        world.setBlockAndUpdate(new BlockPos(1, 63, 1), Blocks.FARMLAND.defaultBlockState());
        world.setBlockAndUpdate(new BlockPos(0, 63, 1), Blocks.FARMLAND.defaultBlockState());
        world.setBlockAndUpdate(new BlockPos(-1, 64, -1), Blocks.OAK_SAPLING.defaultBlockState());
    }
}

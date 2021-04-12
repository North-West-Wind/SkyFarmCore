package ml.northwestwind.skyfarm.events;


import com.google.common.collect.Maps;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.misc.NoDamageExplosion;
import ml.northwestwind.skyfarm.misc.VoidTeleporter;
import ml.northwestwind.skyfarm.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.packet.message.SLaunchPlayerExplosionPacket;
import ml.northwestwind.skyfarm.world.data.SkyblockNetherData;
import ml.northwestwind.skyfarm.world.generators.SkyblockChunkGenerator;
import ml.northwestwind.skyfarm.world.data.SkyblockData;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID)
public class SkyblockEvents {
    public static final Map<UUID, Integer> buildingSpeed = Maps.newHashMap();

    @SubscribeEvent
    public static void playerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player.level.isClientSide) return;
        ServerWorld world = (ServerWorld) player.getCommandSenderWorld();
        if (!world.dimension().equals(World.OVERWORLD)) return;
        if (SkyblockChunkGenerator.isWorldSkyblock(world)) {
            SkyblockData data = SkyblockData.get(world);
            if (!data.isWorldGenerated()) {
                generateIsland(world);
                data.setWorldGenerated(true);
            }
            if (data.isFirstSpawn(player.getUUID())) {
                player.teleportTo(0.5, 64, 0.5);
                player.setSleepingPos(new BlockPos(0.5, 64, 0.5));
                if (!world.isClientSide()) {
                    MinecraftServer server = world.getServer();
                    Advancement advancement = server.getAdvancements().getAdvancement(new ResourceLocation(SkyFarm.MOD_ID, "skyfarm/root"));
                    if (advancement != null) {
                        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                        serverPlayer.getAdvancements().award(advancement, "JoinGame");
                    }
                }
                data.playerJoin(player);
            }
            data.setDirty();
            world.getDataStorage().set(data);
        }
    }

    @SubscribeEvent
    public static void playerRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player.level.isClientSide) return;
        ServerWorld world = (ServerWorld) player.getCommandSenderWorld();
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

    @SubscribeEvent
    public static void playerTick(final TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        if (player == null || player.level.isClientSide || !SkyblockChunkGenerator.isWorldSkyblock((ServerWorld) player.level)) return;
        ItemStack boots = player.getItemBySlot(EquipmentSlotType.FEET);
        if (boots.getItem().equals(RegistryEvents.Items.OVERWORLD_VOID_SHIFTER_NETHER)) {
            if (player.level.dimension().equals(World.NETHER)) {
                if (player.isCrouching() && player.isOnGround()) {
                    int tick = 0;
                    if (buildingSpeed.containsKey(player.getUUID())) tick = buildingSpeed.get(player.getUUID());
                    tick = Math.min(120, tick + 1);
                    if (tick >= 120) player.displayClientMessage(new TranslationTextComponent("usage.skyfarm.nether_void_shifter"), true);
                    float f = (float)Math.pow(2.0D, (tick - 120D) / 120D);
                    player.level.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_HARP, SoundCategory.PLAYERS, 3f, f);
                    buildingSpeed.put(player.getUUID(), tick);
                } else if (buildingSpeed.containsKey(player.getUUID())) {
                    int tick = buildingSpeed.get(player.getUUID());
                    if (tick >= 120) {
                        player.addEffect(new EffectInstance(Effects.LEVITATION, 20 * 60 * 30, 32, false, false, false));
                        NoDamageExplosion explosion = new NoDamageExplosion(player.level, player.blockPosition(), 3, Explosion.Mode.NONE);
                        explosion.explode();
                        explosion.finalizeExplosion(true);
                        SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SLaunchPlayerExplosionPacket(player.blockPosition(), player.level.dimension()));
                    }
                    buildingSpeed.remove(player.getUUID());
                } else if (player.getY() <= -60) player.teleportTo(player.getX(), 316, player.getZ());
                else if (player.getY() >= 320) {
                    ServerWorld world = ((ServerWorld) player.level).getServer().overworld();
                    player.changeDimension(world, new VoidTeleporter(false));
                }
            } else if (player.level.dimension().equals(World.OVERWORLD)) {
                if (player.getY() <= -60) {
                    ServerWorld world = ((ServerWorld) player.level).getServer().getLevel(World.NETHER);
                    if (world == null) return;
                    player.changeDimension(world, new VoidTeleporter(true));
                } else if (player.isCrouching() && player.hasEffect(Effects.LEVITATION)) player.removeEffect(Effects.LEVITATION);
            }
        } else if (player.getY() <= -60) player.teleportTo(player.getX(), 316, player.getZ());
    }

    @SubscribeEvent
    public static void playerFall(final LivingFallEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!(entity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) entity;
        if (player.level.isClientSide || !SkyblockChunkGenerator.isWorldSkyblock((ServerWorld) player.level)) return;
        if (event.getDistance() <= 4) {
            event.setDamageMultiplier(0);
            return;
        }
        ServerWorld world = (ServerWorld) player.level;
        ServerWorld netherWorld = world.getServer().getLevel(World.NETHER);
        if (netherWorld == null) return;
        SkyblockNetherData data = SkyblockNetherData.get(netherWorld);
        if (world.dimension().equals(World.NETHER) && !data.hasPlayerLanded(player.getUUID())) {
            data.playerLanded(player.getUUID());
            event.setDamageMultiplier(0);
        }
    }

    @SubscribeEvent
    public static void playerChangeDimension(final EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) event.getEntity();
        if (player.level.isClientSide) return;
        ServerWorld world = (ServerWorld) player.level;
        ServerWorld netherWorld = world.getServer().getLevel(World.NETHER);
        if (netherWorld == null) return;
        SkyblockNetherData data = SkyblockNetherData.get(netherWorld);
        if (!event.getDimension().equals(World.NETHER)) data.playerLeft(player.getUUID());
    }

    @SubscribeEvent
    public static void trySpawnPortal(final BlockEvent.PortalSpawnEvent event) {
        IWorld world = event.getWorld();
        if (world.isClientSide()) return;
        if (SkyblockChunkGenerator.isWorldSkyblock((ServerWorld) world)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void playerDamage(final LivingDamageEvent event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        if (player.level.isClientSide || !SkyblockChunkGenerator.isWorldSkyblock((ServerWorld) player.level)) return;
        if (event.getSource().equals(DamageSource.FALL) && event.getAmount() >= player.getMaxHealth() && player.getHealth() == player.getMaxHealth())
            event.setAmount(player.getMaxHealth() - 0.5f);
    }
}

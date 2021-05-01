package ml.northwestwind.skyfarm.events;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.misc.teleporter.HorizontalTeleporter;
import ml.northwestwind.skyfarm.misc.teleporter.VoidTeleporter;
import ml.northwestwind.skyfarm.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.packet.message.SLaunchPlayerExplosionPacket;
import ml.northwestwind.skyfarm.world.data.SkyblockData;
import ml.northwestwind.skyfarm.world.data.SkyblockNetherData;
import ml.northwestwind.skyfarm.world.generators.SkyblockChunkGenerator;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID)
public class SkyblockEvents {
    public static final Map<UUID, Integer> buildingSpeed = Maps.newHashMap();
    public static final Map<UUID, Vector3d> running = Maps.newHashMap();
    public static final List<UUID> rising = Lists.newArrayList();
    public static final RegistryKey<World> UNDERGARDEN = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("undergarden", "undergarden"));
    public static final RegistryKey<World> LOST_CITIES = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("lostcities", "lostcity"));
    public static final RegistryKey<World> TWILIGHT_FOREST = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("twilightforest", "twilightforest"));

    @SubscribeEvent
    public static void playerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player.level.isClientSide) return;
        ServerWorld world = (ServerWorld) player.getCommandSenderWorld();
        if (!world.dimension().equals(World.OVERWORLD)) return;
        if (SkyblockChunkGenerator.isWorldSkyblock(world)) {
            SkyblockData data = SkyblockData.get(world);
            Iterable<String> stages = data.getStages();
            for (ServerPlayerEntity p : world.getServer().getPlayerList().getPlayers())
                for (String stage : stages) {
                    if (!GameStageHelper.isStageKnown(stage)) continue;
                    GameStageHelper.addStage(p, stage);
                }
            if (!data.isWorldGenerated()) {
                generateIsland(world);
                world.setDefaultSpawnPos(BlockPos.ZERO.offset(0, 64, 0), 0);
                data.setWorldGenerated(true);
            }
            if (data.isFirstSpawn(player.getUUID())) {
                player.teleportTo(0.5, 64, 0.5);
                data.playerJoin(player);
            }
            data.setDirty();
            world.getDataStorage().set(data);
        }
    }

    @SubscribeEvent
    public static void playerLeave(final PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getPlayer().level.isClientSide) return;
        if (!SkyblockData.isVoting) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        if (player.getServer() == null) return;
        SkyblockData.cancelVote(player.getServer(), "leave");
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
        if (player == null || player.level.isClientSide || !SkyblockChunkGenerator.isWorldSkyblock((ServerWorld) player.level))
            return;
        ItemStack boots = player.getItemBySlot(EquipmentSlotType.FEET);
        if (boots.getItem().equals(RegistryEvents.Items.OVERWORLD_VOID_SHIFTER_NETHER))
            handleWorldWarp(World.OVERWORLD, World.NETHER, 8, player);
        else if (boots.getItem().equals(RegistryEvents.Items.OVERWORLD_SKY_SHIFTER_END))
            handleWorldWarp(World.END, World.OVERWORLD, 1, player);
        else if (player.getY() <= -64) player.teleportTo(player.getX(), 316, player.getZ());
        else if (player.getY() >= 320) player.teleportTo(player.getX(), -60, player.getZ());

        if (boots.getItem().equals(RegistryEvents.Items.OVERWORLD_AXIS_SHIFTER_UG))
            speedyWorldWarp(World.OVERWORLD, UNDERGARDEN, player);
        else if (boots.getItem().equals(RegistryEvents.Items.OVERWORLD_AXIS_SHIFTER_TF))
            speedyWorldWarp(TWILIGHT_FOREST, World.OVERWORLD, player);
        else if (boots.getItem().equals(RegistryEvents.Items.OVERWORLD_AXIS_SHIFTER_LC))
            speedyWorldWarp(LOST_CITIES, World.OVERWORLD, player);
    }

    private static void handleWorldWarp(RegistryKey<World> top, RegistryKey<World> bottom, double factor, PlayerEntity player) {
        if (player.level.dimension().equals(bottom)) {
            if (player.isCrouching() && player.isOnGround()) {
                int tick = 0;
                if (buildingSpeed.containsKey(player.getUUID())) tick = buildingSpeed.get(player.getUUID());
                tick = Math.min(120, tick + 1);
                if (tick >= 120)
                    player.displayClientMessage(new TranslationTextComponent("usage.skyfarm.void_shifter"), true);
                float f = (float) Math.pow(2.0D, (tick - 120D) / 120D);
                player.level.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_HARP, SoundCategory.PLAYERS, 3f, f);
                buildingSpeed.put(player.getUUID(), tick);
            } else if (buildingSpeed.containsKey(player.getUUID())) {
                int tick = buildingSpeed.get(player.getUUID());
                if (tick >= 120) {
                    SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SLaunchPlayerExplosionPacket(player.blockPosition(), player.level.dimension()));
                    rising.add(player.getUUID());
                }
                buildingSpeed.remove(player.getUUID());
            } else if (player.getY() <= -64) player.teleportTo(player.getX(), 316, player.getZ());
            else if (player.getY() >= 320) {
                ServerWorld world = ((ServerWorld) player.level).getServer().getLevel(top);
                if (world != null) player.changeDimension(world, new VoidTeleporter(false, factor));
                else player.teleportTo(player.getX(), -60, player.getZ());
            }
        } else if (player.level.dimension().equals(top)) {
            if (player.getY() <= -64) {
                ServerWorld world = ((ServerWorld) player.level).getServer().getLevel(bottom);
                if (world != null) {
                    player.changeDimension(world, new VoidTeleporter(true, 1d / factor));
                    if (top.equals(World.END) && bottom.equals(World.OVERWORLD)) {
                        MinecraftServer server = world.getServer();
                        Advancement advancement = server.getAdvancements().getAdvancement(new ResourceLocation("minecraft", "end/jump_back"));
                        if (advancement != null) ((ServerPlayerEntity) player).getAdvancements().award(advancement, "toOverworld");
                    }
                } else player.teleportTo(player.getX(), 316, player.getZ());
            }
        }
        if (rising.contains(player.getUUID())) {
            if (player.isCrouching()) rising.remove(player.getUUID());
            player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0, 1).add(0, 2, 0));
            ((ServerPlayerEntity) player).connection.send(new SEntityVelocityPacket(player));
        }
    }

    private static void speedyWorldWarp(RegistryKey<World> dim1, RegistryKey<World> dim2, PlayerEntity player) {
        if (!player.level.dimension().equals(dim1) && !player.level.dimension().equals(dim2)) return;
        if ((player.isCrouching() || !player.isOnGround()) && running.containsKey(player.getUUID())) {
            player.displayClientMessage(new TranslationTextComponent("cancelled.skyfarm.void_shifter").setStyle(Style.EMPTY.applyFormat(TextFormatting.RED)), true);
            running.remove(player.getUUID());
            return;
        }
        if (player.isCrouching() && player.isOnGround()) {
            int tick = 0;
            if (buildingSpeed.containsKey(player.getUUID())) tick = buildingSpeed.get(player.getUUID());
            tick = Math.min(120, tick + 1);
            if (tick >= 120)
                player.displayClientMessage(new TranslationTextComponent("usage.skyfarm.void_shifter"), true);
            float f = (float) Math.pow(2.0D, (tick - 120D) / 120D);
            player.level.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_HARP, SoundCategory.PLAYERS, 3f, f);
            buildingSpeed.put(player.getUUID(), tick);
        } else if (buildingSpeed.containsKey(player.getUUID())) {
            int tick = buildingSpeed.get(player.getUUID());
            if (tick >= 120) {
                SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SLaunchPlayerExplosionPacket(player.blockPosition(), player.level.dimension()));
                running.put(player.getUUID(), player.position());
            }
            buildingSpeed.remove(player.getUUID());
        } else if (running.containsKey(player.getUUID())) {
            player.setDeltaMovement(player.getDeltaMovement().add(player.getLookAngle()));
            ((ServerPlayerEntity) player).connection.send(new SEntityVelocityPacket(player));
            if (player.position().distanceTo(running.get(player.getUUID())) >= 100) {
                if (player.level.dimension().equals(dim1)) {
                    ServerWorld world = ((ServerWorld) player.level).getServer().getLevel(dim2);
                    if (world != null) player.changeDimension(world, new HorizontalTeleporter());
                } else if (player.level.dimension().equals(dim2)) {
                    ServerWorld world = ((ServerWorld) player.level).getServer().getLevel(dim1);
                    if (world != null) player.changeDimension(world, new HorizontalTeleporter());
                }
                running.remove(player.getUUID());
            }
        }
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
        else if (event.getSource().equals(DamageSource.OUT_OF_WORLD) || event.getSource().equals(DamageSource.IN_WALL)) event.setCanceled(true);
    }
}

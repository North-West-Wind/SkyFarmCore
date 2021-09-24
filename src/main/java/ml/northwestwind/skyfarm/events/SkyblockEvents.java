package ml.northwestwind.skyfarm.events;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import ml.northwestwind.skyfarm.common.world.data.SkyblockNetherData;
import ml.northwestwind.skyfarm.common.world.generators.SkyblockChunkGenerator;
import ml.northwestwind.skyfarm.config.SkyFarmConfig;
import ml.northwestwind.skyfarm.misc.CuriosStuff;
import ml.northwestwind.skyfarm.misc.KeyBindings;
import ml.northwestwind.skyfarm.misc.teleporter.HorizontalTeleporter;
import ml.northwestwind.skyfarm.misc.teleporter.VoidTeleporter;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.*;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static ml.northwestwind.skyfarm.events.RegistryEvents.Dimensions.*;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID)
public class SkyblockEvents {
    public static final Map<UUID, Integer> buildingSpeed = Maps.newHashMap();
    public static final Map<UUID, Vector3d> running = Maps.newHashMap();
    public static final List<UUID> rising = Lists.newArrayList();

    @SubscribeEvent
    public static void playerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player.level.isClientSide) {
            player.sendMessage(new TranslationTextComponent(
                    "tip.skyfarm.stageMenu",
                    ((IFormattableTextComponent) KeyBindings.stageMenu.getTranslatedKeyMessage()).withStyle(TextFormatting.AQUA)), Util.NIL_UUID);
            return;
        }
        ServerWorld world = (ServerWorld) player.getCommandSenderWorld();
        if (!world.dimension().equals(World.OVERWORLD)) return;
        if (SkyblockChunkGenerator.isWorldSkyblock(world)) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            SkyblockData data = SkyblockData.get(world);
            if (!data.isWorldGenerated()) {
                generateIsland(world, new BlockPos(0, 64, 0));
                world.setDefaultSpawnPos(new BlockPos(0, 64, 0), 0);
                data.setWorldGenerated(true);
                data.addIsland(player.getUUID(), new BlockPos(0, 64, 0));
            }
            if (data.isFirstSpawn(player.getUUID())) firstSpawn(serverPlayer, data);
            if (SkyFarmConfig.GLOBAL_STAGE.get()) syncStages(serverPlayer, data.getGlobalStages());
            else {
                String team = data.getTeam(player.getUUID());
                if (team != null) syncStages(serverPlayer, data.getStages(team));
            }
            if (SkyFarmConfig.HIDE_ADVANCEMENT.get()) hideAdvancements(serverPlayer, world.getServer());
            data.setDirty();
        }
    }

    private static void firstSpawn(ServerPlayerEntity player, SkyblockData data) {
        data.playerJoin(player);
        if (!data.hasIsland(player.getUUID())) {
            if (SkyFarmConfig.ALLOW_SEEK_ISLAND.get()) {
                player.sendMessage(getCreateIslandTips(), ChatType.SYSTEM, Util.NIL_UUID);
                player.teleportTo(0.5, 64, 0.5);
            } else {
                int offset = SkyFarmConfig.ISLAND_OFFSET.get();
                BlockPos pos = data.findPosForNewIsland(offset);
                generateIsland(player.getLevel(), pos);
                data.addIsland(player.getUUID(), pos);
                player.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            }
        } else player.teleportTo(0.5, 64, 0.5);
    }

    private static void syncStages(ServerPlayerEntity player, Iterable<String> stages) {
        for (String stage : stages) {
            if (!GameStageHelper.isStageKnown(stage)) continue;
            GameStageHelper.addStage(player, stage);
        }
        GameStageHelper.syncPlayer(player);
    }

    private static void hideAdvancements(ServerPlayerEntity player, MinecraftServer server) {
        Set<Advancement> advancements = server.getAdvancements().getAllAdvancements().stream().filter(adv ->
                !adv.getId().getNamespace().equals("skyfarm") && (adv.getDisplay() == null || !adv.getDisplay().isHidden()) && adv.getParent() == null
        ).collect(Collectors.toSet());
        player.getAdvancements().visible.removeAll(advancements);
        player.getAdvancements().visibilityChanged.addAll(advancements);
    }

    @SubscribeEvent
    public static void playerLeave(final PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getPlayer().level.isClientSide) return;
        if (!SkyblockData.isVoting) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        if (player.getServer() == null) return;
        SkyblockData.cancelVote(player.getServer(), "leave");
        SkyblockData data = SkyblockData.get(player.getLevel());
        data.cancelRequest(player.getUUID());
    }

    public static void generateIsland(World world, BlockPos center) {
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                BlockPos pos = center.offset(i, -1, j);
                world.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState());
            }
        }
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                BlockPos pos = center.offset(i, -2, j);
                world.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
            }
        }
        world.setBlockAndUpdate(center.offset(0, -1, 0), Blocks.WATER.defaultBlockState());
        world.setBlockAndUpdate(center.offset(1, -1, 0), Blocks.FARMLAND.defaultBlockState());
        world.setBlockAndUpdate(center.offset(1, -1, 1), Blocks.FARMLAND.defaultBlockState());
        world.setBlockAndUpdate(center.offset(0, -1, 1), Blocks.FARMLAND.defaultBlockState());
        world.setBlockAndUpdate(center.offset(-1, 0, -1), Blocks.OAK_SAPLING.defaultBlockState());
    }

    @SubscribeEvent
    public static void playerTick(final TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        if (event.phase.equals(TickEvent.Phase.START) || player == null || player.level.isClientSide || !SkyblockChunkGenerator.isWorldSkyblock((ServerWorld) player.level))
            return;
        ItemStack boots = player.getItemBySlot(EquipmentSlotType.FEET);
        if (ModList.get().isLoaded("curios")) boots = CuriosStuff.playerTick(event, boots);
        if (boots.getItem().equals(RegistryEvents.Items.OVERWORLD_VOID_SHIFTER_NETHER))
            handleWorldWarp(World.OVERWORLD, World.NETHER, 8, player);
        else if (boots.getItem().equals(RegistryEvents.Items.OVERWORLD_SKY_SHIFTER_END))
            handleWorldWarp(World.END, World.OVERWORLD, 1, player);
        else if (boots.getItem().equals(RegistryEvents.Items.OVERWORLD_SKY_SHIFTER_ASTEROIDS))
            handleWorldWarp(RegistryEvents.Dimensions.ASTEROIDS, World.OVERWORLD, 1, player);
        else if (player.getY() <= -64) player.teleportTo(player.getX(), 316, player.getZ());
        else if (player.getY() >= 320) player.teleportTo(player.getX(), -60, player.getZ());

        if (boots.getItem().equals(RegistryEvents.Items.OVERWORLD_AXIS_SHIFTER_UG) && ModList.get().isLoaded("undergarden"))
            speedyWorldWarp(World.OVERWORLD, UNDERGARDEN, player);
        else if (boots.getItem().equals(RegistryEvents.Items.OVERWORLD_AXIS_SHIFTER_TF) && ModList.get().isLoaded("twilightforest"))
            speedyWorldWarp(TWILIGHT_FOREST, World.OVERWORLD, player);
        else if (boots.getItem().equals(RegistryEvents.Items.OVERWORLD_AXIS_SHIFTER_LC) && ModList.get().isLoaded("lostcities"))
            speedyWorldWarp(LOST_CITIES, World.OVERWORLD, player);

        if (player.level.dimension().equals(World.NETHER)) {
            ServerWorld nether = player.getServer().getLevel(World.NETHER);
            if (nether == null) return;
            SkyblockNetherData data = SkyblockNetherData.get(nether);
            if (!data.isPlayerShielded(player.getUUID())) return;
            List<Entity> collided = player.level.getEntities(player, new AxisAlignedBB(player.blockPosition()).inflate(5), entity -> !(entity instanceof PlayerEntity) && !entity.isSpectator());
            collided.forEach(entity -> entity.setDeltaMovement(entity.position().subtract(player.position()).normalize()));
            data.minusTick((ServerPlayerEntity) player);
        }
    }

    public static void handleWorldWarp(RegistryKey<World> top, RegistryKey<World> bottom, double factor, PlayerEntity player) {
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
                    player.level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundCategory.PLAYERS, 1f, 1f);
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
                        Advancement advancement = server.getAdvancements().getAdvancement(new ResourceLocation("skyfarm", "end/jump_back"));
                        if (advancement != null)
                            ((ServerPlayerEntity) player).getAdvancements().award(advancement, "toOverworld");
                    }
                } else player.teleportTo(player.getX(), 316, player.getZ());
            }
        }
        if (rising.contains(player.getUUID())) {
            player.fallDistance = 0;
            if (player.isCrouching()) rising.remove(player.getUUID());
            player.setDeltaMovement(player.getDeltaMovement().multiply(3, 0, 3).add(0, 1, 0));
            ((ServerPlayerEntity) player).connection.send(new SEntityVelocityPacket(player));
        }
    }

    public static void speedyWorldWarp(RegistryKey<World> dim1, RegistryKey<World> dim2, PlayerEntity player) {
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
                player.level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundCategory.PLAYERS, 1f, 1f);
                running.put(player.getUUID(), player.position());
            }
            buildingSpeed.remove(player.getUUID());
        } else if (running.containsKey(player.getUUID())) {
            player.setDeltaMovement(player.getDeltaMovement().add(player.getLookAngle()));
            ((ServerPlayerEntity) player).connection.send(new SEntityVelocityPacket(player));
            if (player.position().distanceTo(running.get(player.getUUID())) >= 50) {
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
            event.setCanceled(true);
            return;
        }
        ServerWorld world = (ServerWorld) player.level;
        ServerWorld netherWorld = world.getServer().getLevel(World.NETHER);
        if (netherWorld == null) return;
        SkyblockNetherData data = SkyblockNetherData.get(netherWorld);
        if (world.dimension().equals(World.NETHER) && data.isPlayerShielded(player.getUUID())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void playerChangeDimension(final EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity) || !event.getDimension().equals(World.NETHER)) return;
        PlayerEntity player = (PlayerEntity) event.getEntity();
        if (player.level.isClientSide) return;
        ServerWorld world = (ServerWorld) player.level;
        ServerWorld netherWorld = world.getServer().getLevel(World.NETHER);
        if (netherWorld == null) return;
        SkyblockNetherData data = SkyblockNetherData.get(netherWorld);
        data.playerEntered(player.getUUID());
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
        else if (event.getSource().equals(DamageSource.OUT_OF_WORLD) || event.getSource().equals(DamageSource.IN_WALL))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onPlayerInteract(final PlayerInteractEvent.RightClickBlock event) {
        ItemStack equipped = event.getItemStack();
        PlayerEntity player = event.getPlayer();
        if (!equipped.isEmpty() && equipped.getItem() == Items.BOWL) {
            BlockRayTraceResult rtr = raytraceFromEntity(player, 4.5F, true);
            if (rtr.getType() == RayTraceResult.Type.BLOCK) {
                BlockPos pos = rtr.getBlockPos();
                if (event.getWorld().getBlockState(pos).getMaterial() == Material.WATER) {
                    if (!event.getWorld().isClientSide) {
                        equipped.shrink(1);
                        Item waterBowl = ForgeRegistries.ITEMS.getValue(new ResourceLocation("botania", "water_bowl"));
                        if (equipped.isEmpty()) player.setItemInHand(event.getHand(), new ItemStack(waterBowl));
                        else player.inventory.placeItemBackInInventory(player.level, new ItemStack(waterBowl));
                    }

                    event.setCanceled(true);
                    event.setCancellationResult(ActionResultType.SUCCESS);
                }
            }
        }
    }

    public static BlockRayTraceResult raytraceFromEntity(Entity e, double distance, boolean fluids) {
        return (BlockRayTraceResult) e.pick(distance, 1, fluids);
    }

    private static ITextComponent getCreateIslandTips() {
        return new TranslationTextComponent("tip.skyfarm.firstTime")
                .append(new TranslationTextComponent("tip.skyfarm.seekIsland", new StringTextComponent("/seekIsland").withStyle(TextFormatting.GOLD)))
                .append(new TranslationTextComponent("tip.skyfarm.createIsland", new StringTextComponent("/createIsland").withStyle(TextFormatting.GOLD)));
    }

    @SubscribeEvent
    public static void livingDrop(final LivingDropsEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.level.isClientSide) return;
        if (entity.getType().equals(EntityType.WITHER_SKELETON) && entity.level.random.nextInt(3) == 0) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("iceandfire", "witherbone"));
            if (item == null) return;
            event.getDrops().add(new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(item)));
        }
    }
}

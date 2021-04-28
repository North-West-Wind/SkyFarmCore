package ml.northwestwind.skyfarm.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Utils {
    public static final long[] AGE_TICKS = {1200000, 1800000, 2400000};
    public static final int[] MULTIPLIER = {10, 8, 6};

    public static Set<IRecipe<?>> findRecipesByType(IRecipeType<?> typeIn, World world) {
        return world != null ? world.getRecipeManager().getRecipes().stream()
                .filter(recipe -> recipe.getType() == typeIn).collect(Collectors.toSet()) : Collections.emptySet();
    }

    public static String formatDuration(long ticks) {
        long seconds = ticks / 20;
        if (seconds > 3600) {
            long hours = seconds / 3600;
            seconds %= 3600;
            long minutes = seconds / 60;
            seconds %= 60;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            long minutes = seconds / 60;
            seconds %= 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public static Vector3d blockPosToVector3d(BlockPos pos) {
        return new Vector3d(pos.getX(), pos.getY(), pos.getZ());
    }

    @Nonnull
    public static Item getItemByModAndName(String modid, String name) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(modid, name));
        return item == null ? Items.AIR : item;
    }

    public static Entity summonDragon(DragonType type, ServerWorld world, BlockPos pos) {
        if (!ModList.get().isLoaded("iceandfire")) return null;
        EntityType<?> dragon = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("iceandfire", type.name));
        if (dragon == null) return null;
        int index = world.random.nextInt(AGE_TICKS.length);
        long ticks = AGE_TICKS[index];
        int color = world.random.nextInt(4);
        int gender = world.random.nextInt(2) + 1;
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong("AgeTicks", ticks);
        nbt.putInt("Variant", color);
        nbt.putInt("Gender", gender);
        LivingEntity entity = (LivingEntity) dragon.spawn(world, nbt, null, null, pos, SpawnReason.MOB_SUMMONED, true, true);
        if (entity == null) return null;
        ModifiableAttributeInstance attribute = entity.getAttributes().getInstance(Attributes.MAX_HEALTH);
        if (attribute != null) {
            AttributeModifier modifier = new AttributeModifier(UUID.randomUUID().toString(), MULTIPLIER[index], AttributeModifier.Operation.MULTIPLY_BASE);
            attribute.removeModifier(modifier);
            attribute.addPermanentModifier(new AttributeModifier(modifier.getId(), "", modifier.getAmount(), modifier.getOperation()));
        }
        return entity;
    }

    public static void overrideServerConfig(MinecraftServer server) throws IOException {
        if (!new File("./skyfarm/serverconfig").exists()) {
            LogManager.getLogger().info("Did not find default serverconfig. Cancelled replacing.");
            return;
        }
        File world = server.getWorldPath(FolderName.ROOT).toFile();
        if (!world.exists()) return;
        File serverConfig = new File(world.getAbsolutePath() + File.separator + "serverconfig");
        if (serverConfig.exists()) {
            LogManager.getLogger().info("Deleting serverconfig of " + world.getName());
            FileUtils.deleteDirectory(serverConfig);
        }
        LogManager.getLogger().info("Copying default serverconfig to world " + world.getName());
        FileUtils.copyDirectory(new File("./skyfarm/serverconfig"), serverConfig);
    }

    public enum DragonType {
        FIRE("fire_dragon"),
        ICE("ice_dragon"),
        LIGHTNING("lightning_dragon");

        final String name;

        DragonType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

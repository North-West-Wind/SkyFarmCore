package ml.northwestwind.skyfarm.misc;

import mekanism.api.chemical.gas.GasStack;
import mekanism.api.inventory.IgnoredIInventory;
import mekanism.api.recipes.GasToGasRecipe;
import mekanism.api.recipes.inputs.chemical.GasStackIngredient;
import ml.northwestwind.skyfarm.SkyFarm;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {
    public static final long[] AGE_TICKS = {1200000, 1800000, 2400000};
    public static final int[] MULTIPLIER = {10, 8, 6};
    private static final int BUFFER_SIZE = 4096;

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

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(SkyFarm.MOD_ID, name);
    }

    public static boolean isVersionGreater(String first, String second) {
        String[] firsts = first.split("\\.");
        String[] seconds = second.split("\\.");
        for (int i = 0; i < Math.min(firsts.length, seconds.length); i++) {
            if (Integer.parseInt(firsts[i]) > Integer.parseInt(seconds[i])) return true;
        }
        return firsts.length > seconds.length;
    }

    public static String downloadFile(String fileURL, String saveDir) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();
        String saveFilePath = null;
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");

            if (disposition != null) {
                int index = disposition.indexOf("filename=");
                if (index > 0) fileName = disposition.substring(index + 10, disposition.length() - 1);
            } else fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
            InputStream inputStream = httpConn.getInputStream();
            saveFilePath = saveDir + File.separator + fileName;
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) outputStream.write(buffer, 0, bytesRead);

            outputStream.close();
            inputStream.close();
        } else SkyFarm.LOGGER.info("No file to download. Server replied HTTP code: " + responseCode);
        httpConn.disconnect();
        return saveFilePath;
    }

    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) destDir.mkdir();
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            File dir = new File(filePath);
            FileUtils.deleteDirectory(dir);
            if (!entry.isDirectory()) extractFile(zipIn, filePath);
            else dir.mkdirs();
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) bos.write(bytesIn, 0, read);
        bos.close();
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

    public static class GTGRecipe extends GasToGasRecipe {
        public GTGRecipe(ResourceLocation id, GasStackIngredient input, GasStack output) {
            super(id, input, output);
        }

        @Override
        public ItemStack assemble(IgnoredIInventory p_77572_1_) {
            return null;
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return null;
        }

        @Override
        public IRecipeType<?> getType() {
            return null;
        }
    }
}

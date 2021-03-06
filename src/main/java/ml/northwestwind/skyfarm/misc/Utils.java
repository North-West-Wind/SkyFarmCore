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
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Utils {
    public static final long[] AGE_TICKS = {1800000, 2400000, 3000000};
    public static final int[] MULTIPLIER = {10, 8, 6};
    private static final Random RNG = new Random();

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

    public static Vector3d wrapToEdge(Vector3d pos, boolean top) {
        return pos.multiply(1, 0, 1).add(0, top ? 316 : -60, 0);
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
            AttributeModifier modifier = new AttributeModifier(UUID.randomUUID().toString(), ticks * 2 / 100000, AttributeModifier.Operation.MULTIPLY_BASE);
            attribute.removeModifier(modifier);
            attribute.addPermanentModifier(new AttributeModifier(modifier.getId(), "", modifier.getAmount(), modifier.getOperation()));
        }
        return entity;
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(SkyFarm.MOD_ID, name);
    }

    public static <K, V> V getRandomValueFromMap(Map<K, V> map) {
        Object[] values = map.values().toArray();
        return (V) values[RNG.nextInt(values.length)];
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

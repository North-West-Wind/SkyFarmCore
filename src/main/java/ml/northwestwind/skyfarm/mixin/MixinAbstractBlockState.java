package ml.northwestwind.skyfarm.mixin;

import com.google.common.collect.Lists;
import ml.northwestwind.skyfarm.SkyFarm;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Mixin(value = AbstractBlock.AbstractBlockState.class)
public abstract class MixinAbstractBlockState {
    @Shadow public abstract Block getBlock();

    private static final Random rng = new Random();
    private static final String[] BLACKLIST = {"nether_star_seeds", "dragon_egg_seeds", "nitro_crystal_seeds"};
    private static final Method GET_SEEDS = ObfuscationReflectionHelper.findMethod(CropsBlock.class, "func_199772_f");

    @Inject(at = @At("RETURN"), method = "getDrops", cancellable = true)
    public void getDrops(LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (!(this.getBlock() instanceof CropsBlock) || !this.getBlock().getRegistryName().getNamespace().equals("mysticalagriculture") || Arrays.asList(BLACKLIST).contains(this.getBlock().getRegistryName().getPath())) return;
        if (!((CropsBlock) this.getBlock()).isMaxAge((BlockState) (Object) this)) return;
        if (rng.nextDouble() < 0.2) {
            List<ItemStack> drops = cir.getReturnValue();
            List<ItemStack> newDrops = Lists.newArrayList();
            Item seeds = getSeed(this.getBlock());
            if (seeds == null) return;
            for (ItemStack stack : drops) {
                if (!stack.getItem().equals(seeds)) {
                    newDrops.add(stack);
                    continue;
                }
                stack.grow(1);
                newDrops.add(stack);
            }
            cir.setReturnValue(newDrops);
        }
    }

    private static Item getSeed(Block block) {
        try {
            return (Item) GET_SEEDS.invoke(block);
        } catch (Exception e) {
            SkyFarm.LOGGER.error("Failed to get seed from crop {}", e.getLocalizedMessage());
        }

        return null;
    }
}

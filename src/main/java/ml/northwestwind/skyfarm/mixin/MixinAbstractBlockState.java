package ml.northwestwind.skyfarm.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.CropsBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class MixinAbstractBlockState {
    @Shadow public abstract Block getBlock();

    private static final Random rng = new Random();
    private static final String[] BLACKLIST = {"nether_star_seeds", "dragon_egg_seeds", "nitro_crystal_seeds"};

    @Inject(at = @At("RETURN"), method = "getDrops", cancellable = true)
    public void getDrops(LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (!(this.getBlock() instanceof CropsBlock) || !this.getBlock().getRegistryName().getNamespace().equals("mysticalagriculture") || Arrays.asList(BLACKLIST).contains(this.getBlock().getRegistryName().getPath())) return;
        List<ItemStack> drops = cir.getReturnValue();
        if (rng.nextDouble() < 0.2) {
            drops.add(new ItemStack(this.getBlock()));
            LogManager.getLogger().info("Added secondary seed");
        }
        drops.forEach((stack -> {
            LogManager.getLogger().info(stack);
        }));
        cir.setReturnValue(drops);
    }
}

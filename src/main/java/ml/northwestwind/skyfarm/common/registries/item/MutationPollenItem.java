package ml.northwestwind.skyfarm.common.registries.item;

import ml.northwestwind.skyfarm.SkyFarm;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class MutationPollenItem extends TooltipItem {
    public MutationPollenItem(Properties properties, String registryName) {
        super(properties, registryName);
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity living, Hand hand) {
        if (!ModList.get().isLoaded("resourcefulbees") || !living.getType().equals(EntityType.BEE)) return super.interactLivingEntity(stack, player, living, hand);
        CompoundNBT nbt = stack.getOrCreateTag();
        String type = nbt.getString("Type");
        if (!SkyFarm.BEE_TYPES.containsKey(type)) return super.interactLivingEntity(stack, player, living, hand);
        EntityType<?> eType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("resourcefulbees", type+"_bee"));
        if (eType == null) return super.interactLivingEntity(stack, player, living, hand);
        if (player.level.isClientSide) return ActionResultType.CONSUME;
        Entity entity = eType.spawn((ServerWorld) player.level, null, null, living.blockPosition(), SpawnReason.TRIGGERED, true, true);
        if (entity == null) return super.interactLivingEntity(stack, player, living, hand);
        player.level.playSound(null, player.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 1, 1);
        living.remove();
        return ActionResultType.SUCCESS;
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> stacks) {
        if (this.allowdedIn(group)) {
            for (String type : SkyFarm.BEE_TYPES.keySet()) {
                ItemStack stack = new ItemStack(this);
                stack.getOrCreateTag().putString("Type", type);
                stacks.add(stack);
            }
        }
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        if (!stack.hasTag() || !SkyFarm.BEE_TYPES.containsKey(stack.getTag().getString("Type"))) return new TranslationTextComponent("item.skyfarm."+this.registryName+".default");
        return new TranslationTextComponent("item.skyfarm."+this.registryName,
                new TranslationTextComponent("entity.resourcefulbees."+stack.getTag().getString("Type")+"_bee"));
    }
}

package ml.northwestwind.skyfarm.item;

import ml.northwestwind.skyfarm.SkyFarm;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
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
        if (!ModList.get().isLoaded("resourcefulbees") || !(living instanceof BeeEntity)) return super.interactLivingEntity(stack, player, living, hand);
        CompoundNBT nbt = stack.getOrCreateTag();
        String type = nbt.getString("Type");
        if (!SkyFarm.BEE_TYPES.containsKey(type)) return super.interactLivingEntity(stack, player, living, hand);
        EntityType<?> eType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("resourcefulbees", type+"_bee"));
        if (eType == null) return super.interactLivingEntity(stack, player, living, hand);
        if (player.level.isClientSide) return ActionResultType.CONSUME;
        Entity entity = eType.spawn((ServerWorld) player.level, null, null, living.blockPosition(), SpawnReason.TRIGGERED, true, true);
        if (entity == null) return super.interactLivingEntity(stack, player, living, hand);
        living.remove();
        return ActionResultType.SUCCESS;
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> stacks) {
        if (this.allowdedIn(group)) {
            for (String type : SkyFarm.BEE_TYPES.keySet()) {
                CompoundNBT nbt = new CompoundNBT();
                nbt.putString("Type", type);
                stacks.add(new ItemStack(this, 1, nbt));
            }
        }
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        if (!stack.hasTag() || !SkyFarm.BEE_TYPES.containsKey(stack.getTag().getString("Type"))) return new TranslationTextComponent("item.skyfarm."+this.registryName+".default");
        return new TranslationTextComponent("item.skyfarm."+this.registryName,
                new TranslationTextComponent("entity.resourcefulbees."+stack.getTag().getString("Type")+"_bee").getString().replace(
                        " "+new TranslationTextComponent("entity.minecraft.bee").getString(), ""
                ));
    }
}

package ml.northwestwind.skyfarm.common.registries.entity;

import ml.northwestwind.skyfarm.common.recipes.CompactBrickRecipe;
import ml.northwestwind.skyfarm.common.registries.tile.handler.SkyFarmItemHandler;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import ml.northwestwind.skyfarm.misc.NoDamageExplosion;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nullable;
import java.util.List;

public class CompactBrickEntity extends ProjectileItemEntity {

    public CompactBrickEntity(EntityType<CompactBrickEntity> type, World world) {
        super(type, world);
    }

    public CompactBrickEntity(LivingEntity entity, World world) {
        super(RegistryEvents.EntityTypes.COMPACT_BRICK, entity, world);
    }

    @Override
    public void setOwner(@Nullable Entity entityIn) {
        super.setOwner(entityIn);
    }

    @Override
    protected void onHit(RayTraceResult result) {
        super.onHit(result);
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove();
        }
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        entity.hurt(DamageSource.thrown(this, this.getOwner()), 2f);
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult result) {
        BlockPos pos = result.getBlockPos();
        BlockState state = level.getBlockState(pos);
        CompactBrickRecipe recipe = this.getRecipe(new ItemStack(state.getBlock()));
        if (recipe != null) {
            explode(pos);
            if (!level.isClientSide) {
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                if (random.nextDouble() < recipe.getChance()) level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), recipe.getResultItem()));
            }
        }
        super.onHitBlock(result);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected Item getDefaultItem() {
        return RegistryEvents.Items.COMPACT_BRICK;
    }

    public Explosion explode(BlockPos pos) {
        NoDamageExplosion explosion = new NoDamageExplosion(level, pos, 1, Explosion.Mode.NONE);
        if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(level, explosion)) return explosion;
        explosion.explode();
        explosion.finalizeExplosion(true);
        return explosion;
    }

    @Nullable
    private CompactBrickRecipe getRecipe(ItemStack stack) {
        if (stack == null || this.getServer() == null) return null;
        SkyFarmItemHandler fakeInv = new SkyFarmItemHandler(1, stack);
        List<IRecipe<IInventory>> recipes = this.getServer().getRecipeManager().getAllRecipesFor(RegistryEvents.Recipes.COMPACT_BRICK.getType());
        for (IRecipe<?> iRecipe : recipes) {
            CompactBrickRecipe recipe = (CompactBrickRecipe) iRecipe;
            if (recipe.matches(new RecipeWrapper(fakeInv), this.level)) return recipe;
        }
        return null;
    }
}


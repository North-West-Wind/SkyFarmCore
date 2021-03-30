package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.block.NaturalEvaporatorBlock;
import ml.northwestwind.skyfarm.recipes.EvaporatingRecipe;
import ml.northwestwind.skyfarm.recipes.IEvaporatingRecipe;
import ml.northwestwind.skyfarm.recipes.serializer.EvaporatingRecipeSerializer;
import ml.northwestwind.skyfarm.tile.NaturalEvaporatorTileEntity;
import ml.northwestwind.skyfarm.world.SkyblockWorldType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(SkyFarm.MOD_ID)
public class RegistryEvents {

    @SubscribeEvent
    public static void registerWorldType(final RegistryEvent.Register<ForgeWorldType> event) {
        event.getRegistry().register(SkyblockWorldType.INSTANCE.setRegistryName("skyfarm"));
    }

    @SubscribeEvent
    public static void registerBlock(final RegistryEvent.Register<Block> event) {
        event.getRegistry().register(SkyFarmBlocks.NATURAL_EVAPORATOR);
    }

    @SubscribeEvent
    public static void registerItem(final RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new BlockItem(SkyFarmBlocks.NATURAL_EVAPORATOR, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE).stacksTo(64)).setRegistryName("natural_evaporator"));
    }

    @SubscribeEvent
    public static void registerTileEntityType(final RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(SkyFarmTileEntityTypes.NATURAL_EVAPORATOR);
    }

    @SubscribeEvent
    public static void registerRecipeSerializer(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().register(SkyFarmRecipeSerializers.EVAPORATING_SERIALIZER);
    }

    public static class SkyFarmBlocks {
        public static final Block NATURAL_EVAPORATOR = new NaturalEvaporatorBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F).sound(SoundType.WOOD).noOcclusion()).setRegistryName("natural_evaporator");
    }

    public static class SkyFarmTileEntityTypes {
        public static final TileEntityType<NaturalEvaporatorTileEntity> NATURAL_EVAPORATOR = (TileEntityType<NaturalEvaporatorTileEntity>) TileEntityType.Builder.of(NaturalEvaporatorTileEntity::new, SkyFarmBlocks.NATURAL_EVAPORATOR).build(null).setRegistryName("natural_evaporator");
    }

    public static class SkyFarmRecipeSerializers {
        public static final IRecipeSerializer<EvaporatingRecipe> EVAPORATING_SERIALIZER = (IRecipeSerializer<EvaporatingRecipe>) new EvaporatingRecipeSerializer().setRegistryName("evaporating");
        public static final IRecipeType<IEvaporatingRecipe> EVAPORATING_TYPE = registerType(IEvaporatingRecipe.RECIPE_TYPE_ID);

        private static <T extends IRecipeType<?>> T registerType(ResourceLocation recipeTypeId) {
            return (T)  Registry.register(Registry.RECIPE_TYPE, recipeTypeId, new RecipeType<>());
        }

        private static class RecipeType<T extends IRecipe<?>> implements IRecipeType<T> {
            @Override
            public String toString() {
                return Registry.RECIPE_TYPE.getKey(this).toString();
            }
        }
    }
}

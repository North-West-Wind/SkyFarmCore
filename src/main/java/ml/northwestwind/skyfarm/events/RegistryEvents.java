package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.block.NaturalEvaporatorBlock;
import ml.northwestwind.skyfarm.block.ParaboxBlock;
import ml.northwestwind.skyfarm.block.VoidGeneratorBlock;
import ml.northwestwind.skyfarm.container.ParaboxContainer;
import ml.northwestwind.skyfarm.entity.CompactBrickEntity;
import ml.northwestwind.skyfarm.item.CompactBrickItem;
import ml.northwestwind.skyfarm.item.StoneVariatorItem;
import ml.northwestwind.skyfarm.item.TooltipBlockItem;
import ml.northwestwind.skyfarm.item.WaterBowlItem;
import ml.northwestwind.skyfarm.recipes.AbstractEvaporatingRecipe;
import ml.northwestwind.skyfarm.recipes.serializer.EvaporatingRecipeSerializer;
import ml.northwestwind.skyfarm.tile.NaturalEvaporatorTileEntity;
import ml.northwestwind.skyfarm.tile.ParaboxTileEntity;
import ml.northwestwind.skyfarm.tile.VoidGeneratorTileEntity;
import ml.northwestwind.skyfarm.world.SkyblockWorldType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.function.Predicate;
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
        event.getRegistry().registerAll(
                Blocks.NATURAL_EVAPORATOR,
                Blocks.PARABOX,
                Blocks.VOID_GENERATOR
        );
        RenderTypeLookup.setRenderLayer(Blocks.PARABOX, RenderType.cutoutMipped());
        RenderTypeLookup.setRenderLayer(Blocks.VOID_GENERATOR, RenderType.cutoutMipped());
    }

    @SubscribeEvent
    public static void registerItem(final RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new TooltipBlockItem(Blocks.NATURAL_EVAPORATOR, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE).stacksTo(64), "natural_evaporator"),
                new TooltipBlockItem(Blocks.PARABOX, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE).stacksTo(1), "parabox"),
                new TooltipBlockItem(Blocks.VOID_GENERATOR, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE).stacksTo(1).rarity(Rarity.EPIC), "void_generator"),
                Items.COMPACT_BRICK,
                Items.WATER_BOWL,
                Items.BOWL,
                Items.STONE_VARIATOR,
                Items.OVERWORLD_VOID_SHIFTER_NETHER,
                Items.OVERWORLD_SKY_SHIFTER_END
        );
        if (ModList.get().isLoaded("undergarden")) event.getRegistry().register(Items.OVERWORLD_VOID_SHIFTER_UG);
        if (ModList.get().isLoaded("twilightforest")) event.getRegistry().register(Items.OVERWORLD_SKY_SHIFTER_TF);
        if (ModList.get().isLoaded("lostcities")) event.getRegistry().register(Items.OVERWORLD_SKY_SHIFTER_LC);
    }

    @SubscribeEvent
    public static void registerTileEntityType(final RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                TileEntityTypes.NATURAL_EVAPORATOR,
                TileEntityTypes.PARABOX,
                TileEntityTypes.VOID_GENERATOR
        );
    }

    @SubscribeEvent
    public static void registerRecipeSerializer(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
        Recipes.EVAPORATING.register(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerEntityType(final RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(EntityTypes.COMPACT_BRICK);
    }

    @SubscribeEvent
    public static void registerContainerType(final RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().registerAll(
                ContainerTypes.PARABOX
        );
    }

    public static class Blocks {
        public static final Block NATURAL_EVAPORATOR = new NaturalEvaporatorBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F).sound(SoundType.WOOD).noOcclusion()).setRegistryName("natural_evaporator");
        public static final Block PARABOX = new ParaboxBlock(AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE)).setRegistryName("parabox");
        public static final Block VOID_GENERATOR = new VoidGeneratorBlock(AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(4).strength(50F, 3600000.0F)).setRegistryName("void_generator");
    }

    public static class TileEntityTypes {
        public static final TileEntityType<NaturalEvaporatorTileEntity> NATURAL_EVAPORATOR = (TileEntityType<NaturalEvaporatorTileEntity>) TileEntityType.Builder.of(NaturalEvaporatorTileEntity::new, Blocks.NATURAL_EVAPORATOR).build(null).setRegistryName("natural_evaporator");
        public static final TileEntityType<ParaboxTileEntity> PARABOX = (TileEntityType<ParaboxTileEntity>) TileEntityType.Builder.of(ParaboxTileEntity::new, Blocks.PARABOX).build(null).setRegistryName("parabox");
        public static final TileEntityType<VoidGeneratorTileEntity> VOID_GENERATOR = (TileEntityType<VoidGeneratorTileEntity>) TileEntityType.Builder.of(VoidGeneratorTileEntity::new, Blocks.VOID_GENERATOR).build(null).setRegistryName("void_generator");
    }

    public static class Recipes<S extends IRecipeSerializer<? extends IRecipe<?>>> {
        public static final Recipes<EvaporatingRecipeSerializer> EVAPORATING = new Recipes<>(new EvaporatingRecipeSerializer(), AbstractEvaporatingRecipe.RECIPE_TYPE_ID);

        private static <T extends IRecipe<?>> IRecipeType<T> customType(ResourceLocation rl) {
            return Registry.register(Registry.RECIPE_TYPE, rl, new IRecipeType<T>() {
                public String toString() {
                    return rl.toString();
                }
            });
        }

        final ResourceLocation rl;
        IRecipeType<? extends IRecipe<?>> type = null;
        S serializer;

        private Recipes(S serializer, ResourceLocation rl) {
            this.serializer = serializer;
            this.rl = rl;
        }

        public S getSerializer() {
            return serializer;
        }

        @SuppressWarnings("unchecked")
        public <T extends IRecipeType<?>> T getType() {
            return (T) type;
        }

        public void register(IForgeRegistry<IRecipeSerializer<?>> registry) {
            if (type == null) type = customType(rl);

            registry.register(serializer.setRegistryName(rl));
        }
    }

    public static class EntityTypes {
        public static final EntityType<CompactBrickEntity> COMPACT_BRICK = (EntityType<CompactBrickEntity>) EntityType.Builder.<CompactBrickEntity>of(CompactBrickEntity::new, EntityClassification.MISC).sized(0.25f, 0.25f).build("compact_brick_entity").setRegistryName("compact_brick_entity");
    }

    public static class Items {
        public static final Item COMPACT_BRICK = new CompactBrickItem(new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE).stacksTo(16), "compact_brick");
        public static final Item WATER_BOWL = new Item(new Item.Properties().stacksTo(1).tab(SkyFarm.SkyFarmItemGroup.INSTANCE)).setRegistryName("water_bowl");
        public static final Item BOWL = new WaterBowlItem(new Item.Properties().stacksTo(64).tab(SkyFarm.SkyFarmItemGroup.INSTANCE), true).setRegistryName("minecraft", "bowl");
        public static final Item STONE_VARIATOR = new StoneVariatorItem(new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE).stacksTo(1).defaultDurability(128), "stone_variator");
        public static final Item OVERWORLD_VOID_SHIFTER_NETHER = new ArmorItem(ArmorMaterial.LEATHER, EquipmentSlotType.FEET, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE)).setRegistryName("overworld_void_shifter_nether");
        public static final Item OVERWORLD_SKY_SHIFTER_END = new ArmorItem(ArmorMaterial.LEATHER, EquipmentSlotType.FEET, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE)).setRegistryName("overworld_sky_shifter_end");
        public static final Item OVERWORLD_VOID_SHIFTER_UG = new ArmorItem(ArmorMaterial.LEATHER, EquipmentSlotType.FEET, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE)).setRegistryName("overworld_void_shifter_ug");
        public static final Item OVERWORLD_SKY_SHIFTER_TF = new ArmorItem(ArmorMaterial.LEATHER, EquipmentSlotType.FEET, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE)).setRegistryName("overworld_sky_shifter_tf");
        public static final Item OVERWORLD_SKY_SHIFTER_LC = new ArmorItem(ArmorMaterial.LEATHER, EquipmentSlotType.FEET, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE)).setRegistryName("overworld_sky_shifter_lc");
    }

    public static class ContainerTypes {
        public static final ContainerType<ParaboxContainer> PARABOX = (ContainerType<ParaboxContainer>) new ContainerType<>((IContainerFactory<Container>) ParaboxContainer::new).setRegistryName("parabox");
    }
}

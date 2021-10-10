package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.common.recipes.CompactBrickRecipe;
import ml.northwestwind.skyfarm.common.recipes.EvaporatingRecipe;
import ml.northwestwind.skyfarm.common.recipes.serializer.CompactBrickRecipeSerializer;
import ml.northwestwind.skyfarm.common.recipes.serializer.EvaporatingRecipeSerializer;
import ml.northwestwind.skyfarm.common.registries.block.NaturalEvaporatorBlock;
import ml.northwestwind.skyfarm.common.registries.block.ParaboxBlock;
import ml.northwestwind.skyfarm.common.registries.block.VoidGeneratorBlock;
import ml.northwestwind.skyfarm.common.registries.container.ParaboxContainer;
import ml.northwestwind.skyfarm.common.registries.effect.MegaEffect;
import ml.northwestwind.skyfarm.common.registries.effect.MiniEffect;
import ml.northwestwind.skyfarm.common.registries.entity.CompactBrickEntity;
import ml.northwestwind.skyfarm.common.registries.item.*;
import ml.northwestwind.skyfarm.common.registries.tile.NaturalEvaporatorTileEntity;
import ml.northwestwind.skyfarm.common.registries.tile.ParaboxTileEntity;
import ml.northwestwind.skyfarm.common.registries.tile.VoidGeneratorTileEntity;
import ml.northwestwind.skyfarm.common.world.SkyblockWorldType;
import ml.northwestwind.skyfarm.common.world.features.AsteroidFeature;
import ml.northwestwind.skyfarm.misc.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Food;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(SkyFarm.MOD_ID)
public class RegistryEvents {
    @SubscribeEvent
    public static void registerWorldType(final RegistryEvent.Register<ForgeWorldType> event) {
        event.getRegistry().registerAll(SkyblockWorldType.INSTANCE.setRegistryName("skyfarm"));
    }

    @SubscribeEvent
    public static void registerBlock(final RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                Blocks.NATURAL_EVAPORATOR,
                Blocks.PARABOX,
                Blocks.POWERBOX,
                Blocks.VOID_GENERATOR
        );
    }

    @SubscribeEvent
    public static void registerItem(final RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new TooltipBlockItem(Blocks.NATURAL_EVAPORATOR, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE).stacksTo(64), "natural_evaporator"),
                new TooltipBlockItem(Blocks.PARABOX, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE).stacksTo(1), "parabox"),
                new TooltipBlockItem(Blocks.POWERBOX, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE).stacksTo(1), "powerbox"),
                new TooltipBlockItem(Blocks.VOID_GENERATOR, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE).stacksTo(1).rarity(Rarity.EPIC), "void_generator"),
                Items.COMPACT_BRICK,
                Items.STONE_VARIATOR,
                Items.OVERWORLD_VOID_SHIFTER_NETHER,
                Items.OVERWORLD_SKY_SHIFTER_END,
                Items.OVERWORLD_AXIS_SHIFTER_UG,
                Items.OVERWORLD_AXIS_SHIFTER_TF,
                Items.OVERWORLD_AXIS_SHIFTER_LC,
                Items.OVERWORLD_SKY_SHIFTER_ASTEROIDS
        );
        if (ModList.get().isLoaded("iceandfire")) event.getRegistry().register(Items.DRAGON_SUMMONER);
        if (ModList.get().isLoaded("resourcefulbees")) event.getRegistry().register(Items.MUTATION_POLLEN);
        if (ModList.get().isLoaded("pehkui")) event.getRegistry().registerAll(
                Items.MEGA_MUSHROOM,
                Items.MINI_MUSHROOM
        );
    }

    @SubscribeEvent
    public static void registerTileEntityType(final RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                TileEntityTypes.NATURAL_EVAPORATOR,
                TileEntityTypes.PARABOX,
                TileEntityTypes.POWERBOX,
                TileEntityTypes.VOID_GENERATOR
        );
    }

    @SubscribeEvent
    public static void registerRecipeSerializer(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
        Recipes.EVAPORATING.register(event.getRegistry());
        Recipes.COMPACT_BRICK.register(event.getRegistry());
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

    @SubscribeEvent
    public static void registerEffect(final RegistryEvent.Register<Effect> event) {
        event.getRegistry().registerAll(
                Effects.MEGA,
                Effects.MINI
        );
    }

    @SubscribeEvent
    public static void registerFeature(final RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().registerAll(
                Features.ASTEROID
        );

        Features.register("skyfarm:asteroid", Features.ASTEROID.configured(NoFeatureConfig.INSTANCE).chance(4).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(32, 0, 224))));
    }

    public static class Blocks {
        public static final Block NATURAL_EVAPORATOR = new NaturalEvaporatorBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F).sound(SoundType.WOOD).noOcclusion().harvestTool(ToolType.AXE)).setRegistryName("natural_evaporator");
        public static final Block PARABOX = new ParaboxBlock(AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE), false).setRegistryName("parabox");
        public static final Block POWERBOX = new ParaboxBlock(AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE), true).setRegistryName("powerbox");
        public static final Block VOID_GENERATOR = new VoidGeneratorBlock(AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(4).strength(50F, 3600000.0F)).setRegistryName("void_generator");
    }

    public static class TileEntityTypes {
        public static final TileEntityType<NaturalEvaporatorTileEntity> NATURAL_EVAPORATOR = (TileEntityType<NaturalEvaporatorTileEntity>) TileEntityType.Builder.of(NaturalEvaporatorTileEntity::new, Blocks.NATURAL_EVAPORATOR).build(null).setRegistryName("natural_evaporator");
        public static final TileEntityType<ParaboxTileEntity> PARABOX = (TileEntityType<ParaboxTileEntity>) TileEntityType.Builder.of(() -> new ParaboxTileEntity(TileEntityTypes.PARABOX, false), Blocks.PARABOX).build(null).setRegistryName("parabox");
        public static final TileEntityType<ParaboxTileEntity> POWERBOX = (TileEntityType<ParaboxTileEntity>) TileEntityType.Builder.of(() -> new ParaboxTileEntity(TileEntityTypes.POWERBOX, true), Blocks.POWERBOX).build(null).setRegistryName("powerbox");
        public static final TileEntityType<VoidGeneratorTileEntity> VOID_GENERATOR = (TileEntityType<VoidGeneratorTileEntity>) TileEntityType.Builder.of(VoidGeneratorTileEntity::new, Blocks.VOID_GENERATOR).build(null).setRegistryName("void_generator");
    }

    public static class Recipes<S extends IRecipeSerializer<? extends IRecipe<?>>> {
        public static final Recipes<EvaporatingRecipeSerializer> EVAPORATING = new Recipes<>(new EvaporatingRecipeSerializer(), EvaporatingRecipe.RECIPE_TYPE_ID);
        public static final Recipes<CompactBrickRecipeSerializer> COMPACT_BRICK = new Recipes<>(new CompactBrickRecipeSerializer(), CompactBrickRecipe.RECIPE_TYPE_ID);

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
        public static final Item STONE_VARIATOR = new StoneVariatorItem(new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE).stacksTo(1).defaultDurability(128), "stone_variator");
        public static final Item OVERWORLD_VOID_SHIFTER_NETHER = new ShifterItem(ModArmorMaterial.NETHER_SHIFTER, EquipmentSlotType.FEET, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE), "overworld_void_shifter_nether");
        public static final Item OVERWORLD_SKY_SHIFTER_END = new ShifterItem(ModArmorMaterial.END_SHIFTER, EquipmentSlotType.FEET, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE), "overworld_sky_shifter_end");
        public static final Item OVERWORLD_AXIS_SHIFTER_UG = new ShifterItem(ModArmorMaterial.UG_SHIFTER, EquipmentSlotType.FEET, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE), "overworld_axis_shifter_ug", "undergarden");
        public static final Item OVERWORLD_AXIS_SHIFTER_TF = new ShifterItem(ModArmorMaterial.TF_SHIFTER, EquipmentSlotType.FEET, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE), "overworld_axis_shifter_tf", "twilightforest");
        public static final Item OVERWORLD_AXIS_SHIFTER_LC = new ShifterItem(ModArmorMaterial.LC_SHIFTER, EquipmentSlotType.FEET, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE), "overworld_axis_shifter_lc", "lostcities");
        public static final Item OVERWORLD_SKY_SHIFTER_ASTEROIDS = new ShifterItem(ModArmorMaterial.ASTEROIDS_SHIFTER, EquipmentSlotType.FEET, new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE), "overworld_sky_shifter_asteroids");
        public static final Item DRAGON_SUMMONER = new DragonSummonerItem(new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE).stacksTo(16), "dragon_summoner");
        public static final Item MUTATION_POLLEN = new MutationPollenItem(new Item.Properties().tab(SkyFarm.SkyFarmItemGroup.INSTANCE).stacksTo(64), "mutation_pollen");
        public static final Item MEGA_MUSHROOM = new TooltipItem(new Item.Properties().stacksTo(4).tab(SkyFarm.SkyFarmItemGroup.INSTANCE).food(Foods.MEGA_MUSHROOM), "mega_mushroom");
        public static final Item MINI_MUSHROOM = new TooltipItem(new Item.Properties().stacksTo(4).tab(SkyFarm.SkyFarmItemGroup.INSTANCE).food(Foods.MINI_MUSHROOM), "mini_mushroom");

        public enum ModArmorMaterial implements IArmorMaterial {
            NETHER_SHIFTER(SkyFarm.MOD_ID + ":nether_shifter", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, () -> Ingredient.EMPTY),
            END_SHIFTER(SkyFarm.MOD_ID + ":end_shifter", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, () -> Ingredient.EMPTY),
            UG_SHIFTER(SkyFarm.MOD_ID + ":ug_shifter", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, () -> Ingredient.EMPTY),
            TF_SHIFTER(SkyFarm.MOD_ID + ":tf_shifter", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, () -> Ingredient.EMPTY),
            LC_SHIFTER(SkyFarm.MOD_ID + ":lc_shifter", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, () -> Ingredient.EMPTY),
            ASTEROIDS_SHIFTER(SkyFarm.MOD_ID + ":asteroids_shifter", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, () -> Ingredient.EMPTY);

            private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
            private final String name;
            private final int durabilityMultiplier;
            private final int[] slotProtections;
            private final int enchantmentValue;
            private final SoundEvent sound;
            private final float toughness;
            private final float knockbackResistance;
            private final LazyValue<Ingredient> repairIngredient;

            ModArmorMaterial(String name, int durabilityMul, int[] protections, int enchant, SoundEvent sound, float tough, float kb, Supplier<Ingredient> ingredient) {
                this.name = name;
                this.durabilityMultiplier = durabilityMul;
                this.slotProtections = protections;
                this.enchantmentValue = enchant;
                this.sound = sound;
                this.toughness = tough;
                this.knockbackResistance = kb;
                this.repairIngredient = new LazyValue<>(ingredient);
            }

            public int getDurabilityForSlot(EquipmentSlotType p_200896_1_) {
                return HEALTH_PER_SLOT[p_200896_1_.getIndex()] * this.durabilityMultiplier;
            }

            public int getDefenseForSlot(EquipmentSlotType p_200902_1_) {
                return this.slotProtections[p_200902_1_.getIndex()];
            }

            public int getEnchantmentValue() {
                return this.enchantmentValue;
            }

            public SoundEvent getEquipSound() {
                return this.sound;
            }

            public Ingredient getRepairIngredient() {
                return this.repairIngredient.get();
            }

            @OnlyIn(Dist.CLIENT)
            public String getName() {
                return this.name;
            }

            public float getToughness() {
                return this.toughness;
            }

            public float getKnockbackResistance() {
                return this.knockbackResistance;
            }
        }
    }

    public static class ContainerTypes {
        public static final ContainerType<ParaboxContainer> PARABOX = (ContainerType<ParaboxContainer>) new ContainerType<>((IContainerFactory<Container>) ParaboxContainer::new).setRegistryName("parabox");
    }

    public static class Foods {
        public static final Food MEGA_MUSHROOM = (new Food.Builder()).nutrition(4).saturationMod(0.2F).alwaysEat().effect(() -> new EffectInstance(Effects.MEGA, 6000), 1).build();
        public static final Food MINI_MUSHROOM = (new Food.Builder()).nutrition(4).saturationMod(0.2F).alwaysEat().effect(() -> new EffectInstance(Effects.MINI, 6000), 1).build();
    }

    public static class Effects {
        public static final Effect MEGA = new MegaEffect(EffectType.NEUTRAL, 0xF11629).setRegistryName("mega");
        public static final Effect MINI = new MiniEffect(EffectType.NEUTRAL, 0x1984E6).setRegistryName("mini");
    }

    public static class Features {
        public static final Feature<NoFeatureConfig> ASTEROID = new AsteroidFeature(NoFeatureConfig.CODEC);

        private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> register(String p_243968_0_, ConfiguredFeature<FC, ?> p_243968_1_) {
            return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, p_243968_0_, p_243968_1_);
        }
    }

    // Not registry. Only for reference.
    public static class Dimensions {
        public static final RegistryKey<World> ASTEROIDS = RegistryKey.create(Registry.DIMENSION_REGISTRY, Utils.prefix("asteroids"));
        public static final RegistryKey<World> UNDERGARDEN = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("undergarden", "undergarden"));
        public static final RegistryKey<World> LOST_CITIES = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("lostcities", "lostcity"));
        public static final RegistryKey<World> TWILIGHT_FOREST = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("twilightforest", "twilightforest"));
    }
}

package ml.northwestwind.skyfarm.itemstages;

import com.google.common.collect.*;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.itemstages.bookshelf.ItemStackMap;
import ml.northwestwind.skyfarm.itemstages.bookshelf.StageCompare;
import ml.northwestwind.skyfarm.itemstages.jei.PluginItemStages;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.GameStageSaveHandler;
import net.darkhax.gamestages.event.StagesSyncedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class ItemStages {
    public static final Logger LOG = LogManager.getLogger();
    public static final ItemStackMap<String> ITEM_STAGES = new ItemStackMap<>(StageCompare.INSTANCE);

    public static final ListMultimap<String, ItemStack> SORTED_STAGES = ArrayListMultimap.create();
    public static final SetMultimap<Item, Tuple<ItemStack, String>> SORTED_ITEM_STAGES = Multimaps.newSetMultimap(Maps.newIdentityHashMap(), Sets::newIdentityHashSet);

    private static final String TRANSLATE_DESCRIPTION = "tooltip.itemstages.description";
    private static final String TRANSLATE_INFO = "tooltip.itemstages.info";
    private static final String TRANSLATE_DROP = "message.itemstages.drop";
    private static final String TRANSLATE_UNFAMILIAR = "tooltip.itemstages.name.default";

    public static String getStage(ItemStack stack) {
        if (!stack.isEmpty())
            for (final Tuple<ItemStack, String> entry : SORTED_ITEM_STAGES.get(stack.getItem()))
                if (StageCompare.INSTANCE.isValid(stack, entry.getA())) return entry.getB();
        return null;
    }

    private static IFormattableTextComponent getUnfamiliarName() {
        return new TranslationTextComponent(TRANSLATE_UNFAMILIAR);
    }

    public static void sendDropMessage(PlayerEntity player) {
        player.displayClientMessage(new TranslationTextComponent(TRANSLATE_DROP, getUnfamiliarName()), true);
    }

    @Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onPlayerDig(PlayerEvent.BreakSpeed event) {
            if (!event.getPlayer().isCreative() && !event.getPlayer().level.isClientSide) {
                ItemStack heldItem = event.getPlayer().getMainHandItem();
                final String stage = getStage(heldItem);
                if ((stage != null && !GameStageHelper.hasStage(event.getPlayer(), stage))) {
                    event.setNewSpeed(-1f);
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerInteract(PlayerInteractEvent event) {
            if (event.isCancelable() && !event.getPlayer().isCreative() && !event.getPlayer().level.isClientSide) {
                final String stage = getStage(event.getItemStack());
                if (stage != null && !GameStageHelper.hasStage(event.getPlayer(), stage))
                    event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
            if (event.getEntity() instanceof PlayerEntity && !event.getEntityLiving().level.isClientSide) {
                final PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (player.isCreative()) return;
                for (final EquipmentSlotType slot : EquipmentSlotType.values()) {
                    final ItemStack stack = player.getItemBySlot(slot);
                    final String stage = getStage(stack);
                    if ((stage != null && !GameStageHelper.hasStage(player, stage))) {
                        player.setItemSlot(slot, ItemStack.EMPTY);
                        player.drop(stack, false);
                        sendDropMessage(player);
                    }
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void addReloadListener(AddReloadListenerEvent event) {
            event.addListener(new ReloadListener<Void>() {
                @Override
                protected Void prepare(IResourceManager manager, IProfiler profiler) {
                    return null;
                }

                @Override
                protected void apply(Void obj, IResourceManager manager, IProfiler profiler) {
                    LOG.info("Sorting {} staged items.", ITEM_STAGES.size());
                    final long time = System.currentTimeMillis();
                    for (final Map.Entry<ItemStack, String> entry : ITEM_STAGES.entrySet()) {
                        SORTED_STAGES.put(entry.getValue(), entry.getKey());
                        SORTED_ITEM_STAGES.put(entry.getKey().getItem(), new Tuple<>(entry.getKey(), entry.getValue()));
                    }
                    LOG.info("Sorting complete. Found {} stages. Took {}ms", SORTED_STAGES.keySet().size(), System.currentTimeMillis() - time);
                }
            });
        }
    }

    @Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onTooltip(ItemTooltipEvent event) {
            final ClientPlayerEntity player = PlayerUtils.getClientPlayer();
            if (player != null) {
                final String itemsStage = getStage(event.getItemStack());
                if (itemsStage != null && !GameStageHelper.hasStage(player, GameStageSaveHandler.getClientData(), itemsStage)) {
                    event.getToolTip().clear();
                    event.getToolTip().add(getUnfamiliarName().withStyle(TextFormatting.WHITE));
                    event.getToolTip().add(new StringTextComponent(" "));
                    event.getToolTip().add(new TranslationTextComponent(TRANSLATE_DESCRIPTION).withStyle(TextFormatting.RED, TextFormatting.ITALIC));
                    event.getToolTip().add(new TranslationTextComponent(TRANSLATE_INFO, itemsStage).withStyle(TextFormatting.RED));
                }
            }
        }

        @SubscribeEvent
        public static void onClientSync(StagesSyncedEvent event) {
            if (ModList.get().isLoaded("jei")) {
                LOG.info("GameStages synced! Now syncing with JEI...");
                PluginItemStages.syncHiddenItems(event.getPlayer());
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void addReloadListener(AddReloadListenerEvent event) {
            event.addListener(new ReloadListener<Void>() {
                @Override
                protected Void prepare(IResourceManager manager, IProfiler profiler) {
                    return null;
                }

                @Override
                protected void apply(Void obj, IResourceManager manager, IProfiler profiler) {
                    if (ModList.get().isLoaded("jei")) {
                        LOG.info("Resyncing JEI info.");
                        PluginItemStages.syncHiddenItems(PlayerUtils.getClientPlayer());
                    }
                }
            });
        }

        private static boolean needSync;

        @SubscribeEvent
        public static void onClientLoggedInEvent(ClientPlayerNetworkEvent.LoggedInEvent event) {
            needSync = true;
        }

        @SubscribeEvent
        public static void worldFirstTick(final TickEvent.WorldTickEvent event) {
            if(needSync && event.phase == TickEvent.Phase.END && Minecraft.getInstance().player != null && (Minecraft.getInstance().player.tickCount > 20 || Minecraft.getInstance().isPaused())) {
                needSync = false;
                PluginItemStages.syncHiddenItems(Minecraft.getInstance().player);
            }
        }
    }
}

package ml.northwestwind.skyfarm.mixin;

import mekanism.api.Action;
import mekanism.api.Coord4D;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.gas.attribute.GasAttributes;
import mekanism.api.inventory.AutomationType;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.chemical.multiblock.MultiblockChemicalTankBuilder;
import mekanism.common.capabilities.heat.MultiblockHeatCapacitor;
import mekanism.common.registries.MekanismGases;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorCasing;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(value = FissionReactorMultiblockData.class, remap = false)
public abstract class MixinFissionReactorMultiblockData extends MixinMultiblockData {
    @Shadow public IGasTank fuelTank;
    @Shadow public int fuelAssemblies;
    @Shadow public double burnRemaining;
    @Shadow public double rateLimit;
    @Shadow public MultiblockHeatCapacitor<FissionReactorMultiblockData> heatCapacitor;
    @Shadow public double partialWaste;
    @Shadow public IGasTank wasteTank;
    @Shadow public double lastBurnRate;
    @Shadow public IGasTank heatedCoolantTank;
    @Shadow public IGasTank gasCoolantTank;

    @Inject(at = @At("RETURN"), method = "<init>")
    public void construct(TileEntityFissionReactorCasing tile, CallbackInfo ci) {
        this.fuelTank = MultiblockChemicalTankBuilder.GAS.create((FissionReactorMultiblockData) (Object) this, tile,
                () -> (long)this.fuelAssemblies * 8000L,
                (stack, automationType) -> automationType != AutomationType.EXTERNAL,
                (stack, automationType) -> this.isFormed(),
                (gas) -> gas == MekanismGases.FISSILE_FUEL.getChemical() || gas == RegistryEvents.Gases.FISSILE_FUEL_MK2,
                ChemicalAttributeValidator.ALWAYS_ALLOW, null);
        this.gasTanks.clear();
        this.gasTanks.addAll(Arrays.asList(this.fuelTank, this.heatedCoolantTank, this.wasteTank, this.gasCoolantTank));
    }

    /**
     * @author Mekanism
     * @reason To handle custom fissile fuel
     */
    @Overwrite
    public void burnFuel(World world) {
        double storedFuel = fuelTank.getStored() + this.burnRemaining;
        double toBurn = Math.min(Math.min(this.rateLimit, storedFuel), fuelAssemblies * MekanismGeneratorsConfig.generators.burnPerAssembly.get());
        storedFuel -= toBurn;
        fuelTank.setStackSize((long) storedFuel, Action.EXECUTE);
        burnRemaining = storedFuel % 1;
        if (fuelTank.getStack().getType().equals(RegistryEvents.Gases.FISSILE_FUEL_MK2)) {
            this.heatCapacitor.handleHeat(Math.pow(toBurn * MekanismGeneratorsConfig.generators.energyPerFissionFuel.get().doubleValue(), 3));
            this.partialWaste += Math.pow(toBurn, 2);
        } else {
            this.heatCapacitor.handleHeat(toBurn * MekanismGeneratorsConfig.generators.energyPerFissionFuel.get().doubleValue());
            this.partialWaste += toBurn;
        }
        long newWaste = (long) Math.floor(partialWaste);
        if (newWaste > 0) {
            partialWaste %= 1;
            long leftoverWaste = Math.max(0, newWaste - this.wasteTank.getNeeded());
            GasStack wasteToAdd = MekanismGases.NUCLEAR_WASTE.getStack(newWaste);
            wasteTank.insert(wasteToAdd, Action.EXECUTE, AutomationType.INTERNAL);
            if (leftoverWaste > 0) {
                double radioactivity = wasteToAdd.getType().get(GasAttributes.Radiation.class).getRadioactivity();
                Mekanism.radiationManager.radiate(new Coord4D(this.getBounds().getCenter(), world), leftoverWaste * radioactivity);
            }
        }
        this.lastBurnRate = toBurn;
    }
}

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
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FissionReactorMultiblockData.class, remap = false)
public abstract class MixinFissionReactorMultiblockData extends MixinMultiblockData {

    @Shadow(remap = false) public IGasTank fuelTank;

    @Shadow(remap = false) public int fuelAssemblies;

    @Shadow(remap = false) public double burnRemaining;

    @Shadow(remap = false) public double rateLimit;

    @Shadow(remap = false) public MultiblockHeatCapacitor<FissionReactorMultiblockData> heatCapacitor;

    @Shadow(remap = false) public double partialWaste;

    @Shadow(remap = false) public IGasTank wasteTank;

    @Shadow(remap = false) public double lastBurnRate;

    @Inject(remap = false, at = @At(value = "RETURN", remap = false, target = "Lmekanism/generators/common/content/fission;fuelTank:Lmekanism/api/chemical/gas/IGasTank;", opcode = Opcodes.PUTFIELD), method = "<init>", cancellable = true)
    public void construct(TileEntityFissionReactorCasing tile, CallbackInfo ci) {
        this.fuelTank = MultiblockChemicalTankBuilder.GAS.create((FissionReactorMultiblockData) (Object) this, tile,
                () -> (long)this.fuelAssemblies * 8000L,
                (stack, automationType) -> automationType != AutomationType.EXTERNAL,
                (stack, automationType) -> this.isFormed(),
                (gas) -> gas == MekanismGases.FISSILE_FUEL.getChemical() || gas == RegistryEvents.Gases.FISSILE_FUEL_MK2,
                ChemicalAttributeValidator.ALWAYS_ALLOW, null);
        LogManager.getLogger().info("Injected fuelTank");
    }

    /**
     * @author Mekanism
     */
    @Overwrite(remap = false)
    public void burnFuel(World world) {
        double storedFuel = fuelTank.getStored() + this.burnRemaining;
        double toBurn = Math.min(Math.min(this.rateLimit, storedFuel), fuelAssemblies * MekanismGeneratorsConfig.generators.burnPerAssembly.get());
        storedFuel -= toBurn;
        fuelTank.setStackSize((long) storedFuel, Action.EXECUTE);
        burnRemaining = storedFuel % 1;
        if (fuelTank.getStack().getType().equals(RegistryEvents.Gases.FISSILE_FUEL_MK2)) {
            this.heatCapacitor.handleHeat(toBurn * MekanismGeneratorsConfig.generators.energyPerFissionFuel.get().doubleValue() * 16);
            this.partialWaste += toBurn * 4;
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

package ml.northwestwind.skyfarm.mixin;

import mekanism.api.recipes.GasToGasRecipe;
import mekanism.api.recipes.inputs.chemical.GasStackIngredient;
import mekanism.common.registries.MekanismGases;
import mekanism.generators.client.jei.GeneratorsJEI;
import mekanism.generators.common.registries.GeneratorsBlocks;
import mezz.jei.api.registration.IRecipeRegistration;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import ml.northwestwind.skyfarm.misc.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;

@Mixin(value = GeneratorsJEI.class, remap = false)
public class MixinGeneratorsJEI {
    @Inject(at = @At("RETURN"), method = "registerRecipes")
    public void registerRecipes(IRecipeRegistration registry, CallbackInfo ci) {
        GasToGasRecipe recipe = new Utils.GTGRecipe(Utils.prefix("processing/fissile_fuel_mk2"), GasStackIngredient.from(RegistryEvents.Gases.FISSILE_FUEL_MK2, 1L), MekanismGases.NUCLEAR_WASTE.getStack(1L));
        registry.addRecipes(Collections.singletonList(recipe), GeneratorsBlocks.FISSION_REACTOR_CASING.getRegistryName());
    }
}

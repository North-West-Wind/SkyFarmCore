package ml.northwestwind.skyfarm.common.registries.tile.handler;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.energy.EnergyStorage;

public class VoidGeneratorEnergyStorage extends EnergyStorage {
    public VoidGeneratorEnergyStorage() {
        super(Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
    }

    public void setEnergyStored(int energy) {
        this.energy = energy;
    }

    public void readFromNBT(CompoundNBT compound) {
        this.energy = compound.getInt("Energy");
        this.capacity = compound.getInt("Capacity");
        this.maxReceive = compound.getInt("MaxReceive");
        this.maxExtract = compound.getInt("MaxExtract");
    }

    public void writeToNBT(CompoundNBT compound) {
        compound.putInt("Energy", this.energy);
        compound.putInt("Capacity", this.capacity);
        compound.putInt("MaxReceive", this.maxReceive);
        compound.putInt("MaxExtract", this.maxExtract);
    }
}

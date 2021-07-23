package ml.northwestwind.skyfarm.common.registries.tile.handler;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.energy.EnergyStorage;

public class ParaboxEnergyStorage extends EnergyStorage {
    public ParaboxEnergyStorage() {
        super(4096, 4096, 0);
    }

    public void setNewMax(int capacity) {
        this.capacity = capacity;
        this.maxReceive = capacity;
    }

    public void setEnergyStored(int energy) {
        this.energy = energy;
    }

    public double drainAll() {
        double percentage = energy / (capacity / 2D);
        if (percentage > 1) percentage = ((energy - (capacity / 2D)) / (capacity / 2D)) / 5D + 1;
        energy = 0;
        return percentage;
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

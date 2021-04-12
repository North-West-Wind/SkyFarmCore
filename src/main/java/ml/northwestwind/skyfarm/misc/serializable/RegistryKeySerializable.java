package ml.northwestwind.skyfarm.misc.serializable;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;

import java.io.Serializable;

public class RegistryKeySerializable<T> implements Comparable<RegistryKeySerializable<?>>, Serializable {
    private final String registryName;
    private final String location;

    public RegistryKeySerializable(RegistryKey<T> registryKey) {
        registryName = registryKey.getRegistryName().toString();
        location = registryKey.location().toString();
    }

    public ResourceLocation getRegistryName() {
        return new ResourceLocation(registryName);
    }

    public ResourceLocation location() {
        return new ResourceLocation(location);
    }

    public RegistryKey<T> toRegistryKey() {
        return RegistryKey.create(RegistryKey.createRegistryKey(getRegistryName()), location());
    }

    @Override
    public int compareTo(RegistryKeySerializable<?> o) {
        int ret = this.getRegistryName().compareTo(o.getRegistryName());
        if (ret == 0) ret = this.location().compareTo(o.location());
        return ret;
    }
}

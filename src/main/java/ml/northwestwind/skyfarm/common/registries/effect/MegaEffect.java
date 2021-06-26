package ml.northwestwind.skyfarm.common.registries.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.server.dedicated.DedicatedServer;

public class MegaEffect extends Effect {
    public MegaEffect(EffectType type, int color) {
        super(type, color);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeModifierManager manager, int amplifier) {
        super.addAttributeModifiers(entity, manager, amplifier);
        if (!(entity instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        if (player.getServer() instanceof DedicatedServer) ((DedicatedServer) player.getServer()).runCommand("scale set 10 " + player.getName().getString());
        else player.getServer().getCommands().performCommand(player.getServer().createCommandSourceStack(), "scale set 10 " + player.getName().getString());
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeModifierManager manager, int amplifier) {
        super.removeAttributeModifiers(entity, manager, amplifier);
        if (!(entity instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        if (player.getServer() instanceof DedicatedServer) ((DedicatedServer) player.getServer()).runCommand("scale set 1 " + player.getName().getString());
        else player.getServer().getCommands().performCommand(player.getServer().createCommandSourceStack(), "scale set 1 " + player.getName().getString());
    }
}

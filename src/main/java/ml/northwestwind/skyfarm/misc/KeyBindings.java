package ml.northwestwind.skyfarm.misc;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyBinding stageMenu = new KeyBinding(new TranslationTextComponent("key.skyfarm.stageMenu").getString(), GLFW.GLFW_KEY_I, "itemGroup.skyfarm");

    public static void register() {
        ClientRegistry.registerKeyBinding(stageMenu);
    }
}

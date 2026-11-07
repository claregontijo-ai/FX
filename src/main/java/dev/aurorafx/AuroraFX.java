package dev.aurorafx;

import dev.aurorafx.module.ModuleManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = AuroraFX.MODID, name = "AuroraFX", version = "1.0.0", clientSideOnly = true)
public class AuroraFX {
    public static final String MODID = "aurorafx";

    public static ModuleManager manager;
    public static KeyBinding menuKey;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        manager = new ModuleManager();
        menuKey = new KeyBinding("Abrir menu AuroraFX", Keyboard.KEY_RSHIFT, "AuroraFX");
        ClientRegistry.registerKeyBinding(menuKey);
        MinecraftForge.EVENT_BUS.register(manager);
    }
}

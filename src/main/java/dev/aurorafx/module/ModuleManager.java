package dev.aurorafx.module;

import dev.aurorafx.AuroraFX;
import dev.aurorafx.gui.MenuScreen;
import dev.aurorafx.module.impl.HitEffect;
import dev.aurorafx.module.impl.TargetAura;
import dev.aurorafx.module.impl.Trails;
import dev.aurorafx.module.impl.WatermarkHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public final List<Module> modules = new ArrayList<Module>();
    private final Minecraft mc = Minecraft.getMinecraft();

    public ModuleManager() {
        modules.add(new TargetAura());
        modules.add(new HitEffect());
        modules.add(new Trails());
        modules.add(new WatermarkHud());
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END || mc.thePlayer == null || mc.theWorld == null) return;

        if (AuroraFX.menuKey.isPressed() && mc.currentScreen == null) {
            mc.displayGuiScreen(new MenuScreen(this));
        }
        for (Module m : modules) {
            if (m.enabled) m.onTick();
        }
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent e) {
        if (e.entityPlayer != mc.thePlayer || e.target == null) return;
        for (Module m : modules) {
            if (m.enabled) m.onAttack(e.target);
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent e) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        for (Module m : modules) {
            if (m.enabled) m.onRender3D(e.partialTicks);
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post e) {
        if (e.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (mc.thePlayer == null || mc.theWorld == null) return;
        ScaledResolution sr = new ScaledResolution(mc);
        for (Module m : modules) {
            if (m.enabled) m.onRenderHud(sr);
        }
    }
}

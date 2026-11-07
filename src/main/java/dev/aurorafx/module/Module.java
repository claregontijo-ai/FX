package dev.aurorafx.module;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String name;
    public boolean enabled;
    public final List<Setting> settings = new ArrayList<Setting>();

    protected Module(String name, boolean enabledByDefault) {
        this.name = name;
        this.enabled = enabledByDefault;
    }

    protected Setting add(Setting s) {
        settings.add(s);
        return s;
    }

    public void onTick() {}

    public void onAttack(Entity target) {}

    public void onRender3D(float partialTicks) {}

    public void onRenderHud(ScaledResolution sr) {}
}

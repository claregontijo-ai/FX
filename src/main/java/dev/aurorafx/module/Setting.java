package dev.aurorafx.module;

public class Setting {
    public final String name;
    public final boolean isBool;
    public final float min;
    public final float max;
    public float value;

    public Setting(String name, float def, float min, float max) {
        this.name = name;
        this.isBool = false;
        this.min = min;
        this.max = max;
        this.value = def;
    }

    public Setting(String name, boolean def) {
        this.name = name;
        this.isBool = true;
        this.min = 0f;
        this.max = 1f;
        this.value = def ? 1f : 0f;
    }

    public boolean bool() {
        return value >= 0.5f;
    }

    public void toggle() {
        value = bool() ? 0f : 1f;
    }

    public void set(float v) {
        value = Math.max(min, Math.min(max, v));
    }
}

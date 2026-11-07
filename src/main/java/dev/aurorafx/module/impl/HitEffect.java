package dev.aurorafx.module.impl;

import dev.aurorafx.module.Module;
import dev.aurorafx.module.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;

import java.util.Random;

/** Explosão de partículas quando você acerta uma entidade. */
public class HitEffect extends Module {
    private final Setting amount = add(new Setting("Quantidade", 14f, 4f, 40f));
    private final Setting spread = add(new Setting("Espalhamento", 0.35f, 0.1f, 1f));
    private final Setting magic = add(new Setting("Estilo mágico", true));

    private final Random rand = new Random();

    public HitEffect() {
        super("Hit Effect", true);
    }

    @Override
    public void onAttack(Entity target) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return;

        EnumParticleTypes type = magic.bool() ? EnumParticleTypes.CRIT_MAGIC : EnumParticleTypes.CRIT;
        int n = (int) amount.value;
        double cy = target.posY + target.height * 0.55;
        for (int i = 0; i < n; i++) {
            double vx = (rand.nextDouble() - 0.5) * spread.value * 2.0;
            double vy = (rand.nextDouble() - 0.2) * spread.value * 2.0;
            double vz = (rand.nextDouble() - 0.5) * spread.value * 2.0;
            mc.theWorld.spawnParticle(type,
                target.posX + (rand.nextDouble() - 0.5) * target.width,
                cy + (rand.nextDouble() - 0.5) * target.height * 0.5,
                target.posZ + (rand.nextDouble() - 0.5) * target.width,
                vx, vy, vz);
        }
    }
}

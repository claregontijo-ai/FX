package dev.aurorafx.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public final class RenderUtil {
    private RenderUtil() {}

    /** Posição interpolada da câmera (evita depender de campos do RenderManager). */
    public static double[] camera(float pt) {
        Entity v = Minecraft.getMinecraft().getRenderViewEntity();
        return new double[] {
            v.lastTickPosX + (v.posX - v.lastTickPosX) * pt,
            v.lastTickPosY + (v.posY - v.lastTickPosY) * pt,
            v.lastTickPosZ + (v.posZ - v.lastTickPosZ) * pt
        };
    }

    public static double[] entityPos(Entity e, float pt) {
        return new double[] {
            e.lastTickPosX + (e.posX - e.lastTickPosX) * pt,
            e.lastTickPosY + (e.posY - e.lastTickPosY) * pt,
            e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * pt
        };
    }

    public static float[] hue(float h) {
        int rgb = Color.HSBtoRGB(h - (float) Math.floor(h), 0.65f, 1f);
        return new float[] {
            ((rgb >> 16) & 255) / 255f,
            ((rgb >> 8) & 255) / 255f,
            (rgb & 255) / 255f
        };
    }

    /** Prepara o estado do GL para desenhar linhas/faixas translúcidas (com teste de profundidade). */
    public static void begin3D(float lineWidth) {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.depthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(lineWidth);
    }

    public static void end3D() {
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.popMatrix();
    }

    /** Retângulo com cantos chanfrados (aproxima um pill sem texturas). */
    public static void pill(int x, int y, int w, int h, int color) {
        Gui.drawRect(x + 3, y, x + w - 3, y + h, color);
        Gui.drawRect(x + 1, y + 1, x + w - 1, y + h - 1, color);
        Gui.drawRect(x, y + 3, x + w, y + h - 3, color);
    }
}

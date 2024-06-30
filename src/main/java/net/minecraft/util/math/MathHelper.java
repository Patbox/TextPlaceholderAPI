package net.minecraft.util.math;

public class MathHelper {
    public static final float TAU = (float) (Math.PI * 2);

    public static float lerp(float progress, float l, float l1) {
        return l * (1 - progress) + l1 * progress;
    }

    public static float clamp(float v, float min, float max) {
        return Math.max(Math.min(v, max), min);
    }

    public static float sqrt(float v) {
        return (float) Math.sqrt(v);
    }

    public static float atan2(float b, float a) {
        return (float) Math.atan2(b, a);
    }

    public static float cos(float h) {
        return (float) Math.cos(h);
    }

    public static float sin(float h) {
        return (float) Math.sin(h);
    }
}

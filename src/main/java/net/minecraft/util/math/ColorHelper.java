package net.minecraft.util.math;

import java.awt.*;

public class ColorHelper {
    public static class Argb {

        public static int getRed(int rgb) {
            return (rgb >> 16) & 0xFF;
        }

        public static int getGreen(int rgb) {
            return (rgb >> 8) & 0xFF;
        }

        public static int getBlue(int rgb) {
            return (rgb) & 0xFF;
        }
    }
}

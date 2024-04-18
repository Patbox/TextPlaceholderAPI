package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.impl.GeneralUtils;
import eu.pb4.placeholders.impl.color.HSV;
import eu.pb4.placeholders.impl.color.OkLab;
import eu.pb4.placeholders.impl.color.OkLch;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class GradientNode extends ParentNode {
    private final GradientProvider gradientProvider;

    public GradientNode(TextNode[] children, GradientProvider gradientBuilder) {
        super(children);
        this.gradientProvider = gradientBuilder;
    }

    public static Text apply(Text text, GradientProvider gradientProvider) {
        return GeneralUtils.toGradient(text, gradientProvider);
    }

    public static GradientNode rainbow(float saturation, float value, float frequency, float offset, int gradientLength, TextNode... nodes) {
        return new GradientNode(nodes, GradientProvider.rainbow(saturation, value, frequency, offset, gradientLength));
    }

    public static GradientNode rainbow(float saturation, float value, float frequency, float offset, TextNode... nodes) {
        return new GradientNode(nodes, GradientProvider.rainbow(saturation, value, frequency, offset));
    }

    public static GradientNode rainbow(float saturation, float value, float frequency, TextNode... nodes) {
        return rainbow(saturation, value, frequency, 0, nodes);
    }

    public static GradientNode rainbow(float saturation, float value, TextNode... nodes) {
        return rainbow(saturation, value, 1, 0, nodes);
    }

    public static GradientNode rainbow(float saturation, TextNode... nodes) {
        return rainbow(saturation, 1, 1, 0, nodes);
    }

    public static GradientNode rainbow(TextNode... nodes) {
        return rainbow(1, 1, 1, 0, nodes);
    }

    public static GradientNode colors(TextColor from, TextColor to, TextNode... nodes) {
        return colors(List.of(from, to), nodes);
    }

    public static GradientNode colors(List<TextColor> colors, TextNode... nodes) {
        return new GradientNode(nodes, GradientProvider.colors(colors));
    }

    public static GradientNode colorsHard(TextColor from, TextColor to, TextNode... nodes) {
        return colorsHard(List.of(from, to), nodes);
    }

    public static GradientNode colorsHard(List<TextColor> colors, TextNode... nodes) {
        return new GradientNode(nodes, GradientProvider.colorsHard(colors));
    }

    @Override
    protected Text applyFormatting(MutableText out, ParserContext context) {
        return GeneralUtils.toGradient(out, this.gradientProvider);
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new GradientNode(children, this.gradientProvider);
    }

    @Override
    public String toString() {
        return "GradientNode{" +
                "gradientProvider=" + gradientProvider +
                ", children=" + Arrays.toString(children) +
                '}';
    }

    @FunctionalInterface
    public interface GradientProvider {
        TextColor getColorAt(int index, int length);


        static GradientProvider colors(List<TextColor> colors) {
            return colorsOkLab(colors);
        }

        static GradientProvider colorsOkLab(List<TextColor> colors) {
            var hvs = new ArrayList<OkLab>(colors.size());
            for (var color : colors) {
                hvs.add(OkLab.fromRgb(color.getRgb()));
            }

            if (hvs.isEmpty()) {
                hvs.add(new OkLab(1, 1, 1));
            } else if (hvs.size() == 1) {
                hvs.add(hvs.get(0));
            }

            final int colorSize = hvs.size();

            return (pos, length) -> {
                final float sectionSize = ((float) length) / (colorSize - 1);
                final float progress = (pos % sectionSize) / sectionSize;
                OkLab colorA = hvs.get(Math.min((int) (pos / sectionSize), colorSize - 1));
                OkLab colorB = hvs.get(Math.min((int) (pos / sectionSize) + 1, colorSize - 1));

                float l = MathHelper.lerp(progress, colorA.l(), colorB.l());
                float a = MathHelper.lerp(progress, colorA.a(), colorB.a());
                float b = MathHelper.lerp(progress, colorA.b(), colorB.b());

                return TextColor.fromRgb(OkLab.toRgb(l, a, b));
            };
        }

        static GradientProvider colorsHvs(List<TextColor> colors) {
            var hvs = new ArrayList<HSV>(colors.size());
            for (var color : colors) {
                hvs.add(HSV.fromRgb(color.getRgb()));
            }

            if (hvs.isEmpty()) {
                hvs.add(new HSV(1, 1, 1));
            } else if (hvs.size() == 1) {
                hvs.add(hvs.get(0));
            }

            final int colorSize = hvs.size();

            return (pos, length) -> {
                final double step = ((double) colorSize - 1) / length;
                final float sectionSize = ((float) length) / (colorSize - 1);
                final float progress = (pos % sectionSize) / sectionSize;

                HSV colorA = hvs.get(Math.min((int) (pos / sectionSize), colorSize - 1));
                HSV colorB = hvs.get(Math.min((int) (pos / sectionSize) + 1, colorSize - 1));

                float hue;
                {
                    float h = colorB.h() - colorA.h();
                    float delta = (h + ((Math.abs(h) > 0.50001) ? ((h < 0) ? 1 : -1) : 0));

                    float futureHue = (float) (colorA.h() + delta * step * (pos % sectionSize));
                    if (futureHue < 0) {
                        futureHue += 1;
                    } else if (futureHue > 1) {
                        futureHue -= 1;
                    }
                    hue = futureHue;
                }

                float sat = MathHelper.clamp(colorB.s() * progress + colorA.s() * (1 - progress), 0, 1);
                float value = MathHelper.clamp(colorB.v() * progress + colorA.v() * (1 - progress), 0, 1);

                return TextColor.fromRgb(HSV.toRgb(
                        MathHelper.clamp(hue, 0, 1),
                        sat,
                        value));
            };
        }

        static GradientProvider colorsHard(List<TextColor> colors) {
            final int colorSize = colors.size();

            return  (pos, length) -> {
                if (length == 0) {
                    return colors.get(0);
                }

                final float sectionSize = ((float) length) / colorSize;

                return colors.get(Math.min((int) (pos / sectionSize), colorSize - 1));
            };
        }

        static GradientProvider rainbow(float saturation, float value, float frequency, float offset, int gradientLength) {
            return rainbowHvs(saturation, value, frequency, offset, gradientLength);
        }
        static GradientProvider rainbowHvs(float saturation, float value, float frequency, float offset, int gradientLength) {
            final float finalFreqLength = (frequency < 0 ? -frequency : 0);

            return (pos, length) ->
                    TextColor.fromRgb(HSV.toRgb((((pos * frequency) + (finalFreqLength * length)) / (gradientLength + 1) + offset) % 1,
                            saturation,
                            value));
        }

        static GradientProvider rainbowOkLch(float saturation, float value, float frequency, float offset, int gradientLength) {
            final float finalFreqLength = (frequency < 0 ? -frequency : 0);

            return (pos, length) ->
                    TextColor.fromRgb(OkLch.toRgb(value, saturation / 2, (((pos * frequency * MathHelper.TAU) + (finalFreqLength * length)) / (gradientLength + 1) + offset) % 1));
        }

        static GradientProvider rainbow(float saturation, float value, float frequency, float offset) {
            return rainbowHvs(saturation, value, frequency, offset);
        }

        static GradientProvider rainbowHvs(float saturation, float value, float frequency, float offset) {
            final float finalFreqLength = (frequency < 0 ? -frequency : 0);

            return (pos, length) -> TextColor.fromRgb(HSV.toRgb((((pos * frequency) + (finalFreqLength * length)) / (length + 1) + offset),
                    saturation, value));
        }

        static GradientProvider rainbowOkLch(float saturation, float value, float frequency, float offset) {
            final float finalFreqLength = (frequency < 0 ? -frequency : 0);

            return (pos, length) ->
                    TextColor.fromRgb(OkLch.toRgb(value, saturation / 2, (((pos * frequency * MathHelper.TAU) + (finalFreqLength * length)) / (length) + offset)));
        }
    }
}

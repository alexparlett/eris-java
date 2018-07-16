package org.homonoia.sw.procedural;

import com.badlogic.gdx.graphics.Color;

import static com.badlogic.gdx.math.MathUtils.clamp;
import static java.lang.Math.log;
import static java.lang.Math.pow;

public final class BlackBodySpectrum {

    public static Color kelvinToColor(final float kelvin) {
        float red, green, blue;

        float temperature = kelvin / 100;

        if (temperature < 66) {
            red = 255;
        } else {
            red = temperature - 60;
            red = (float) (329.698727466f * pow(red, -0.1332047592));
            red = clamp(red, 0, 255);
        }

        if (temperature <= 66) {
            green = temperature;
            green = (float) (99.4708025861 * log(green) - 161.1195681661);
            green = clamp(green, 0, 255);
        } else {
            green = temperature - 60;
            green = (float) (288.1221695283 * pow(green, -0.0755148492));
            green = clamp(green, 0, 255);
        }

        if (temperature >= 66) {
            blue = 255;
        } else if (temperature <= 19) {
            blue = 0;
        } else {
            blue = temperature - 10;
            blue = (float) (138.5177312231 * log(blue) - 305.0447927307);
            blue = clamp(blue, 0, 255);
        }

        return new Color(red / 255, green / 255, blue / 255, 1);
    }

}

package org.homonoia.sw.procedural;

import com.badlogic.gdx.graphics.Color;

import static java.lang.Math.*;

public final class BlackBodySpectrum {

    public static Color Generate(final float kelvin) {
        float red, green, blue;

        float temperature = kelvin / 100;

        if (temperature < 66) {
            red = 1;
        } else {
            red = temperature - 60;
            red = (float) (329.698727466f * pow(red, -0.1332047592));
            red = max(0, Math.min(255, red));
        }

        if (temperature <= 66) {
            green = temperature;
            green = (float) (99.4708025861 * log(green) - 161.1195681661);
            green = max(0, Math.min(255, green));
        } else {
            green = temperature - 60;
            green = (float) (288.1221695283 * pow(green, -0.0755148492));
            green = max(0, Math.min(255, green));
        }

        if (temperature >= 66) {
            blue = 255;
        } else if (temperature <= 19) {
            blue = 0;
        } else {
            blue = temperature - 10;
            blue = (float) (138.5177312231 * log(blue) - 305.0447927307);
            blue = max(0, Math.min(255, blue));
        }

        return new Color(red / 255, green / 255, blue / 255, 1);
    }

}

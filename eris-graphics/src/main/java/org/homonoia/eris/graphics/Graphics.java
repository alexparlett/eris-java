package org.homonoia.eris.graphics;

import org.homonoia.eris.core.exceptions.InitializationException;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.List;

/**
 * Created by alexparlett on 06/02/2016.
 */
public interface Graphics {

    void initialize() throws InitializationException;

    void terminate();

    void maximize();

    void minimize();

    void restore();

    void hide();

    void show();

    void close();

    void setSize(final int width, final int height);

    void setSamples(final int samples);

    void setGamma(final float gamma);

    void setTitle(final String title);

    void setFullscreen(final boolean fullscreen);

    void setResizable(final boolean resizable);

    void setBorderless(final boolean borderless);

    void setVSync(final boolean vsync);

    void setIcon(final String icon);

    boolean isFullscreen();

    boolean isResizable();

    boolean isBorderless();

    boolean isVsync();

    boolean isInitialized();

    int getWidth();

    int getHeight();

    int getSamples();

    float getGamma();

    String getTitle();

    long getRenderWindow();

    long getBackgroundWindow();

    String getIcon();

    List<GLFWVidMode> getResolutions();

    GLFWVidMode getResolution();
}

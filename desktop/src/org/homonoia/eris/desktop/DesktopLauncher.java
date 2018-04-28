package org.homonoia.eris.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.homonoia.eris.SolarianWarsGame;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class DesktopLauncher {
	public static void main (String[] arg) {
		GLFW.glfwSetErrorCallback(GLFWErrorCallback.create((error, description) ->{
			Gdx.app.error("ERROR", GLFWErrorCallback.getDescription(description));
		}));

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.useOpenGL3(false, 3, 2);
//		config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		config.enableGLDebugOutput(true, System.out);
		config.setTitle("Solarian Wars");
		config.setMaximized(true);
		config.setInitialVisible(true);
		config.setResizable(false);
		config.setWindowedMode(680,480);

		try {
			new Lwjgl3Application(new SolarianWarsGame(), config);
		} catch (Exception e) {
			Gdx.app.error("", e.getMessage());
		}
	}
}

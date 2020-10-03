package com.wgsoft.game.timesaver.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.wgsoft.game.timesaver.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Time Saver";
		config.width = 1366;
		config.height = 768;
		config.forceExit = false;
		new LwjglApplication(new MyGdxGame(), config);
	}
}

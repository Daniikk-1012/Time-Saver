package com.wgsoft.game.timesaver.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.wgsoft.game.timesaver.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Time Saver");
		config.setWindowedMode(1366, 768);
		new Lwjgl3Application(new MyGdxGame(), config);
	}
}

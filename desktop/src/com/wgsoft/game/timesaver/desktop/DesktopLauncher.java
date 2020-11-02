package com.wgsoft.game.timesaver.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.wgsoft.game.timesaver.MyGdxGame;
import com.wgsoft.game.timesaver.screens.SettingsScreen;

import java.util.Locale;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Time Saver");
		config.setWindowedMode(1366, 768);
		String[] localeNames = new String[SettingsScreen.LANGUAGES.length];
		for (int i = 0; i < localeNames.length; i++) {
			String[] localeString = SettingsScreen.LANGUAGES[i].split("_");
			Locale locale;
			if (localeString.length == 1) {
				locale = new Locale(localeString[0]);
			} else if (localeString.length == 2) {
				locale = new Locale(localeString[0], localeString[1]);
			} else {
				locale = new Locale(localeString[0], localeString[1], localeString[2]);
			}
			localeNames[i] = locale.getDisplayName(locale);
		}
		new Lwjgl3Application(new MyGdxGame(localeNames), config);
	}
}

package com.wgsoft.game.timesaver;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.wgsoft.game.timesaver.MyGdxGame;
import com.wgsoft.game.timesaver.screens.SettingsScreen;

import java.util.Locale;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useCompass = false;
		config.useAccelerometer = false;
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
		initialize(new MyGdxGame(localeNames), config);
	}
}

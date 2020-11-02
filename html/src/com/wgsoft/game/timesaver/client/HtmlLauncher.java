package com.wgsoft.game.timesaver.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.wgsoft.game.timesaver.MyGdxGame;
import com.wgsoft.game.timesaver.screens.SettingsScreen;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(true);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                String[] localeNames = new String[SettingsScreen.LANGUAGES.length];
                for(int i = 0; i < localeNames.length; i++){
                        localeNames[i] = LocaleInfo.getLocaleNativeDisplayName(SettingsScreen.LANGUAGES[i]);
                }
                return new MyGdxGame(localeNames);
        }
}
package com.wgsoft.game.timesaver;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.wgsoft.game.timesaver.screens.GameScreen;
import com.wgsoft.game.timesaver.screens.MenuScreen;

import java.util.Locale;
import java.util.Properties;

import static com.wgsoft.game.timesaver.Const.*;

public class MyGdxGame extends Game implements Localizable{
	public SpriteBatch batch;

	public Sound slashSound;
	public Sound monsterDeathSound;
	public Sound timeOverSound;
	public Sound timeFillSound;
	public Sound jumpSound;
	public Sound deathSound;
	public Sound selectSound;
	public Sound respawnSound;

	public Skin skin;
	public Properties properties;
	public I18NBundle bundle;

	public Preferences prefs;

	public MenuScreen menuScreen;
	public GameScreen gameScreen;

	public MyGdxGame(){
		game = this;
	}

	@Override
	public void create() {
		batch = new SpriteBatch();

		slashSound = Gdx.audio.newSound(Gdx.files.internal("snd/slash.wav"));
		monsterDeathSound = Gdx.audio.newSound(Gdx.files.internal("snd/monster-death.wav"));
		timeOverSound = Gdx.audio.newSound(Gdx.files.internal("snd/time-over.wav"));
		timeFillSound = Gdx.audio.newSound(Gdx.files.internal("snd/time-fill.wav"));
		jumpSound = Gdx.audio.newSound(Gdx.files.internal("snd/jump.wav"));
		deathSound = Gdx.audio.newSound(Gdx.files.internal("snd/death.wav"));
		selectSound = Gdx.audio.newSound(Gdx.files.internal("snd/select.wav"));
		respawnSound = Gdx.audio.newSound(Gdx.files.internal("snd/respawn.wav"));

		skin = new Skin(Gdx.files.internal("img/skin.json"));
		ObjectMap.Entries<String, BitmapFont> entries = new ObjectMap.Entries<>(skin.getAll(BitmapFont.class));
		for(ObjectMap.Entry<String, BitmapFont> entry : entries){
			Array.ArrayIterator<TextureRegion> arrayIterator = new Array.ArrayIterator<>(entry.value.getRegions());
			for(TextureRegion region : arrayIterator){
				region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			}
		}
		skin.get("time", ProgressBar.ProgressBarStyle.class).knobBefore = skin.getTiledDrawable("progress-bar/time/knob-before");
		skin.get("time", ProgressBar.ProgressBarStyle.class).knob = new BaseDrawable();
		properties = new Properties();
		try {
			properties.load(Gdx.files.internal("bundle/properties.properties").read());

		}catch (Exception ignored){
		}

		prefs = Gdx.app.getPreferences(properties.getProperty("package-name"));

		menuScreen = new MenuScreen();
		gameScreen = new GameScreen();

		init();

		setScreen(menuScreen);
	}

	@Override
	public void localize() {
		menuScreen.localize();
		gameScreen.localize();
	}

	private void init(){
		if(prefs.getBoolean("firstRun", true)){
			initBundle(null, null, null);
			if(bundle.getLocale().getCountry().equals("")) {
				prefs.putString("settings.language", bundle.getLocale().getLanguage());
			}else if(bundle.getLocale().getVariant().equals("")){
				prefs.putString("settings.language", bundle.getLocale().getLanguage()+"_"+bundle.getLocale().getCountry());
			}else{
				prefs.putString("settings.language", bundle.getLocale().getLanguage()+"_"+bundle.getLocale().getCountry()+"_"+bundle.getLocale().getVariant());
			}
			prefs.putBoolean("firstRun", false);
			prefs.flush();
		}else {
			String[] localeStrings = prefs.getString("settings.language").split("_");
			if (localeStrings.length == 1) {
				initBundle(localeStrings[0], null, null);
			} else if (localeStrings.length == 2) {
				initBundle(localeStrings[0], localeStrings[1], null);
			} else {
				initBundle(localeStrings[0], localeStrings[1], localeStrings[2]);
			}
		}
		localize();
	}

	private void initBundle(String s1, String s2, String s3){
		FileHandle fileHandle = Gdx.files.internal("bundle/bundle");
		if(s1 == null){
			bundle = I18NBundle.createBundle(fileHandle);
		}else{
			Locale locale;
			if(s2 == null){
				locale = new Locale(s1);
			}else if(s3 == null){
				locale = new Locale(s1, s2);
			}else{
				locale = new Locale(s1, s2, s3);
			}
			bundle = I18NBundle.createBundle(fileHandle, locale);
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();

		slashSound.dispose();
		monsterDeathSound.dispose();
		timeOverSound.dispose();
		timeFillSound.dispose();
		jumpSound.dispose();
		deathSound.dispose();
		selectSound.dispose();
		respawnSound.dispose();

		skin.dispose();

		menuScreen.dispose();
		gameScreen.dispose();
	}
}

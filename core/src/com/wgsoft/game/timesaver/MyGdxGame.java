package com.wgsoft.game.timesaver;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;

import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import static com.wgsoft.game.timesaver.Const.*;

public class MyGdxGame extends Game implements Localizable{
	public SpriteBatch batch;

	public Skin skin;
	public Properties properties;
	public I18NBundle bundle;

	public Preferences prefs;

	public GameScreen gameScreen;

	public MyGdxGame(){
		game = this;
	}

	@Override
	public void create() {
		batch = new SpriteBatch();

		skin = new Skin(Gdx.files.internal("img/skin.json"));
		ObjectMap.Entries<String, BitmapFont> entries = new ObjectMap.Entries<>(skin.getAll(BitmapFont.class));
		for(ObjectMap.Entry<String, BitmapFont> entry : entries){
			Array.ArrayIterator<TextureRegion> arrayIterator = new Array.ArrayIterator<>(entry.value.getRegions());
			for(TextureRegion region : arrayIterator){
				region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			}
		}
		properties = new Properties();
		try {
			properties.load(Gdx.files.internal("bundle/properties.properties").read());

		}catch (Exception ignored){
		}

		prefs = Gdx.app.getPreferences(properties.getProperty("package-name"));

		gameScreen = new GameScreen();

		init();

		setScreen(gameScreen);
	}

	@Override
	public void localize() {
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
			localize();
			prefs.putBoolean("firstRun", false);
			prefs.flush();
		}else{
			String[] localeStrings = prefs.getString("settings.language").split("_");
			if(localeStrings.length == 1){
				initBundle(localeStrings[0], null, null);
			}else if(localeStrings.length == 2){
				initBundle(localeStrings[0], localeStrings[1], null);
			}else{
				initBundle(localeStrings[0], localeStrings[1], localeStrings[2]);
			}
			localize();
		}
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

		skin.dispose();

		gameScreen.dispose();
	}
}

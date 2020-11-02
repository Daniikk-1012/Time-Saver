package com.wgsoft.game.timesaver.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wgsoft.game.timesaver.Localizable;
import com.wgsoft.game.timesaver.objects.ShadowActor;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class SettingsScreen implements Screen, Localizable {
    public static final int LIST_COUNT_MAX = 2;

    public static final float SOUND_DEFAULT = 0.5f;
    public static final float MUSIC_DEFAULT = 0.5f;
    public static final float SLIDER_STEP_SIZE = 0.01f;
    public static final float SETTINGS_PADDING = 50f;
    public static final float LABEL_PADDING = 50f;

    public static final String[] LANGUAGES = new String[]{
            "en",
            "ru_RU"
    };

    private static String[] localesNames;

    private final Stage backgroundStage;
    private final Stage uiStage;

    private final InputMultiplexer inputMultiplexer;

    private final Label settingsLabel;
    private final Label soundLabel;
    private final Slider soundSlider;
    private final Label musicLabel;
    private final Slider musicSlider;
    private final Label languageLabel;
    private final SelectBox<String> languageSelectBox;
    private final TextButton backButton;
    private final TextButton acceptButton;

    public SettingsScreen(){
        backgroundStage = new Stage(new ScreenViewport(), game.batch);
        uiStage = new Stage(new ScreenViewport(), game.batch);

        inputMultiplexer = new InputMultiplexer(uiStage, backgroundStage);

        Image backgroundImage = new Image(game.skin, "background");
        backgroundImage.setFillParent(true);
        backgroundStage.addActor(backgroundImage);

        Table rootTable = new Table(game.skin);
        rootTable.setFillParent(true);

        rootTable.add().grow();
        rootTable.row();

        settingsLabel = new Label("settings.settings", game.skin, "boldestLarge");
        rootTable.add(settingsLabel).colspan(2);

        rootTable.row();

        Table settingsTable = new Table(game.skin);

        soundLabel = new Label("settings.sound", game.skin, "boldestMedium");
        settingsTable.add(soundLabel).pad(LABEL_PADDING).right();

        soundSlider = new Slider(0f, 1f, SLIDER_STEP_SIZE, false, game.skin, "sound"){
            @Override
            public float getPrefWidth() {
                return getStyle().background.getLeftWidth()+getStyle().background.getMinWidth()+getStyle().background.getRightWidth();
            }
        };
        settingsTable.add(soundSlider);

        settingsTable.row();

        musicLabel = new Label("settings.music", game.skin, "boldestMedium");
        settingsTable.add(musicLabel).pad(LABEL_PADDING).right();

        musicSlider = new Slider(0f, 1f, SLIDER_STEP_SIZE, false, game.skin, "music"){
            @Override
            public float getPrefWidth() {
                return getStyle().background.getLeftWidth()+getStyle().background.getMinWidth()+getStyle().background.getRightWidth();
            }
        };
        settingsTable.add(musicSlider);

        settingsTable.row();

        languageLabel = new Label("settings.language", game.skin, "boldestMedium");
        settingsTable.add(languageLabel).pad(LABEL_PADDING).right();

        languageSelectBox = new SelectBox<>(game.skin, "language");
        languageSelectBox.setAlignment(Align.center);
        languageSelectBox.getList().setAlignment(Align.center);
        languageSelectBox.setMaxListCount(LIST_COUNT_MAX);
        for (String localesName : localesNames) {
            languageSelectBox.getItems().add(localesName);
        }
        languageSelectBox.setItems(languageSelectBox.getItems());
        settingsTable.add(languageSelectBox).pad(SETTINGS_PADDING);

        rootTable.add(settingsTable).colspan(2);

        rootTable.row();
        rootTable.add().grow();
        rootTable.row();

        backButton = new TextButton("settings.back", game.skin, "menuButton");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.menuScreen);
            }
        });
        rootTable.add(backButton).expandX();

        acceptButton = new TextButton("settings.accept", game.skin, "menuButton");
        acceptButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.prefs.putFloat("settings.sound", soundSlider.getValue());
                game.prefs.putFloat("settings.music", musicSlider.getValue());
                game.prefs.putString("settings.language", LANGUAGES[languageSelectBox.getSelectedIndex()]);
                game.prefs.flush();
                game.init();
            }
        });
        rootTable.add(acceptButton).expandX();

        uiStage.addActor(rootTable);
    }

    public static void setLocaleNames(String[] localesNames){
        SettingsScreen.localesNames = localesNames;
    }

    @Override
    public void localize() {
        settingsLabel.setText(game.bundle.get("settings.settings"));
        soundLabel.setText(game.bundle.get("settings.sound"));
        musicLabel.setText(game.bundle.get("settings.music"));
        languageLabel.setText(game.bundle.get("settings.language"));
        backButton.setText(game.bundle.get("settings.back"));
        acceptButton.setText(game.bundle.get("settings.accept"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        backgroundStage.addActor(ShadowActor.getInstance());
        soundSlider.setValue(game.prefs.getFloat("settings.sound", SOUND_DEFAULT));
        musicSlider.setValue(game.prefs.getFloat("settings.music", MUSIC_DEFAULT));
        for(int i = 0; i < languageSelectBox.getItems().size; i++){
            if(LANGUAGES[i].equals(game.prefs.getString("settings.language"))){
                languageSelectBox.setSelectedIndex(i);
                break;
            }
        }
    }

    @Override
    public void render(float delta) {
        backgroundStage.act(delta);
        uiStage.act(delta);
        backgroundStage.draw();
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if((float)width/height > SCREEN_WIDTH/SCREEN_HEIGHT){
            ((ScreenViewport) backgroundStage.getViewport()).setUnitsPerPixel(SCREEN_HEIGHT/height);
            ((ScreenViewport) uiStage.getViewport()).setUnitsPerPixel(SCREEN_HEIGHT/height);
        }else{
            ((ScreenViewport) backgroundStage.getViewport()).setUnitsPerPixel(SCREEN_WIDTH/width);
            ((ScreenViewport) uiStage.getViewport()).setUnitsPerPixel(SCREEN_WIDTH/width);
        }
        backgroundStage.getViewport().update(width, height, true);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
    }
}

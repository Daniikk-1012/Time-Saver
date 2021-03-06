package com.wgsoft.game.timesaver.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wgsoft.game.timesaver.Localizable;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class HtmlScreen implements Screen, Localizable {
    private final Stage backgroundStage;
    private final Stage uiStage;

    private final InputMultiplexer inputMultiplexer;

    private final TextButton playButton;

    public HtmlScreen(){
        backgroundStage = new Stage(new ScreenViewport(), game.batch);
        backgroundStage.getRoot().setTouchable(Touchable.disabled);
        uiStage = new Stage(new ScreenViewport(), game.batch);
        uiStage.getRoot().setTouchable(Touchable.childrenOnly);

        inputMultiplexer = new InputMultiplexer(uiStage, backgroundStage);

        Image backgroundImage = new Image(game.skin, "html/background");
        backgroundImage.setFillParent(true);
        backgroundImage.setScaling(Scaling.fit);

        backgroundStage.addActor(backgroundImage);

        playButton = new TextButton("html.play", game.skin, "boldestLarge");
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.initSounds();
                game.applyMusicVolume();
                game.menuMusic.play();
                if(game.prefs.getBoolean("story", true)){
                    game.setScreen(game.storyScreen);
                    game.prefs.putBoolean("story", false);
                    game.prefs.flush();
                }else {
                    game.setScreen(game.menuScreen);
                }
            }
        });

        Container<TextButton> container = new Container<>(playButton);
        container.setFillParent(true);

        uiStage.addActor(container);
    }

    @Override
    public void localize() {
        playButton.setText(game.bundle.get("html.play"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
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
        if((float)width/height > SCREEN_WIDTH/SCREEN_HEIGHT) {
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

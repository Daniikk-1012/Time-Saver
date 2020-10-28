package com.wgsoft.game.timesaver.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wgsoft.game.timesaver.Localizable;
import com.wgsoft.game.timesaver.objects.ShadowActor;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class MenuScreen implements Screen, Localizable {
    public static final float BUTTON_PADDING = 50f;

    private final Stage backgroundStage;
    private final Stage uiStage;

    private final InputMultiplexer inputMultiplexer;

    private final TextButton startButton;
    private final TextButton tutorialButton;
    private final TextButton exitButton;

    public MenuScreen(){
        backgroundStage = new Stage(new ScreenViewport(), game.batch);
        backgroundStage.getRoot().setTouchable(Touchable.disabled);
        uiStage = new Stage(new ScreenViewport(), game.batch);
        uiStage.getRoot().setTouchable(Touchable.childrenOnly);

        inputMultiplexer = new InputMultiplexer(uiStage, backgroundStage);

        Image backgroundImage = new Image(game.skin, "background");
        backgroundImage.setFillParent(true);
        backgroundStage.addActor(backgroundImage);

        Table rootTable = new Table(game.skin);
        rootTable.setFillParent(true);

        Image titleImage = new Image(game.skin, "menu/title");
        titleImage.setTouchable(Touchable.disabled);
        rootTable.add(titleImage);

        rootTable.row();

        startButton = new TextButton("menu.start", game.skin, "menuButton");
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.gameScreen.createGame(0);
                game.menuMusic.stop();
                game.setScreen(game.gameScreen);
            }
        });
        rootTable.add(startButton).pad(BUTTON_PADDING);

        rootTable.row();

        tutorialButton = new TextButton("menu.tutorial", game.skin, "menuButton");
        tutorialButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.tutorialScreen);
            }
        });
        rootTable.add(tutorialButton).pad(BUTTON_PADDING);

        rootTable.row();

        exitButton = new TextButton("menu.exit", game.skin, "menuButton");
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        rootTable.add(exitButton).pad(BUTTON_PADDING);

        uiStage.addActor(rootTable);
    }

    @Override
    public void localize() {
        startButton.setText(game.bundle.get("menu.start"));
        tutorialButton.setText(game.bundle.get("menu.tutorial"));
        exitButton.setText(game.bundle.get("menu.exit"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        backgroundStage.addActor(ShadowActor.getInstance());
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

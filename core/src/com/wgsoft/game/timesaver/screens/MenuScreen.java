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

import static com.wgsoft.game.timesaver.Const.*;

public class MenuScreen implements Screen, Localizable {
    private Stage backgroundStage;
    private Stage uiStage;

    private InputMultiplexer inputMultiplexer;

    private TextButton startButton;
    private TextButton exitButton;

    public MenuScreen(){
        backgroundStage = new Stage(new ScreenViewport(), game.batch);
        backgroundStage.getRoot().setTouchable(Touchable.disabled);
        uiStage = new Stage(new ScreenViewport(), game.batch);
        uiStage.getRoot().setTouchable(Touchable.childrenOnly);

        inputMultiplexer = new InputMultiplexer(uiStage, backgroundStage);

        Table rootTable = new Table(game.skin);
        rootTable.setFillParent(true);
        rootTable.setBackground("menu/background");

        Image titleImage = new Image(game.skin, "menu/title");
        rootTable.add(titleImage);

        rootTable.row();

        startButton = new TextButton("menu.start", game.skin, "menuButton");
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.gameScreen.createGame();
                game.setScreen(game.gameScreen);
            }
        });
        rootTable.add(startButton).pad(MENU_BUTTON_PADDING);

        rootTable.row();

        exitButton = new TextButton("menu.exit", game.skin, "menuButton");
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        rootTable.add(exitButton).pad(MENU_BUTTON_PADDING);

        uiStage.addActor(rootTable);
    }

    @Override
    public void localize() {
        startButton.setText(game.bundle.get("menu.start"));
        exitButton.setText(game.bundle.get("menu.exit"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
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
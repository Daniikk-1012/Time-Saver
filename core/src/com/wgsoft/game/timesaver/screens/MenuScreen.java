package com.wgsoft.game.timesaver.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wgsoft.game.timesaver.Localizable;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class MenuScreen implements Screen, Localizable {
    public static final float COLOR_CHANGE_DURATION = 5f;
    public static final float BUTTON_PADDING = 50f;

    private final Stage backgroundStage;
    private final Stage uiStage;

    private final InputMultiplexer inputMultiplexer;

    private final TextButton startButton;
    private final TextButton exitButton;

    public MenuScreen(){
        backgroundStage = new Stage(new ScreenViewport(), game.batch);
        backgroundStage.getRoot().setTouchable(Touchable.disabled);
        uiStage = new Stage(new ScreenViewport(), game.batch);
        uiStage.getRoot().setTouchable(Touchable.childrenOnly);

        inputMultiplexer = new InputMultiplexer(uiStage, backgroundStage);

        Actor backgroundActor = new Actor(){
            @Override
            public void act(float delta) {
                setSize(getStage().getWidth(), getStage().getHeight());
                super.act(delta);
            }
            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.draw(game.skin.getRegion("menu/background"), getX(), getY(), getWidth(), getHeight());
            }
        };
        backgroundStage.addActor(backgroundActor);

        Actor shadowActor = new Actor(){
            @Override
            public void act(float delta) {
                setSize(getStage().getWidth(), getStage().getHeight());
                super.act(delta);
            }
            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.setColor(getColor());
                batch.draw(game.skin.getRegion("menu/shadow"), getX(), getY(), getWidth(), getHeight());
                batch.setColor(1f, 1f, 1f, 1f);
            }
        };
        shadowActor.setColor(Color.GREEN);
        shadowActor.addAction(Actions.forever(Actions.sequence(Actions.color(Color.PURPLE, COLOR_CHANGE_DURATION, Interpolation.fade), Actions.color(Color.GREEN, COLOR_CHANGE_DURATION, Interpolation.fade))));
        backgroundStage.addActor(shadowActor);

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
                game.gameScreen.createLevel();
                game.setScreen(game.gameScreen);
            }
        });
        rootTable.add(startButton).pad(BUTTON_PADDING);

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
        exitButton.setText(game.bundle.get("menu.exit"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        game.menuMusic.play();
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
        game.menuMusic.stop();
    }

    @Override
    public void dispose() {
    }
}

package com.wgsoft.game.timesaver.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wgsoft.game.timesaver.Localizable;
import com.wgsoft.game.timesaver.objects.ShadowActor;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class StoryScreen implements Screen, Localizable {
    private static final int PAGE_COUNT = 3;

    private final Stage backgroundStage;
    private final Stage uiStage;

    private final InputMultiplexer inputMultiplexer;

    private final TextButton storyButton;

    private int page;

    public StoryScreen(){
        backgroundStage = new Stage(new ScreenViewport(), game.batch);
        uiStage = new Stage(new ScreenViewport(), game.batch);

        inputMultiplexer = new InputMultiplexer(uiStage, backgroundStage);

        Image backgroundImage = new Image(game.skin, "background");
        backgroundImage.setFillParent(true);
        backgroundStage.addActor(backgroundImage);

        Table rootTable = new Table(game.skin);
        rootTable.setFillParent(true);

        storyButton = new TextButton("story.story", game.skin, "storyButton");
        storyButton.getLabel().setWrap(true);
        storyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(page < PAGE_COUNT-1) {
                    setPage(page + 1);
                }else{
                    game.setScreen(game.menuScreen);
                }
            }
        });
        rootTable.add(storyButton);

        uiStage.addActor(rootTable);
    }

    private void setPage(int page){
        storyButton.setText(game.bundle.get("story.story"+page));
        this.page = page;
    }

    @Override
    public void localize() {
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        backgroundStage.addActor(ShadowActor.getInstance());
        setPage(0);
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

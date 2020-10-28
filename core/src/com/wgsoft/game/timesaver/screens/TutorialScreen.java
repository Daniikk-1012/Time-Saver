   package com.wgsoft.game.timesaver.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wgsoft.game.timesaver.Localizable;
import com.wgsoft.game.timesaver.objects.ShadowActor;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class TutorialScreen implements Screen, Localizable {
    public static final int PAGE_COUNT = 4;

    public static final float PAGE_IMAGE_PADDING = 25f;
    public static final float DESCRIPTION_IMAGE_PADDING_RIGHT = 50f;
    public static final float BUTTON_IMAGE_PADDING_BOTTOM = 50f;

    private final Stage backgroundStage;
    private final Stage uiStage;

    private final InputMultiplexer inputMultiplexer;

    private final Label tutorialLabel;
    private final Label pageLabel;
    private final Image descriptionImage;
    private final Image buttonImage;
    private final Label descriptionLabel;
    private final TextButton backButton;
    private final Image[] pageImages;
    private final TextButton nextButton;

    private int page;

    public TutorialScreen(){
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

        rootTable.add().grow();
        rootTable.row();

        tutorialLabel = new Label("tutorial.tutorial", game.skin, "boldestSmall");
        rootTable.add(tutorialLabel).colspan(2+PAGE_COUNT).expandX();

        rootTable.row();

        pageLabel = new Label("tutorial.page", game.skin, "boldestMedium");
        rootTable.add(pageLabel).colspan(2+PAGE_COUNT).expandX();

        rootTable.row();

        Table descriptionTable = new Table(game.skin);

        descriptionImage = new Image();
        descriptionTable.add(descriptionImage).padRight(DESCRIPTION_IMAGE_PADDING_RIGHT);

        Table instructionTable = new Table(game.skin);

        buttonImage = new Image();
        instructionTable.add(buttonImage).padBottom(BUTTON_IMAGE_PADDING_BOTTOM);

        instructionTable.row();

        descriptionLabel = new Label("tutorial.description", game.skin, "boldestSmall");
        descriptionLabel.setWrap(true);
        instructionTable.add(descriptionLabel).growX();

        descriptionTable.add(instructionTable);

        rootTable.add(descriptionTable).colspan(2+PAGE_COUNT).growX();

        rootTable.row();
        rootTable.add().grow();
        rootTable.row();

        backButton = new TextButton("tutorial.back", game.skin, "menuButton");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(page > 0){
                    setPage(page-1);
                }else{
                    game.setScreen(game.menuScreen);
                }
            }
        });
        rootTable.add(backButton).expandX();

        pageImages = new Image[PAGE_COUNT];
        for(int i = 0; i < pageImages.length; i++){
            pageImages[i] = new Image(game.skin, "tutorial/page");
            rootTable.add(pageImages[i]).pad(PAGE_IMAGE_PADDING);
        }

        nextButton = new TextButton("tutorial.next", game.skin, "menuButton");
        nextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(page < PAGE_COUNT-1){
                    setPage(page+1);
                }else{
                    game.setScreen(game.menuScreen);
                }
            }
        });
        rootTable.add(nextButton).expandX();

        uiStage.addActor(rootTable);
    }

    private void setPage(int page){
        pageLabel.setText(game.bundle.get("tutorial.page"+page));
        descriptionImage.setDrawable(game.skin, "tutorial/"+page+"/image");
        buttonImage.setDrawable(game.skin, "tutorial/"+page+"/button");
        descriptionLabel.setText(game.bundle.get("tutorial.description"+page));
        pageImages[this.page].setDrawable(game.skin, "tutorial/page");
        pageImages[page].setDrawable(game.skin, "tutorial/page-current");
        this.page = page;
    }

    @Override
    public void localize() {
        tutorialLabel.setText(game.bundle.get("tutorial.tutorial"));
        backButton.setText(game.bundle.get("tutorial.back"));
        nextButton.setText(game.bundle.get("tutorial.next"));
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

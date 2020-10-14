package com.wgsoft.game.timesaver.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wgsoft.game.timesaver.Localizable;
import com.wgsoft.game.timesaver.objects.game.Bubble;
import com.wgsoft.game.timesaver.objects.game.DrugDealer;
import com.wgsoft.game.timesaver.objects.game.Eye;
import com.wgsoft.game.timesaver.objects.game.Ground;
import com.wgsoft.game.timesaver.objects.game.Hatch;
import com.wgsoft.game.timesaver.objects.game.Player;
import com.wgsoft.game.timesaver.objects.game.PlayerItem;
import com.wgsoft.game.timesaver.objects.game.Portal;
import com.wgsoft.game.timesaver.objects.game.Scientist;
import com.wgsoft.game.timesaver.objects.game.Shop;
import com.wgsoft.game.timesaver.objects.game.Track;

import static com.wgsoft.game.timesaver.Const.*;

public class GameScreen implements Screen, Localizable {
    private final Stage backgroundStage;
    private final Stage buildingsStage;
    private final Stage gameStage;
    private final Stage uiStage;
    private final Stage foregroundStage;
    private final Stage timeOverStage;

    private final InputMultiplexer inputMultiplexer;

    private Player player;
    private float maxTime;

    private final TextButton menuButton;
    private final ProgressBar timeProgressBar;
    private final TextButton settingsButton;
    private final Label timeLabel;
    private final CheckBox katanaCheckBox;
    private final CheckBox timeMineCheckBox;
    //private CheckBox hourglassCheckBox;

    private final Label timeOverLabel;

    public GameScreen(){
        backgroundStage = new Stage(new ScreenViewport(), game.batch);
        backgroundStage.getRoot().setTouchable(Touchable.disabled);
        buildingsStage = new Stage(new ScreenViewport(), game.batch);
        buildingsStage.getRoot().setTouchable(Touchable.disabled);
        gameStage = new Stage(new ScreenViewport(), game.batch);
        gameStage.getRoot().setTouchable(Touchable.disabled);
        uiStage = new Stage(new ScreenViewport(), game.batch);
        uiStage.getRoot().setTouchable(Touchable.childrenOnly);
        foregroundStage = new Stage(new ScreenViewport(), game.batch);
        foregroundStage.getRoot().setTouchable(Touchable.disabled);
        timeOverStage = new Stage(new ScreenViewport(), game.batch);
        timeOverStage.addAction(Actions.alpha(0f));
        timeOverStage.getRoot().setTouchable(Touchable.disabled);

        inputMultiplexer = new InputMultiplexer(timeOverStage, foregroundStage, uiStage, gameStage, buildingsStage, backgroundStage);

        Actor backgroundActor = new Actor(){
            @Override
            public void act(float delta) {
                setSize(getStage().getWidth(), getStage().getHeight());
                super.act(delta);
            }

            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.draw(game.skin.getRegion("game/background"), getX(), getY(), getWidth(), getHeight());
            }
        };
        backgroundStage.addActor(backgroundActor);

        float buildingsWidth = 0f;
        for(int i = 0; i < GAME_BUILDING_COUNT; i++){
            buildingsWidth += game.skin.getRegion("game/building/"+i).getRegionWidth();
        }
        final float finalBuildingsWidth = buildingsWidth;
        Actor buildingsActor = new Actor(){
            @Override
            public void act(float delta) {
                setSize(getStage().getWidth(), getStage().getHeight());
                setX((float)Math.round((getStage().getCamera().position.x+getStage().getWidth()/2f)/finalBuildingsWidth)*finalBuildingsWidth-getStage().getWidth()/2f);
                super.act(delta);
            }

            @Override
            public void draw(Batch batch, float parentAlpha) {
                float offset = 0f;
                for(int i = 0; i < GAME_BUILDING_COUNT; i++){
                    batch.draw(game.skin.getRegion("game/building/"+i), getX()+offset-finalBuildingsWidth, getY(), game.skin.getRegion("game/building/"+i).getRegionWidth(), game.skin.getRegion("game/building/"+i).getRegionHeight());
                    batch.draw(game.skin.getRegion("game/building/"+i), getX()+offset, getY(), game.skin.getRegion("game/building/"+i).getRegionWidth(), game.skin.getRegion("game/building/"+i).getRegionHeight());
                    offset += game.skin.getRegion("game/building/"+i).getRegionWidth();
                }
            }
        };
        buildingsStage.addActor(buildingsActor);

        Table rootTable = new Table(game.skin);
        rootTable.setFillParent(true);

        Table topTable = new Table(game.skin);

        menuButton = new TextButton("game.menu", game.skin, "boldestMedium");
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.menuScreen);
            }
        });
        topTable.add(menuButton).padLeft(GAME_BUTTON_PADDING_HORIZONTAL).padTop(GAME_BUTTON_PADDING_TOP).left();

        timeProgressBar = new ProgressBar(0f, 1f, GAME_TIME_PROGRESS_BAR_STEP, false, game.skin, "time"){
            @Override
            public float getPrefWidth() {
                return getStyle().knobBefore.getMinWidth();
            }
        };
        timeProgressBar.setRound(false);
        timeProgressBar.setTouchable(Touchable.disabled);
        topTable.add(timeProgressBar).expandX().padTop(GAME_BUTTON_PADDING_TOP);

        settingsButton = new TextButton("game.settings", game.skin, "boldestMedium");
        settingsButton.setVisible(false);
        topTable.add(settingsButton).padRight(GAME_BUTTON_PADDING_HORIZONTAL).padTop(GAME_BUTTON_PADDING_TOP).right();

        topTable.row();

        topTable.add().growX();

        timeLabel = new Label("game.time", game.skin, "boldestMedium");
        timeLabel.setTouchable(Touchable.disabled);
        topTable.add(timeLabel);

        topTable.add().growX();

        rootTable.add(topTable).growX();

        rootTable.row();

        Table inventoryTable = new Table(game.skin);
        uiStage.setKeyboardFocus(inventoryTable);
        inventoryTable.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode){
                    case Input.Keys.NUM_1:
                    case Input.Keys.NUMPAD_1:
                        katanaCheckBox.setChecked(true);
                        return true;
                    case Input.Keys.NUM_2:
                    case Input.Keys.NUMPAD_2:
                        timeMineCheckBox.setChecked(true);
                        return true;
                    /*case Input.Keys.NUM_3:
                    case Input.Keys.NUMPAD_3:
                        hourglassCheckBox.setChecked(true);
                        return true;*/
                }
                return false;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                switch (keycode){
                    case Input.Keys.NUM_1:
                    case Input.Keys.NUMPAD_1:
                    case Input.Keys.NUM_2:
                    case Input.Keys.NUMPAD_2:
                    case Input.Keys.NUM_3:
                    case Input.Keys.NUMPAD_3:
                        return true;
                }
                return false;
            }
        });

        katanaCheckBox = new CheckBox(null, game.skin, "inventory");
        katanaCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(katanaCheckBox.isChecked()){
                    player.setPlayerItem(PlayerItem.KATANA);
                }
            }
        });
        Image katanaImage = new Image(game.skin, "check-box/inventory/icon/katana");
        katanaImage.setTouchable(Touchable.disabled);
        inventoryTable.stack(katanaCheckBox, katanaImage);

        Label katanaLabel = new Label("1", game.skin, "boldestMedium");
        katanaLabel.setTouchable(Touchable.disabled);
        inventoryTable.add(katanaLabel);

        inventoryTable.add().growX();

        inventoryTable.row();

        timeMineCheckBox = new CheckBox(null, game.skin, "inventory");
        timeMineCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(timeMineCheckBox.isChecked()){
                    player.setPlayerItem(PlayerItem.TIME_MINE);
                }
            }
        });
        Image timeMineImage = new Image(game.skin, "check-box/inventory/icon/time-mine");
        timeMineImage.setTouchable(Touchable.disabled);
        inventoryTable.stack(timeMineCheckBox, timeMineImage);

        Label timeMineLabel = new Label("2", game.skin, "boldestMedium");
        timeMineLabel.setTouchable(Touchable.disabled);
        inventoryTable.add(timeMineLabel);

        inventoryTable.add().growX();

        inventoryTable.row();

        /*hourglassCheckBox = new CheckBox(null, game.skin, "inventory");
        hourglassCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(hourglassCheckBox.isChecked()){
                    player.setPlayerItem(PlayerItem.HOURGLASS);
                }
            }
        });
        Image hourglassImage = new Image(game.skin, "check-box/inventory/icon/hourglass");
        hourglassImage.setTouchable(Touchable.disabled);
        inventoryTable.stack(hourglassCheckBox, hourglassImage);

        Label hourglassLabel = new Label("3", game.skin, "boldestMedium");
        hourglassLabel.setTouchable(Touchable.disabled);
        inventoryTable.add(hourglassLabel);

        inventoryTable.add().growX();

        inventoryTable.row();*/

        inventoryTable.add().growY();

        new ButtonGroup<>(katanaCheckBox, timeMineCheckBox/*, hourglassCheckBox*/);

        rootTable.add(inventoryTable).grow();

        uiStage.addActor(rootTable);

        Actor shadowActor = new Actor(){
            @Override
            public void act(float delta) {
                setSize(getStage().getWidth(), getStage().getHeight());
                super.act(delta);
            }

            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.draw(game.skin.getRegion("game/foreground"), getX(), getY(), getWidth(), getHeight());
            }
        };
        foregroundStage.addActor(shadowActor);

        Table timeOverTable = new Table(game.skin);
        timeOverTable.setFillParent(true);
        timeOverTable.setBackground("game/time-over");

        timeOverLabel = new Label("game.time-over", game.skin, "boldestLarge");
        timeOverTable.add(timeOverLabel);

        timeOverStage.addActor(timeOverTable);
    }

    public void createGame(){
        maxTime = GAME_PLAYER_MAX_TIME_DEFAULT;
        player = null;
        game.timeFillSound.play(game.prefs.getFloat("settings.sound", SETTINGS_SOUND_DEFAULT));
        createLevel();
    }

    public void createLevel(){
        if(player != null){
            maxTime = player.getMaxTime();
        }
        gameStage.clear();
        Ground ground = new Ground();
        gameStage.addActor(new Track(2660f, ground.getTop()));
        gameStage.addActor(new Shop(4370f, ground.getTop()));
        player = new Player(maxTime);
        Bubble bubble = new Bubble(player);
        Hatch hatch = new Hatch(8950f, ground.getTop());
        gameStage.addActor(new Portal(player, hatch, bubble, 8950f, ground.getTop()));
        gameStage.addActor(hatch);
        gameStage.setKeyboardFocus(player);
        gameStage.addActor(player);
        gameStage.addActor(ground);
        gameStage.addActor(new Scientist(player, bubble, 1800f, 0f, false));
        gameStage.addActor(new Scientist(player, bubble, 2670f, 0f, false));
        gameStage.addActor(new Scientist(player, bubble, 2960f, 0f, true));
        gameStage.addActor(new DrugDealer(player, bubble, 3260f));
        gameStage.addActor(new Scientist(player, bubble, 4650f, 490f, false));
        gameStage.addActor(new Eye(player, bubble, 5250f, 500f));
        gameStage.addActor(new Scientist(player, bubble, 5350f, 0f, false));
        gameStage.addActor(new Eye(player, bubble, 6050f, 800f));
        gameStage.addActor(new Eye(player, bubble, 6900f, 580f));
        gameStage.addActor(new Scientist(player, bubble, 7000f, 0f, false));
        gameStage.addActor(new Eye(player, bubble, 7150f, 780f));
        gameStage.addActor(new Scientist(player, bubble, 8600f, 0f, false));
        gameStage.addActor(new Eye(player, bubble, 8500f, 580f));
        gameStage.addActor(new Scientist(player, bubble, 9300f, 0f, false));
        gameStage.addActor(new Eye(player, bubble, 9200f, 800f));
        gameStage.addActor(bubble);
        katanaCheckBox.setChecked(true);
    }

    @Override
    public void localize() {
        menuButton.setText(game.bundle.get("game.menu"));
        settingsButton.setText(game.bundle.get("game.settings"));
        //timeLabel is updated each frame
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        backgroundStage.act(delta);
        gameStage.act(delta);
        if(player.getStage() == null || player.getTime() < 0f){
            if(player.getStage() != null) {
                game.timeOverSound.play(game.prefs.getFloat("settings.sound", SETTINGS_SOUND_DEFAULT));
            }
            timeOverStage.getRoot().clearActions();
            timeOverStage.addAction(Actions.sequence(Actions.alpha(1f), Actions.alpha(0f, GAME_TIME_OVER_ANIMATION_DURATION, Interpolation.fade)));
            timeOverStage.addAction(Actions.delay(GAME_TIME_FILL_SOUND_DELAY, Actions.run(new Runnable() {
                @Override
                public void run() {
                    game.respawnSound.play(game.prefs.getFloat("settings.sound", SETTINGS_SOUND_DEFAULT));
                }
            })));
            if(player.getStage() == null){
                timeOverLabel.setText(game.bundle.get("game.game-over"));
            }else{
                timeOverLabel.setText(game.bundle.get("game.time-over"));
            }
            createLevel();
            gameStage.act(delta);
        }
        buildingsStage.getCamera().position.x = GAME_BUILDINGS_PARALLAX*gameStage.getCamera().position.x;
        buildingsStage.act(delta); //After gameStage.act because we firstly move the player, then center the camera
        timeProgressBar.setValue(player.getTime()/maxTime);
        String timeString = String.valueOf(player.getTime());
        int index = timeString.indexOf(".");
        if(GAME_TIME_LABEL_AFTER_DOT_COUNT == 0){
            timeLabel.setText(game.bundle.format("game.time", timeString.substring(0, index), player.getMaxTime()));
        }else {
            timeLabel.setText(game.bundle.format("game.time", timeString.substring(0, index+1+GAME_TIME_LABEL_AFTER_DOT_COUNT), player.getMaxTime()));
        }
        uiStage.act(delta);
        foregroundStage.act(delta);
        timeOverStage.act(delta);
        game.batch.setColor(1f, 1f, 1f, 1f); //Because alpha does not restore color of SpriteBatch
        backgroundStage.draw();
        buildingsStage.draw();
        gameStage.draw();
        uiStage.draw();
        foregroundStage.draw();
        timeOverStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if((float)width/height > SCREEN_WIDTH/SCREEN_HEIGHT) {
            ((ScreenViewport) backgroundStage.getViewport()).setUnitsPerPixel(SCREEN_HEIGHT/height);
            ((ScreenViewport) buildingsStage.getViewport()).setUnitsPerPixel(SCREEN_HEIGHT/height);
            ((ScreenViewport) gameStage.getViewport()).setUnitsPerPixel(SCREEN_HEIGHT/height);
            ((ScreenViewport) uiStage.getViewport()).setUnitsPerPixel(SCREEN_HEIGHT/height);
            ((ScreenViewport) foregroundStage.getViewport()).setUnitsPerPixel(SCREEN_HEIGHT/height);
            ((ScreenViewport) timeOverStage.getViewport()).setUnitsPerPixel(SCREEN_HEIGHT/height);
        }else{
            ((ScreenViewport) backgroundStage.getViewport()).setUnitsPerPixel(SCREEN_WIDTH/width);
            ((ScreenViewport) buildingsStage.getViewport()).setUnitsPerPixel(SCREEN_WIDTH/width);
            ((ScreenViewport) gameStage.getViewport()).setUnitsPerPixel(SCREEN_WIDTH/width);
            ((ScreenViewport) uiStage.getViewport()).setUnitsPerPixel(SCREEN_WIDTH/width);
            ((ScreenViewport) foregroundStage.getViewport()).setUnitsPerPixel(SCREEN_WIDTH/width);
            ((ScreenViewport) timeOverStage.getViewport()).setUnitsPerPixel(SCREEN_WIDTH/width);
        }
        backgroundStage.getViewport().update(width, height, true);
        buildingsStage.getViewport().update(width, height);
        buildingsStage.getCamera().position.y = buildingsStage.getViewport().getWorldHeight()/2f;
        gameStage.getViewport().update(width, height);
        gameStage.getCamera().position.y = gameStage.getViewport().getWorldHeight()/2f;
        uiStage.getViewport().update(width, height, true);
        foregroundStage.getViewport().update(width, height, true);
        timeOverStage.getViewport().update(width, height, true);
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

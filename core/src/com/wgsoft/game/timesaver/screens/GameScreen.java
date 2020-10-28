package com.wgsoft.game.timesaver.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wgsoft.game.timesaver.Localizable;
import com.wgsoft.game.timesaver.objects.game.Bench;
import com.wgsoft.game.timesaver.objects.game.Bubble;
import com.wgsoft.game.timesaver.objects.game.DrugDealer;
import com.wgsoft.game.timesaver.objects.game.Eye;
import com.wgsoft.game.timesaver.objects.game.Ground;
import com.wgsoft.game.timesaver.objects.game.Hatch;
import com.wgsoft.game.timesaver.objects.game.HoverBoard;
import com.wgsoft.game.timesaver.objects.game.Lamp;
import com.wgsoft.game.timesaver.objects.game.Light;
import com.wgsoft.game.timesaver.objects.game.Player;
import com.wgsoft.game.timesaver.objects.game.PlayerItem;
import com.wgsoft.game.timesaver.objects.game.Portal;
import com.wgsoft.game.timesaver.objects.game.Scientist;
import com.wgsoft.game.timesaver.objects.game.Shop;
import com.wgsoft.game.timesaver.objects.game.Truck;
import com.wgsoft.game.timesaver.objects.game.Wreckage;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

//TODO More levels
public class GameScreen implements Screen, Localizable {
    public static final int BUILDING_COUNT = 4;
    public static final int TIME_LABEL_AFTER_DOT_COUNT = 1;

    public static final float BUILDINGS_PARALLAX = 0.5f;
    public static final float VICTORY_UI_FADE_DURATION = 3f;
    public static final float VICTORY_ALPHA_DURATION = 2f;
    public static final float VICTORY_SHIFT_DURATION = 4f;
    public static final float BUTTON_PADDING_HORIZONTAL = 60f;
    public static final float BUTTON_PADDING_TOP = 50f;
    public static final float TIME_PROGRESS_BAR_STEP = 0.005f;
    public static final float GRAVITY = 1500f;
    public static final float TIME_OVER_ANIMATION_DURATION = 3f;
    public static final float TIME_FILL_SOUND_DELAY = 2f;
    public static final float BUILDING_WIDTH = 1920f;
    public static final float BUILDING_HEIGHT = 471f;
    public static final float BAR_HEIGHT = 170f;
    public static final float CUT_SCENE_ZOOM = 0.87f;
    public static final float CUT_SCENE_END_DURATION = 3f;
    public static final float WRECKAGE_SAFE_ZONE = 960f;

    public static final float[] BORDERS_LEFT = new float[]{
            -960f
    };
    public static final float[] BORDERS_RIGHT = new float[]{
            9360f
    };

    private final Stage backgroundStage;
    private final Stage buildingsStage;
    private final Stage gameStage;
    private final Stage uiStage;
    private final Stage foregroundStage;
    private final Stage timeOverStage;
    private final Stage victoryStage;
    private final Stage barStage;

    private final InputMultiplexer inputMultiplexer;

    private boolean finishing;
    private int level;
    private float maxTime;
    private Player player;
    private Bubble bubble;
    private Portal portal;
    private Hatch hatch;
    private Scientist scientist;

    private final TextButton menuButton;
    private final ProgressBar timeProgressBar;
    private final TextButton settingsButton;
    private final Label timeLabel;
    private final CheckBox katanaCheckBox;
    private final CheckBox timeMineCheckBox;

    private final Label timeOverLabel;

    private final Label blueVictoryLabel;
    private final Label redVictoryLabel;
    private final Label victoryLabel;
    private final Stack victoryStack;
    private final TextButton victoryMenuButton;

    private final Table barTable;
    private final Image topBarImage;
    private final Image bottomBarImage;

    public GameScreen(){
        backgroundStage = new Stage(new ScreenViewport(), game.batch);
        backgroundStage.getRoot().setTouchable(Touchable.disabled);
        buildingsStage = new Stage(new ScreenViewport(), game.batch);
        buildingsStage.getRoot().setTouchable(Touchable.disabled);
        gameStage = new Stage(new ScreenViewport(), game.batch);
        gameStage.getRoot().setTouchable(Touchable.disabled);
        uiStage = new Stage(new ScreenViewport(), game.batch);
        foregroundStage = new Stage(new ScreenViewport(), game.batch);
        foregroundStage.getRoot().setTouchable(Touchable.disabled);
        timeOverStage = new Stage(new ScreenViewport(), game.batch);
        timeOverStage.getRoot().setColor(1f, 1f, 1f, 0f);
        timeOverStage.getRoot().setTouchable(Touchable.disabled);
        victoryStage = new Stage(new ScreenViewport(), game.batch);
        barStage = new Stage(new ScreenViewport(), game.batch);
        barStage.getRoot().setTouchable(Touchable.disabled);

        inputMultiplexer = new InputMultiplexer(barStage, victoryStage, timeOverStage, foregroundStage, uiStage, gameStage, buildingsStage, backgroundStage);

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

        Actor buildingsActor = new Actor(){
            @Override
            public void act(float delta) {
                setSize(getStage().getWidth(), getStage().getHeight());
                setX((float)Math.round((getStage().getCamera().position.x+getStage().getWidth()/2f)/(BUILDING_WIDTH*BUILDING_COUNT))*(BUILDING_WIDTH*BUILDING_COUNT)-getStage().getWidth()/2f);
                super.act(delta);
            }

            @Override
            public void draw(Batch batch, float parentAlpha) {
                for(int i = 0; i < BUILDING_COUNT; i++){
                    batch.draw(game.skin.getRegion("game/building/"+i), getX()+BUILDING_WIDTH*(i-BUILDING_COUNT), getY(), BUILDING_WIDTH, BUILDING_HEIGHT);
                    batch.draw(game.skin.getRegion("game/building/"+i), getX()+BUILDING_WIDTH*i, getY(), BUILDING_WIDTH, BUILDING_HEIGHT);
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
                game.menuMusic.play();
                game.setScreen(game.menuScreen);
            }
        });
        topTable.add(menuButton).padLeft(BUTTON_PADDING_HORIZONTAL).padTop(BUTTON_PADDING_TOP).left();

        timeProgressBar = new ProgressBar(0f, 1f, TIME_PROGRESS_BAR_STEP, false, game.skin, "time"){
            @Override
            public float getPrefWidth() {
                return getStyle().knobBefore.getMinWidth();
            }
        };
        timeProgressBar.setRound(false);
        timeProgressBar.setTouchable(Touchable.disabled);
        topTable.add(timeProgressBar).expandX().padTop(BUTTON_PADDING_TOP);

        settingsButton = new TextButton("game.settings", game.skin, "boldestMedium");
        settingsButton.setVisible(false); //TODO Create settings
        topTable.add(settingsButton).padRight(BUTTON_PADDING_HORIZONTAL).padTop(BUTTON_PADDING_TOP).right();

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
        timeOverTable.setBackground("black");

        timeOverLabel = new Label("game.time-over", game.skin, "boldestLarge");
        timeOverTable.add(timeOverLabel);

        timeOverStage.addActor(timeOverTable);

        Table victoryTable = new Table(game.skin);
        victoryTable.setFillParent(true);

        Image victoryBackgroundImage = new Image(game.skin, "game/victory-background");
        victoryBackgroundImage.setTouchable(Touchable.disabled);
        blueVictoryLabel = new Label("game.blue-victory", game.skin, "boldestMedium");
        blueVictoryLabel.setAlignment(Align.center);
        blueVictoryLabel.setTouchable(Touchable.disabled);
        redVictoryLabel = new Label("game.red-victory", game.skin, "boldestMedium");
        redVictoryLabel.setTouchable(Touchable.disabled);
        redVictoryLabel.setAlignment(Align.center);
        victoryLabel = new Label("game.victory", game.skin, "boldestMedium");
        victoryLabel.setTouchable(Touchable.disabled);
        victoryLabel.setAlignment(Align.center);
        victoryStack = victoryTable.stack(victoryBackgroundImage, blueVictoryLabel, redVictoryLabel, victoryLabel).getActor();

        victoryTable.row();

        victoryMenuButton = new TextButton("game.victory-menu", game.skin, "boldestMedium");
        victoryMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.menuMusic.play();
                game.setScreen(game.menuScreen);
            }
        });
        victoryTable.add(victoryMenuButton);

        victoryStage.addActor(victoryTable);

        barTable = new Table(game.skin);
        barTable.setFillParent(true);

        topBarImage = new Image(game.skin, "black");
        barTable.add(topBarImage).growX().height(BAR_HEIGHT);

        barTable.row();
        barTable.add().grow();
        barTable.row();

        bottomBarImage = new Image(game.skin, "black");
        barTable.add(bottomBarImage).growX().height(BAR_HEIGHT);

        barStage.addActor(barTable);
    }

    public void createGame(int level){
        this.level = level;
        maxTime = Player.TIME_MAX_DEFAULT;
        player = null;
        game.timeFillSound.play(game.prefs.getFloat("settings.sound", SOUND_DEFAULT));
        uiStage.getRoot().setColor(1f, 1f, 1f, 1f);
        uiStage.getRoot().setTouchable(Touchable.childrenOnly);
        victoryStage.getRoot().setColor(1f, 1f, 1f, 0f);
        victoryStage.getRoot().setTouchable(Touchable.disabled);
        createLevel();
        if(level == 0){
            barStage.getRoot().setColor(1f, 1f, 1f, 1f);
            ((OrthographicCamera)gameStage.getCamera()).zoom = CUT_SCENE_ZOOM;
            barTable.invalidate();
            barTable.validate();
            Gdx.input.setInputProcessor(null);
            uiStage.getRoot().setColor(1f, 1f, 1f, 0f);
            final Action animateAction = new Action() {
                @Override
                public boolean act(float delta) {
                    return scientist.isDying();
                }
            };
            gameStage.addAction(Actions.sequence(animateAction, Actions.parallel(new Action() {
                boolean firstRun = true;
                Action action;

                @Override
                public boolean act(float delta) {
                    if (firstRun) {
                        action = Actions.moveBy(0f, topBarImage.getHeight(), CUT_SCENE_END_DURATION, Interpolation.fade);
                        topBarImage.addAction(action);
                        firstRun = false;
                    }
                    return action.getActor() == null;
                }
            }, new Action() {
                boolean firstRun = true;
                Action action;

                @Override
                public boolean act(float delta) {
                    if (firstRun) {
                        action = Actions.moveBy(0f, -bottomBarImage.getHeight(), CUT_SCENE_END_DURATION, Interpolation.fade);
                        bottomBarImage.addAction(action);
                        firstRun = false;
                    }
                    return action.getActor() == null;
                }
            }, new Action() {
                boolean firstRun = true;
                Action action;

                @Override
                public boolean act(float delta) {
                    if (firstRun) {
                        action = Actions.alpha(1f, CUT_SCENE_END_DURATION, Interpolation.fade);
                        uiStage.addAction(action);
                        firstRun = false;
                    }
                    return action.getActor() == null;
                }
            }, new TemporalAction(CUT_SCENE_END_DURATION, Interpolation.fade) {
                @Override
                protected void update(float percent) {
                    ((OrthographicCamera) gameStage.getCamera()).zoom = getInterpolation().apply(CUT_SCENE_ZOOM, 1f, percent);
                    gameStage.getCamera().position.y = getInterpolation().apply(gameStage.getHeight() / 2f - BAR_HEIGHT / CUT_SCENE_ZOOM, gameStage.getHeight() / 2f, percent);
                }
            }), Actions.addAction(Actions.alpha(0f), barStage.getRoot()), Actions.run(new Runnable() {
                @Override
                public void run() {
                    Gdx.input.setInputProcessor(inputMultiplexer);
                }
            })));
            gameStage.addAction(new Action() {
                @Override
                public boolean act(float delta) {
                    gameStage.getCamera().position.y = gameStage.getHeight()/2f-BAR_HEIGHT/CUT_SCENE_ZOOM;
                    return animateAction.act(0f);
                }
            });
        }else{
            barStage.getRoot().setColor(1f, 1f, 1f, 0f);
        }
    }

    public void createLevel(){
        finishing = false;
        switch(level) {
            case 0:
                if (player != null) {
                    maxTime = player.getMaxTime();
                }
                gameStage.clear();
                Lamp[] lamps = new Lamp[]{
                        new Lamp(115f, Ground.HEIGHT),
                        new Lamp(3015f, Ground.HEIGHT),
                        new Lamp(5400f, Ground.HEIGHT),
                        new Lamp(9090f, Ground.HEIGHT)
                };
                for (Lamp lamp : lamps) {
                    gameStage.addActor(lamp);
                }
                gameStage.addActor(new Bench(500f, Ground.HEIGHT));
                gameStage.addActor(new Bench(6000f, Ground.HEIGHT));
                gameStage.addActor(new Truck(2660f, Ground.HEIGHT));
                gameStage.addActor(new Shop(4370f, Ground.HEIGHT));
                player = new Player(BORDERS_LEFT[level], BORDERS_RIGHT[level], maxTime);
                portal = new Portal(player, 8950f, Ground.HEIGHT);
                gameStage.addActor(portal);
                hatch = new Hatch(8950f, Ground.HEIGHT);
                gameStage.addActor(hatch);
                Label scientistLabel = new Label(game.bundle.get("game.scientist"), game.skin, "gameLabel");
                scientistLabel.setAlignment(Align.center);
                scientistLabel.setPosition(50f, 100f);
                gameStage.addActor(scientistLabel);
                Label drugDealerLabel = new Label(game.bundle.get("game.drug-dealer"), game.skin, "gameLabel");
                drugDealerLabel.setPosition(3450f, 100f);
                drugDealerLabel.setAlignment(Align.center);
                gameStage.addActor(drugDealerLabel);
                gameStage.setKeyboardFocus(player);
                gameStage.addActor(player);
                gameStage.addActor(new Ground());
                bubble = new Bubble(player);
                scientist = new Scientist(player, bubble, scientistLabel, BORDERS_LEFT[0], BORDERS_RIGHT[0], 650f, 0f, false, false);
                gameStage.addActor(scientist);
                gameStage.addActor(new Scientist(player, bubble, scientistLabel, BORDERS_LEFT[level], BORDERS_RIGHT[level], 1800f, 0f, false, true));
                gameStage.addActor(new Scientist(player, bubble, scientistLabel, BORDERS_LEFT[level], BORDERS_RIGHT[level], 2670f, 0f, false, true));
                gameStage.addActor(new Scientist(player, bubble, scientistLabel, BORDERS_LEFT[level], BORDERS_RIGHT[level], 2960f, 0f, true, true));
                gameStage.addActor(new DrugDealer(player, bubble, drugDealerLabel, BORDERS_LEFT[level], BORDERS_RIGHT[level], 3260f));
                gameStage.addActor(new Scientist(player, bubble, scientistLabel, BORDERS_LEFT[level], BORDERS_RIGHT[level], 4650f, 490f, false, true));
                gameStage.addActor(new Eye(player, bubble, BORDERS_LEFT[level], BORDERS_RIGHT[level], 5250f, 500f));
                gameStage.addActor(new Scientist(player, bubble, scientistLabel, BORDERS_LEFT[level], BORDERS_RIGHT[level], 5350f, 0f, false, true));
                gameStage.addActor(new Eye(player, bubble, BORDERS_LEFT[level], BORDERS_RIGHT[level], 6050f, 800f));
                gameStage.addActor(new Eye(player, bubble, BORDERS_LEFT[level], BORDERS_RIGHT[level], 6900f, 580f));
                gameStage.addActor(new Scientist(player, bubble, scientistLabel, BORDERS_LEFT[level], BORDERS_RIGHT[level], 7000f, 0f, false, true));
                gameStage.addActor(new Eye(player, bubble, BORDERS_LEFT[level], BORDERS_RIGHT[level], 7150f, 780f));
                gameStage.addActor(new Scientist(player, bubble, scientistLabel, BORDERS_LEFT[level], BORDERS_RIGHT[level], 8600f, 0f, false, true));
                gameStage.addActor(new Eye(player, bubble, BORDERS_LEFT[level], BORDERS_RIGHT[level], 8500f, 580f));
                gameStage.addActor(new Scientist(player, bubble, scientistLabel, BORDERS_LEFT[level], BORDERS_RIGHT[level], 9300f, 0f, false, true));
                gameStage.addActor(new Eye(player, bubble, BORDERS_LEFT[level], BORDERS_RIGHT[level], 9200f, 800f));
                for (Lamp lamp : lamps) {
                    gameStage.addActor(new Light(lamp));
                }
                gameStage.addActor(bubble);
                gameStage.addAction(Actions.forever(Actions.delay(Wreckage.SPAWN_INTERVAL, Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        if (!finishing) {
                            gameStage.addActor(new Wreckage(player, bubble, MathUtils.random(-BORDERS_LEFT[level]+WRECKAGE_SAFE_ZONE, BORDERS_RIGHT[level]), gameStage.getHeight(), true));
                        }
                    }
                }))));
                portal.addAction(Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        portal.addAction(Actions.sequence(Actions.delay(MathUtils.random(HoverBoard.SPAWN_INTERVAL_MIN, HoverBoard.SPAWN_INTERVAL_MAX), Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                if (!finishing) {
                                    gameStage.addActor(new HoverBoard(player, bubble, portal.getX() - HoverBoard.SIZE, portal.getY() + HoverBoard.SPAWN_OFFSET_Y, false));
                                }
                            }
                        })), Actions.run(this)));
                    }
                }));
                gameStage.addActor(new Wreckage(player, bubble, scientist.getX(Align.center), gameStage.getHeight(), false));
                break;
        }
        katanaCheckBox.setChecked(true);
    }

    public void finish(){
        finishing = true;
        switch (level){
            case 0:
                bubble.addAction(Actions.alpha(0f));
                player.setAnimations(player.getStayAnimations());
                if(portal.getX(Align.center) > player.getX(Align.center)){
                    player.setScaleX(1f/Player.WIDTH_SCALE);
                }else{
                    player.setScaleX(-1f/Player.WIDTH_SCALE);
                }
                final float currentTime = player.getTime();
                player.addAction(Actions.sequence(new TemporalAction(Player.TIME_FILL_DURATION, Interpolation.fade) {
                    @Override
                    protected void update(float percent) {
                        player.setTime(Interpolation.linear.apply(currentTime, maxTime, percent));
                    }
                }, Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        player.setAnimations(player.getFinishAnimations());
                    }
                }), new Action() {
                    @Override
                    public boolean act(float delta) {
                        player.setTime(Interpolation.linear.apply(0f, maxTime, 1f - player.getAnimationTime() / player.getCurrentAnimations().get(Math.round(player.getTime() / maxTime * Player.FULLNESS_LEVEL_MAX)).getAnimationDuration()));
                        return player.getCurrentAnimations().get(Math.round(player.getTime() / maxTime * Player.FULLNESS_LEVEL_MAX)).isAnimationFinished(player.getAnimationTime());
                    }
                }, Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        player.setTime(0f);
                        portal.shrink();
                    }
                }), new Action() {
                    @Override
                    public boolean act(float delta) {
                        return portal.isShrinkFinished();
                    }
                }, Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        game.hatchSound.play(game.prefs.getFloat("settings.sound", SOUND_DEFAULT));
                        hatch.addAction(Actions.moveBy(-hatch.getWidth(), 0f, Hatch.MOVE_DURATION, Interpolation.fade));
                    }
                }), Actions.delay(Hatch.MOVE_DURATION, Actions.sequence(Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        player.setAnimations(player.getRunAnimations());
                    }
                }), Actions.moveToAligned(hatch.getX(Align.center), player.getY(), Align.center | Align.bottom, Player.MOVE_DURATION, Interpolation.fade), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        player.setAnimations(player.getDownAnimations());
                    }
                }), Actions.moveBy(0f, -(player.getY() + player.getHeight() * player.getScaleY()), Player.DOWN_DURATION, Interpolation.exp5In), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        victoryStack.invalidate();
                        victoryStack.validate();
                    }
                }), Actions.parallel(Actions.addAction(Actions.sequence(Actions.alpha(1f, GameScreen.VICTORY_ALPHA_DURATION, Interpolation.fade), Actions.touchable(Touchable.childrenOnly)), victoryStage.getRoot()), Actions.addAction(Actions.moveToAligned(0f, blueVictoryLabel.getY(), Align.right | Align.bottom, GameScreen.VICTORY_SHIFT_DURATION, Interpolation.fade), blueVictoryLabel), Actions.addAction(Actions.moveTo(victoryStage.getWidth(), redVictoryLabel.getY(), GameScreen.VICTORY_SHIFT_DURATION, Interpolation.fade), redVictoryLabel))))));
                uiStage.addAction(Actions.sequence(Actions.touchable(Touchable.disabled), Actions.alpha(0f, GameScreen.VICTORY_UI_FADE_DURATION, Interpolation.fade)));
                break;
        }
    }

    public boolean isNotFinishing(){
        return !finishing;
    }

    @Override
    public void localize() {
        menuButton.setText(game.bundle.get("game.menu"));
        settingsButton.setText(game.bundle.get("game.settings"));
        //timeLabel is updated each frame

        blueVictoryLabel.setText(game.bundle.get("game.blue-victory"));
        redVictoryLabel.setText(game.bundle.get("game.red-victory"));
        victoryLabel.setText(game.bundle.get("game.victory"));
        victoryMenuButton.setText(game.bundle.get("game.victory-menu"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        game.commonMusic.play();
    }

    @Override
    public void render(float delta) {
        backgroundStage.act(delta);
        gameStage.act(delta);
        if(player.getStage() == null || !finishing && player.getTime() < 0f){
            if(player.getStage() != null) {
                game.timeOverSound.play(game.prefs.getFloat("settings.sound", SOUND_DEFAULT));
            }
            timeOverStage.getRoot().clearActions();
            timeOverStage.addAction(Actions.sequence(Actions.alpha(1f), Actions.alpha(0f, TIME_OVER_ANIMATION_DURATION, Interpolation.fade)));
            timeOverStage.addAction(Actions.delay(TIME_FILL_SOUND_DELAY, Actions.run(new Runnable() {
                @Override
                public void run() {
                    game.respawnSound.play(game.prefs.getFloat("settings.sound", SOUND_DEFAULT));
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
        buildingsStage.getCamera().position.x = BUILDINGS_PARALLAX*gameStage.getCamera().position.x;
        buildingsStage.act(delta); //After gameStage.act because we firstly move the player, then center the camera
        timeProgressBar.setValue(player.getTime()/maxTime);
        String timeString = String.valueOf(player.getTime());
        int index = timeString.indexOf(".");
        if(TIME_LABEL_AFTER_DOT_COUNT == 0){
            timeLabel.setText(game.bundle.format("game.time", timeString.substring(0, index), player.getMaxTime()));
        }else {
            timeLabel.setText(game.bundle.format("game.time", timeString.substring(0, index+1+TIME_LABEL_AFTER_DOT_COUNT), player.getMaxTime()));
        }
        uiStage.act(delta);
        foregroundStage.act(delta);
        timeOverStage.act(delta);
        victoryStage.act(delta);
        barStage.act(delta);
        game.batch.setColor(1f, 1f, 1f, 1f); //Because alpha does not restore color of SpriteBatch
        backgroundStage.draw();
        buildingsStage.draw();
        gameStage.draw();
        uiStage.draw();
        foregroundStage.draw();
        timeOverStage.draw();
        victoryStage.draw();
        barStage.draw();
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
            ((ScreenViewport) victoryStage.getViewport()).setUnitsPerPixel(SCREEN_HEIGHT/height);
            ((ScreenViewport) barStage.getViewport()).setUnitsPerPixel(SCREEN_HEIGHT/height);
        }else{
            ((ScreenViewport) backgroundStage.getViewport()).setUnitsPerPixel(SCREEN_WIDTH/width);
            ((ScreenViewport) buildingsStage.getViewport()).setUnitsPerPixel(SCREEN_WIDTH/width);
            ((ScreenViewport) gameStage.getViewport()).setUnitsPerPixel(SCREEN_WIDTH/width);
            ((ScreenViewport) uiStage.getViewport()).setUnitsPerPixel(SCREEN_WIDTH/width);
            ((ScreenViewport) foregroundStage.getViewport()).setUnitsPerPixel(SCREEN_WIDTH/width);
            ((ScreenViewport) timeOverStage.getViewport()).setUnitsPerPixel(SCREEN_WIDTH/width);
            ((ScreenViewport) victoryStage.getViewport()).setUnitsPerPixel(SCREEN_WIDTH/width);
            ((ScreenViewport) barStage.getViewport()).setUnitsPerPixel(SCREEN_WIDTH/width);
        }
        backgroundStage.getViewport().update(width, height, true);
        buildingsStage.getViewport().update(width, height);
        buildingsStage.getCamera().position.y = buildingsStage.getViewport().getWorldHeight()/2f;
        gameStage.getViewport().update(width, height);
        gameStage.getCamera().position.y = gameStage.getViewport().getWorldHeight()/2f;
        uiStage.getViewport().update(width, height, true);
        foregroundStage.getViewport().update(width, height, true);
        timeOverStage.getViewport().update(width, height, true);
        victoryStage.getViewport().update(width, height, true);
        barStage.getViewport().update(width, height, true);
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
        game.commonMusic.stop();
    }

    @Override
    public void dispose() {
    }
}

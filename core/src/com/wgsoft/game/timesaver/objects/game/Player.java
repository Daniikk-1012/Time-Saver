package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.wgsoft.game.timesaver.screens.GameScreen;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class Player extends Actor {
    public static final int FULLNESS_LEVEL_MAX = 5;
    public static final int STATE_COUNT = 4;

    public static final float FRAME_DURATION = 0.1f;
    public static final float WIDTH_SCALE = 0.55f;
    public static final float HEIGHT_SCALE = 0.6f;
    public static final float SPEED = 500f;
    public static final float SPRINT_SCALE = 1.5f;
    public static final float JUMP_IMPULSE = 1300f;
    public static final float DOWN_IMPULSE = -500f;
    public static final float ATTACK_DURATION = 0.25f;
    public static final float ATTACK_SPEED = 2000f;
    public static final float ATTACK_TIME_CONSUME_SPEED = 10f;
    public static final float STATE_ALPHA_SPEED = 2f;
    public static final float TIME_FILL_DURATION = 1f;
    public static final float MOVE_DURATION = 1f;
    public static final float DOWN_DURATION = 1f;
    public static final float TIME_MAX_DEFAULT = 10f;
    public static final float SIZE = 250f;

    private static class PlayerState{
        float x, y;
        float alpha;
        float scaleX;
        TextureRegion region;
    }

    private final float borderLeft;
    private final float borderRight;
    private Portal portal;
    private final Array<Animation<TextureRegion>> stayAnimations;
    private final Array<Animation<TextureRegion>> runAnimations;
    private final Array<Animation<TextureRegion>> attackAnimations;
    private final Array<Animation<TextureRegion>> upAnimations;
    private final Array<Animation<TextureRegion>> downAnimations;
    private final Array<Animation<TextureRegion>> dieAnimations;
    private final Array<Animation<TextureRegion>> finishAnimations;
    private float animationTime;
    private Array<Animation<TextureRegion>> currentAnimations;
    private boolean left, right, shift;
    private float velocity;
    private boolean ground;
    private float maxTime;
    private float time;
    private float attackTime;
    private PlayerItem playerItem = PlayerItem.KATANA;
    private final Array<PlayerState> playerStates;
    private final float prevMaxTime;
    private boolean finishing;
    private boolean moving;
    private final Array<ParticleEffectPool.PooledEffect> attackParticleEffects;

    public Player(float borderLeft, float borderRight, float time){
        this.borderLeft = borderLeft;
        this.borderRight= borderRight;
        prevMaxTime = maxTime = time;
        this.time = maxTime;
        stayAnimations = new Array<>();
        for(int i = 0; i <= FULLNESS_LEVEL_MAX; i++){
            stayAnimations.add(new Animation<>(FRAME_DURATION, game.skin.getRegions("game/player/"+i+"/stay"), Animation.PlayMode.LOOP));
        }
        runAnimations = new Array<>();
        for(int i = 0; i <= FULLNESS_LEVEL_MAX; i++){
            runAnimations.add(new Animation<>(FRAME_DURATION, game.skin.getRegions("game/player/"+i+"/run"), Animation.PlayMode.LOOP));
        }
        attackAnimations = new Array<>();
        for(int i = 0; i <= FULLNESS_LEVEL_MAX; i++){
            attackAnimations.add(new Animation<>(FRAME_DURATION, game.skin.getRegions("game/player/"+i+"/attack"), Animation.PlayMode.LOOP));
        }
        upAnimations = new Array<>();
        for(int i = 0; i <= FULLNESS_LEVEL_MAX; i++){
            upAnimations.add(new Animation<>(FRAME_DURATION, game.skin.getRegions("game/player/"+i+"/up"), Animation.PlayMode.LOOP));
        }
        downAnimations = new Array<>();
        for(int i = 0; i <= FULLNESS_LEVEL_MAX; i++){
            downAnimations.add(new Animation<>(FRAME_DURATION, game.skin.getRegions("game/player/"+i+"/down"), Animation.PlayMode.LOOP));
        }
        dieAnimations = new Array<>();
        for(int i = 0; i <= FULLNESS_LEVEL_MAX; i++){
            dieAnimations.add(new Animation<>(FRAME_DURATION, game.skin.getRegions("game/player/die")));
        }
        finishAnimations = new Array<>();
        for(int i = 0; i <= FULLNESS_LEVEL_MAX; i++){
            finishAnimations.add(new Animation<>(FRAME_DURATION, game.skin.getRegions("game/player/finish")));
        }
        setAnimations(stayAnimations);
        setSize(SIZE*WIDTH_SCALE, SIZE*HEIGHT_SCALE);
        setX(0f, Align.center);
        setOrigin(Align.center|Align.bottom);
        setScale(1f/WIDTH_SCALE, 1f/HEIGHT_SCALE);
        addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(!finishing && currentAnimations != dieAnimations) {
                    switch (keycode) {
                        case Input.Keys.W:
                        case Input.Keys.UP:
                            jump();
                            return true;
                        case Input.Keys.A:
                        case Input.Keys.LEFT:
                            setLeft(true);
                            return true;
                        case Input.Keys.S:
                        case Input.Keys.DOWN:
                            down();
                            return true;
                        case Input.Keys.D:
                        case Input.Keys.RIGHT:
                            setRight(true);
                            return true;
                        case Input.Keys.SHIFT_LEFT:
                        case Input.Keys.SHIFT_RIGHT:
                            setShift(true);
                            return true;
                        case Input.Keys.SPACE:
                            use();
                            return true;
                    }
                }
                return false;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if(currentAnimations != dieAnimations) {
                    switch (keycode) {
                        case Input.Keys.W:
                        case Input.Keys.UP:
                        case Input.Keys.S:
                        case Input.Keys.DOWN:
                        case Input.Keys.SPACE:
                            return true;
                        case Input.Keys.A:
                        case Input.Keys.LEFT:
                            setLeft(false);
                            return true;
                        case Input.Keys.D:
                        case Input.Keys.RIGHT:
                            setRight(false);
                            return true;
                        case Input.Keys.SHIFT_LEFT:
                        case Input.Keys.SHIFT_RIGHT:
                            setShift(false);
                            return true;
                    }
                }
                return false;
            }
        });
        playerStates = new Array<>();
        attackParticleEffects = new Array<>();
    }

    public void finish(Portal portal, Bubble bubble){
        this.portal = portal;
        bubble.addAction(Actions.alpha(0f));
        finishing = true;
        setAnimations(stayAnimations);
        if(portal.getX(Align.center) > getX(Align.center)){
            setScaleX(1f/WIDTH_SCALE);
        }else{
            setScaleX(-1f/WIDTH_SCALE);
        }
        final float currentTime = time;
        addAction(Actions.sequence(new TemporalAction(TIME_FILL_DURATION, Interpolation.fade) {
            @Override
            protected void update(float percent) {
                time = Interpolation.linear.apply(currentTime, prevMaxTime, percent);
            }
        }, Actions.run(new Runnable() {
            @Override
            public void run() {
                setAnimations(finishAnimations);
            }
        })));
    }

    public boolean isNotFinishing(){
        return !finishing;
    }

    public boolean isGround(){
        return ground;
    }

    public void setPlayerItem(PlayerItem playerItem){
        if(this.playerItem != playerItem){
            this.playerItem = playerItem;
            game.selectSound.play(game.prefs.getFloat("settings.sound", SOUND_DEFAULT));
        }
    }

    public void die(){
        setAnimations(dieAnimations);
        addAction(Actions.delay(currentAnimations.get(Math.round(time / maxTime * FULLNESS_LEVEL_MAX)).getAnimationDuration(), Actions.removeActor()));
        game.deathSound.play(game.prefs.getFloat("settings.sound", SOUND_DEFAULT));
    }

    public boolean isNotAttacking(){
        return !(attackTime > 0f);
    }

    public boolean isNotDying(){
        return currentAnimations != dieAnimations;
    }

    public float getTime(){
        return time;
    }

    public float getMaxTime(){
        return maxTime;
    }

    public void addMaxTime(float time){
        maxTime += time;
    }

    public float getVelocity(){
        return velocity;
    }

    public void use(){
        switch (playerItem) {
            case KATANA:
                attack();
                break;
            case TIME_MINE:
                timeMine();
                break;
            case HOURGLASS:
                break;
        }
    }

    private void attack(){
        if(attackTime <= 0f) {
            playerStates.clear();
            attackTime = ATTACK_DURATION;
            game.slashSound.play(game.prefs.getFloat("settings.sound", SOUND_DEFAULT));
            attackParticleEffects.add(game.attackParticleEffectPool.obtain());
        }
    }

    private void timeMine(){
        if(attackTime <= 0f && time > TimeMine.TIME_CONSUMPTION){
            if(getScaleX() < 0f){
                getStage().addActor(new TimeMine(borderLeft, borderRight, getX()-Bottle.SIZE, getY(Align.center)-Bottle.SIZE/2f, false));
            }else{
                getStage().addActor(new TimeMine(borderLeft, borderRight, getRight(), getY(Align.center)-Bottle.SIZE/2f, true));
            }
            time -= TimeMine.TIME_CONSUMPTION;
        }
    }

    public void jump(){
        if(ground) {
            velocity = JUMP_IMPULSE;
            ground = false;
            game.jumpSound.play(game.prefs.getFloat("settings.sound", SOUND_DEFAULT));
        }
    }

    public void down(){
        if(!ground && velocity > DOWN_IMPULSE){
            velocity = DOWN_IMPULSE;
        }
    }

    public void setLeft(boolean left){
        this.left = left;
    }

    public void setRight(boolean right){
        this.right = right;
    }

    public void setShift(boolean shift){
        this.shift = shift;
    }

    public void stopUp(){
        velocity = 0f;
    }

    public void stopDown(){
        velocity = 0f;
        ground = true;
    }

    private void setAnimations(Array<Animation<TextureRegion>> animations){
        animationTime = 0f;
        currentAnimations = animations;
    }

    public void move(Hatch hatch, Stage victoryStage, final Stack victoryStack, final Label blueVictoryLabel, final Label redVictoryLabel){
        moving = true;
        time = 0f;
        game.hatchSound.play(game.prefs.getFloat("settings.sound", SOUND_DEFAULT));
        hatch.addAction(Actions.moveBy(-hatch.getWidth(), 0f, Hatch.MOVE_DURATION, Interpolation.fade));
        addAction(Actions.delay(Hatch.MOVE_DURATION, Actions.sequence(Actions.run(new Runnable() {
            @Override
            public void run() {
                setAnimations(runAnimations);
            }
        }), Actions.moveToAligned(hatch.getX(Align.center), getY(), Align.center|Align.bottom, MOVE_DURATION, Interpolation.fade), Actions.run(new Runnable() {
            @Override
            public void run() {
                toBack();
                setAnimations(downAnimations);
            }
        }), Actions.moveBy(0f, -(getY()+getHeight()*getScaleY()), DOWN_DURATION, Interpolation.exp5In), Actions.run(new Runnable() {
            @Override
            public void run() {
                victoryStack.invalidate();
                victoryStack.validate();
            }
        }), Actions.parallel(Actions.addAction(Actions.sequence(Actions.alpha(1f, GameScreen.VICTORY_ALPHA_DURATION, Interpolation.fade), Actions.touchable(Touchable.childrenOnly)), victoryStage.getRoot()), Actions.addAction(Actions.moveToAligned(0f, blueVictoryLabel.getY(), Align.right|Align.bottom, GameScreen.VICTORY_SHIFT_DURATION, Interpolation.fade), blueVictoryLabel), Actions.addAction(Actions.moveTo(victoryStage.getWidth(), redVictoryLabel.getY(), GameScreen.VICTORY_SHIFT_DURATION, Interpolation.fade), redVictoryLabel)))));
    }

    public boolean isNotMoving(){
        return !moving;
    }

    @Override
    public boolean remove() {
        for(int i = 0; i < attackParticleEffects.size; i++){
            attackParticleEffects.get(i).free();
        }
        return super.remove();
    }

    @Override
    public void act(float delta) {
        animationTime += delta;
        for (int i = 0; i < playerStates.size; i++) {
            playerStates.get(i).alpha -= delta * STATE_ALPHA_SPEED;
            if (playerStates.get(i).alpha < 0f) {
                playerStates.get(i).alpha = 0f;
            }
        }
        if(!finishing && currentAnimations != dieAnimations) {
            if (attackTime > 0f) {
                time -= delta * ATTACK_TIME_CONSUME_SPEED;
                if (currentAnimations != attackAnimations) {
                    setAnimations(attackAnimations);
                }
                while (playerStates.size < STATE_COUNT * (ATTACK_DURATION - attackTime) / ATTACK_DURATION) {
                    PlayerState playerState = new PlayerState();
                    playerState.x = getX();
                    playerState.y = getY();
                    playerState.alpha = 1f;
                    playerState.region = currentAnimations.get(Math.round(time / maxTime * FULLNESS_LEVEL_MAX)).getKeyFrame(animationTime);
                    playerState.scaleX = getScaleX();
                    playerStates.add(playerState);
                }
                attackTime -= delta;
                if (getScaleX() < 0f) {
                    moveBy(-delta * ATTACK_SPEED, 0f);
                } else {
                    moveBy(delta * ATTACK_SPEED, 0f);
                }
                for (int i = 0; i < getStage().getActors().size; i++) {
                    if (getStage().getActors().get(i) instanceof Monster) {
                        ((Monster) getStage().getActors().get(i)).hit(this);
                    }
                }
            } else {
                if(currentAnimations == attackAnimations){
                    setAnimations(stayAnimations);
                }
                if (left && !right || !left && right || velocity != 0f) {
                    time -= delta;
                }
                if (left != right) {
                    if (left) {
                        if (shift) {
                            moveBy(-delta * SPEED * SPRINT_SCALE, 0f);
                        } else {
                            moveBy(-delta * SPEED, 0f);
                        }
                        setScaleX(-1f / WIDTH_SCALE);
                    } else {
                        if (shift) {
                            moveBy(delta * SPEED * SPRINT_SCALE, 0f);
                        } else {
                            moveBy(delta * SPEED, 0f);
                        }
                        setScaleX(1f / WIDTH_SCALE);
                    }
                    if (velocity == 0f && currentAnimations != runAnimations) {
                        setAnimations(runAnimations);
                    }
                } else {
                    if (velocity == 0f && currentAnimations != stayAnimations) {
                        setAnimations(stayAnimations);
                    }
                }
            }
            if (getX() < borderLeft && getRight() > borderRight) {
                setX((borderLeft + borderRight) / 2f, Align.center);
            } else if (getX() < borderLeft) {
                setX(borderLeft);
            } else if (getRight() > borderRight) {
                setX(borderRight, Align.right);
            }
            velocity -= delta * GameScreen.GRAVITY;
            moveBy(0f, delta * velocity);
            for (int i = 0; i < getStage().getActors().size; i++) {
                if (getStage().getActors().get(i) instanceof Solid) {
                    ((Solid) getStage().getActors().get(i)).overlap(this);
                }
            }
            if (velocity > 0f) {
                if (currentAnimations != upAnimations && attackTime <= 0f) {
                    setAnimations(upAnimations);
                }
                ground = false;
            } else if (velocity < 0f) {
                if (currentAnimations != downAnimations && attackTime <= 0f) {
                    setAnimations(downAnimations);
                }
                ground = false;
            }
            if (getX(Align.center) < borderLeft + getStage().getWidth() / 2f && getX(Align.center) > borderRight - getStage().getWidth() / 2f) {
                getStage().getCamera().position.x = (borderLeft + borderRight) / 2f;
            } else if (getX(Align.center) < borderLeft + getStage().getWidth() / 2f) {
                getStage().getCamera().position.x = borderLeft + getStage().getWidth() / 2f;
            } else {
                getStage().getCamera().position.x = Math.min(getX(Align.center), borderRight - getStage().getWidth() / 2f);
            }
        }else if(currentAnimations == finishAnimations &&  currentAnimations.get(Math.round(time/maxTime*FULLNESS_LEVEL_MAX)).isAnimationFinished(animationTime) && portal.isNotShrinking()){
            portal.shrink();
        }else if(currentAnimations == finishAnimations && !currentAnimations.get(Math.round(time/maxTime*FULLNESS_LEVEL_MAX)).isAnimationFinished(animationTime)){
            time = Interpolation.linear.apply(0f, prevMaxTime, 1f-animationTime/currentAnimations.get(Math.round(time/maxTime*FULLNESS_LEVEL_MAX)).getAnimationDuration());
        }
        super.act(delta);
        for(int i = 0; i < attackParticleEffects.size; i++) {
            attackParticleEffects.get(i).setPosition(getX(Align.center), getY(Align.center));
            if(getScaleX() < 0f) {
                attackParticleEffects.get(i).getEmitters().first().getAngle().setLow(360f-(attackParticleEffects.get(i).getEmitters().first().getAngle().getHighMax()-attackParticleEffects.get(i).getEmitters().first().getAngle().getHighMin())/2f);
            }else if(getScaleX() > 0f){
                attackParticleEffects.get(i).getEmitters().first().getAngle().setLow(180f-(attackParticleEffects.get(i).getEmitters().first().getAngle().getHighMax()-attackParticleEffects.get(i).getEmitters().first().getAngle().getHighMin())/2f);
            }
            attackParticleEffects.get(i).update(delta);
            if(attackParticleEffects.get(i).isComplete()){
                attackParticleEffects.get(i).free();
                attackParticleEffects.removeIndex(i);
                i--;
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for(int i = 0; i < playerStates.size; i++){
            batch.setColor(1f, 1f, 1f, playerStates.get(i).alpha);
            batch.draw(playerStates.get(i).region, playerStates.get(i).x, playerStates.get(i).y, getOriginX(), getOriginY(), getWidth(), getHeight(), playerStates.get(i).scaleX, getScaleY(), getRotation());
        }
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(currentAnimations.get(Math.round(time/maxTime*FULLNESS_LEVEL_MAX)).getKeyFrame(animationTime), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        for(int i = 0; i < attackParticleEffects.size; i++) {
            attackParticleEffects.get(i).draw(batch);
        }
    }
}

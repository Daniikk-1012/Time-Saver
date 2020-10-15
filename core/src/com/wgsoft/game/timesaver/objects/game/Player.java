package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import static com.wgsoft.game.timesaver.Const.*;

public class Player extends Actor {
    private static class PlayerState{
        float x, y;
        float alpha;
        float scaleX;
        TextureRegion region;
    }

    private Portal portal;
    private final Array<Animation<TextureRegion>> stayAnimations;
    private final Array<Animation<TextureRegion>> runAnimations;
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

    public Player(float time){
        prevMaxTime = maxTime = time;
        this.time = maxTime;
        stayAnimations = new Array<>();
        for(int i = 0; i <= GAME_PLAYER_FULLNESS_LEVEL_MAX; i++){
            stayAnimations.add(new Animation<>(GAME_PLAYER_FRAME_DURATION, game.skin.getRegions("game/player/"+i+"/stay"), Animation.PlayMode.LOOP));
        }
        runAnimations = new Array<>();
        for(int i = 0; i <= GAME_PLAYER_FULLNESS_LEVEL_MAX; i++){
            runAnimations.add(new Animation<>(GAME_PLAYER_FRAME_DURATION, game.skin.getRegions("game/player/"+i+"/run"), Animation.PlayMode.LOOP));
        }
        upAnimations = new Array<>();
        for(int i = 0; i <= GAME_PLAYER_FULLNESS_LEVEL_MAX; i++){
            upAnimations.add(new Animation<>(GAME_PLAYER_FRAME_DURATION, game.skin.getRegions("game/player/"+i+"/up"), Animation.PlayMode.LOOP));
        }
        downAnimations = new Array<>();
        for(int i = 0; i <= GAME_PLAYER_FULLNESS_LEVEL_MAX; i++){
            downAnimations.add(new Animation<>(GAME_PLAYER_FRAME_DURATION, game.skin.getRegions("game/player/"+i+"/down"), Animation.PlayMode.LOOP));
        }
        dieAnimations = new Array<>();
        for(int i = 0; i <= GAME_PLAYER_FULLNESS_LEVEL_MAX; i++){
            dieAnimations.add(new Animation<>(GAME_PLAYER_FRAME_DURATION, game.skin.getRegions("game/player/die")));
        }
        finishAnimations = new Array<>();
        for(int i = 0; i <= GAME_PLAYER_FULLNESS_LEVEL_MAX; i++){
            finishAnimations.add(new Animation<>(GAME_PLAYER_FRAME_DURATION, game.skin.getRegions("game/player/finish")));
        }
        setAnimations(stayAnimations);
        setSize(GAME_UNIT_SIZE*GAME_PLAYER_WIDTH_SCALE, GAME_UNIT_SIZE*GAME_PLAYER_HEIGHT_SCALE);
        setX(0f, Align.center);
        setOrigin(Align.center|Align.bottom);
        setScale(1f/GAME_PLAYER_WIDTH_SCALE, 1f/GAME_PLAYER_HEIGHT_SCALE);
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
    }

    public void finish(Portal portal, Bubble bubble){
        this.portal = portal;
        bubble.addAction(Actions.alpha(0f));
        finishing = true;
        setAnimations(stayAnimations);
        if(portal.getX(Align.center) > getX(Align.center)){
            setScaleX(1f/GAME_PLAYER_WIDTH_SCALE);
        }else{
            setScaleX(-1f/GAME_PLAYER_WIDTH_SCALE);
        }
        final float currentTime = time;
        addAction(Actions.sequence(new TemporalAction(GAME_PLAYER_TIME_FILL_DURATION, Interpolation.fade) {
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
            game.selectSound.play(game.prefs.getFloat("settings.sound", SETTINGS_SOUND_DEFAULT));
        }
    }

    public void die(){
        setAnimations(dieAnimations);
        addAction(Actions.delay(currentAnimations.get(Math.round(time / maxTime * GAME_PLAYER_FULLNESS_LEVEL_MAX)).getAnimationDuration(), Actions.removeActor()));
        game.deathSound.play(game.prefs.getFloat("settings.sound", SETTINGS_SOUND_DEFAULT));
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
            attackTime = GAME_PLAYER_ATTACK_DURATION;
            game.slashSound.play(game.prefs.getFloat("settings.sound", SETTINGS_SOUND_DEFAULT));
        }
    }

    private void timeMine(){
        if(attackTime <= 0f && time > GAME_TIME_MINE_TIME_CONSUMPTION){
            if(getScaleX() < 0f){
                getStage().addActor(new TimeMine(getX()-GAME_THROWABLE_SIZE, getY(Align.center)-GAME_THROWABLE_SIZE/2f, false));
            }else{
                getStage().addActor(new TimeMine(getRight(), getY(Align.center)-GAME_THROWABLE_SIZE/2f, true));
            }
            time -= GAME_TIME_MINE_TIME_CONSUMPTION;
        }
    }

    public void jump(){
        if(ground) {
            velocity = GAME_PLAYER_JUMP_IMPULSE;
            ground = false;
            game.jumpSound.play(game.prefs.getFloat("settings.sound", SETTINGS_SOUND_DEFAULT));
        }
    }

    public void down(){
        if(!ground && velocity > GAME_PLAYER_DOWN_IMPULSE){
            velocity = GAME_PLAYER_DOWN_IMPULSE;
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

    public void move(Hatch hatch){
        moving = true;
        time = 0f;
        game.hatchSound.play(game.prefs.getFloat("settings.sound", SETTINGS_SOUND_DEFAULT));
        hatch.addAction(Actions.moveBy(-hatch.getWidth(), 0f, GAME_HATCH_MOVE_DURATION, Interpolation.fade));
        addAction(Actions.delay(GAME_HATCH_MOVE_DURATION, Actions.sequence(Actions.run(new Runnable() {
            @Override
            public void run() {
                setAnimations(runAnimations);
            }
        }), Actions.moveToAligned(hatch.getX(Align.center), getY(), Align.center|Align.bottom, GAME_PLAYER_MOVE_DURATION, Interpolation.fade), Actions.run(new Runnable() {
            @Override
            public void run() {
                toBack();
                setAnimations(downAnimations);
            }
        }), Actions.moveBy(0f, -(getY()+getHeight()*getScaleY()), GAME_PLAYER_DOWN_DURATION, Interpolation.pow2In), Actions.run(new Runnable() {
            @Override
            public void run() {
                game.setScreen(game.menuScreen);
            }
        }))));
    }

    public boolean isNotMoving(){
        return !moving;
    }

    @Override
    public void act(float delta) {
        animationTime += delta;
        for (int i = 0; i < playerStates.size; i++) {
            playerStates.get(i).alpha -= delta * GAME_PLAYER_STATE_ALPHA_SPEED;
            if (playerStates.get(i).alpha < 0f) {
                playerStates.get(i).alpha = 0f;
            }
        }
        if(!finishing && currentAnimations != dieAnimations) {
            if (attackTime > 0f) {
                time -= delta * GAME_PLAYER_ATTACK_TIME_CONSUME_SPEED;
                while (playerStates.size < GAME_PLAYER_STATE_COUNT * (GAME_PLAYER_ATTACK_DURATION - attackTime) / GAME_PLAYER_ATTACK_DURATION) {
                    PlayerState playerState = new PlayerState();
                    playerState.x = getX();
                    playerState.y = getY();
                    playerState.alpha = 1f;
                    playerState.region = currentAnimations.get(Math.round(time / maxTime * GAME_PLAYER_FULLNESS_LEVEL_MAX)).getKeyFrame(animationTime);
                    playerState.scaleX = getScaleX();
                    playerStates.add(playerState);
                }
                attackTime -= delta;
                if (velocity == 0f && currentAnimations != runAnimations) {
                    setAnimations(runAnimations);
                }
                if (getScaleX() < 0f) {
                    moveBy(-delta * GAME_PLAYER_ATTACK_SPEED, 0f);
                } else {
                    moveBy(delta * GAME_PLAYER_ATTACK_SPEED, 0f);
                }
                for (int i = 0; i < getStage().getActors().size; i++) {
                    if (getStage().getActors().get(i) instanceof Hitable) {
                        ((Hitable) getStage().getActors().get(i)).hit(this);
                    }
                }
            } else {
                if (left && !right || !left && right || velocity != 0f) {
                    time -= delta;
                }
                if (left != right) {
                    if (left) {
                        if (shift) {
                            moveBy(-delta * GAME_PLAYER_SPEED * GAME_PLAYER_SPRINT_SCALE, 0f);
                        } else {
                            moveBy(-delta * GAME_PLAYER_SPEED, 0f);
                        }
                        setScaleX(-1f / GAME_PLAYER_WIDTH_SCALE);
                    } else {
                        if (shift) {
                            moveBy(delta * GAME_PLAYER_SPEED * GAME_PLAYER_SPRINT_SCALE, 0f);
                        } else {
                            moveBy(delta * GAME_PLAYER_SPEED, 0f);
                        }
                        setScaleX(1f / GAME_PLAYER_WIDTH_SCALE);
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
            if (getX() < GAME_BORDER_LEFT && getRight() > GAME_BORDER_RIGHT) {
                setX((GAME_BORDER_LEFT + GAME_BORDER_RIGHT) / 2f, Align.center);
            } else if (getX() < GAME_BORDER_LEFT) {
                setX(GAME_BORDER_LEFT);
            } else if (getRight() > GAME_BORDER_RIGHT) {
                setX(GAME_BORDER_RIGHT, Align.right);
            }
            velocity -= delta * GAME_GRAVITY;
            moveBy(0f, delta * velocity);
            for (int i = 0; i < getStage().getActors().size; i++) {
                if (getStage().getActors().get(i) instanceof Solid) {
                    ((Solid) getStage().getActors().get(i)).overlap(this);
                }
            }
            if (velocity > 0f) {
                if (currentAnimations != upAnimations) {
                    setAnimations(upAnimations);
                }
                ground = false;
            } else if (velocity < 0f) {
                if (currentAnimations != downAnimations) {
                    setAnimations(downAnimations);
                }
                ground = false;
            }
            if (getX(Align.center) < GAME_BORDER_LEFT + getStage().getWidth() / 2f && getX(Align.center) > GAME_BORDER_RIGHT - getStage().getWidth() / 2f) {
                getStage().getCamera().position.x = (GAME_BORDER_LEFT + GAME_BORDER_RIGHT) / 2f;
            } else if (getX(Align.center) < GAME_BORDER_LEFT + getStage().getWidth() / 2f) {
                getStage().getCamera().position.x = GAME_BORDER_LEFT + getStage().getWidth() / 2f;
            } else {
                getStage().getCamera().position.x = Math.min(getX(Align.center), GAME_BORDER_RIGHT - getStage().getWidth() / 2f);
            }
        }else if(currentAnimations == finishAnimations &&  currentAnimations.get(Math.round(time/maxTime*GAME_PLAYER_FULLNESS_LEVEL_MAX)).isAnimationFinished(animationTime) && portal.isNotShrinking()){
            portal.shrink();
        }else if(currentAnimations == finishAnimations && !currentAnimations.get(Math.round(time/maxTime*GAME_PLAYER_FULLNESS_LEVEL_MAX)).isAnimationFinished(animationTime)){
            time = Interpolation.linear.apply(0f, prevMaxTime, 1f-animationTime/currentAnimations.get(Math.round(time/maxTime*GAME_PLAYER_FULLNESS_LEVEL_MAX)).getAnimationDuration());
        }
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for(int i = 0; i < playerStates.size; i++){
            batch.setColor(1f, 1f, 1f, playerStates.get(i).alpha);
            batch.draw(playerStates.get(i).region, playerStates.get(i).x, playerStates.get(i).y, getOriginX(), getOriginY(), getWidth(), getHeight(), playerStates.get(i).scaleX, getScaleY(), getRotation());
        }
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(currentAnimations.get(Math.round(time/maxTime*GAME_PLAYER_FULLNESS_LEVEL_MAX)).getKeyFrame(animationTime), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}

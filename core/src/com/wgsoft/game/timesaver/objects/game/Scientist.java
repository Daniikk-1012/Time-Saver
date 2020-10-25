package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.wgsoft.game.timesaver.screens.GameScreen;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class Scientist extends Actor implements Monster {
    public static final float WIDTH_SCALE = 0.5f;
    public static final float HEIGHT_SCALE = 1f;
    public static final float STAY_FRAME_DURATION = 0.5f;
    public static final float DIE_FRAME_DURATION = 0.1f;
    public static final float RUN_FRAME_DURATION = 0.4f;
    public static final float ATTACK_FRAME_DURATION = 0.2f;
    public static final float DEATH_MAX_TIME_BONUS = 0.5f;
    public static final float SPEED = 100f;
    public static final float ATTACK_INTERVAL = 1f;
    public static final float SIZE = 250f;

    private final Player player;
    private final Bubble bubble;
    private final Label label;
    private final float borderLeft;
    private final float borderRight;
    private final Animation<TextureRegion> stayAnimation;
    private final Animation<TextureRegion> runAnimation;
    private final Animation<TextureRegion> attackAnimation;
    private final Animation<TextureRegion> dieAnimation;
    private float animationTime;
    private Animation<TextureRegion> currentAnimation;
    private float velocity;
    private float attackTime;
    private final ParticleEffectPool.PooledEffect bloodParticleEffect;

    //x is for left side
    public Scientist(Player player, Bubble bubble, Label label, float borderLeft, float borderRight, float x, float y, boolean right){
        this.player = player;
        this.bubble = bubble;
        this.label = label;
        this.borderLeft = borderLeft;
        this.borderRight = borderRight;
        setBounds(x, y, SIZE*WIDTH_SCALE, SIZE*HEIGHT_SCALE);
        setOrigin(Align.right|Align.bottom);
        if(right) {
            setScale(1f / WIDTH_SCALE, 1f / HEIGHT_SCALE);
        }else{
            setScale(-1f / WIDTH_SCALE, 1f / HEIGHT_SCALE);
        }
        stayAnimation = new Animation<>(STAY_FRAME_DURATION, game.skin.getRegions("game/scientist/stay"), Animation.PlayMode.LOOP);
        runAnimation = new Animation<>(RUN_FRAME_DURATION, game.skin.getRegions("game/scientist/run"), Animation.PlayMode.LOOP);
        attackAnimation = new Animation<>(ATTACK_FRAME_DURATION, game.skin.getRegions("game/scientist/attack"));
        dieAnimation = new Animation<>(DIE_FRAME_DURATION, game.skin.getRegions("game/scientist/die"));
        setAnimation(stayAnimation);
        bloodParticleEffect = game.bloodParticleEffectPool.obtain();
        bloodParticleEffect.start();
    }

    private float sqr(float x){
        return x*x;
    }

    @Override
    public void hit(Player player) {
        if(currentAnimation != dieAnimation && player.getX() < getRight() && player.getRight() > getX() && player.getY() < getTop() && player.getTop() > getY()){
            die();
        }
    }

    @Override
    public void hit(TimeMine timeMine) {
        if(currentAnimation != dieAnimation && timeMine.getX() < getRight() && timeMine.getRight() > getX() && timeMine.getY() < getTop() && timeMine.getTop() > getY()){
            die();
            timeMine.remove();
        }
    }

    private void die(){
        setAnimation(dieAnimation);
        game.monsterDeathSound.play(game.prefs.getFloat("settings.sound", SOUND_DEFAULT));
        bloodParticleEffect.allowCompletion();
        player.addMaxTime(DEATH_MAX_TIME_BONUS);
        if(label != null){
            label.remove();
        }
    }

    public float getVelocity(){
        return velocity;
    }

    public void stopUp(){
        velocity = 0f;
    }

    public void stopDown(){
        velocity = 0f;
    }

    private void setAnimation(Animation<TextureRegion> animation){
        animationTime = 0f;
        currentAnimation = animation;
    }

    @Override
    public void act(float delta) {
        boolean inBubble = sqr(getX(Align.center)-bubble.getX(Align.center))+sqr(getY(Align.center)-bubble.getY(Align.center)) < sqr(bubble.getWidth()/2f);
        if(inBubble){
            animationTime += delta;
        }else{
            animationTime += delta*Bubble.OUTSIDE_SPEED_SCALE;
        }
        if(currentAnimation != dieAnimation) {
            if(player.getX(Align.center) < getX(Align.center) && getX(Align.center)-player.getRight() < Monster.VISIBLE_RADIUS){
                if(inBubble) {
                    attackTime += delta;
                }else{
                    attackTime += delta*Bubble.OUTSIDE_SPEED_SCALE;
                }
                while(attackTime >= ATTACK_INTERVAL){
                    getStage().addActor(new Bottle(player, bubble, getX()-Bottle.SIZE, getY(Align.center)-Bottle.SIZE/2f, false));
                    if(currentAnimation != attackAnimation) {
                        setAnimation(attackAnimation);
                    }
                    attackTime -= ATTACK_INTERVAL;
                }
                if(inBubble) {
                    moveBy(-delta * SPEED, 0f);
                }else{
                    moveBy(-delta * SPEED * Bubble.OUTSIDE_SPEED_SCALE, 0f);
                }
                if((currentAnimation != attackAnimation || currentAnimation.isAnimationFinished(animationTime)) && currentAnimation != runAnimation){
                    setAnimation(runAnimation);
                }
                setScaleX(-1f/WIDTH_SCALE);
            }else if(player.getX(Align.center) > getX(Align.center) && player.getX()-getX(Align.center) < Monster.VISIBLE_RADIUS){
                if(inBubble) {
                    attackTime += delta;
                }else{
                    attackTime += delta*Bubble.OUTSIDE_SPEED_SCALE;
                }
                while(attackTime >= ATTACK_INTERVAL){
                    getStage().addActor(new Bottle(player, bubble, getRight(), getY(Align.center)-Bottle.SIZE/2f, true));
                    if(currentAnimation != attackAnimation) {
                        setAnimation(attackAnimation);
                    }
                    attackTime -= ATTACK_INTERVAL;
                }
                if(inBubble) {
                    moveBy(delta * SPEED, 0f);
                }else{
                    moveBy(delta * SPEED * Bubble.OUTSIDE_SPEED_SCALE, 0f);
                }
                if((currentAnimation != attackAnimation || currentAnimation.isAnimationFinished(animationTime)) && currentAnimation != runAnimation){
                    setAnimation(runAnimation);
                }
                setScaleX(1f/WIDTH_SCALE);
            }else if((currentAnimation != attackAnimation || currentAnimation.isAnimationFinished(animationTime)) && currentAnimation != stayAnimation){
                setAnimation(stayAnimation);
            }
            if (getX() < borderLeft && getRight() > borderRight) {
                setX((borderLeft + borderRight) / 2f, Align.center);
            } else if (getX() < borderLeft) {
                setX(borderLeft);
            } else if (getRight() > borderRight) {
                setX(borderRight, Align.right);
            }
            if(inBubble) {
                velocity -= delta * GameScreen.GRAVITY;
                moveBy(0f, delta * velocity);
            }else{
                velocity -= delta * GameScreen.GRAVITY * Bubble.OUTSIDE_SPEED_SCALE;
                moveBy(0f, delta * velocity * Bubble.OUTSIDE_SPEED_SCALE);
            }
            for (int i = 0; i < getStage().getActors().size; i++) {
                if (getStage().getActors().get(i) instanceof Solid) {
                    ((Solid) getStage().getActors().get(i)).overlap(this);
                }
            }
        }else if(currentAnimation.isAnimationFinished(animationTime)){
            remove();
            return;
        }
        super.act(delta);
        bloodParticleEffect.setPosition(getX(Align.center), getY(Align.center));
        bloodParticleEffect.update(delta);
    }

    @Override
    public boolean remove() {
        bloodParticleEffect.free();
        return super.remove();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(getScaleX() < 0f){
            batch.draw(currentAnimation.getKeyFrame(animationTime), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), -getScaleX(), getScaleY(), getRotation());
        }else{
            batch.draw(currentAnimation.getKeyFrame(animationTime), getX()-getWidth(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), -getScaleX(), getScaleY(), getRotation());
        }
        bloodParticleEffect.draw(batch);
    }
}

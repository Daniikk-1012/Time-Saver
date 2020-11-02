package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.wgsoft.game.timesaver.screens.SettingsScreen;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class Eye extends Actor implements Monster {
    public static final float WIDTH_SCALE = 0.8f;
    public static final float HEIGHT_SCALE = 0.8f;
    public static final float FLY_FRAME_DURATION = 0.2f;
    public static final float DIE_FRAME_DURATION = 0.1f;
    public static final float ACCELERATION = 500f;
    public static final float DEATH_MAX_TIME_BONUS = 1.5f;
    public static final float SIZE = 250f;

    private final Player player;
    private final Bubble bubble;
    private final float borderLeft;
    private final float borderRight;
    private final Animation<TextureRegion> dieAnimation;
    private float animationTime;
    private Animation<TextureRegion> currentAnimation;
    private final Vector2 velocity;
    private boolean aggressive;
    private final ParticleEffectPool.PooledEffect bloodParticleEffect;

    //x and y are for left bottom corner
    public Eye(Player player, Bubble bubble, float borderLeft, float borderRight, float x, float y){
        this.player = player;
        this.bubble = bubble;
        this.borderLeft = borderLeft;
        this.borderRight = borderRight;
        setBounds(x, y, SIZE*WIDTH_SCALE, SIZE*HEIGHT_SCALE);
        setOrigin(Align.center);
        setScale(1f/WIDTH_SCALE, 1f/HEIGHT_SCALE);
        Animation<TextureRegion> flyAnimation = new Animation<>(FLY_FRAME_DURATION, game.skin.getRegions("game/eye/fly"), Animation.PlayMode.LOOP);
        dieAnimation = new Animation<>(DIE_FRAME_DURATION, game.skin.getRegions("game/eye/die"));
        setAnimation(flyAnimation);
        velocity = new Vector2();
        bloodParticleEffect = game.bloodParticleEffectPool.obtain();
        bloodParticleEffect.start();
    }

    private void setAnimation(Animation<TextureRegion> animation){
        animationTime = 0f;
        currentAnimation = animation;
    }

    @Override
    public void hit(Player player) {
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
        game.monsterDeathSound.play(game.prefs.getFloat("settings.sound", SettingsScreen.SOUND_DEFAULT));
        bloodParticleEffect.allowCompletion();
        player.addMaxTime(DEATH_MAX_TIME_BONUS);
    }

    public float getVelocityX(){
        return velocity.x;
    }

    public float getVelocityY(){
        return velocity.y;
    }

    public void stopUp(){
        velocity.y = 0f;
    }

    public void stopLeft(){
        velocity.x = 0f;
    }

    public void stopDown(){
        velocity.y = 0f;
    }

    public void stopRight(){
        velocity.x = 0f;
    }

    private float sqr(float x){
        return x*x;
    }

    @Override
    public boolean remove() {
        bloodParticleEffect.free();
        return super.remove();
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
            if(Math.abs(getX(Align.center)-player.getRight()) < Monster.VISIBLE_RADIUS){
                aggressive = true;
            }
            if(aggressive) {
                if (inBubble) {
                    float x = velocity.x, y = velocity.y;
                    velocity.set(player.getX(Align.center)-getX(Align.center), player.getY(Align.center)-getY(Align.center));
                    if(velocity.isZero()){
                        velocity.set(-x, -y);
                    }
                    velocity.setLength(ACCELERATION*delta);
                    velocity.add(x, y);
                    setRotation(velocity.angleDeg());
                    moveBy(delta * velocity.x, delta * velocity.y);
                } else {
                    float x = velocity.x, y = velocity.y;
                    velocity.set(player.getX(Align.center)-getX(Align.center), player.getY(Align.center)-getY(Align.center));
                    if(velocity.isZero()){
                        velocity.set(-x, -y);
                    }
                    velocity.setLength(Bubble.OUTSIDE_SPEED_SCALE*ACCELERATION*delta);
                    velocity.add(x, y);
                    setRotation(velocity.angleDeg());
                    moveBy(delta * velocity.x * Bubble.OUTSIDE_SPEED_SCALE, delta * velocity.y * Bubble.OUTSIDE_SPEED_SCALE);
                }
            }
            if (getX() < borderLeft && getRight() > borderRight) {
                setX((borderLeft + borderRight) / 2f, Align.center);
            } else if (getX() < borderLeft) {
                setX(borderLeft);
            } else if (getRight() > borderRight) {
                setX(borderRight, Align.right);
            }
            for (int i = 0; i < getStage().getActors().size; i++) {
                if (getStage().getActors().get(i) instanceof Solid) {
                    ((Solid) getStage().getActors().get(i)).overlap(this);
                }
            }
            if(player.isNotDying() && player.getX() < getRight() && player.getRight() > getX() && player.getY() < getTop() && player.getTop() > getY()){
                player.die();
                return;
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
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(currentAnimation.getKeyFrame(animationTime), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        bloodParticleEffect.draw(batch);
    }
}

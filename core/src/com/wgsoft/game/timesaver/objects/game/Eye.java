package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import static com.wgsoft.game.timesaver.Const.*;

public class Eye extends Actor implements Hitable {
    private final Player player;
    private final Bubble bubble;
    private final Animation<TextureRegion> flyAnimation;
    private final Animation<TextureRegion> dieAnimation;
    private float animationTime;
    private Animation<TextureRegion> currentAnimation;
    private final Vector2 velocity;
    private boolean wasInBubble;
    private boolean aggressive;

    //x and y are for left bottom corner
    public Eye(Player player, Bubble bubble, float x, float y){
        this.player = player;
        this.bubble = bubble;
        setBounds(x, y, GAME_UNIT_SIZE*GAME_EYE_WIDTH_SCALE, GAME_UNIT_SIZE*GAME_EYE_HEIGHT_SCALE);
        setOrigin(Align.center);
        setScale(1f/GAME_EYE_WIDTH_SCALE, 1f/GAME_EYE_HEIGHT_SCALE);
        flyAnimation = new Animation<>(GAME_EYE_FLY_FRAME_DURATION, game.skin.getRegions("game/eye/fly"), Animation.PlayMode.LOOP);
        dieAnimation = new Animation<>(GAME_EYE_DIE_FRAME_DURATION, game.skin.getRegions("game/eye/die"));
        setAnimation(flyAnimation);
        velocity = new Vector2();
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
        addAction(Actions.delay(currentAnimation.getAnimationDuration(), Actions.removeActor()));
        game.monsterDeathSound.play(game.prefs.getFloat("settings.sound", SETTINGS_SOUND_DEFAULT));
        player.addMaxTime(GAME_EYE_DEATH_MAX_TIME_BONUS);
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
    public void act(float delta) {
        animationTime += delta;
        if(wasInBubble && sqr(getX(Align.center)-bubble.getX(Align.center))+sqr(getY(Align.center)-bubble.getY(Align.center)) >= sqr(bubble.getWidth()/2f)){
            wasInBubble = false;
            flyAnimation.setFrameDuration(GAME_EYE_FLY_FRAME_DURATION/GAME_OUTSIDE_BUBBLE_SPEED_SCALE);
            dieAnimation.setFrameDuration(GAME_EYE_DIE_FRAME_DURATION/GAME_OUTSIDE_BUBBLE_SPEED_SCALE);
        }else if(!wasInBubble && sqr(getX(Align.center)-bubble.getX(Align.center))+sqr(getY(Align.center)-bubble.getY(Align.center)) < sqr(bubble.getWidth()/2f)){
            wasInBubble = true;
            flyAnimation.setFrameDuration(GAME_EYE_FLY_FRAME_DURATION);
            dieAnimation.setFrameDuration(GAME_EYE_DIE_FRAME_DURATION);
        }
        if(currentAnimation != dieAnimation) {
            if(Math.abs(getX(Align.center)-player.getRight()) < GAME_MONSTER_VISIBLE_RADIUS){
                aggressive = true;
            }
            if(aggressive) {
                if (wasInBubble) {
                    float x = velocity.x, y = velocity.y;
                    velocity.set(player.getX(Align.center)-getX(Align.center), player.getY(Align.center)-getY(Align.center));
                    if(velocity.isZero()){
                        velocity.set(-x, -y);
                    }
                    velocity.setLength(GAME_EYE_ACCELERATION*delta);
                    velocity.add(x, y);
                    setRotation(velocity.angleDeg());
                    moveBy(delta * velocity.x, delta * velocity.y);
                } else {
                    float x = velocity.x, y = velocity.y;
                    velocity.set(player.getX(Align.center)-getX(Align.center), player.getY(Align.center)-getY(Align.center));
                    if(velocity.isZero()){
                        velocity.set(-x, -y);
                    }
                    velocity.setLength(GAME_OUTSIDE_BUBBLE_SPEED_SCALE*GAME_EYE_ACCELERATION*delta);
                    velocity.add(x, y);
                    setRotation(velocity.angleDeg());
                    moveBy(delta * velocity.x * GAME_OUTSIDE_BUBBLE_SPEED_SCALE, delta * velocity.y * GAME_OUTSIDE_BUBBLE_SPEED_SCALE);
                }
            }
            if (getX() < GAME_BORDER_LEFT && getRight() > GAME_BORDER_RIGHT) {
                setX((GAME_BORDER_LEFT + GAME_BORDER_RIGHT) / 2f, Align.center);
            } else if (getX() < GAME_BORDER_LEFT) {
                setX(GAME_BORDER_LEFT);
            } else if (getRight() > GAME_BORDER_RIGHT) {
                setX(GAME_BORDER_RIGHT, Align.right);
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
        }
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(currentAnimation.getKeyFrame(animationTime), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}

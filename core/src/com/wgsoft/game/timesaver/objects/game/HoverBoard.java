package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class HoverBoard extends Actor {
    public static final float START_FRAME_DURATION = 0.5f;
    public static final float FLY_FRAME_DURATION = 0.2f;
    public static final float ACCELERATION = 800f;
    public static final float VELOCITY_MAX = 750f;
    public static final float SIZE = 125f;
    public static final float HEIGHT_SCALE = 0.5f;
    public static final float SPAWN_INTERVAL_MIN = 10f;
    public static final float SPAWN_INTERVAL_MAX = 15f;
    public static final float SPAWN_OFFSET_Y = 75f;

    private final Player player;
    private final Bubble bubble;
    private final Animation<TextureRegion> startAnimation;
    private final Animation<TextureRegion> flyAnimation;
    private float animationTime;
    private Animation<TextureRegion> currentAnimation;
    private float velocity;

    public HoverBoard(Player player, Bubble bubble, float x, float y, boolean right){
        this.player = player;
        this.bubble = bubble;
        startAnimation = new Animation<>(START_FRAME_DURATION, game.skin.getRegions("game/hover-board/start"));
        flyAnimation = new Animation<>(FLY_FRAME_DURATION, game.skin.getRegions("game/hover-board/fly"), Animation.PlayMode.LOOP_PINGPONG);
        setSize(SIZE, SIZE*HEIGHT_SCALE);
        if(right){
            setScaleX(1f);
        }else{
            setScaleX(-1f);
        }
        setScaleY(1f/HEIGHT_SCALE);
        setOrigin(Align.center);
        setPosition(x, y);
        setAnimation(startAnimation);
    }

    private void setAnimation(Animation<TextureRegion> animation){
        animationTime = 0f;
        currentAnimation = animation;
    }

    private float sqr(float x){
        return x*x;
    }

    @Override
    public void act(float delta) {
        boolean inBubble = sqr(getX(Align.center)-bubble.getX(Align.center))+sqr(getY(Align.center)-bubble.getY(Align.center)) < sqr(bubble.getWidth()/2f);
        if(inBubble){
            animationTime += delta;
            if(getScaleX() < 0f){
                velocity -= delta*ACCELERATION;
            }else{
                velocity += delta*ACCELERATION;
            }
        }else{
            animationTime += delta*Bubble.OUTSIDE_SPEED_SCALE;
            if(getScaleX() < 0f){
                velocity -= delta*ACCELERATION*Bubble.OUTSIDE_SPEED_SCALE;
            }else{
                velocity += delta*ACCELERATION*Bubble.OUTSIDE_SPEED_SCALE;

            }
        }
        if(getScaleX() < 0f){
            if(velocity < -VELOCITY_MAX){
                velocity = -VELOCITY_MAX;
            }
        }else{
            if(velocity > -VELOCITY_MAX){
                velocity = VELOCITY_MAX;
            }
        }
        if(inBubble){
            moveBy(delta*velocity, 0f);
        }else{
            moveBy(delta*velocity*Bubble.OUTSIDE_SPEED_SCALE, 0f);
        }
        if(currentAnimation == startAnimation && startAnimation.isAnimationFinished(animationTime)){
            setAnimation(flyAnimation);
        }
        for(int i = 0; i < getStage().getActors().size; i++){
            if(getStage().getActors().get(i) instanceof Solid){
                ((Solid) getStage().getActors().get(i)).overlap(this);
                if(getStage() == null){
                    return;
                }
            }
        }
        if(currentAnimation != startAnimation && game.gameScreen.isNotFinishing() && player.isNotDying() && player.getX() < getRight() && player.getRight() > getX() && player.getY() < getTop() && player.getTop() > getY()){
            if(player.isNotAttacking()) {
                player.die();
            }
            remove();
            return;
        }
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(currentAnimation.getKeyFrame(animationTime), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}

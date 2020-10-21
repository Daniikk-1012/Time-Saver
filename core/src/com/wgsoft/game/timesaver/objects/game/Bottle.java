package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.wgsoft.game.timesaver.screens.GameScreen;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class Bottle extends Actor {
    public static final float ROTATION_SPEED_AMPLITUDE = 180f;
    public static final float SPEED = 500f;
    public static final float IMPULSE = 500f;
    public static final float SIZE = 62.5f;

    private final Player player;
    private final Bubble bubble;
    private final boolean right;
    private float velocity;
    private final float rotationSpeed;

    public Bottle(Player player, Bubble bubble, float x, float y, boolean right){
        this.player = player;
        this.bubble = bubble;
        setBounds(x, y, SIZE, SIZE);
        setOrigin(Align.center);
        rotationSpeed = MathUtils.random(-ROTATION_SPEED_AMPLITUDE, ROTATION_SPEED_AMPLITUDE);
        velocity = IMPULSE;
        this.right = right;
    }

    private float sqr(float x){
        return x*x;
    }

    public float getVelocity(){
        return velocity;
    }

    @Override
    public void act(float delta) {
        boolean inBubble = sqr(getX(Align.center)-bubble.getX(Align.center))+sqr(getY(Align.center)-bubble.getY(Align.center)) < sqr(bubble.getWidth()/2f);
        if(inBubble){
            velocity -= delta * GameScreen.GRAVITY;
        }else{
            velocity -= delta * GameScreen.GRAVITY * Bubble.OUTSIDE_SPEED_SCALE;
        }
        if(right){
            if(inBubble) {
                moveBy(delta * SPEED, delta * velocity);
            }else{
                moveBy(delta * SPEED * Bubble.OUTSIDE_SPEED_SCALE, delta * velocity * Bubble.OUTSIDE_SPEED_SCALE);
            }
        }else{
            if(inBubble) {
                moveBy(-delta * SPEED, delta * velocity);
            }else{
                moveBy(-delta * SPEED * Bubble.OUTSIDE_SPEED_SCALE, delta * velocity * Bubble.OUTSIDE_SPEED_SCALE);
            }
        }
        if(inBubble) {
            rotateBy(delta * rotationSpeed);
        }else{
            rotateBy(delta * rotationSpeed * Bubble.OUTSIDE_SPEED_SCALE);
        }
        for(int i = 0; i < getStage().getActors().size; i++){
            if(getStage().getActors().get(i) instanceof Solid){
                ((Solid) getStage().getActors().get(i)).overlap(this);
                if(getStage() == null){
                    return;
                }
            }
        }
        if(player.isNotFinishing() && player.isNotDying() && player.getX() < getRight() && player.getRight() > getX() && player.getY() < getTop() && player.getTop() > getY()){
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
        batch.draw(game.skin.getRegion("game/bottle"), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}

package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

import static com.wgsoft.game.timesaver.Const.*;

public class Drug extends Actor {
    private final Player player;
    private final Bubble bubble;
    private final boolean right;
    private float velocity;
    private final float rotationSpeed;

    public Drug(Player player, Bubble bubble, float x, float y, boolean right){
        this.player = player;
        this.bubble = bubble;
        setBounds(x, y, GAME_THROWABLE_SIZE, GAME_THROWABLE_SIZE);
        setOrigin(Align.center);
        rotationSpeed = MathUtils.random(-GAME_DRUG_ROTATION_SPEED_AMPLITUDE, GAME_DRUG_ROTATION_SPEED_AMPLITUDE);
        velocity = GAME_DRUG_IMPULSE;
        this.right = right;
    }

    public float getVelocity(){
        return velocity;
    }

    private float sqr(float x){
        return x*x;
    }

    @Override
    public void act(float delta) {
        boolean inBubble = sqr(getX(Align.center)-bubble.getX(Align.center))+sqr(getY(Align.center)-bubble.getY(Align.center)) < sqr(bubble.getWidth()/2f);
        if(inBubble){
            velocity -= delta * GAME_GRAVITY;
        }else{
            velocity -= delta * GAME_GRAVITY * GAME_OUTSIDE_BUBBLE_SPEED_SCALE;
        }
        if(right){
            if(inBubble) {
                moveBy(delta * GAME_DRUG_SPEED, delta * velocity);
            }else{
                moveBy(delta * GAME_DRUG_SPEED * GAME_OUTSIDE_BUBBLE_SPEED_SCALE, delta * velocity * GAME_OUTSIDE_BUBBLE_SPEED_SCALE);
            }
        }else{
            if(inBubble) {
                moveBy(-delta * GAME_DRUG_SPEED, delta * velocity);
            }else{
                moveBy(-delta * GAME_DRUG_SPEED * GAME_OUTSIDE_BUBBLE_SPEED_SCALE, delta * velocity * GAME_OUTSIDE_BUBBLE_SPEED_SCALE);
            }
        }
        if(inBubble) {
            rotateBy(delta * rotationSpeed);
        }else{
            rotateBy(delta * rotationSpeed * GAME_OUTSIDE_BUBBLE_SPEED_SCALE);
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
            player.die();
            remove();
            return;
        }
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(game.skin.getRegion("game/drug"), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}

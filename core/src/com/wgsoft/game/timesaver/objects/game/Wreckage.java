package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

import static com.wgsoft.game.timesaver.Const.*;

public class Wreckage extends Actor {
    private final Player player;
    private final Bubble bubble;
    private final float rotationSpeed;
    private float velocity;

    public Wreckage(Player player, Bubble bubble, float x, float y){
        this.player = player;
        this.bubble = bubble;
        setSize(GAME_WRECKAGE_SIZE, GAME_WRECKAGE_SIZE);
        setPosition(x, y, Align.center|Align.bottom);
        setOrigin(Align.center);
        rotationSpeed = MathUtils.random(-GAME_WRECKAGE_ROTATION_SPEED_AMPLITUDE, GAME_WRECKAGE_ROTATION_SPEED_AMPLITUDE);
    }

    private float sqr(float x){
        return x*x;
    }

    @Override
    public void act(float delta) {
        boolean inBubble = sqr(getX(Align.center)-bubble.getX(Align.center))+sqr(getY(Align.center)-bubble.getY(Align.center)) < sqr(bubble.getWidth()/2f);
        if(inBubble){
            velocity -= delta*GAME_GRAVITY;
            moveBy(0f, delta * velocity);
            rotateBy(delta * rotationSpeed);
        }else{
            velocity -= delta*GAME_GRAVITY*GAME_OUTSIDE_BUBBLE_SPEED_SCALE;
            moveBy(0f, delta * velocity * GAME_OUTSIDE_BUBBLE_SPEED_SCALE);
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
        batch.draw(game.skin.getRegion("game/wreckage"), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}

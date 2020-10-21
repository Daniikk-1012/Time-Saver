package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.wgsoft.game.timesaver.screens.GameScreen;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class Wreckage extends Actor {
    public static final float SIZE = 125f;
    public static final float SPAWN_INTERVAL = 1f;
    public static final float ROTATION_SPEED_AMPLITUDE = 90f;

    private final Player player;
    private final Bubble bubble;
    private final float rotationSpeed;
    private float velocity;

    public Wreckage(Player player, Bubble bubble, float x, float y){
        this.player = player;
        this.bubble = bubble;
        setSize(SIZE, SIZE);
        setPosition(x, y, Align.center|Align.bottom);
        setOrigin(Align.center);
        rotationSpeed = MathUtils.random(-ROTATION_SPEED_AMPLITUDE, ROTATION_SPEED_AMPLITUDE);
    }

    private float sqr(float x){
        return x*x;
    }

    @Override
    public void act(float delta) {
        boolean inBubble = sqr(getX(Align.center)-bubble.getX(Align.center))+sqr(getY(Align.center)-bubble.getY(Align.center)) < sqr(bubble.getWidth()/2f);
        if(inBubble){
            velocity -= delta* GameScreen.GRAVITY;
            moveBy(0f, delta * velocity);
            rotateBy(delta * rotationSpeed);
        }else{
            velocity -= delta*GameScreen.GRAVITY*Bubble.OUTSIDE_SPEED_SCALE;
            moveBy(0f, delta * velocity * Bubble.OUTSIDE_SPEED_SCALE);
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
        if(game.gameScreen.isNotFinishing() && player.isNotDying() && player.getX() < getRight() && player.getRight() > getX() && player.getY() < getTop() && player.getTop() > getY()){
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

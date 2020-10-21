package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class TimeMine extends Actor {
    public static final float SPEED = 2000f;
    public static final float SHIFT_INTERVAL = 0.2f;
    public static final float SHIFT_AMPLITUDE = 50f;
    public static final float TIME_CONSUMPTION = 5f;
    public static final float SIZE = 62.5f;

    private final float borderLeft;
    private final float borderRight;
    private final boolean right;
    private float time;
    private float difference;

    //x and y are for left bottom corner
    public TimeMine(float borderLeft, float borderRight, float x, float y, boolean right){
        this.borderLeft = borderLeft;
        this.borderRight = borderRight;
        setBounds(x, y, SIZE, SIZE);
        this.right = right;
    }

    public float getDifference(){
        return difference;
    }

    @Override
    public void act(float delta) {
        time += delta;
        while(time >= SHIFT_INTERVAL){
            difference = MathUtils.random(-SHIFT_AMPLITUDE, SHIFT_AMPLITUDE);
            moveBy(0f, difference);
            time -= SHIFT_INTERVAL;
        }
        if(right){
            moveBy(delta*SPEED, 0f);
        }else{
            moveBy(-delta*SPEED, 0f);
        }
        for(int i = 0; i < getStage().getActors().size; i++){
            if(getStage().getActors().get(i) instanceof Solid){
                ((Solid) getStage().getActors().get(i)).overlap(this);
                if(getStage() == null){
                    return;
                }
            }
        }
        for(int i = 0; i < getStage().getActors().size; i++){
            if(getStage().getActors().get(i) instanceof Monster){
                ((Monster) getStage().getActors().get(i)).hit(this);
                if(getStage() == null){
                    return;
                }
            }
        }
        if(getRight() < borderLeft || getX() > borderRight){
            remove();
            return;
        }
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(game.skin.getRegion("game/time-mine"), getX(), getY(), getWidth(), getHeight());
    }
}

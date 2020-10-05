package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.wgsoft.game.timesaver.Const.*;

public class TimeMine extends Actor {
    private boolean right;
    private float time;
    private float difference;

    //x and y are for left bottom corner
    public TimeMine(float x, float y, boolean right){
        setBounds(x, y, GAME_THROWABLE_SIZE, GAME_THROWABLE_SIZE);
        this.right = right;
    }

    public float getDifference(){
        return difference;
    }

    @Override
    public void act(float delta) {
        time += delta;
        while(time >= GAME_TIME_MINE_SHIFT_INTERVAL){
            difference = MathUtils.random(-GAME_TIME_MINE_SHIFT_AMPLITUDE, GAME_TIME_MINE_SHIFT_AMPLITUDE);
            moveBy(0f, difference);
            time -= GAME_TIME_MINE_SHIFT_INTERVAL;
        }
        if(right){
            moveBy(delta*GAME_TIME_MINE_SPEED, 0f);
        }else{
            moveBy(-delta*GAME_TIME_MINE_SPEED, 0f);
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
            if(getStage().getActors().get(i) instanceof Hitable){
                ((Hitable) getStage().getActors().get(i)).hit(this);
                if(getStage() == null){
                    return;
                }
            }
        }
        if(getRight() < GAME_BORDER_LEFT || getX() > GAME_BORDER_RIGHT){
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

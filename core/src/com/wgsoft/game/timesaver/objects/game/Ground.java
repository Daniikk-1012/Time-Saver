package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class Ground extends Actor implements Solid {
    public static final float HEIGHT = 65f;

    public Ground(){
        setHeight(HEIGHT);
    }

    @Override
    public void overlap(Player player) {
        if(player.getY() < getTop()){
            player.setY(getTop());
            player.stopDown();
        }
    }

    @Override
    public void overlap(TimeMine timeMine) {
        if(timeMine.getY() < getTop()){
            timeMine.remove();
        }
    }

    @Override
    public void overlap(Scientist scientist) {
        if(scientist.getY() < getTop()){
            scientist.setY(getTop());
            scientist.stopDown();
        }
    }

    @Override
    public void overlap(Bottle bottle) {
        if(bottle.getY() < getTop()){
            bottle.remove();
        }
    }

    @Override
    public void overlap(DrugDealer drugDealer) {
        if(drugDealer.getY() < getTop()){
            drugDealer.setY(getTop());
            drugDealer.stopDown();
        }
    }

    @Override
    public void overlap(Drug drug) {
        if(drug.getY() < getTop()){
            drug.remove();
        }
    }

    @Override
    public void overlap(Eye eye) {
        if(eye.getY() < getTop()){
            eye.setY(getTop());
            eye.stopDown();
        }
    }

    @Override
    public void overlap(Wreckage wreckage) {
        if(wreckage.getY() < getTop()){
            wreckage.remove();
        }
    }

    @Override
    public void overlap(HoverBoard hoverBoard) {
        if(hoverBoard.getY() < getTop()){
            hoverBoard.remove();
        }
    }

    @Override
    public void act(float delta) {
        setWidth(getStage().getWidth());
        setX(getStage().getCamera().position.x, Align.center);
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(game.skin.getRegion("game/ground"), getX(), getY(), getWidth(), getHeight());
    }
}

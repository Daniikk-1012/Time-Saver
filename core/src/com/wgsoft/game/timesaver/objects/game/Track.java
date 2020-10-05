package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

import static com.wgsoft.game.timesaver.Const.*;

public class Track extends Actor implements Solid {
    public Track(float x, float y){
        setBounds(x, y, game.skin.getRegion("game/truck").getRegionWidth()*GAME_TRUCK_WIDTH_SCALE, game.skin.getRegion("game/truck").getRegionHeight()*GAME_TRUCK_HEIGHT_SCALE);
        setOrigin(Align.right|Align.bottom);
        setScale(1f/GAME_TRUCK_WIDTH_SCALE, 1f/GAME_TRUCK_HEIGHT_SCALE);
    }

    @Override
    public void overlap(Player player) {
        if(player.getVelocity() < 0f && player.getRight() > getX() && player.getX() < getRight() && player.getY() > getTop()-GAME_TRUCK_MAX_OFFSET && player.getY() < getTop()){
            player.setY(getTop());
            player.stopDown();
        }
    }

    @Override
    public void overlap(TimeMine timeMine) {
        if(timeMine.getDifference() < 0f && timeMine.getRight() > getX() && timeMine.getX() < getRight() && timeMine.getY() > getTop()-GAME_TRUCK_MAX_OFFSET && timeMine.getY() < getTop()){
            timeMine.remove();
        }
    }

    @Override
    public void overlap(Scientist scientist) {
        if(scientist.getVelocity() < 0f && scientist.getRight() > getX() && scientist.getX() < getRight() && scientist.getY() > getTop()-GAME_TRUCK_MAX_OFFSET && scientist.getY() < getTop()){
            scientist.setY(getTop());
            scientist.stopDown();
        }
    }

    @Override
    public void overlap(Bottle bottle) {
        if(bottle.getVelocity() < 0f && bottle.getRight() > getX() && bottle.getX() < getRight() && bottle.getY() > getTop()-GAME_TRUCK_MAX_OFFSET && bottle.getY() < getTop()){
            bottle.remove();
        }
    }

    @Override
    public void overlap(DrugDealer drugDealer) {
        if(drugDealer.getVelocity() < 0f && drugDealer.getRight() > getX() && drugDealer.getX() < getRight() && drugDealer.getY() > getTop()-GAME_TRUCK_MAX_OFFSET && drugDealer.getY() < getTop()){
            drugDealer.setY(getTop());
            drugDealer.stopDown();
        }
    }

    @Override
    public void overlap(Drug drug) {
        if(drug.getVelocity() < 0f && drug.getRight() > getX() && drug.getX() < getRight() && drug.getY() > getTop()-GAME_TRUCK_MAX_OFFSET && drug.getY() < getTop()){
            drug.remove();
        }
    }

    @Override
    public void overlap(Eye eye) {
        if(eye.getVelocityY() < 0f && eye.getRight() > getX() && eye.getX() < getRight() && eye.getY() > getTop()-GAME_TRUCK_MAX_OFFSET && eye.getY() < getTop()){
            eye.setY(getTop());
            eye.stopDown();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(game.skin.getRegion("game/truck"), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}

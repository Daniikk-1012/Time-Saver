package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class Truck extends Actor implements Solid {
    public static final float WIDTH = 871f;
    public static final float HEIGHT = 458f;
    public static final float WIDTH_SCALE = 0.65f;
    public static final float HEIGHT_SCALE = 1f;
    public static final float MAX_OFFSET = 50f;

    public Truck(float x, float y){
        setBounds(x, y, WIDTH*WIDTH_SCALE, WIDTH*HEIGHT_SCALE);
        setOrigin(Align.right|Align.bottom);
        setScale(1f/WIDTH_SCALE, 1f/HEIGHT_SCALE);
    }

    @Override
    public void overlap(Player player) {
        if(player.getVelocity() < 0f && player.getRight() > getX() && player.getX() < getRight() && player.getY() > getTop()-MAX_OFFSET && player.getY() < getTop()){
            player.setY(getTop());
            player.stopDown();
        }
    }

    @Override
    public void overlap(TimeMine timeMine) {
        if(timeMine.getDifference() < 0f && timeMine.getRight() > getX() && timeMine.getX() < getRight() && timeMine.getY() > getTop()-MAX_OFFSET && timeMine.getY() < getTop()){
            timeMine.remove();
        }
    }

    @Override
    public void overlap(Scientist scientist) {
        if(scientist.getVelocity() < 0f && scientist.getRight() > getX() && scientist.getX() < getRight() && scientist.getY() > getTop()-MAX_OFFSET && scientist.getY() < getTop()){
            scientist.setY(getTop());
            scientist.stopDown();
        }
    }

    @Override
    public void overlap(Bottle bottle) {
        if(bottle.getVelocity() < 0f && bottle.getRight() > getX() && bottle.getX() < getRight() && bottle.getY() > getTop()-MAX_OFFSET && bottle.getY() < getTop()){
            bottle.remove();
        }
    }

    @Override
    public void overlap(DrugDealer drugDealer) {
        if(drugDealer.getVelocity() < 0f && drugDealer.getRight() > getX() && drugDealer.getX() < getRight() && drugDealer.getY() > getTop()-MAX_OFFSET && drugDealer.getY() < getTop()){
            drugDealer.setY(getTop());
            drugDealer.stopDown();
        }
    }

    @Override
    public void overlap(Drug drug) {
        if(drug.getVelocity() < 0f && drug.getRight() > getX() && drug.getX() < getRight() && drug.getY() > getTop()-MAX_OFFSET && drug.getY() < getTop()){
            drug.remove();
        }
    }

    @Override
    public void overlap(Eye eye) {
        if(eye.getVelocityY() < 0f && eye.getRight() > getX() && eye.getX() < getRight() && eye.getY() > getTop()-MAX_OFFSET && eye.getY() < getTop()){
            eye.setY(getTop());
            eye.stopDown();
        }
    }

    @Override
    public void overlap(Wreckage wreckage) {
        if(wreckage.getRight() > getX() && wreckage.getX() < getRight() && wreckage.getY() > getTop()-MAX_OFFSET && wreckage.getY() < getTop()){
            wreckage.remove();
        }
    }

    @Override
    public void overlap(HoverBoard hoverBoard) {
        if(hoverBoard.getRight() > getX() && hoverBoard.getX() < getRight() && hoverBoard.getY() > getTop()-MAX_OFFSET && hoverBoard.getY() < getTop()){
            hoverBoard.remove();
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

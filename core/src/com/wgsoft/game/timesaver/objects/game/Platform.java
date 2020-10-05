package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

import static com.wgsoft.game.timesaver.Const.*;

public class Platform extends Actor implements Solid {
    //It gets width and height from TextureRegion of platform
    //x and y are left-bottom coordinates
    public Platform(float x, float y){
        setBounds(x, y, game.skin.getRegion("game/platform").getRegionWidth(), game.skin.getRegion("game/platform").getRegionHeight());
    }

    @Override
    public void overlap(Player player) {
        if(player.getX() < getRight() && player.getRight() > getX() && player.getY() < getTop() && player.getTop() > getY()){
            if(player.getX(Align.center) < getX(Align.center)){
                if(player.getY(Align.center) < getY(Align.center)){
                    if(player.getRight()-getX() < player.getTop()-getY()){
                        player.setX(getX(), Align.right);
                    }else{
                        player.setY(getY(), Align.top);
                        if(player.getVelocity() > 0f) {
                            player.stopUp();
                        }
                    }
                }else{
                    if(player.getRight()-getX() < getTop()-player.getY()){
                        player.setX(getX(), Align.right);
                    }else{
                        player.setY(getTop());
                        if(player.getVelocity() < 0f) {
                            player.stopDown();
                        }
                    }
                }
            }else{
                if (player.getY(Align.center) < getY(Align.center)) {
                    if(getRight()-player.getX() < player.getTop()-getY()){
                        player.setX(getRight());
                    }else{
                        player.setY(getY(), Align.top);
                        if(player.getVelocity() > 0f) {
                            player.stopUp();
                        }
                    }
                }else{
                    if(getRight()-player.getX() < getTop()-player.getY()){
                        player.setX(getRight());
                    }else{
                        player.setY(getTop());
                        if(player.getVelocity() < 0f) {
                            player.stopDown();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void overlap(TimeMine timeMine) {
        if(timeMine.getX() < getRight() && timeMine.getRight() > getX() && timeMine.getY() < getTop() && timeMine.getTop() > getY()){
            timeMine.remove();
        }
    }

    @Override
    public void overlap(Scientist scientist) {
        if(scientist.getX() < getRight() && scientist.getRight() > getX() && scientist.getY() < getTop() && scientist.getTop() > getY()){
            if(scientist.getX(Align.center) < getX(Align.center)){
                if(scientist.getY(Align.center) < getY(Align.center)){
                    if(scientist.getRight()-getX() < scientist.getTop()-getY()){
                        scientist.setX(getX(), Align.right);
                    }else{
                        scientist.setY(getY(), Align.top);
                        if(scientist.getVelocity() > 0f) {
                            scientist.stopUp();
                        }
                    }
                }else{
                    if(scientist.getRight()-getX() < getTop()-scientist.getY()){
                        scientist.setX(getX(), Align.right);
                    }else{
                        scientist.setY(getTop());
                        if(scientist.getVelocity() < 0f) {
                            scientist.stopDown();
                        }
                    }
                }
            }else{
                if (scientist.getY(Align.center) < getY(Align.center)) {
                    if(getRight()-scientist.getX() < scientist.getTop()-getY()){
                        scientist.setX(getRight());
                    }else{
                        scientist.setY(getY(), Align.top);
                        if(scientist.getVelocity() > 0f) {
                            scientist.stopUp();
                        }
                    }
                }else{
                    if(getRight()-scientist.getX() < getTop()-scientist.getY()){
                        scientist.setX(getRight());
                    }else{
                        scientist.setY(getTop());
                        if(scientist.getVelocity() < 0f) {
                            scientist.stopDown();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void overlap(Bottle bottle) {
        if(bottle.getX() < getRight() && bottle.getRight() > getX() && bottle.getY() < getTop() && bottle.getTop() > getY()){
            bottle.remove();
        }
    }

    @Override
    public void overlap(DrugDealer drugDealer) {
        if(drugDealer.getX() < getRight() && drugDealer.getRight() > getX() && drugDealer.getY() < getTop() && drugDealer.getTop() > getY()){
            if(drugDealer.getX(Align.center) < getX(Align.center)){
                if(drugDealer.getY(Align.center) < getY(Align.center)){
                    if(drugDealer.getRight()-getX() < drugDealer.getTop()-getY()){
                        drugDealer.setX(getX(), Align.right);
                    }else{
                        drugDealer.setY(getY(), Align.top);
                        if(drugDealer.getVelocity() > 0f) {
                            drugDealer.stopUp();
                        }
                    }
                }else{
                    if(drugDealer.getRight()-getX() < getTop()-drugDealer.getY()){
                        drugDealer.setX(getX(), Align.right);
                    }else{
                        drugDealer.setY(getTop());
                        if(drugDealer.getVelocity() < 0f) {
                            drugDealer.stopDown();
                        }
                    }
                }
            }else{
                if (drugDealer.getY(Align.center) < getY(Align.center)) {
                    if(getRight()-drugDealer.getX() < drugDealer.getTop()-getY()){
                        drugDealer.setX(getRight());
                    }else{
                        drugDealer.setY(getY(), Align.top);
                        if(drugDealer.getVelocity() > 0f) {
                            drugDealer.stopUp();
                        }
                    }
                }else{
                    if(getRight()-drugDealer.getX() < getTop()-drugDealer.getY()){
                        drugDealer.setX(getRight());
                    }else{
                        drugDealer.setY(getTop());
                        if(drugDealer.getVelocity() < 0f) {
                            drugDealer.stopDown();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void overlap(Drug drug) {
        if(drug.getX() < getRight() && drug.getRight() > getX() && drug.getY() < getTop() && drug.getTop() > getY()){
            drug.remove();
        }
    }

    @Override
    public void overlap(Eye eye) {
        if(eye.getX() < getRight() && eye.getRight() > getX() && eye.getY() < getTop() && eye.getTop() > getY()){
            if(eye.getX(Align.center) < getX(Align.center)){
                if(eye.getY(Align.center) < getY(Align.center)){
                    if(eye.getRight()-getX() < eye.getTop()-getY()){
                        eye.setX(getX(), Align.right);
                        if(eye.getVelocityX() > 0f) {
                            eye.stopRight();
                        }
                    }else{
                        eye.setY(getY(), Align.top);
                        if(eye.getVelocityY() > 0f) {
                            eye.stopUp();
                        }
                    }
                }else{
                    if(eye.getRight()-getX() < getTop()-eye.getY()){
                        eye.setX(getX(), Align.right);
                        if(eye.getVelocityX() > 0f) {
                            eye.stopRight();
                        }
                    }else{
                        eye.setY(getTop());
                        if(eye.getVelocityY() < 0f) {
                            eye.stopDown();
                        }
                    }
                }
            }else{
                if (eye.getY(Align.center) < getY(Align.center)) {
                    if(getRight()-eye.getX() < eye.getTop()-getY()){
                        eye.setX(getRight());
                        if(eye.getVelocityX() < 0f) {
                            eye.stopLeft();
                        }
                    }else{
                        eye.setY(getY(), Align.top);
                        if(eye.getVelocityY() > 0f) {
                            eye.stopUp();
                        }
                    }
                }else{
                    if(getRight()-eye.getX() < getTop()-eye.getY()){
                        eye.setX(getRight());
                        if(eye.getVelocityX() < 0f) {
                            eye.stopLeft();
                        }
                    }else{
                        eye.setY(getTop());
                        if(eye.getVelocityY() < 0f) {
                            eye.stopDown();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(game.skin.getRegion("game/platform"), getX(), getY(), getWidth(), getHeight());
    }
}

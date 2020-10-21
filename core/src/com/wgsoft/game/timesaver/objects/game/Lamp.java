package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class Lamp extends Actor {
    public static final float WIDTH = 330f;
    public static final float HEIGHT = 781f;

    public Lamp(float x, float y){
        setBounds(x, y, WIDTH, HEIGHT);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(game.skin.getRegion("game/lamp"), getX(), getY(), getWidth(), getHeight());
    }
}

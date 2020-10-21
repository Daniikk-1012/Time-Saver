package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class Hatch extends Actor {
    public static final float SIZE = 250f;
    public static final float MOVE_DURATION = 3f;

    public Hatch(float x, float y){
        setBounds(x, y, SIZE, SIZE);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(game.skin.getRegion("game/hatch"), getX(), getY(), getWidth(), getHeight());
    }
}

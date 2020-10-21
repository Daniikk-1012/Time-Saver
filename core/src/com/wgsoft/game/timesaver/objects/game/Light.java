package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class Light extends Actor {
    public static final float WIDTH = 1814f;
    public static final float HEIGHT = 781f;

    public Light(Lamp lamp){
        setSize(WIDTH, HEIGHT);
        setPosition(lamp.getX(Align.center), lamp.getY(), Align.center|Align.bottom);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(game.skin.getRegion("game/light"), getX(), getY(), getWidth(), getHeight());
    }
}

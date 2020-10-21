package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class Bench extends Actor {
    public static final float WIDTH = 328f;
    public static final float HEIGHT = 168f;

    public Bench(float x, float y){
        setBounds(x, y, WIDTH, HEIGHT);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(game.skin.getRegion("game/bench"), getX(), getY(), getWidth(), getHeight());
    }
}

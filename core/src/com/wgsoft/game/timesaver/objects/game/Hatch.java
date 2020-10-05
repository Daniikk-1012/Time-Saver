package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.wgsoft.game.timesaver.Const.*;

public class Hatch extends Actor {
    public Hatch(float x, float y){
        setBounds(x, y, GAME_HATCH_WIDTH, GAME_HATCH_HEIGHT);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(game.skin.getRegion("game/hatch"), getX(), getY(), getWidth(), getHeight());
    }
}

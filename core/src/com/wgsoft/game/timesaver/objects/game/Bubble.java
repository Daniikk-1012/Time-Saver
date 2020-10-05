package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

import static com.wgsoft.game.timesaver.Const.*;

public class Bubble extends Actor {
    private Player player;

    public Bubble(Player player){
        this.player = player;
    }

    @Override
    public void act(float delta) {
        float size = Interpolation.linear.apply(GAME_BUBBLE_SIZE_MAX, GAME_BUBBLE_SIZE_MIN, player.getTime()/player.getMaxTime());
        setSize(size, size);
        setPosition(player.getX(Align.center), player.getY(Align.center), Align.center);
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(1f, 1f, 1f, getColor().a*parentAlpha);
        batch.draw(game.skin.getRegion("game/bubble"), getX(), getY(), getWidth(), getHeight());
        batch.setColor(1f, 1f, 1f, 1f);
    }
}

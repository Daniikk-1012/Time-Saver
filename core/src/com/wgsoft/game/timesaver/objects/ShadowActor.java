package com.wgsoft.game.timesaver.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class ShadowActor extends Actor {
    public static final float COLOR_CHANGE_DURATION = 5f;

    private static final ShadowActor instance = new ShadowActor();

    public static ShadowActor getInstance(){
        return instance;
    }

    private ShadowActor(){
        setColor(Color.GREEN);
        addAction(
                Actions.forever(
                        Actions.sequence(
                                Actions.color(Color.PURPLE, COLOR_CHANGE_DURATION, Interpolation.fade),
                                Actions.color(Color.GREEN, COLOR_CHANGE_DURATION, Interpolation.fade)
                        )
                )
        );
    }

    @Override
    public void act(float delta) {
        setSize(getStage().getWidth(), getStage().getHeight());
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor());
        batch.draw(game.skin.getRegion("shadow"), getX(), getY(), getWidth(), getHeight());
        batch.setColor(1f, 1f, 1f, 1f);
    }
}

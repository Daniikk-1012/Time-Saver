package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.wgsoft.game.timesaver.MyGdxGame.*;

public class Portal extends Actor {
    public static final float WIDTH = 250f;
    public static final float HEIGHT = 375f;
    public static final float STAY_FRAME_DURATION = 0.2f;
    public static final float SHRINK_FRAME_DURATION = 0.1f;

    private final Player player;
    private final Animation<TextureRegion> shrinkAnimation;
    private float animationTime;
    private Animation<TextureRegion> currentAnimation;

    public Portal(Player player, float x, float y){
        this.player = player;
        setBounds(x, y, WIDTH, HEIGHT);
        shrinkAnimation = new Animation<>(SHRINK_FRAME_DURATION, game.skin.getRegions("game/portal/shrink"));
        setAnimation(new Animation<>(STAY_FRAME_DURATION, game.skin.getRegions("game/portal/stay"), Animation.PlayMode.LOOP_PINGPONG));
    }

    private void setAnimation(Animation<TextureRegion> animation){
        animationTime = 0f;
        currentAnimation = animation;
    }

    public boolean isShrinkFinished(){
        return shrinkAnimation.isAnimationFinished(animationTime);
    }

    public void shrink(){
        setAnimation(shrinkAnimation);
    }

    @Override
    public void act(float delta) {
        animationTime += delta;
        boolean killed = true;
        for(int i = 0; i < getStage().getActors().size; i++){
            if(getStage().getActors().get(i) instanceof Monster){
                killed = false;
                break;
            }
        }
        if(killed && game.gameScreen.isNotFinishing() && currentAnimation != shrinkAnimation && player.isGround() && player.getX() < getRight() && player.getRight() > getX() && player.getY() < getTop() && player.getTop() > getY()){
            game.gameScreen.finish();
        }
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(currentAnimation.getKeyFrame(animationTime), getX(), getY(), getWidth(), getHeight());
    }
}

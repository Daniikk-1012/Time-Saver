package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

import static com.wgsoft.game.timesaver.Const.*;

public class Portal extends Actor {
    private final Player player;
    private final Hatch hatch;
    private final Bubble bubble;
    private final Stage victoryStage;
    private final Stack victoryStack;
    private final Stage uiStage;
    private final Label blueVictoryLabel;
    private final Label redVictoryLabel;
    private final Animation<TextureRegion> shrinkAnimation;
    private float animationTime;
    private Animation<TextureRegion> currentAnimation;
    private boolean shrinking;

    public Portal(Player player, Hatch hatch, Bubble bubble, Stage victoryStage, Stack victoryStack, Stage uiStage, Label blueVictoryLabel, Label redVictoryLabel, float x, float y){
        this.player = player;
        this.hatch = hatch;
        this.bubble = bubble;
        this.victoryStage = victoryStage;
        this.victoryStack = victoryStack;
        this.uiStage = uiStage;
        this.blueVictoryLabel = blueVictoryLabel;
        this.redVictoryLabel = redVictoryLabel;
        setBounds(x, y, GAME_PORTAL_WIDTH, GAME_PORTAL_HEIGHT);
        shrinkAnimation = new Animation<>(GAME_PORTAL_SHRINK_FRAME_DURATION, game.skin.getRegions("game/portal/shrink"));
        setAnimation(new Animation<>(GAME_PORTAL_STAY_FRAME_DURATION, game.skin.getRegions("game/portal/stay"), Animation.PlayMode.LOOP_PINGPONG));
    }

    private void setAnimation(Animation<TextureRegion> animation){
        animationTime = 0f;
        currentAnimation = animation;
    }

    public boolean isNotShrinking(){
        return !shrinking;
    }

    public void shrink(){
        shrinking = true;
        setAnimation(shrinkAnimation);
    }

    @Override
    public void act(float delta) {
        animationTime += delta;
        boolean killed = true;
        for(int i = 0; i < getStage().getActors().size; i++){
            if(getStage().getActors().get(i) instanceof Hitable){
                killed = false;
                break;
            }
        }
        if(killed && player.isNotFinishing() && currentAnimation != shrinkAnimation && player.isGround() && player.getX() < getRight() && player.getRight() > getX() && player.getY() < getTop() && player.getTop() > getY()){
            player.finish(this, bubble);
            uiStage.addAction(Actions.sequence(Actions.touchable(Touchable.disabled), Actions.alpha(0f, GAME_VICTORY_UI_FADE_DURATION, Interpolation.fade)));
        }else if(shrinking && player.isNotMoving() && currentAnimation.isAnimationFinished(animationTime)){
            player.move(hatch, victoryStage, victoryStack, blueVictoryLabel, redVictoryLabel);
        }
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(currentAnimation.getKeyFrame(animationTime), getX(), getY(), getWidth(), getHeight());
    }
}

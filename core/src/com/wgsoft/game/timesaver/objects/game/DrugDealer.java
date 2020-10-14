package com.wgsoft.game.timesaver.objects.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import static com.wgsoft.game.timesaver.Const.*;

public class DrugDealer extends Actor implements Hitable {
    private final Player player;
    private final Bubble bubble;
    private final Animation<TextureRegion> stayAnimation;
    private final Animation<TextureRegion> attackAnimation;
    private final Animation<TextureRegion> dieAnimation;
    private float animationTime;
    private Animation<TextureRegion> currentAnimation;
    private float velocity;
    private boolean wasInBubble;
    private float attackTime;
    private boolean aggressive;

    public DrugDealer(Player player, Bubble bubble, float x){
        this.player = player;
        this.bubble = bubble;
        setBounds(x, 0f, GAME_DRUG_DEALER_WIDTH_SCALE*GAME_UNIT_SIZE, GAME_DRUG_DEALER_HEIGHT_SCALE*GAME_UNIT_SIZE);
        setOrigin(Align.center|Align.bottom);
        setScale(1f/GAME_DRUG_DEALER_WIDTH_SCALE, 1f/GAME_DRUG_DEALER_HEIGHT_SCALE);
        stayAnimation = new Animation<>(GAME_DRUG_DEALER_STAY_FRAME_DURATION/GAME_OUTSIDE_BUBBLE_SPEED_SCALE, game.skin.getRegions("game/drug-dealer/stay"), Animation.PlayMode.LOOP);
        attackAnimation = new Animation<>(GAME_DRUG_DEALER_ATTACK_FRAME_DURATION/GAME_OUTSIDE_BUBBLE_SPEED_SCALE, game.skin.getRegions("game/drug-dealer/attack"));
        dieAnimation = new Animation<>(GAME_DRUG_DEALER_DIE_FRAME_DURATION/GAME_OUTSIDE_BUBBLE_SPEED_SCALE, game.skin.getRegions("game/drug-dealer/die"));
        setAnimation(stayAnimation);
    }

    private float sqr(float x){
        return x*x;
    }

    @Override
    public void hit(Player player) {
        if(aggressive && currentAnimation != dieAnimation && player.getX() < getRight() && player.getRight() > getX() && player.getY() < getTop() && player.getTop() > getY()){
            die();
        }
    }

    @Override
    public void hit(TimeMine timeMine) {
        if(aggressive && currentAnimation != dieAnimation && timeMine.getX() < getRight() && timeMine.getRight() > getX() && timeMine.getY() < getTop() && timeMine.getTop() > getY()){
            die();
            timeMine.remove();
        }
    }

    private void die(){
        setAnimation(dieAnimation);
        addAction(Actions.delay(currentAnimation.getAnimationDuration(), Actions.removeActor()));
        game.monsterDeathSound.play(game.prefs.getFloat("settings.sound", SETTINGS_SOUND_DEFAULT));
        player.addMaxTime(GAME_DRUG_DEALER_DEATH_MAX_TIME_BONUS);
    }

    public float getVelocity(){
        return velocity;
    }

    public void stopUp(){
        velocity = 0f;
    }

    public void stopDown(){
        velocity = 0f;
    }

    private void setAnimation(Animation<TextureRegion> animation){
        animationTime = 0f;
        currentAnimation = animation;
    }

    @Override
    public void act(float delta) {
        animationTime += delta;
        if(wasInBubble && sqr(getX(Align.center)-bubble.getX(Align.center))+sqr(getY(Align.center)-bubble.getY(Align.center)) >= sqr(bubble.getWidth()/2f)){
            wasInBubble = false;
            stayAnimation.setFrameDuration(GAME_DRUG_DEALER_STAY_FRAME_DURATION/GAME_OUTSIDE_BUBBLE_SPEED_SCALE);
            attackAnimation.setFrameDuration(GAME_DRUG_DEALER_ATTACK_FRAME_DURATION/GAME_OUTSIDE_BUBBLE_SPEED_SCALE);
            dieAnimation.setFrameDuration(GAME_DRUG_DEALER_DIE_FRAME_DURATION/GAME_OUTSIDE_BUBBLE_SPEED_SCALE);
        }else if(!wasInBubble && sqr(getX(Align.center)-bubble.getX(Align.center))+sqr(getY(Align.center)-bubble.getY(Align.center)) < sqr(bubble.getWidth()/2f)){
            wasInBubble = true;
            stayAnimation.setFrameDuration(GAME_DRUG_DEALER_STAY_FRAME_DURATION);
            attackAnimation.setFrameDuration(GAME_DRUG_DEALER_ATTACK_FRAME_DURATION);
            dieAnimation.setFrameDuration(GAME_DRUG_DEALER_DIE_FRAME_DURATION);
        }
        if(player.getX() > getRight()){
            aggressive = true;
        }
        if(currentAnimation != dieAnimation) {
            if(player.getX(Align.center) < getX(Align.center) && getX(Align.center)-player.getRight() < GAME_MONSTER_VISIBLE_RADIUS){
                if(aggressive) {
                    if (wasInBubble) {
                        attackTime += delta;
                    } else {
                        attackTime += delta * GAME_OUTSIDE_BUBBLE_SPEED_SCALE;
                    }
                }
                while(attackTime >= GAME_DRUG_DEALER_ATTACK_INTERVAL){
                    if(aggressive) {
                        getStage().addActor(new Drug(player, bubble, getX() - GAME_THROWABLE_SIZE, getY(Align.center) - GAME_THROWABLE_SIZE / 2f, false));
                        if(currentAnimation != attackAnimation) {
                            setAnimation(attackAnimation);
                        }
                    }
                    attackTime -= GAME_DRUG_DEALER_ATTACK_INTERVAL;
                }
                setScaleX(-1f/GAME_DRUG_DEALER_WIDTH_SCALE);
            }else if(player.getX(Align.center) > getX(Align.center) && player.getX()-getX(Align.center) < GAME_MONSTER_VISIBLE_RADIUS){
                if(aggressive) {
                    if (wasInBubble) {
                        attackTime += delta;
                    } else {
                        attackTime += delta * GAME_OUTSIDE_BUBBLE_SPEED_SCALE;
                    }
                }
                if(wasInBubble) {
                    attackTime += delta;
                }else{
                    attackTime += delta*GAME_OUTSIDE_BUBBLE_SPEED_SCALE;
                }
                while(attackTime >= GAME_DRUG_DEALER_ATTACK_INTERVAL){
                    if(aggressive) {
                        getStage().addActor(new Drug(player, bubble, getRight(), getY(Align.center)-GAME_THROWABLE_SIZE/2f, true));
                        if(currentAnimation != attackAnimation) {
                            setAnimation(attackAnimation);
                        }
                    }
                    attackTime -= GAME_DRUG_DEALER_ATTACK_INTERVAL;
                }
                setScaleX(1f/GAME_DRUG_DEALER_WIDTH_SCALE);
            }
            if((currentAnimation != attackAnimation || currentAnimation.isAnimationFinished(animationTime)) && currentAnimation != stayAnimation){
                setAnimation(stayAnimation);
            }
            if (getX() < GAME_BORDER_LEFT && getRight() > GAME_BORDER_RIGHT) {
                setX((GAME_BORDER_LEFT + GAME_BORDER_RIGHT) / 2f, Align.center);
            } else if (getX() < GAME_BORDER_LEFT) {
                setX(GAME_BORDER_LEFT);
            } else if (getRight() > GAME_BORDER_RIGHT) {
                setX(GAME_BORDER_RIGHT, Align.right);
            }
            if(wasInBubble) {
                velocity -= delta * GAME_GRAVITY;
                moveBy(0f, delta * velocity);
            }else{
                velocity -= delta * GAME_GRAVITY * GAME_OUTSIDE_BUBBLE_SPEED_SCALE;
                moveBy(0f, delta * velocity * GAME_OUTSIDE_BUBBLE_SPEED_SCALE);
            }
            for (int i = 0; i < getStage().getActors().size; i++) {
                if (getStage().getActors().get(i) instanceof Solid) {
                    ((Solid) getStage().getActors().get(i)).overlap(this);
                }
            }
        }
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(currentAnimation.getKeyFrame(animationTime), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), -getScaleX(), getScaleY(), getRotation());
    }
}

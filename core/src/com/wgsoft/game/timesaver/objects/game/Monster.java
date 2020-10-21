package com.wgsoft.game.timesaver.objects.game;

public interface Monster {
    float VISIBLE_RADIUS = 960f;
    void hit(Player player);
    void hit(TimeMine timeMine);
}

package com.wgsoft.game.timesaver.objects.game;

public interface Hitable {
    void hit(Player player);
    void hit(TimeMine timeMine);
}

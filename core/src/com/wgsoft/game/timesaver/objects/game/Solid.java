package com.wgsoft.game.timesaver.objects.game;

public interface Solid {
    void overlap(Player player);
    void overlap(TimeMine timeMine);
    void overlap(Scientist scientist);
    void overlap(Bottle bottle);
    void overlap(DrugDealer drugDealer);
    void overlap(Drug drug);
    void overlap(Eye eye);
    void overlap(Wreckage wreckage);
}

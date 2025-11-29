package org.ivangeevo.inthegloom.entity.interfaces;

public interface PlayerEntityAdded {

    void btwr$updateGloomState();

    void btwr$setInGloomCounter(int newValue);

    void btwr$setGloomLevel(int newValue);

    void btwr$setPreviousGloomLevel(int newValue);

    int btwr$getInGloomCounter();

    int btwr$getGloomLevel();

    int btwr$getPreviousGloomLevel();

}
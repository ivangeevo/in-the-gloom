package org.ivangeevo.inthegloom.entity.interfaces;

import net.minecraft.entity.data.DataTracker;

public interface PlayerEntityAdded
{
    void updateGloomState();

    void setInGloomCounter(int newValue);

    void setGloomLevel(int newValue);

    void setPreviousGloomLevel(int newValue);

    int getInGloomCounter();

    int getGloomLevel();

    int getPreviousGloomLevel();

}

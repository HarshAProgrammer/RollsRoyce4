package com.rackluxury.rollsroyce.reddit;

public interface SetAsWallpaperCallback {
    void setToHomeScreen(int viewPagerPosition);
    void setToLockScreen(int viewPagerPosition);
    void setToBoth(int viewPagerPosition);
}
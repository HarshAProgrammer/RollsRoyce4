package com.rackluxury.rollsroyce.reddit.events;

public class ChangeHidePostTypeEvent {
    public boolean hidePostType;

    public ChangeHidePostTypeEvent(boolean hidePostType) {
        this.hidePostType = hidePostType;
    }
}

package com.rackluxury.rolex.reddit.events;

public class ChangeSavePostFeedScrolledPositionEvent {
    public boolean savePostFeedScrolledPosition;

    public ChangeSavePostFeedScrolledPositionEvent(boolean savePostFeedScrolledPosition) {
        this.savePostFeedScrolledPosition = savePostFeedScrolledPosition;
    }
}

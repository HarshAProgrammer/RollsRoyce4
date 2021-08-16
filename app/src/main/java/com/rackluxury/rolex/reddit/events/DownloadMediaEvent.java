package com.rackluxury.rolex.reddit.events;

public class DownloadMediaEvent {
    public boolean isSuccessful;

    public DownloadMediaEvent(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }
}

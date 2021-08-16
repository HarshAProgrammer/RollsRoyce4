package com.rackluxury.rollsroyce.reddit.events;

public class ChangeNetworkStatusEvent {
    public int connectedNetwork;

    public ChangeNetworkStatusEvent(int connectedNetwork) {
        this.connectedNetwork = connectedNetwork;
    }
}

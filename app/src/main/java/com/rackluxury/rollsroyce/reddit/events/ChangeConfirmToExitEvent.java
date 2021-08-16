package com.rackluxury.rollsroyce.reddit.events;

public class ChangeConfirmToExitEvent {
    public boolean confirmToExit;

    public ChangeConfirmToExitEvent(boolean confirmToExit) {
        this.confirmToExit = confirmToExit;
    }
}

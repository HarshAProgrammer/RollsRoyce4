package com.rackluxury.rollsroyce.reddit.events;

public class ChangeRequireAuthToAccountSectionEvent {
    public boolean requireAuthToAccountSection;

    public ChangeRequireAuthToAccountSectionEvent(boolean requireAuthToAccountSection) {
        this.requireAuthToAccountSection = requireAuthToAccountSection;
    }
}

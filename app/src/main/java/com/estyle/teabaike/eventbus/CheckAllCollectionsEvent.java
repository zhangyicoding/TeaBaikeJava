package com.estyle.teabaike.eventbus;

public class CheckAllCollectionsEvent {

    private String checkAllText;

    public CheckAllCollectionsEvent(String checkAllText) {
        this.checkAllText = checkAllText;
    }

    public String getCheckAllText() {
        return checkAllText;
    }
}

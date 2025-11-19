package com.iskahoot.common.messages;

import java.io.Serializable;

public class TimeMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private long currentTimeMillis;

    public TimeMessage(long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public void setCurrentTimeMillis(long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }
}
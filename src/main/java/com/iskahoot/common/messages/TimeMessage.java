package com.iskahoot.common.messages;

import java.io.Serializable;

public class TimeMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private long currentTimeMillis;
    private int timeToEndRound;//TODO usar timesatmp

    public TimeMessage(long currentTimeMillis, int timeToEndRound) {
        this.currentTimeMillis = currentTimeMillis;
        this.timeToEndRound = timeToEndRound;
    }

    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public void setCurrentTimeMillis(long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    public long getTimeToEndRound() {
        return timeToEndRound;
    }
    public void setTimeToEndRound(int timeToEndRound) {
        this.timeToEndRound = timeToEndRound;
    }
}
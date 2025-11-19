package com.iskahoot.common.messages;

import java.io.Serializable;

public class ReceptionConfirmationMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private long receivedAtMillis;

    public ReceptionConfirmationMessage(long receivedAtMillis) {
        this.receivedAtMillis = receivedAtMillis;
    }

    public long getReceivedAtMillis() {
        return receivedAtMillis;
    }
}
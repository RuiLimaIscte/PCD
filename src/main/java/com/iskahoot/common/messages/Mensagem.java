package com.iskahoot.common.messages;

import java.io.Serializable;

public class Mensagem implements Serializable {

    int id;
    String message;

    public Mensagem(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }
    public String getMessage() {
        return message;
    }
}

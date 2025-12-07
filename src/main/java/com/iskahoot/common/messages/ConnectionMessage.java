package com.iskahoot.common.messages;

import java.io.Serializable;

public class ConnectionMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String gameCode;
    private String teamCode;
    private String clientCode;

    public ConnectionMessage(String gameCode, String teamCode, String clientCode) {
        this.gameCode = gameCode;
        this.teamCode = teamCode;
        this.clientCode = clientCode;
    }
    public String getGameCode() {
        return gameCode;
    }
    public String getTeamCode() {
        return teamCode;
    }
    public String getClientCode() {
        return clientCode;
    }


}

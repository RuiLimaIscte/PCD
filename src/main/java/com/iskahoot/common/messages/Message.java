package com.iskahoot.common.messages;

import java.io.Serializable;

/**
 * Base class for all messages exchanged between client and server
 */
//public class Message implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    public enum MessageType {
//        // Client -> Server
//        JOIN_ROOM,
//        SUBMIT_ANSWER,
//
//        // Server -> Client
//        JOIN_ACCEPTED,
//        JOIN_REJECTED,
//        GAME_STARTING,
//        QUESTION_BROADCAST,
//        ROUND_ENDED,
//        SCOREBOARD_UPDATE,
//        GAME_ENDED,
//
//        // Bidirectional
//        PING,
//        DISCONNECT
//    }
//
//    private MessageType type;
//    private Object payload;
//    private long timestamp;
//
//    public Message(MessageType type, Object payload) {
//        this.type = type;
//        this.payload = payload;
//        this.timestamp = System.currentTimeMillis();
//    }
//
//    public MessageType getType() {
//        return type;
//    }
//
//    public Object getPayload() {
//        return payload;
//    }
//
//    public long getTimestamp() {
//        return timestamp;
//    }
//
//    @Override
//    public String toString() {
//        return "Message{" +
//                "type=" + type +
//                ", timestamp=" + timestamp +
//                '}';
//    }
//}


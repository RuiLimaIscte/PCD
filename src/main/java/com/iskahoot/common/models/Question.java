package com.iskahoot.common.models;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a quiz question
 */
public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    private String question;
    private int points;
    private int correct;  // Index of correct answer (0-based)
    private String type;  // "individual" or "team"
    private List<String> options;

    public Question() {
    }

    public Question(String question, int points, int correct, String type, List<String> options) {
        this.question = question;
        this.points = points;
        this.correct = correct;
        this.type = type;
        this.options = options;
    }

    // Getters and Setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public boolean isIndividual() {
        return "individual".equalsIgnoreCase(type);
    }

    public boolean isTeam() {
        return "team".equalsIgnoreCase(type);
    }

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", points=" + points +
                ", type='" + type + '\'' +
                '}';
    }
}


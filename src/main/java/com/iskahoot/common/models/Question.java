package com.iskahoot.common.models;

import java.io.Serializable;
import java.util.List;


public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    private String question;
    private int points;
    private int correct;  // Index of correct answer (0-based)
    private String type;  // "individual" or "team"
    private List<String> options;

    public Question(String question,int correct, int points, String type, List<String> options) {
        this.question = question;
        this.points = points;
        this.correct = correct;
        this.type = type;
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public int getPoints() {
        return points;
    }

    public int getCorrect() {
        return correct;
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

    public boolean isIndividual() {
        return type.equalsIgnoreCase("individual");
    }

    public boolean isTeam() {
        return type.equalsIgnoreCase("team");
    }

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", points=" + points +
                ", correct=" + correct +
                ", type='" + type + '\'' +
                ", options=" + options +
                '}';
    }
}


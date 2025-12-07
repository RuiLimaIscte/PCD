package com.iskahoot.common.models;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static com.iskahoot.utils.QuestionLoader.loadFromFile;


public class Quiz implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<Question> questions;

    public Quiz()  {
    }

    public Quiz(String name, List<Question> questions) {
        this.name = name;
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "name='" + name + '\'' +
                ", questions=" + questions.size() +
                '}';
    }
}


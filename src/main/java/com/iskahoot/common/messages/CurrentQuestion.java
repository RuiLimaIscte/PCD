package com.iskahoot.common.messages;

import java.io.Serializable;
import java.util.List;

public class CurrentQuestion implements Serializable {

    private String questionText;
    private List<String> options;

    private Integer selectedAnswerIndex = null;

    public CurrentQuestion(String questionText, List<String> options) {
        this.questionText = questionText;
        this.options = options;
    }

    public String getQuestionText() {
        return questionText;
    }
    public List<String> getOptions() {
        return options;
    }
    public Integer getSelectedAnswerIndex() {
        return selectedAnswerIndex;
    }

    // Setter para o Cliente preencher a resposta
    public void setSelectedAnswerIndex(Integer index) {
        this.selectedAnswerIndex = index;
    }
}

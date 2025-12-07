package com.iskahoot.common.messages;

import java.io.Serializable;
import java.util.List;

public class QuestionMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String clientCode;

    private String questionText;
    private List<String> options;

    private Integer selectedAnswerIndex = null;

    public QuestionMessage(String questionText, List<String> options) {
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

    public void setSelectedAnswerIndex(Integer index) {
        this.selectedAnswerIndex = index;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getClientCode() {
        return clientCode;
    }
}

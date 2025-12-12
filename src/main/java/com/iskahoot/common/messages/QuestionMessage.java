package com.iskahoot.common.messages;

import java.io.Serializable;
import java.util.List;

public class QuestionMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String clientCode;

    private String questionText;
    private String questionType;
    private List<String> options;
    private long currentTimeMillis;
    private int timeToEndRound;

    private Integer selectedAnswerIndex = null;

    public QuestionMessage(String questionText, String questionType, List<String> options, long currentTimeMillis, int timeToEndRound) {
        this.questionText = questionText;
        this.questionType = questionType;
        System.out.println(questionType);
        this.options = options;
        this.currentTimeMillis = currentTimeMillis;
        this.timeToEndRound = timeToEndRound;
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

    public String getQuestionType() {
        return questionType;
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

    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public int getTimeToEndRound() {
        return timeToEndRound;
    }
}

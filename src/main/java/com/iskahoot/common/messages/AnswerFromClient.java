package com.iskahoot.common.messages;

import java.io.Serializable;

public class AnswerFromClient implements Serializable {
    private static final long serialVersionUID = 1L;

   // private int questionId;
    private int selectedOptionIndex;

    public AnswerFromClient(int selectedOptionIndex) {
      //  this.questionId = questionId;
        this.selectedOptionIndex = selectedOptionIndex;
    }

//    public int getQuestionId() {
//        return questionId;
//    }

    public int getSelectedOptionIndex() {
        return selectedOptionIndex;
    }

}

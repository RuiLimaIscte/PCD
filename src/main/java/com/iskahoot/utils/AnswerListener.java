package com.iskahoot.utils;

import com.iskahoot.common.messages.CurrentQuestion;

public interface AnswerListener {
    void onAnswerSelected(CurrentQuestion currentQuestion);
}
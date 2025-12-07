package com.iskahoot.utils;

import com.iskahoot.common.messages.QuestionMessage;

public interface AnswerListener {
    void onAnswerSelected(QuestionMessage questionMessage);
}
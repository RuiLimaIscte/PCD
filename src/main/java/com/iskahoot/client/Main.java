package com.iskahoot.client;

import com.iskahoot.common.models.Question;
import com.iskahoot.utils.QuestionLoader;

import java.util.List;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws Exception {
        QuestionLoader loader = new QuestionLoader();
        List<Question> questions = loader.loadFromFile("src/main/resources/questions.json");

        ClientGUI gui = new ClientGUI(questions);
        gui.resetForNextQuestion();
        Thread.sleep(1000);
        gui.showNextQuestion();
    }
}

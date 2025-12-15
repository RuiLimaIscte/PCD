package com.iskahoot.utils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.iskahoot.common.models.Question;
import com.iskahoot.common.models.Quiz;

import java.io.*;




public class QuestionLoader {


    public static Quiz loadFromFile(String filePath) {
        Gson gson = new Gson();
        JsonReader jsonReader;
            try {
                jsonReader = readDataFromFile(filePath);
                Quiz quiz = gson.fromJson(jsonReader, Quiz.class);
                for(Question question : quiz.getQuestions()){
                    if(Math.random() < 0.5) question.setType("individual");
                    else question.setType("team");
//                    question.setType("team");
//                    System.out.println(question.getType());
                }
                return quiz;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        return null;
    }


    private static JsonReader readDataFromFile (String fileName) throws FileNotFoundException {
        return new JsonReader(new FileReader(fileName));
    }
}




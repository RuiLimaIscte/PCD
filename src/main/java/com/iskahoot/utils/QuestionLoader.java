package com.iskahoot.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.iskahoot.common.models.Question;
import com.iskahoot.common.models.Quiz;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;



public class QuestionLoader {


    public static Quiz loadFromFile(String filePath) {
        Gson gson = new Gson();
        JsonReader jsonReader;
            try {
                jsonReader = readDataFromFile(filePath);
                Quiz quiz = gson.fromJson(jsonReader, Quiz.class);
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




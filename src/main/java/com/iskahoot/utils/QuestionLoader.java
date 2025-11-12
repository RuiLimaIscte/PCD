package com.iskahoot.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.iskahoot.common.models.Question;
import com.iskahoot.common.models.Quiz;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to load questions from JSON file using Gson
 */
public class QuestionLoader {
    private static final Gson gson = new Gson();

    /**
     * Load quiz from JSON file in resources
     * @param resourcePath Path to JSON file in resources folder
     * @return Quiz object with questions
     * @throws IOException if file cannot be read
     */
    public static Quiz loadFromResources(String resourcePath) throws IOException {
        InputStream inputStream = QuestionLoader.class.getClassLoader()
                .getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }

        try (Reader reader = new InputStreamReader(inputStream)) {
            // Parse as array of questions directly (questions.json is a JSON array)
            Type questionListType = new TypeToken<ArrayList<Question>>(){}.getType();
            List<Question> questions = gson.fromJson(reader, questionListType);

            // Create Quiz object with the questions
            return new Quiz("PCD Quiz 2025", questions);
        }
    }

    public List<Question> loadFromFile(String filePath) throws IOException {
        Gson gson = new Gson();
        JsonReader jsonReader;
            try {
                jsonReader = readDataFromFile(filePath);
                Type questionListType = new TypeToken<ArrayList<Question>>() {
                }.getType();

                ArrayList<Question> questions = gson.fromJson(jsonReader, questionListType);

                System.out.println(questions);
                return questions;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        return null;
    }


    private static JsonReader readDataFromFile (String fileName) throws FileNotFoundException {
        return new JsonReader(new FileReader(fileName));
    }
}




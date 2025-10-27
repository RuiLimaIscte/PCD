package com.iskahoot.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iskahoot.common.models.Quiz;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Utility class to load questions from JSON file using Gson
 */
//public class QuestionLoader {
//    private static final Gson gson = new Gson();
//
//    /**
//     * Load quiz from JSON file in resources
//     * @param resourcePath Path to JSON file in resources folder
//     * @return Quiz object with questions
//     * @throws IOException if file cannot be read
//     */
//    public static Quiz loadFromResources(String resourcePath) throws IOException {
//        InputStream inputStream = QuestionLoader.class.getClassLoader()
//                .getResourceAsStream(resourcePath);
//
//        if (inputStream == null) {
//            throw new IOException("Resource not found: " + resourcePath);
//        }
//
//        try (Reader reader = new InputStreamReader(inputStream)) {
//            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
//            JsonObject quizzesObject = jsonObject.getAsJsonObject("quizzes");
//            return gson.fromJson(quizzesObject, Quiz.class);
//        }
//    }
//
//    /**
//     * Load quiz from JSON file path
//     * @param filePath Path to JSON file
//     * @return Quiz object with questions
//     * @throws IOException if file cannot be read
//     */
//    public static Quiz loadFromFile(String filePath) throws IOException {
//        try (Reader reader = new FileReader(filePath)) {
//            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
//            JsonObject quizzesObject = jsonObject.getAsJsonObject("quizzes");
//            return gson.fromJson(quizzesObject, Quiz.class);
//        }
//    }
//}


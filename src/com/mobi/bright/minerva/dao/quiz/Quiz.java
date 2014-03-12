package com.mobi.bright.minerva.dao.quiz;

import android.database.Cursor;
import com.mobi.bright.minerva.dao.Dao;

import java.util.ArrayList;
import java.util.List;


public class Quiz extends Dao {
// ------------------------------ FIELDS ------------------------------

    public static final String TABLE_NAME = "quiz";

    String name;
    int quizId;

// --------------------------- CONSTRUCTORS ---------------------------

    public Quiz() {
        super();
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

// -------------------------- OTHER METHODS --------------------------

    public String getJson(String quizId) {
        //SELECT json FROM quiz where quizId = 2
        Cursor cursor = getSqlLite().rawQuery("Select json from " + TABLE_NAME + " where quizId=?",
                                          new String[]{
                                                  quizId,
                                          });

        String json = "[]"; //empty json array

        if (cursor.moveToNext()) {
            json = cursor.getString(0);
        }

        cursor.close();

        return json;
    }
    
    public boolean hasQuiz() {
        List<Quiz> quizzes = getQuizzes();
        return quizzes.size() > 0;
    }

    public List<Quiz> getQuizzes() {
        Cursor cursor = getSqlLite().rawQuery("Select  quizId, name from " + TABLE_NAME,
                                          null);

        List<Quiz> quizList = new ArrayList<Quiz>();

        while (cursor.moveToNext()) {
            Quiz quiz = new Quiz();
            quiz.quizId = cursor.getInt(0);
            quiz.name = cursor.getString(1);
            quizList.add(quiz);
        }

        cursor.close();

        return quizList;
    }
}




package com.mobi.bright.minerva.dao.quiz;

import android.database.Cursor;
import com.mobi.bright.minerva.dao.Dao;

import java.util.Date;


public class Question extends Dao {

    public static final String TABLE_NAME = "question";

    public Question() {
        super();
    }


    private int performCountQuery(final String quizId,
                                  final String strSQL) {
        Cursor cursor = getSqlLite().rawQuery(strSQL,
                                          new String[]{
                                                  quizId
                                          });
        int count = -1;

        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        return count;
    }


    public int getQuestionCount(final String quizId) {
        String strSQL = "select count(*) from " + TABLE_NAME + " where questionType!='Information' and quizId=?";

        return performCountQuery(quizId,
                                 strSQL);
    }

    public int getCorrectCount(final String quizId) {
        String strSQL = "select count(*) from " + TABLE_NAME + " where correct=1  and quizId=?";

        return performCountQuery(quizId,
                                 strSQL);
    }

    public int getTakenCount(final String quizId) {
        String strSQL = "select count(*) from " + TABLE_NAME + " where correct is not null and quizId=?";

        return performCountQuery(quizId,
                                 strSQL);
    }



    public void setAnswers(final String questionId,
                           final String answerArrayJson,
                           final boolean isCorrect) {

        String strSQL = "UPDATE " + TABLE_NAME + " set answers=?, answeredDate=?, correct=?  WHERE questionId=?";


        getSqlLite().execSQL(strSQL,
                         new String[]{
                                 answerArrayJson,
                                 "" + new Date().getTime(),
                                 isCorrect ? "1" : "0",
                                 questionId});

    }

    public String getAnswers(final String questionId) {


        Cursor cursor = getSqlLite().rawQuery("Select answers from " + TABLE_NAME + " where questionId=?",
                                          new String[]{
                                                  questionId
                                          });

        String answers = null;

        if (cursor.moveToNext()) {
            answers = cursor.getString(0);
        }

        cursor.close();


        if (answers == null) {
            answers = "[]";
        }

        return answers;

    }


    public String getCorrect(final String questionId) {

        Cursor cursor = getSqlLite().rawQuery("Select correct from " + TABLE_NAME + " where questionId=?",
                                          new String[]{
                                                  questionId,
                                          });

        Integer correct = null;

        if (cursor.moveToNext()) {
            correct = cursor.getInt(0);
        }

        cursor.close();


        if (correct == null) {
            return "false";
        }

        if (correct == 1) {
            return "true";
        } else {
            return "false";
        }

    }


}

package com.mohyeddin.passwordmanager.models;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

public class LoginPasswordModel {
    private String password;
    private String forgetPassQuestion;
    private String forgetPassAnswer;
    public static final String PASSWORD_KEY="password";
    public static final String QUESTION_KEY="quest";
    public static final String ANSWER_KEY="answer";

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getForgetPassQuestion() {
        return forgetPassQuestion;
    }

    public void setForgetPassQuestion(String forgetPassQuestion) {
        this.forgetPassQuestion = forgetPassQuestion;
    }

    public String getForgetPassAnswer() {
        return forgetPassAnswer;
    }

    public void setForgetPassAnswer(String forgetPassAnswer) {
        this.forgetPassAnswer = forgetPassAnswer;
    }
    public ContentValues getContentValue(){
        ContentValues values=new ContentValues();
        values.put(PASSWORD_KEY,password);
        values.put(QUESTION_KEY,forgetPassQuestion);
        values.put(ANSWER_KEY,forgetPassAnswer);
        return values;
    }
    public static LoginPasswordModel cursorToPass(Cursor cursor){
        LoginPasswordModel model=new LoginPasswordModel();
        model.setPassword(cursor.getString(cursor.getColumnIndex(PASSWORD_KEY)));
        model.setForgetPassQuestion(cursor.getString(cursor.getColumnIndex(QUESTION_KEY)));
        model.setForgetPassAnswer(cursor.getString(cursor.getColumnIndex(ANSWER_KEY)));
        return model;
    }
    @NonNull
    @Override
    public String toString() {
        return password;
    }
}

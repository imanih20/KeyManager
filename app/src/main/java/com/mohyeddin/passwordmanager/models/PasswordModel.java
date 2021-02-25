package com.mohyeddin.passwordmanager.models;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

public class PasswordModel  {
    public static final String PASSWORD_KEY="password";
    public static final String TITLE_KEY="title";
    public static final String ID_KEY="id";
    private String passWord="";
    private String title="";
    private int id;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public ContentValues getContentValue(){
        ContentValues values=new ContentValues();
        values.put(PASSWORD_KEY,passWord);
        values.put(TITLE_KEY,title);
        return values;
    }
    public ContentValues getContentValuesWithId(){
        ContentValues values=new ContentValues();
        values.put(ID_KEY,id);
        values.put(PASSWORD_KEY,passWord);
        values.put(TITLE_KEY,title);
        return values;
    }
    public static PasswordModel cursorToPasswordModel(Cursor cursor){
        PasswordModel model=new PasswordModel();
        model.setPassWord(cursor.getString(cursor.getColumnIndex(PASSWORD_KEY)));
        model.setTitle(cursor.getString(cursor.getColumnIndex(TITLE_KEY)));
        model.setId(cursor.getInt(cursor.getColumnIndex(ID_KEY)));
        return model;
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }
}

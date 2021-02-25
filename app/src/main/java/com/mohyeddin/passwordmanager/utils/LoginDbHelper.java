package com.mohyeddin.passwordmanager.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import androidx.annotation.Nullable;

import com.mohyeddin.passwordmanager.models.LoginPasswordModel;

public class LoginDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME="login_db";
    private static final int DB_VERSION=1;
    private static final String TABLE_LOGIN_NAME="login_table";
    private static final String CMD_LOGIN_PASSWORD="CREATE TABLE '"+TABLE_LOGIN_NAME+"' ('"+
            LoginPasswordModel.PASSWORD_KEY +"' TEXT  PRIMARY KEY , '"+
            LoginPasswordModel.QUESTION_KEY +"' TEXT , '"+
            LoginPasswordModel.ANSWER_KEY+"' TEXT )";
    public LoginDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CMD_LOGIN_PASSWORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '"+TABLE_LOGIN_NAME+"'");
        onCreate(db);
    }
    public String getPassword(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM '"+TABLE_LOGIN_NAME+"'",null);
        if (cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndex(LoginPasswordModel.PASSWORD_KEY));
        }
        cursor.close();
        if (db.isOpen())db.close();
        return null;
    }
    public boolean insertLoginPassword(LoginPasswordModel model){
        SQLiteDatabase db=getWritableDatabase();
        long id=db.insert(TABLE_LOGIN_NAME,null,model.getContentValue());
        if (db.isOpen())db.close();
        return id!=-1;
    }
    public LoginPasswordModel getLoginPassword(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM '"+TABLE_LOGIN_NAME+"'",null);
        if (cursor.moveToFirst()){
            return LoginPasswordModel.cursorToPass(cursor);
        }
        cursor.close();
        if (db.isOpen())db.close();
        return null;
    }
    public void updatePassword(String newPassword, String oldPassword){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(LoginPasswordModel.PASSWORD_KEY,newPassword);
        db.update(TABLE_LOGIN_NAME,values,LoginPasswordModel.PASSWORD_KEY+"="+oldPassword,null);
        if (db.isOpen())db.close();
    }
    public void updateContents(String oldPass,ContentValues values){
        SQLiteDatabase db=getWritableDatabase();
        db.update(TABLE_LOGIN_NAME,values,LoginPasswordModel.PASSWORD_KEY+"="+oldPass,null);
        if (db.isOpen())db.close();
    }
}

package com.mohyeddin.passwordmanager.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

import com.mohyeddin.passwordmanager.models.PasswordModel;
import java.util.ArrayList;
import java.util.List;


public class PasswordDbHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION=1;
    public static final String DB_NAME="passwords_db";
    public static final String TABLE_PASSWORDS_NAME ="passwords_table";
    private static final String CMD_PASSWORDS_TABLE="CREATE TABLE '"+ TABLE_PASSWORDS_NAME +"' ('"
            + PasswordModel.ID_KEY+"' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, '"
            + PasswordModel.PASSWORD_KEY+"' TEXT, '"
            + PasswordModel.TITLE_KEY+"' TEXT"
            + ")";
    public PasswordDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CMD_PASSWORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '"+ TABLE_PASSWORDS_NAME +"'");
        onCreate(db);
    }

    public void insertPassword(PasswordModel pm){
        SQLiteDatabase db=getWritableDatabase();
        db.insert(TABLE_PASSWORDS_NAME,null,pm.getContentValue());
        if (db.isOpen())db.close();
    }
    public List<PasswordModel> getAllPasswords(){
        SQLiteDatabase db=getReadableDatabase();
        List<PasswordModel> passwords=new ArrayList<>();
        Cursor cursor=db.rawQuery("SELECT * FROM '"+TABLE_PASSWORDS_NAME+"'",null);
        if (cursor.moveToFirst()){
            do {
                passwords.add(PasswordModel.cursorToPasswordModel(cursor));
            }while (cursor.moveToNext());
            cursor.close();
        }
        if (db.isOpen())db.close();
        return passwords;
    }
    public void deletePassword(int passwordId){
        SQLiteDatabase db=getWritableDatabase();
        db.delete(TABLE_PASSWORDS_NAME,PasswordModel.ID_KEY+"="+passwordId,null);
        if (db.isOpen())db.close();
    }
    public boolean updateTitle(int id,String title){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(PasswordModel.TITLE_KEY,title);
        long i=db.update(TABLE_PASSWORDS_NAME,values,PasswordModel.ID_KEY+"="+id,null);
        if (db.isOpen())db.close();
        return i!=-1;
    }
    public List<PasswordModel> searchInPasswords(String title){
        SQLiteDatabase db=getReadableDatabase();
        List<PasswordModel> passwords=new ArrayList<>();
        Cursor cursor=db.rawQuery("SELECT * FROM '"+TABLE_PASSWORDS_NAME+"' WHERE "+PasswordModel.TITLE_KEY+" LIKE '%"+title+"%'",null);
        if (cursor.moveToFirst()){
            do {
                passwords.add(PasswordModel.cursorToPasswordModel(cursor));
            }while (cursor.moveToNext());
            cursor.close();
        }
        if (db.isOpen())db.close();
        return passwords;
    }
    public void updatePassword(int id, String pass){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(PasswordModel.PASSWORD_KEY,pass);
        db.update(TABLE_PASSWORDS_NAME,values,PasswordModel.ID_KEY+"="+id,null);
        if (db.isOpen())db.close();
    }
}

package com.idealist.www.useopencvwithcmake;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// 메세지 목록 SQLite에 저장하는 부분! 아직 서버에다가는 저장안함.
public class DatabaseHelper2 extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "DATA_BASE2";
    public static final String TABLE_NAME = "RoomList";
    public static final String RoomList_COL_1 = "Id";
    public static final String RoomList_COL_2 = "RoomName";

    public DatabaseHelper2(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db2 = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db2) {
        db2.execSQL("create table " + TABLE_NAME + "(Num INTEGER PRIMARY KEY AUTOINCREMENT,ID TEXT,RoomName TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db2, int oldVersion, int newVersion) {
        db2.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db2);
    }

    public boolean insertData(String Id, String RoomName) {
        SQLiteDatabase db2 = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RoomList_COL_1, Id);
        contentValues.put(RoomList_COL_2, RoomName);

        long result = db2.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getRoomData(String Id) {
        SQLiteDatabase db2 = this.getWritableDatabase();
        Cursor res = db2.rawQuery("select * from " + TABLE_NAME + " WHERE Id = ?", new String[]{Id});
        return res;
    }

    public Cursor getRoomDupCheck(String Id, String RoomName) {
        SQLiteDatabase db2 = this.getWritableDatabase();
        Cursor res = db2.rawQuery("select * from " + TABLE_NAME + " WHERE Id = ? AND RoomName=?", new String[]{Id, RoomName});
        return res;
    }
}


package com.idealist.www.useopencvwithcmake;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// 메세지 목록 SQLite에 저장하는 부분! 아직 서버에다가는 저장안함.
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "DATA_BASE";
    public static final String TABLE_NAME = "MessageList";

    public static final String RoomList_COL_1 = "RoomName";
    public static final String RoomList_COL_2 = "Sender";
    public static final String RoomList_COL_3 = "Msg";
    public static final String RoomList_COL_4 = "Regdate";
    public static final String RoomList_COL_5 = "Type";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(Num INTEGER PRIMARY KEY AUTOINCREMENT,RoomName TEXT ,Sender TEXT,Msg TEXT, Regdate TEXT, Type TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String RoomName, String Sender, String Msg, String Regdate,String Type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RoomList_COL_1, RoomName);
        contentValues.put(RoomList_COL_2, Sender);
        contentValues.put(RoomList_COL_3, Msg);
        contentValues.put(RoomList_COL_4, Regdate);
        contentValues.put(RoomList_COL_5, Type);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getAllData(String roomname) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" WHERE RoomName = ?",new String[] {roomname});

        return res;
    }
}

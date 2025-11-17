package edu.ewubd.cse489118;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassSummaryDB extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "lectures";
    public ClassSummaryDB(Context context) {
        super(context, "ClassSummaryDB.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("DB@OnCreate");

        String sql = "CREATE TABLE lectures  ("
                + "ID TEXT PRIMARY KEY,"
                + "course TEXT,"
                + "type TEXT,"
                + "datetime INTEGER,"
                + "lecture TEXT,"
                + "topic TEXT,"
                + "summary TEXT"
                + ")";
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("Write code to modify database schema here");
        // db.execSQL("ALTER table my_table  ......");
        // db.execSQL("CREATE TABLE  ......");
    }
    public void insertLecture(String ID, String course, String type, long date, String lecture, String topic, String summary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();
        cols.put("ID", ID);
        cols.put("course", course);
        cols.put("type", type);
        cols.put("datetime", date);
        cols.put("lecture", lecture);
        cols.put("topic", topic);
        cols.put("summary", summary);
        db.insert("lectures", null ,  cols);
        db.close();
    }
    public void updateLecture(String ID, String course, String type, long date, String lecture, String topic, String summary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();
        cols.put("course", course);
        cols.put("type", type);
        cols.put("datetime", date);
        cols.put("lecture", lecture);
        cols.put("topic", topic);
        cols.put("summary", summary);
        db.update("lectures", cols, "ID=?", new String[] {ID} );
        db.close();
    }
    public void deleteLecture(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("lectures", "ID=?", new String[ ] {ID} );
        db.close();
        Log.d("DeletionSuccess", "Lecture deleted from local database with ID: " + ID);
    }
    public Cursor selectLectures(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=null;
        try {
            res = db.rawQuery(query, null);
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    private String formatDate(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(milliseconds));
    }
    public boolean isLectureExist(String ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM lectures WHERE ID=?", new String[]{ID});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }


}

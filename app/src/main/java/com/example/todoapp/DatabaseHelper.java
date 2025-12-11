package com.example.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ToDoApp.db";

    // User Table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "ID";
    private static final String COL_USERNAME = "USERNAME";
    private static final String COL_PASSWORD = "PASSWORD";

    // Task Table
    private static final String TABLE_TASKS = "tasks";
    private static final String COL_TASK_ID = "ID";
    private static final String COL_TASK_TITLE = "TITLE";
    private static final String COL_TASK_DESC = "DESCRIPTION";
    private static final String COL_TASK_USER_ID = "USER_ID"; // Linking column

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create User Table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, USERNAME TEXT, PASSWORD TEXT)");

        // Create Task Table with Foreign Key logic
        db.execSQL("CREATE TABLE " + TABLE_TASKS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, DESCRIPTION TEXT, USER_ID INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    // --- USER LOGIC ---

    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE USERNAME=? AND PASSWORD=?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Helper to get ID so we know which user is logged in
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID FROM " + TABLE_USERS + " WHERE USERNAME=?", new String[]{username});
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        return -1;
    }

    // --- TASK LOGIC ---

    public boolean addTask(String title, String desc, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TASK_TITLE, title);
        contentValues.put(COL_TASK_DESC, desc);
        contentValues.put(COL_TASK_USER_ID, userId);
        long result = db.insert(TABLE_TASKS, null, contentValues);
        return result != -1;
    }

    public Cursor getTasksForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE USER_ID=?", new String[]{String.valueOf(userId)});
    }

    public void deleteTask(String taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, "ID=?", new String[]{taskId});
    }
}
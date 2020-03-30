package com.legacy.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ToDo.db";
    public static final String TABLE_NAME = "Tasks";
    public static final String COLUMN_ID = "Id";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_DETAILS = "Details";
    public static final String COLUMN_STATUS = "Status";

    Context _context;

    public SQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this._context = context;
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_DETAILS + " TEXT," +
                COLUMN_STATUS + " INTEGER)");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    boolean createTask(String title, String detail){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DETAILS, detail);
        values.put(COLUMN_STATUS, 0);

        return db.insert(TABLE_NAME, null, values) != -1;
    }


    void UpdateStatus(final int id, boolean status) {
        //update db and query the status column
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, status ? 1 : 0);

        db.update(TABLE_NAME, cv, COLUMN_ID + " = " + id, null);
        db.close();

        calculateProgress();
    }


    private void calculateProgress() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor done = db.rawQuery("SELECT * FROM Tasks WHERE Status = 1", null);
        Cursor all = db.rawQuery("SELECT * FROM Tasks", null);


        double doneTasks = done.getCount() * 100 /all.getCount();
        ((MainActivity)_context).refreshProgress((int)doneTasks);

        done.close();
        all.close();
    }

    boolean edit(int id, String title, String desc){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DETAILS, desc);

        int update = db.update(TABLE_NAME, cv, COLUMN_ID + " = " + id, null);

        return update != 0;
    }

    boolean delete(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        int done = db.delete(TABLE_NAME, COLUMN_ID + " = " + id, null);

        return done != 0;
    }
}

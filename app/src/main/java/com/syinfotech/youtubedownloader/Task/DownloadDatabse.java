package com.syinfotech.youtubedownloader.Task;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DownloadDatabse extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABSE_NAME = "downloadtable";
    private static final String TABLE_Downloads = "downloads";
    private static final String KEY_FILENAME = "filename";
    private static final String KEY_TITLE = "title";
    private static final String KEY_URI = "uri";
    private final Context context;


    public DownloadDatabse(@Nullable Context context) {
        super(context, DATABSE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CHAT_TABLE = "CREATE TABLE " + TABLE_Downloads + "("
                + KEY_TITLE + " TEXT PRIMARY KEY," + KEY_FILENAME + " TEXT," + KEY_URI + " TEXT);";
        sqLiteDatabase.execSQL(CREATE_CHAT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_Downloads);
        onCreate(sqLiteDatabase);
    }

    public void AddToDownloadlist(String title, String filename, String uri) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, title);
        values.put(KEY_FILENAME, filename);
        values.put(KEY_URI, uri);
        database.insert(TABLE_Downloads, null, values);


    }

    public List<ModelClass> getDownloadsitems(){
        String selectQuery = "SELECT * FROM " +TABLE_Downloads;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        java.util.List<ModelClass> cartitems = new ArrayList<ModelClass>();

        Cursor cursor= sqLiteDatabase.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            do {
                ModelClass Model = new ModelClass();
                Model.setTitle(cursor.getString(0));
                Model.setFilename(cursor.getString(1));
                Model.setUri(cursor.getString(2));
                cartitems.add(Model);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return cartitems;
    }
    public void RemoveAllDownloads() {
        String selectQuery = "DELETE FROM " + TABLE_Downloads;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL(selectQuery);
    }

    public void RemoveDownloadFromList(String id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_Downloads, KEY_TITLE + "= ?", new String[]{id});
        sqLiteDatabase.close();
    }


}
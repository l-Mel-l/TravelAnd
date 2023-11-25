package com.example.traveland;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

public class DataBaseAccessor extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "db.db";
    private static final int DB_VERSION = 3;

    private static final String TABLE_NOTE = "NOTE";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_THEME = "theme";
    public static final String COLUMN_NOTE = "note";


    public DataBaseAccessor(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NOTE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_THEME + " TEXT,"
                + COLUMN_NOTE + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            // Дополнительные обновления схемы базы данных для новой версии
            db.execSQL("ALTER TABLE " + TABLE_NOTE + " ADD COLUMN new_column INTEGER DEFAULT 0;");
        }
    }
    //удаление из базы данных
    public void deleteNoteById(int noteId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NOTE, COLUMN_ID + "=?", new String[]{String.valueOf(noteId)});
    }

    public SimpleCursorAdapter getCursorAdapter(Context context, int layout, int[] viewIds) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NOTE, null);

        String[] columns = new String[]{COLUMN_THEME, COLUMN_NOTE};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(context, layout, cursor, columns, viewIds, 0);
        return adapter;
    }

    public void updateNote(int id, String theme, String note) {
        getWritableDatabase().execSQL("UPDATE " + TABLE_NOTE
                + " SET "
                + COLUMN_THEME + "='" + theme + "', "
                + COLUMN_NOTE + "='" + note + "'"
                + " WHERE "
                + COLUMN_ID + "=" + id);
    }
    SQLiteDatabase database = getWritableDatabase();
    public long insertNote(String title, String content) {
        ContentValues values = new ContentValues();
        values.put("theme", title);
        values.put("note", content);
        return database.insert("NOTE", null, values);
    }

    // Метод для получения всех заметок из базы данных
    public Cursor getAllNotes() {
        return database.query("NOTE", null, null, null, null, null, null);
    }

    public Cursor getNoteById(int noteId) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = COLUMN_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(noteId)};
        return db.query(TABLE_NOTE, null, selection, selectionArgs, null, null, null);
    }
}


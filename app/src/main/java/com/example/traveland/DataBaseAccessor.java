package com.example.traveland;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.SimpleCursorAdapter;

//Класс для доступа к БД
public class DataBaseAccessor extends SQLiteOpenHelper
{
    // Основные данные базы
    private static final String DATABASE_NAME = "db.db";
    private static final int DB_VERSION = 3;

    // таблицы
    private static final String TABLE_NOTE = "NOTE";


    // столбцы таблицы Note
    private static final String COLUMN_ID = "_id";//Обязательно с подчеркиванием
    static final String COLUMN_THEME = "theme";
    static final String COLUMN_NOTE = "note";


    public DataBaseAccessor(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создать таблицу
        db.execSQL("CREATE TABLE " + TABLE_NOTE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_THEME + " TEXT,"
                + COLUMN_NOTE + " TEXT);");

        // Добавить пару записей в таблицу
        db.execSQL("INSERT INTO " + TABLE_NOTE + "(" + COLUMN_THEME + ", "+ COLUMN_NOTE+") values('theme 1','note 1')");
        db.execSQL("INSERT INTO " + TABLE_NOTE + "(" + COLUMN_THEME + ", "+ COLUMN_NOTE+") values('theme 2','note 2')");
        db.execSQL("DELETE FROM "+ TABLE_NOTE +" WHERE "+ COLUMN_ID + "=1");
    }

    /**
     * получить адаптер для listview.
     * @param layout  - разметка отдельного элемента listview
     * @param viewIds - идентификаторы элементов разметки ListView
     */
    public SimpleCursorAdapter getCursorAdapter(Context context,int layout, int[] viewIds)
    {
        // запрос данных для отображения
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NOTE,null);

        // какие столбцы и в каком порядке показывать в listview
        String[] columns = new  String[] {COLUMN_THEME,COLUMN_NOTE};

        // создание адаптера
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(context,layout,cursor,columns,viewIds,0);
        return  adapter;
    }

    /**
     * Обновить данные в БД
     * @param id - идентификатор записи в БД
     * @param theme - тема заметки
     * @param note - текст заметки
     */
    public void updateNote(int id, String theme,String note)
    {
        // выполнить запрос на обновление БД
        getReadableDatabase().execSQL("UPDATE "+ TABLE_NOTE
                + " SET "
                + COLUMN_THEME + "='" + theme + "', "
                + COLUMN_NOTE + "='" + note + "'"
                + " WHERE "
                + COLUMN_ID + "=" + id);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try
        {
            //удалить старую таблицу
            db.execSQL("DROP TABLE " + TABLE_NOTE);
        }
        catch (Exception exception)
        {

        }
        finally {
            //создать новую и заполнить ее
            onCreate(db);
        }
    }
}

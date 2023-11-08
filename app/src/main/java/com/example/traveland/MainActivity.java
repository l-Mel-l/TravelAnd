package com.example.traveland;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private DataBaseAccessor dataBaseAccessor;
    private SimpleCursorAdapter cursorAdapter;
    private ListView listView;// Объявляем переменную listView здесь, чтобы она была доступна во всем классе

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataBaseAccessor = new DataBaseAccessor(this);
        listView = findViewById(R.id.listView); // Инициализируем переменную listView

        // Получаем адаптер курсора из базы данных
        cursorAdapter = dataBaseAccessor.getCursorAdapter(this, android.R.layout.simple_list_item_1,
                new int[]{android.R.id.text1});
        listView.setAdapter(cursorAdapter);

        // Назначаем слушатель для кнопки добавления заметки
        Button addNoteButton = findViewById(R.id.addNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Открываем вторую активити для редактирования заметки
                Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        // Назначаем слушатель для элементов списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                int noteId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseAccessor.COLUMN_ID));
                String theme = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseAccessor.COLUMN_THEME));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseAccessor.COLUMN_NOTE));

                // Обработка нажатия на элемент списка
                Toast.makeText(getApplicationContext(), "Note ID: " + noteId + ", Theme: " + theme + ", Note: " + note,
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
                intent.putExtra("noteId", noteId); // Передаем noteId в Intent
                intent.putExtra("theme", theme);
                intent.putExtra("note", note);
                startActivityForResult(intent, 1); // Используем startActivityForResult для получения результата от NoteEditActivity
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    // Обработка результата из второй активити
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            int position = data.getIntExtra("position", -1);
            String title = data.getStringExtra("title");
            String content = data.getStringExtra("content");

            if (position != -1) {
                // Редактирование существующей заметки
                dataBaseAccessor.updateNote(position, title, content);
            } else {
                // Создание новой заметки
                dataBaseAccessor.insertNote(title, content);
            }

            // Обновляем курсор адаптера
            cursorAdapter.swapCursor(dataBaseAccessor.getAllNotes());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursorAdapter.getCursor().close();
    }
}
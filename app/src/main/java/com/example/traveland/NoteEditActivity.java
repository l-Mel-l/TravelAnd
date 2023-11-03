package com.example.traveland;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class NoteEditActivity extends AppCompatActivity {                 //Второе активити для редактирования заметок

    private int position = -1;                                            //переменная position для хранения позиции заметки в списке

    private EditText titleEditText;                                       //заголовок заметки
    private EditText contentEditText;                                     //содержимое заметки
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        if (position != -1) {                                               //получаются данные из переданного Intent. Если позиция в списке не равна -1, то это означает, что редактируется существующая заметка
            String title = intent.getStringExtra("title");            //Заголовок и текст извлекаются из intentи ставятся в текстовые поля
            String content = intent.getStringExtra("content");
            titleEditText.setText(title);
            contentEditText.setText(content);
        }
    }

    public void onSaveNoteClick(View view) {                            //метод для кнопки Сохранить
        String title = titleEditText.getText().toString();              //получает тему
        String content = contentEditText.getText().toString();          //получает текст

        Intent data = new Intent();                                     //создаёт intent в который помещает данные
        data.putExtra("position", position);
        data.putExtra("title", title);
        data.putExtra("content", content);
        setResult(RESULT_OK, data);
        finish();                                                       //завершается активити
    }
}
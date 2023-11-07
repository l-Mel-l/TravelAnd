package com.example.traveland;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class NoteEditActivity extends AppCompatActivity {                 //Второе активити для редактирования заметок

    private int noteId = -1;                                            //переменная noteId для хранения позиции заметки в списке

    private EditText titleEditText;                                       //заголовок заметки
    private EditText contentEditText;
    private Button backbutton;
    private Button saveButton;

    //содержимое заметки
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        backbutton = findViewById(R.id.backbutton);
        saveButton = findViewById(R.id.saveButton);
        Intent intent = getIntent();
        int noteId = intent.getIntExtra("noteId", -1);
        if (noteId != -1) {                                               //получаются данные из переданного Intent. Если позиция в списке не равна -1, то это означает, что редактируется существующая заметка
            String title = intent.getStringExtra("theme");            //Заголовок и текст извлекаются из intentи ставятся в текстовые поля
            String content = intent.getStringExtra("note");
            titleEditText.setText(title);
            contentEditText.setText(content);
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                       //метод для кнопки Сохранить
                String title = titleEditText.getText().toString();              //получает тему
                String content = contentEditText.getText().toString();          //получает текст

                Intent data = new Intent();                                     //создаёт intent в который помещает данные
                data.putExtra("position", noteId);
                data.putExtra("title", title);
                data.putExtra("content", content);
                setResult(RESULT_OK, data);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                //завершается активити
            }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
}
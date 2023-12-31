package com.example.traveland;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class NoteEditFragment extends Fragment {
    private EditText titleEditText;
    private EditText contentEditText;
    private Button backbutton;
    private Button saveButton;

    private int noteId = -1;
    private String theme = "";
    private String noteContent = "";

    public NoteEditFragment() {
        // Обязательный пустой конструктор
    }

    public static NoteEditFragment newInstance(int noteId, String name, String text) {
        NoteEditFragment fragment = new NoteEditFragment();
        Bundle args = new Bundle();
        args.putInt("noteId", noteId);
        args.putString("name", name);
        args.putString("text", text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_edit, container, false);

        titleEditText = view.findViewById(R.id.titleEditText);
        contentEditText = view.findViewById(R.id.contentEditText);
        backbutton = view.findViewById(R.id.backbutton);
        saveButton = view.findViewById(R.id.saveButton);
        Bundle args = getArguments();

        if (args != null) {
            noteId = args.getInt("noteId");
            theme = args.getString("name");
            noteContent = args.getString("text");
        }

        // Update data in EditText fields
        titleEditText.setText(theme);
        contentEditText.setText(noteContent);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEditText.getText().toString().trim();
                String content = contentEditText.getText().toString().trim();

                if (title.isEmpty()) {
                    // Показываем предупреждение
                    showAlert("Предупреждение", "Пожалуйста, введите Название пути.");
                } else {
                    // Передаем данные в активность
                    if (getActivity() != null) {
                        ((MainActivity) getActivity()).onNoteEditResult(noteId, title, content);

                        // Вызываем метод для отправки данных на сервер
                        ServerAccesor.sendDataToServer(title, content);

                        // Закрываем фрагмент
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            }
        });



        backbutton.setOnClickListener(view1 -> getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.detailFragment, new BlankFragment()).commit());

        return view;
    }

    private void showAlert(String title, String message) {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Закрываем диалоговое окно
                        }
                    })
                    .show();
        }
    }
    public void updateData(int noteId, String theme, String note) {
        this.noteId = noteId;
        this.theme = theme;
        this.noteContent = note;
        titleEditText.setText(theme);
        contentEditText.setText(note);
    }
}

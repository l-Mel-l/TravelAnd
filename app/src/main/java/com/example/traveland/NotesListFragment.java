package com.example.traveland;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.fragment.app.Fragment;

public class NotesListFragment extends Fragment {
    private DataBaseAccessor dataBaseAccessor;
    private SimpleCursorAdapter cursorAdapter;
    private ListView listView;

    public NotesListFragment() {
        // Обязательный пустой конструктор
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_list, container, false);

        dataBaseAccessor = new DataBaseAccessor(requireContext());
        listView = view.findViewById(R.id.listView);

        cursorAdapter = dataBaseAccessor.getCursorAdapter(requireContext(), android.R.layout.simple_list_item_1,
                new int[]{android.R.id.text1});
        listView.setAdapter(cursorAdapter);

        Button addNoteButton = view.findViewById(R.id.addNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Открываем второй фрагмент для редактирования заметки
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).showNoteEditFragment(-1, "", ""); // -1 indicates a new note
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                int noteId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseAccessor.COLUMN_ID));
                String theme = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseAccessor.COLUMN_THEME));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseAccessor.COLUMN_NOTE));

                // Открываем второй фрагмент для редактирования существующей заметки
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).showNoteEditFragment(noteId, theme, note);
                }
            }
        });

        return view;
    }
}
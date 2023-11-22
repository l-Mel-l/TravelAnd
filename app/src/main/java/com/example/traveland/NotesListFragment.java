package com.example.traveland;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class NotesListFragment extends Fragment {
    private DataBaseAccessor dataBaseAccessor;
    private SimpleCursorAdapter cursorAdapter;
    private ListView listView;

    public NotesListFragment() {
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
        // Добавляем долгое нажатие на элемент списка для удаления заметки
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteConfirmationDialog(id);
                return true;
            }
            });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                int noteId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseAccessor.COLUMN_ID));
                String theme = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseAccessor.COLUMN_THEME));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseAccessor.COLUMN_NOTE));
                String toastMessage = "ID: " + id + "\nTheme: " + theme + "\nNote: " + note;
                Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show();

                // Открываем второй фрагмент для редактирования существующей заметки
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).showNoteEditFragment(noteId, theme, note);
                }
            }
        });

        return view;
    }
    public void updateNotesList() {
        cursorAdapter.changeCursor(dataBaseAccessor.getAllNotes());
    }
    // Показывает всплывающее окно подтверждения удаления заметки
    private void showDeleteConfirmationDialog(final long noteId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Удаление заметки")
                .setMessage("Вы действительно хотите удалить эту заметку?")
                .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNoteById(noteId);
                        updateNotesList();
                        Toast.makeText(requireContext(), "Заметка удалена", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    // Удаляет заметку по идентификатору
    private void deleteNoteById(long noteId) {
        dataBaseAccessor.deleteNoteById((int) noteId);
    }
}

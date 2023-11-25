package com.example.traveland;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.content.res.Configuration;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    ListView ThemesListView;
    ArrayList<Note> notes;
    ArrayAdapter<String> noteAdapter;
    ServerAccesor serverAccessor = new ServerAccesor(SERVICE_ADDRESS);

    private DataBaseAccessor dataBaseAccessor;
    private FragmentManager fragmentManager;
    private NotesListFragment notesListFragment;
    private NoteEditFragment noteEditFragment;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        if (isTablet()) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_main);
        }
        ThemesListView = findViewById(R.id.listView);
        noteAdapter = AdapterUpdate(new ArrayList<ContactsContract.CommonDataKinds.Note>());
        Intent NoteIntent = new Intent(this, NoteEditFragment.class);


        dataBaseAccessor = new DataBaseAccessor(this);
        fragmentManager = getSupportFragmentManager();

        notesListFragment = new NotesListFragment();
        noteEditFragment = new NoteEditFragment();

        if (savedInstanceState == null) {
            // Если это первый запуск, открываем фрагмент со списком заметок
            showNotesListFragment();
        }
    }
    private ArrayAdapter<String> AdapterUpdate(ArrayList<Note> list) {

        ArrayList<String> stringList = serverAccessor.getStringListFromNoteList(list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, stringList);

// установить адаптер в listview
        ThemesListView.setAdapter(adapter);
        return adapter;
    }

    // Метод для показа фрагмента со списком заметок
    private void showNotesListFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.listFragment, notesListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Метод для показа фрагмента редактирования заметки
    public void showNoteEditFragment(int noteId, String theme, String note) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        getSupportFragmentManager().beginTransaction().replace(R.id.detailFragment, NoteEditFragment    .newInstance(noteId, theme, note)).commit();
        }

    // Обработка результата из фрагмента редактирования заметки
    public void onNoteEditResult(int position, String title, String content) {
        if (position != -1) {
            // Редактирование существующей заметки
            dataBaseAccessor.updateNote(position, title, content);
        } else {
            // Создание новой заметки
            dataBaseAccessor.insertNote(title, content);
        }

        // Обновляем список заметок
        notesListFragment.updateNotesList();
       showNotesListFragment(); // Переключаемся обратно на список заметок
    }
    private boolean isTablet() {
        int screenLayout = getResources().getConfiguration().screenLayout;
        int screenSize = screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        return screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE
                || screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
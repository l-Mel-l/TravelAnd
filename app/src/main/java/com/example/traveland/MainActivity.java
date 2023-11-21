package com.example.traveland;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private DataBaseAccessor dataBaseAccessor;
    private FragmentManager fragmentManager;
    private NotesListFragment notesListFragment;
    private NoteEditFragment noteEditFragment;

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

        dataBaseAccessor = new DataBaseAccessor(this);
        fragmentManager = getSupportFragmentManager();

        notesListFragment = new NotesListFragment();
        noteEditFragment = new NoteEditFragment();

        if (savedInstanceState == null) {
            // Если это первый запуск, открываем фрагмент со списком заметок
            showNotesListFragment();
        }
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
        Bundle args = new Bundle();
        args.putInt("noteId", noteId);
        args.putString("theme", theme);
        args.putString("note", note);

        // Проверяем, есть ли уже существующий NoteEditFragment
        NoteEditFragment existingFragment = (NoteEditFragment) fragmentManager.findFragmentByTag(NoteEditFragment.class.getSimpleName());

        if (existingFragment == null) {
            // Если фрагмент не существует, создаем новый
            noteEditFragment.setArguments(args);
            transaction.replace(R.id.detailFragment, noteEditFragment, NoteEditFragment.class.getSimpleName());
            transaction.addToBackStack(null);
        } else {
            // Если фрагмент существует, обновляем его данные
            existingFragment.updateData(noteId, theme, note);
        }

        transaction.commit();
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
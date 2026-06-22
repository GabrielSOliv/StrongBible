package com.devgabriel.bstrong;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.devgabriel.bstrong.core.database.BibleDatabaseHelper;
import com.devgabriel.bstrong.features.books.data.datasource.BookLocalDataSource;
import com.devgabriel.bstrong.features.books.data.repository.BookRepositoryImpl;
import com.devgabriel.bstrong.features.books.domain.model.Book;
import com.devgabriel.bstrong.features.books.domain.repository.BookRepository;
import com.devgabriel.bstrong.features.books.presentation.view.BookSelectionView;
import com.devgabriel.bstrong.features.books.presentation.viewmodel.BookViewModel;
import com.devgabriel.bstrong.features.chapters.data.datasource.ChapterLocalDataSource;
import com.devgabriel.bstrong.features.chapters.data.repository.ChapterRepositoryImpl;
import com.devgabriel.bstrong.features.chapters.domain.repository.ChapterRepository;
import com.devgabriel.bstrong.features.chapters.presentation.view.ChapterSelectionView;
import com.devgabriel.bstrong.features.chapters.presentation.viewmodel.ChapterViewModel;
import com.devgabriel.bstrong.features.search.data.datasource.SearchLocalDataSource;
import com.devgabriel.bstrong.features.search.data.repository.SearchRepositoryImpl;
import com.devgabriel.bstrong.features.search.domain.repository.SearchRepository;
import com.devgabriel.bstrong.features.search.presentation.view.SearchView;
import com.devgabriel.bstrong.features.search.presentation.viewmodel.SearchViewModel;
import com.devgabriel.bstrong.features.strong.data.datasource.StrongLocalDataSource;
import com.devgabriel.bstrong.features.strong.data.repository.StrongRepositoryImpl;
import com.devgabriel.bstrong.features.strong.domain.repository.StrongRepository;
import com.devgabriel.bstrong.features.strong.presentation.view.StrongBottomSheetView;
import com.devgabriel.bstrong.features.strong.presentation.viewmodel.StrongViewModel;
import com.devgabriel.bstrong.features.verses.data.datasource.VerseLocalDataSource;
import com.devgabriel.bstrong.features.verses.data.repository.VerseRepositoryImpl;
import com.devgabriel.bstrong.features.verses.domain.model.WordToken;
import com.devgabriel.bstrong.features.verses.domain.repository.VerseRepository;
import com.devgabriel.bstrong.features.verses.presentation.view.VerseReaderView;
import com.devgabriel.bstrong.features.verses.presentation.viewmodel.VerseViewModel;
import com.devgabriel.bstrong.features.commentary.data.datasource.CommentaryLocalDataSource;
import com.devgabriel.bstrong.features.commentary.data.repository.CommentaryRepositoryImpl;
import com.devgabriel.bstrong.features.commentary.domain.model.Commentary;
import com.devgabriel.bstrong.features.commentary.domain.repository.CommentaryRepository;
import com.devgabriel.bstrong.features.commentary.presentation.view.CommentaryView;
import com.devgabriel.bstrong.features.commentary.presentation.viewmodel.CommentaryViewModel;
import com.devgabriel.bstrong.core.database.CommentaryDatabaseHelper;
import com.devgabriel.bstrong.features.verses.domain.model.Verse;
import com.devgabriel.bstrong.core.preferences.repository.PreferencesRepository;
import androidx.lifecycle.Observer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ComponentActivity {

    private static final int SCREEN_HOME           = 0;
    private static final int SCREEN_CHAPTER_SELECT = 1;
    private static final int SCREEN_READING        = 2;
    private static final int SCREEN_SEARCH         = 3;
    private static final int SCREEN_COMMENTARY     = 4;

    private static final String KEY_SCREEN     = "current_screen";
    private static final String KEY_BOOK_ID    = "selected_book_id";
    private static final String KEY_CHAPTER    = "selected_chapter";
    private static final String KEY_FONT_SIZE  = "preference_font_size";
    private static final String KEY_HISTORY    = "screen_history";

    private static final String KEY_COMMENTARY_ACTIVE      = "commentary_active";
    private static final String KEY_COMMENTARY_CHAPTER     = "commentary_chapter";
    private static final String KEY_COMMENTARY_VERSE       = "commentary_verse";
    private static final String KEY_COMMENTARY_FULL_CHAPTER = "commentary_full_chapter";

    private static final String PREFS_NAME = "bstrong_preferences";
    private static final String KEY_THEME_INDEX = "selected_theme_index";

    private final int[] appThemes = {
        R.style.Theme_StrongBible,
        R.style.Theme_StrongBible_Light,
        R.style.Theme_StrongBible_Black
    };
    private int currentThemeIndex = 0;
    private PreferencesRepository preferencesRepository;

    private int prefrerenceFontSize;
    private FrameLayout contentFrame;

    private int currentScreen = SCREEN_HOME;

    private final List<Integer> screenHistory = new ArrayList<>();
    private final List<Object>  stateHistory  = new ArrayList<>();

    private Book selectedBook       = null;
    private int  selectedChapter    = 1;
    private int  preferenceFontSize = 18;
    private int commentaryChapter   = 1;

    private int     commentaryVerseNumber     = -1;
    private boolean commentaryFullChapterMode = false;

    private List<Book> allBooks = new ArrayList<>();

    private ChapterLocalDataSource chapterLocalDataSource;
    private ChapterViewModel       chapterViewModel;
    private BookLocalDataSource    bookLocalDataSource;
    private BookViewModel          bookViewModel;
    private VerseViewModel         verseViewModel;
    private StrongViewModel        strongViewModel;
    private SearchViewModel        searchViewModel;
    private StrongBottomSheetView  strongBottomSheetView;
    private CommentaryViewModel    commentaryViewModel;
    private CommentaryView activeCommentaryView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferencesRepository = new PreferencesRepository(this);
        currentThemeIndex = preferencesRepository.getThemeIndex();
        preferenceFontSize = preferencesRepository.getFontSize();

        setTheme(appThemes[currentThemeIndex]);

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        
        int dynamicBgColor = getThemeColor(R.attr.appBackgroundColor);
        getWindow().setStatusBarColor(dynamicBgColor);
        getWindow().setNavigationBarColor(dynamicBgColor);

        setContentView(R.layout.activity_main);
        contentFrame = findViewById(R.id.content_frame);

        BibleDatabaseHelper db = new BibleDatabaseHelper(this);
        chapterLocalDataSource = new ChapterLocalDataSource(db);
        bookLocalDataSource = new BookLocalDataSource(db);
        VerseLocalDataSource  verseLocalDataSource  = new VerseLocalDataSource(db);
        StrongLocalDataSource strongLocalDataSource = new StrongLocalDataSource(db);
        SearchLocalDataSource searchLocalDataSource = new SearchLocalDataSource(db);
        CommentaryLocalDataSource commentaryLocalDataSource = new CommentaryLocalDataSource(new CommentaryDatabaseHelper(this));

        BookRepository bookRepository = new BookRepositoryImpl(bookLocalDataSource);
        bookViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new BookViewModel(bookRepository);
            }
        }).get(BookViewModel.class);

        ChapterRepository chapterRepository = new ChapterRepositoryImpl(chapterLocalDataSource);
        chapterViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ChapterViewModel(chapterRepository);
            }
        }).get(ChapterViewModel.class);

        VerseRepository verseRepository = new VerseRepositoryImpl(verseLocalDataSource);
        verseViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new VerseViewModel(verseRepository);
            }
        }).get(VerseViewModel.class);

        StrongRepository strongRepository = new StrongRepositoryImpl(strongLocalDataSource);
        strongViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new StrongViewModel(strongRepository);
            }
        }).get(StrongViewModel.class);

        SearchRepository searchRepository = new SearchRepositoryImpl(searchLocalDataSource);
        searchViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new SearchViewModel(searchRepository);
            }
        }).get(SearchViewModel.class);

        CommentaryRepository commentaryRepository = new CommentaryRepositoryImpl(commentaryLocalDataSource);
        commentaryViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new CommentaryViewModel(commentaryRepository);
            }
        }).get(CommentaryViewModel.class);

        strongBottomSheetView = new StrongBottomSheetView(this, this, strongViewModel);
        allBooks = bookLocalDataSource.getAllBooks();

        if (savedInstanceState != null) {
            restoreFromBundle(savedInstanceState);
        } else {
            navigateTo(SCREEN_HOME, null);
        }
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (activeCommentaryView != null) {
                    closeCommentaryView();
                    return;
                }
                if (currentScreen == SCREEN_HOME) {
                    screenHistory.clear();
                    stateHistory.clear();
                    finish();
                    return;
                }
                if (!screenHistory.isEmpty()) {
                    int    lastScreen = screenHistory.remove(screenHistory.size() - 1);
                    Object lastState  = stateHistory .remove(stateHistory .size() - 1);
                    restoreState(lastScreen, lastState);
                } else {
                    finish();
                }
            }
        });
    }
    private int getThemeColor(int attrColor) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(attrColor, typedValue, true);
        return typedValue.data;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_SCREEN,    currentScreen);
        outState.putInt(KEY_CHAPTER,   selectedChapter);
        outState.putInt(KEY_FONT_SIZE, preferenceFontSize);

        if (selectedBook != null) {
            outState.putInt(KEY_BOOK_ID, selectedBook.getId());
        }

        int[] historyArr = new int[screenHistory.size()];
        for (int i = 0; i < screenHistory.size(); i++) {
            historyArr[i] = screenHistory.get(i);
        }
        outState.putIntArray(KEY_HISTORY, historyArr);

        boolean commentaryActive = (activeCommentaryView != null);
        outState.putBoolean(KEY_COMMENTARY_ACTIVE, commentaryActive);
        if (commentaryActive) {
            outState.putInt(KEY_COMMENTARY_CHAPTER, commentaryChapter);
            outState.putInt(KEY_COMMENTARY_VERSE, commentaryVerseNumber);
            outState.putBoolean(KEY_COMMENTARY_FULL_CHAPTER, commentaryFullChapterMode);
        }
    }

    private void restoreFromBundle(@NonNull Bundle bundle) {
        preferenceFontSize = bundle.getInt(KEY_FONT_SIZE, 18);
        selectedChapter    = bundle.getInt(KEY_CHAPTER,   1);
        int savedBookId = bundle.getInt(KEY_BOOK_ID, -1);
        if (savedBookId != -1) {
            selectedBook = findBookById(savedBookId);
        }
        int[] historyArr = bundle.getIntArray(KEY_HISTORY);
        if (historyArr != null) {
            for (int screen : historyArr) {
                screenHistory.add(screen);
                stateHistory.add(null);
            }
        }
        int screenToRestore = bundle.getInt(KEY_SCREEN, SCREEN_HOME);
        currentScreen = -1;

        switch (screenToRestore) {
            case SCREEN_CHAPTER_SELECT:
                navigateTo(SCREEN_CHAPTER_SELECT, selectedBook);
                break;
            case SCREEN_READING:
                navigateTo(SCREEN_READING, selectedChapter);
                break;
            case SCREEN_SEARCH:
                navigateTo(SCREEN_SEARCH, null);
                break;
            default:
                navigateTo(SCREEN_HOME, null);
                break;
        }

        boolean commentaryActive = bundle.getBoolean(KEY_COMMENTARY_ACTIVE, false);
        if (commentaryActive && screenToRestore == SCREEN_READING && selectedBook != null) {
            commentaryChapter         = bundle.getInt(KEY_COMMENTARY_CHAPTER, selectedChapter);
            commentaryVerseNumber     = bundle.getInt(KEY_COMMENTARY_VERSE, -1);
            commentaryFullChapterMode = bundle.getBoolean(KEY_COMMENTARY_FULL_CHAPTER, false);
            openCommentaryView();
        }
    }

    private void navigateTo(int screen, Object state) {
        if (currentScreen != screen) {
            screenHistory.add(currentScreen);
            stateHistory.add(saveCurrentState());
        }
        currentScreen = screen;
        contentFrame.removeAllViews();
        switch (screen) {
            case SCREEN_HOME:
                screenHistory.clear();
                stateHistory.clear();
                contentFrame.addView(buildHomeScreen());
                break;
            case SCREEN_CHAPTER_SELECT:
                selectedBook = (Book) state;
                contentFrame.addView(buildChapterSelectScreen());
                break;
            case SCREEN_READING:
                if (state instanceof Integer) selectedChapter = (Integer) state;
                contentFrame.addView(buildReadingScreen());
                break;
            case SCREEN_SEARCH:
                contentFrame.addView(buildSearchScreen());
                break;
        }
    }

    private Object saveCurrentState() {
        if (currentScreen == SCREEN_CHAPTER_SELECT) return selectedBook;
        if (currentScreen == SCREEN_READING)        return selectedChapter;
        return null;
    }

    private void restoreState(int screen, Object state) {
        currentScreen = screen;
        contentFrame.removeAllViews();
        switch (screen) {
            case SCREEN_HOME:
                screenHistory.clear();
                stateHistory.clear();
                contentFrame.addView(buildHomeScreen());
                break;
            case SCREEN_CHAPTER_SELECT:
                if (state instanceof Book) selectedBook = (Book) state;
                contentFrame.addView(buildChapterSelectScreen());
                break;
            case SCREEN_READING:
                if (state instanceof Integer) selectedChapter = (Integer) state;
                contentFrame.addView(buildReadingScreen());
                break;
            case SCREEN_SEARCH:
                contentFrame.addView(buildSearchScreen());
                break;
        }
    }
    private View buildHomeScreen() {
        BookSelectionView selectionView = new BookSelectionView(this, contentFrame, new BookSelectionView.BookSelectionListener() {
            @Override public void onBookSelected(Book book)     { navigateTo(SCREEN_CHAPTER_SELECT, book); }
            @Override public void onTabChanged(int testamentId) { bookViewModel.setTab(testamentId); }
            @Override public void onSearchRequested()           { navigateTo(SCREEN_SEARCH, null); }
            @Override public void onThemeToggleRequested()      { toggleTheme(); }
        });
        
        bookViewModel.getFilteredBooks().observe(this, selectionView::updateBookList);

        return selectionView.getRoot();
    }
    private void toggleTheme() {
        currentThemeIndex = (currentThemeIndex + 1) % appThemes.length;
        preferencesRepository.saveThemeIndex(currentThemeIndex);
        recreate();
    }

    private View buildChapterSelectScreen() {
        ChapterSelectionView selectionView = new ChapterSelectionView(this);
        View view = selectionView.createView();
        selectionView.getBackBtn().setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        selectionView.getTitle().setText(selectedBook.getName());
        GridLayout gridLayout = selectionView.getChaptersGrid();
        chapterViewModel.getChapters().observe(this, chapters -> {
            if (chapters == null) return;
            gridLayout.removeAllViews();
            for (final int chapter : chapters) {
                View btnView = getLayoutInflater().inflate(R.layout.layout_chapter_item, gridLayout, false);
                TextView chBtn = btnView.findViewById(R.id.chapter_btn);
                chBtn.setText(String.valueOf(chapter));
                chBtn.setOnClickListener(v -> navigateTo(SCREEN_READING, chapter));
                gridLayout.addView(btnView);
            }
        });
        chapterViewModel.loadChapters(selectedBook.getId());
        return view;
    }

    private View buildReadingScreen() {
        List<Integer> chapters = chapterLocalDataSource.getChapters(selectedBook.getId());
        VerseReaderView verseReaderView = new VerseReaderView(this, contentFrame, verseViewModel, new VerseReaderView.Listener() {
            @Override public void onBackPressed()            { navigateTo(SCREEN_HOME, null); }
            @Override public void onNavigateToPrevChapter() { navigateTo(SCREEN_READING, selectedChapter - 1); }
            @Override public void onNavigateToNextChapter() { navigateTo(SCREEN_READING, selectedChapter + 1); }
            @Override public void onStrongCodeClicked(WordToken token)  { strongBottomSheetView.show(token); }
            @Override public void onFontSizeChanged(int newSize)        { preferenceFontSize = newSize; preferencesRepository.saveFontSize(newSize);}
            @Override public void onVerseLongPressed(Verse verse)        { showCommentaryForVerse(verse); }
        });
        return verseReaderView.createView(selectedBook, selectedChapter, chapters, preferenceFontSize);
    }

    private View buildSearchScreen() {
        SearchView searchView = new SearchView(this, contentFrame, searchViewModel, allBooks, new SearchView.Listener() {
            @Override public void onBackPressed()                         { getOnBackPressedDispatcher().onBackPressed(); }
            @Override public void onResultClicked(Book book, int chapter) { selectedBook = book; navigateTo(SCREEN_READING, chapter); }
            @Override public void onStrongCodeClicked(WordToken token)    { strongBottomSheetView.show(token); }
        });
        return searchView.createView();
    }

    private Book findBookById(int id) {
        for (Book book : allBooks) {
            if (book.getId() == id) return book;
        }
        return null;
    }

    private void showCommentaryForVerse(Verse verse) {
        commentaryChapter         = selectedChapter;
        commentaryVerseNumber     = verse.getNumber();
        commentaryFullChapterMode = false;
        openCommentaryView();
    }

    private void openCommentaryView() {
        final CommentaryView commentaryView = new CommentaryView(this);

        commentaryView.setFontSize(preferenceFontSize);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        commentaryView.setLayoutParams(params);
        commentaryView.setBackgroundColor(getThemeColor(R.attr.appBackgroundColor));
        commentaryView.setHeaderTitle("Estudo " + selectedBook.getName());
        commentaryViewModel.getCommentaries().observe(this, commentaryView::setCommentaries);

        if (commentaryFullChapterMode) {
            loadFullChapterCommentary(commentaryView);
        } else {
            loadSingleVerseCommentary(commentaryView, commentaryVerseNumber);
        }

        activeCommentaryView = commentaryView;

        commentaryView.setOnCommentaryActionListener(new CommentaryView.OnCommentaryActionListener() {
            @Override public void onBackClicked()     { closeCommentaryView(); }
            @Override public void onShowMoreClicked() { commentaryFullChapterMode = true; loadFullChapterCommentary(commentaryView); }
            @Override public void onPrevClicked()     { changeCommentaryChapter(commentaryView, -1); }
            @Override public void onNextClicked()     { changeCommentaryChapter(commentaryView, 1); }
        });

        contentFrame.addView(commentaryView);
    }

    private void closeCommentaryView() {
        if (activeCommentaryView == null) return;
        commentaryViewModel.getCommentaries().removeObservers(this);
        contentFrame.removeView(activeCommentaryView);
        activeCommentaryView = null;
    }

    private void loadSingleVerseCommentary(CommentaryView commentaryView, int verseNumber) {
        commentaryView.setSubtitle("Comentário Capítulo " + commentaryChapter + " Versículo " + verseNumber);
        commentaryViewModel.loadCommentaryForVerse(selectedBook.getId(), commentaryChapter, verseNumber);
    }

    private void loadFullChapterCommentary(CommentaryView commentaryView) {
        commentaryView.setSubtitle("Comentários Capítulo " + commentaryChapter);
        commentaryViewModel.loadCommentaries(selectedBook.getId(), commentaryChapter);
    }

    private void changeCommentaryChapter(CommentaryView commentaryView, int direction) {
        List<Integer> chapters = chapterLocalDataSource.getChapters(selectedBook.getId());
        int newChapter = commentaryChapter + direction;
        if (chapters == null || !chapters.contains(newChapter)) {
            return;
        }
        commentaryChapter = newChapter;
        loadFullChapterCommentary(commentaryView);
    }
}
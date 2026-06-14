package com.devgabriel.bstrong;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.devgabriel.bstrong.features.chapters.data.datasource.ChapterLocalDataSource;
import com.devgabriel.bstrong.core.database.BibleDatabaseHelper;
import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.devgabriel.bstrong.features.chapters.domain.repository.ChapterRepository;
import com.devgabriel.bstrong.features.chapters.data.repository.ChapterRepositoryImpl;
import com.devgabriel.bstrong.features.chapters.presentation.viewmodel.ChapterViewModel;
import com.devgabriel.bstrong.features.chapters.presentation.view.ChapterSelectionView;
import com.devgabriel.bstrong.features.books.data.datasource.BookLocalDataSource;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.devgabriel.bstrong.features.books.domain.model.Book;
import com.devgabriel.bstrong.features.books.presentation.view.BookSelectionView;
import com.devgabriel.bstrong.features.books.presentation.viewmodel.BookViewModel;
import com.devgabriel.bstrong.features.books.data.repository.BookRepositoryImpl;
import com.devgabriel.bstrong.features.books.domain.repository.BookRepository;
import com.devgabriel.bstrong.features.verses.data.datasource.VerseLocalDataSource;
import com.devgabriel.bstrong.features.verses.domain.model.Verse;
import com.devgabriel.bstrong.features.verses.domain.model.WordToken;
import com.devgabriel.bstrong.features.verses.domain.repository.VerseRepository;
import com.devgabriel.bstrong.features.verses.data.repository.VerseRepositoryImpl;
import com.devgabriel.bstrong.features.verses.presentation.viewmodel.VerseViewModel;
import com.devgabriel.bstrong.features.verses.presentation.view.VerseReaderView;
import com.devgabriel.bstrong.features.strong.domain.repository.StrongRepository;
import com.devgabriel.bstrong.features.strong.data.repository.StrongRepositoryImpl;
import com.devgabriel.bstrong.features.strong.presentation.viewmodel.StrongViewModel;
import com.devgabriel.bstrong.features.strong.presentation.view.StrongBottomSheetView;
import com.devgabriel.bstrong.features.strong.data.datasource.StrongLocalDataSource;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ComponentActivity {

    private static final int COLOR_BACKGROUND = Color.parseColor("#1E1E2C");
    private static final int COLOR_PRIMARY_CYAN = Color.parseColor("#33E1ED");
    private static final int COLOR_TEXT_MUTED = Color.parseColor("#9A9AB0");

    private static final int SCREEN_HOME = 0;
    private static final int SCREEN_CHAPTER_SELECT = 1;
    private static final int SCREEN_READING = 2;
    private static final int SCREEN_SEARCH = 3;

    private BibleRepository repository;
    private FrameLayout contentFrame;
    private int currentScreen = SCREEN_HOME;
    
    private final List<Integer> screenHistory = new ArrayList<>();
    private final List<Object> stateHistory = new ArrayList<>();

    private Book selectedBook = null;
    private int selectedChapter = 1;
    private int preferenceFontSize = 18;

    private List<Book> allBooks = new ArrayList<>();
    private int selectedTab = 1;
    private ChapterLocalDataSource chapterLocalDataSource;
    private ChapterViewModel chapterViewModel;
    private BookLocalDataSource bookLocalDataSource;
    private BookViewModel bookViewModel;
    private VerseLocalDataSource verseLocalDataSource;
    private VerseViewModel verseViewModel;
    private StrongLocalDataSource strongLocalDataSource;
    private StrongBottomSheetView strongBottomSheetView;
    private StrongViewModel strongViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(COLOR_BACKGROUND);
        getWindow().setNavigationBarColor(COLOR_BACKGROUND);

        setContentView(R.layout.activity_main);
        contentFrame = findViewById(R.id.content_frame);

        chapterLocalDataSource = new ChapterLocalDataSource(new BibleDatabaseHelper(this));
        bookLocalDataSource = new BookLocalDataSource(new BibleDatabaseHelper(this));
        verseLocalDataSource = new VerseLocalDataSource(new BibleDatabaseHelper(this));
        strongLocalDataSource = new StrongLocalDataSource(new BibleDatabaseHelper(this));
        BookRepository bookRepository = new BookRepositoryImpl(bookLocalDataSource);
        bookViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new BookViewModel(bookRepository);
            }
        }).get(BookViewModel.class);
        ChapterRepository chapterRepository = new ChapterRepositoryImpl(chapterLocalDataSource);
        chapterViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {    
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ChapterViewModel(chapterRepository);
            }
        }).get(ChapterViewModel.class);
        VerseRepository verseRepository = new VerseRepositoryImpl(verseLocalDataSource);
        verseViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new VerseViewModel(verseRepository);
            }
        }).get(VerseViewModel.class);

        StrongRepository strongRepository = new StrongRepositoryImpl(strongLocalDataSource);
        strongViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new StrongViewModel(strongRepository);
            }
        }).get(StrongViewModel.class);

        strongBottomSheetView = new StrongBottomSheetView(this, this, strongViewModel);

        repository = new BibleRepository(this);
        allBooks = bookLocalDataSource.getAllBooks();

        navigateTo(SCREEN_HOME, null);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!screenHistory.isEmpty()) {
                    int lastScreen = screenHistory.remove(screenHistory.size() - 1);
                    Object lastState = stateHistory.remove(stateHistory.size() - 1);
                    restoreState(lastScreen, lastState);
                } else {
                    finish();
                }
            }
        });
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
                contentFrame.addView(buildHomeScreen());
                break;
            case SCREEN_CHAPTER_SELECT:
                selectedBook = (Book) state;
                contentFrame.addView(buildChapterSelectScreen());
                break;
            case SCREEN_READING:
                if (state instanceof Integer) {
                    selectedChapter = (Integer) state;
                }
                contentFrame.addView(buildReadingScreen());
                break;
            case SCREEN_SEARCH:
                contentFrame.addView(buildSearchScreen());
                break;
        }
    }

    private Object saveCurrentState() {
        if (currentScreen == SCREEN_CHAPTER_SELECT) {
            return selectedBook;
        } else if (currentScreen == SCREEN_READING) {
            return selectedChapter;
        }
        return null;
    }

    private void restoreState(int screen, Object state) {
        currentScreen = screen;
        contentFrame.removeAllViews();
        switch (screen) {
            case SCREEN_HOME:
                contentFrame.addView(buildHomeScreen());
                break;
            case SCREEN_CHAPTER_SELECT:
                selectedBook = (Book) state;
                contentFrame.addView(buildChapterSelectScreen());
                break;
            case SCREEN_READING:
                selectedChapter = (Integer) state;
                contentFrame.addView(buildReadingScreen());
                break;
            case SCREEN_SEARCH:
                contentFrame.addView(buildSearchScreen());
                break;
        }
    }

    private View buildHomeScreen() {
    BookSelectionView selectionView = new BookSelectionView(this, contentFrame, new BookSelectionView.BookSelectionListener() {
        @Override
        public void onBookSelected(Book book) {
            navigateTo(SCREEN_CHAPTER_SELECT, book);
        }

        @Override
        public void onTabChanged(int testamentId) {
            bookViewModel.setTab(testamentId);
        }

        @Override
        public void onSearchRequested() {
            navigateTo(SCREEN_SEARCH, null);
        }
    });
    bookViewModel.getFilteredBooks().observe(this, books -> {
        selectionView.updateBookList(books);
    });
    return selectionView.getRoot();
    }

    private View buildChapterSelectScreen() {
    ChapterSelectionView selectionView = new ChapterSelectionView(this);
    View view = selectionView.createView();
    selectionView.getBackBtn().setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    selectionView.getTitle().setText(selectedBook.getName());
    GridLayout gridLayout = selectionView.getChaptersGrid();
    chapterViewModel.getChapters().observe(this, chapters -> {
        if (chapters != null) {
            gridLayout.removeAllViews();
            for (final int chapter : chapters) {
                View btnView = getLayoutInflater().inflate(R.layout.layout_chapter_item, gridLayout, false);
                TextView chBtn = btnView.findViewById(R.id.chapter_btn);
                chBtn.setText(String.valueOf(chapter));

                chBtn.setOnClickListener(v -> navigateTo(SCREEN_READING, chapter));

                gridLayout.addView(btnView);
            }
        }
    });
    chapterViewModel.loadChapters(selectedBook.getId());
    return view;
}

    private View buildReadingScreen() {
    List<Integer> chapters = chapterLocalDataSource.getChapters(selectedBook.getId());

    VerseReaderView verseReaderView = new VerseReaderView(
        this,
        contentFrame,
        verseViewModel,
        new VerseReaderView.Listener() {
            @Override public void onBackPressed() {
                navigateTo(SCREEN_HOME, null);
            }
            @Override public void onNavigateToPrevChapter() {
                navigateTo(SCREEN_READING, selectedChapter - 1);
            }
            @Override public void onNavigateToNextChapter() {
                navigateTo(SCREEN_READING, selectedChapter + 1);
            }
            @Override public void onStrongCodeClicked(WordToken token) {
                showStrongDetailsBottomSheet(token);
            }
            @Override public void onFontSizeChanged(int newSize) {
                preferenceFontSize = newSize;
            }
        }
    );

    return verseReaderView.createView(selectedBook, selectedChapter, chapters, preferenceFontSize);
}

    private View buildSearchScreen() {
        View view = getLayoutInflater().inflate(R.layout.screen_search, contentFrame, false);

        TextView backBtn = view.findViewById(R.id.back_btn_search);
        final EditText searchInput = view.findViewById(R.id.search_input);
        final TextView labelResults = view.findViewById(R.id.label_results);
        final LinearLayout resultsContainer = view.findViewById(R.id.results_container);

        backBtn.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String q = s.toString().trim();
                resultsContainer.removeAllViews();
                
                if (q.length() < 3) {
                    labelResults.setText("Digite 3 ou mais letras...");
                    return;
                }

                List<SearchResult> matches = repository.search(q);
                labelResults.setText(matches.size() + " versículos encontrados.");

                for (final SearchResult match : matches) {
                    View itemCard = getLayoutInflater().inflate(R.layout.layout_search_item, resultsContainer, false);
                    TextView referenceHeader = itemCard.findViewById(R.id.reference_header);
                    TextView contentText = itemCard.findViewById(R.id.content_text);

                    referenceHeader.setText(match.getBookShortName() + " " + match.getChapter() + ":" + match.getVerse());
                    contentText.setHighlightColor(Color.TRANSPARENT);
                    contentText.setMovementMethod(LinkMovementMethod.getInstance());

                    List<WordToken> tokens = StrongParser.parseVerseText(match.getText());
                    match.setTokens(tokens);

                    SpannableStringBuilder ssb = new SpannableStringBuilder();
                    for (final WordToken token : tokens) {
                        ssb.append(token.getWord());
                        
                        if (token.hasStrong()) {
                            for (final String code : token.getStrongCodes()) {
                                ssb.append(" ");
                                int startCode = ssb.length();
                                ssb.append("<").append(code).append(">");
                                int endCode = ssb.length();
                                
                                ssb.setSpan(new ForegroundColorSpan(COLOR_PRIMARY_CYAN), startCode, endCode, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                ssb.setSpan(new UnderlineSpan(), startCode, endCode, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                
                                ssb.setSpan(new ClickableSpan() {
                                    @Override
                                    public void onClick(@NonNull View widget) {
                                        List<String> tempCodes = new ArrayList<>();
                                        tempCodes.add(code);
                                        showStrongDetailsBottomSheet(new WordToken(token.getWord(), tempCodes));
                                    }

                                    @Override
                                    public void updateDrawState(@NonNull TextPaint ds) {
                                        super.updateDrawState(ds);
                                        ds.setColor(COLOR_PRIMARY_CYAN);
                                        ds.setUnderlineText(true);
                                    }
                                }, startCode, endCode, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                        ssb.append(" ");
                    }

                    contentText.setText(ssb);

                    itemCard.setOnClickListener(v -> {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                        }

                        for (Book b : allBooks) {
                            if (b.getId() == match.getBookId()) {
                                selectedBook = b;
                                break;
                            }
                        }
                        selectedChapter = match.getChapter();
                        navigateTo(SCREEN_READING, selectedChapter);
                    });

                    resultsContainer.addView(itemCard);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchInput.requestFocus();

        return view;
    }

    private void showStrongDetailsBottomSheet(WordToken token) {
        strongBottomSheetView.show(token);
    }
}
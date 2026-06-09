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

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(COLOR_BACKGROUND);
        getWindow().setNavigationBarColor(COLOR_BACKGROUND);

        setContentView(R.layout.activity_main);
        contentFrame = findViewById(R.id.content_frame);

        repository = new BibleRepository(this);
        allBooks = repository.getAllBooks();

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
        View view = getLayoutInflater().inflate(R.layout.screen_home, contentFrame, false);

        ImageView searchIconHome = view.findViewById(R.id.search_icon_home);
        FrameLayout searchBarBtn = view.findViewById(R.id.search_bar_btn);
        TextView tabAt = view.findViewById(R.id.tab_at);
        TextView tabNt = view.findViewById(R.id.tab_nt);
        LinearLayout booksContainer = view.findViewById(R.id.books_container);

        searchIconHome.setOnClickListener(v -> navigateTo(SCREEN_SEARCH, null));
        searchBarBtn.setOnClickListener(v -> navigateTo(SCREEN_SEARCH, null));

        Runnable updateTabsUI = () -> {
            if (selectedTab == 1) {
                tabAt.setBackgroundResource(R.drawable.bg_card_border);
                tabAt.setTextColor(COLOR_PRIMARY_CYAN);
                tabNt.setBackground(null);
                tabNt.setTextColor(COLOR_TEXT_MUTED);
            } else {
                tabNt.setBackgroundResource(R.drawable.bg_card_border);
                tabNt.setTextColor(COLOR_PRIMARY_CYAN);
                tabAt.setBackground(null);
                tabAt.setTextColor(COLOR_TEXT_MUTED);
            }
        };

        updateTabsUI.run();

        View.OnClickListener tabListener = v -> {
            selectedTab = (v == tabAt) ? 1 : 2;
            updateTabsUI.run();
            booksContainer.removeAllViews();
            
            for (Book book : allBooks) {
                if (book.getTestamentId() == selectedTab) {
                    booksContainer.addView(buildBookItemRow(book));
                }
            }
        };
        tabAt.setOnClickListener(tabListener);
        tabNt.setOnClickListener(tabListener);

        for (Book book : allBooks) {
            if (book.getTestamentId() == selectedTab) {
                booksContainer.addView(buildBookItemRow(book));
            }
        }

        return view;
    }

    private View buildBookItemRow(Book book) {
        View row = getLayoutInflater().inflate(R.layout.layout_book_item, null);
        TextView badge = row.findViewById(R.id.book_badge);
        TextView name = row.findViewById(R.id.book_name);

        badge.setText(book.getShortName());
        name.setText(book.getName());

        row.setOnClickListener(v -> navigateTo(SCREEN_CHAPTER_SELECT, book));

        return row;
    }

    private View buildChapterSelectScreen() {
        View view = getLayoutInflater().inflate(R.layout.screen_chapter_select, contentFrame, false);

        TextView backBtn = view.findViewById(R.id.back_btn_chapters);
        TextView title = view.findViewById(R.id.book_title_chapters);
        GridLayout gridLayout = view.findViewById(R.id.chapters_grid);

        backBtn.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        title.setText(selectedBook.getName());

        List<Integer> chapters = repository.getChapters(selectedBook.getId());

        for (final int chapter : chapters) {
            View btnView = getLayoutInflater().inflate(R.layout.layout_chapter_item, gridLayout, false);
            TextView chBtn = btnView.findViewById(R.id.chapter_btn);
            chBtn.setText(String.valueOf(chapter));

            chBtn.setOnClickListener(v -> navigateTo(SCREEN_READING, chapter));

            gridLayout.addView(btnView);
        }

        return view;
    }

    private View buildReadingScreen() {
        View view = getLayoutInflater().inflate(R.layout.screen_reading, contentFrame, false);

        TextView backBtn = view.findViewById(R.id.back_btn_reading);
        TextView titleHeader = view.findViewById(R.id.title_header_reading);
        TextView fontSmaller = view.findViewById(R.id.font_smaller);
        TextView fontBigger = view.findViewById(R.id.font_bigger);
        TextView prevBtn = view.findViewById(R.id.prev_btn);
        TextView nextBtn = view.findViewById(R.id.next_btn);
        LinearLayout versesContainer = view.findViewById(R.id.verses_container);

        backBtn.setOnClickListener(v -> navigateTo(SCREEN_HOME, null));
        titleHeader.setText(selectedBook.getShortName() + " " + selectedChapter);

        List<Integer> chapters = repository.getChapters(selectedBook.getId());
        boolean hasNext = selectedChapter < chapters.size();

        prevBtn.setTextColor(selectedChapter > 1 ? COLOR_PRIMARY_CYAN : COLOR_TEXT_MUTED);
        nextBtn.setTextColor(hasNext ? COLOR_PRIMARY_CYAN : COLOR_TEXT_MUTED);

        if (selectedChapter > 1) {
            prevBtn.setOnClickListener(v -> navigateTo(SCREEN_READING, selectedChapter - 1));
        }
        if (hasNext) {
            nextBtn.setOnClickListener(v -> navigateTo(SCREEN_READING, selectedChapter + 1));
        }

        List<Verse> versesList = repository.getVerses(selectedBook.getId(), selectedChapter);

        Runnable updateVersesFont = () -> {
            versesContainer.removeAllViews();
            for (Verse verse : versesList) {
                versesContainer.addView(buildVerseView(verse));
            }
        };

        updateVersesFont.run();

        fontBigger.setOnClickListener(v -> {
            if (preferenceFontSize < 34) {
                preferenceFontSize += 2;
                updateVersesFont.run();
            }
        });

        fontSmaller.setOnClickListener(v -> {
            if (preferenceFontSize > 12) {
                preferenceFontSize -= 2;
                updateVersesFont.run();
            }
        });

        return view;
    }

    private View buildVerseView(Verse verse) {
        View view = getLayoutInflater().inflate(R.layout.layout_verse_item, null);
        TextView sectionTitle = view.findViewById(R.id.verse_section_title);
        TextView text = view.findViewById(R.id.verse_text);

        if (verse.getSectionTitle() != null && !verse.getSectionTitle().isEmpty()) {
            sectionTitle.setVisibility(View.VISIBLE);
            sectionTitle.setText(verse.getSectionTitle());
            sectionTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, preferenceFontSize - 2);
        } else {
            sectionTitle.setVisibility(View.GONE);
        }

        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, preferenceFontSize);
        text.setHighlightColor(Color.TRANSPARENT);
        text.setMovementMethod(LinkMovementMethod.getInstance());

        List<WordToken> tokens = StrongParser.parseVerseText(verse.getRawText());
        verse.setTokens(tokens);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        String prefix = verse.getNumber() + "  ";
        builder.append(prefix);
        builder.setSpan(new ForegroundColorSpan(COLOR_PRIMARY_CYAN), 0, prefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(Typeface.BOLD), 0, prefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        for (final WordToken token : tokens) {
            builder.append(token.getWord());
            
            if (token.hasStrong()) {
                for (final String code : token.getStrongCodes()) {
                    builder.append(" ");
                    int startCode = builder.length();
                    builder.append("<").append(code).append(">");
                    int endCode = builder.length();
                    
                    builder.setSpan(new ForegroundColorSpan(COLOR_PRIMARY_CYAN), startCode, endCode, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new UnderlineSpan(), startCode, endCode, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    
                    builder.setSpan(new ClickableSpan() {
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
            builder.append(" ");
        }

        text.setText(builder);
        return view;
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
        if (!token.hasStrong()) return;

        final BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        View bottomView = getLayoutInflater().inflate(R.layout.layout_strong_bottom_sheet, null);

        final String mainStrongCode = token.getStrongCodes().get(0);
        StrongEntry entry = repository.getStrong(mainStrongCode);

        TextView badge = bottomView.findViewById(R.id.bottom_sheet_badge);
        TextView langIndicator = bottomView.findViewById(R.id.bottom_sheet_lang);
        TextView unicodeValue = bottomView.findViewById(R.id.bottom_sheet_unicode);
        TextView translitText = bottomView.findViewById(R.id.bottom_sheet_translit);
        TextView meaningText = bottomView.findViewById(R.id.bottom_sheet_meaning);
        TextView closeBtn = bottomView.findViewById(R.id.bottom_sheet_close_btn);

        badge.setText(entry.getCode());
        langIndicator.setText(entry.getLanguage());
        unicodeValue.setText(entry.getOriginalWord());
        translitText.setText(entry.getTransliteration());
        meaningText.setText(entry.getMeaning());

        closeBtn.setOnClickListener(v -> bottomSheet.dismiss());

        bottomSheet.setContentView(bottomView);
        bottomSheet.show();
    }
}
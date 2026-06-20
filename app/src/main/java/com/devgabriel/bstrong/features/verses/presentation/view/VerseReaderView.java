package com.devgabriel.bstrong.features.verses.presentation.view;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.*;
import android.text.method.LinkMovementMethod;
import android.text.style.*;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import com.devgabriel.bstrong.R;
import com.devgabriel.bstrong.StrongParser;
import com.devgabriel.bstrong.features.books.domain.model.Book;
import com.devgabriel.bstrong.features.verses.domain.model.Verse;
import com.devgabriel.bstrong.features.verses.domain.model.WordToken;
import com.devgabriel.bstrong.features.verses.presentation.viewmodel.VerseViewModel;
import java.util.*;

public class VerseReaderView {

    private static final int COLOR_CYAN = Color.parseColor("#33E1ED");
    private static final int COLOR_MUTED = Color.parseColor("#9A9AB0");
    
    private Book currentBook;
    private int currentChapter;

    public interface Listener {
        void onBackPressed();
        void onNavigateToPrevChapter();
        void onNavigateToNextChapter();
        void onStrongCodeClicked(WordToken token);
        void onFontSizeChanged(int newSize);
        void onVerseLongPressed(Verse verse);
    }

    private final LifecycleOwner lifecycleOwner;
    private final ViewGroup parent;
    private final VerseViewModel verseViewModel;
    private final Listener listener;
    private int currentFontSize;
    private boolean isLongPressActive = false;

    public VerseReaderView(
            LifecycleOwner lifecycleOwner,
            ViewGroup parent,
            VerseViewModel verseViewModel,
            Listener listener
    ) {
        this.lifecycleOwner = lifecycleOwner;
        this.parent = parent;
        this.verseViewModel = verseViewModel;
        this.listener = listener;
    }

    public View createView(Book book, int chapter, List<Integer> availableChapters, int initialFontSize) {
        this.currentFontSize = initialFontSize;
        this.currentBook = book;
        this.currentChapter = chapter;

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.screen_reading, parent, false);

        TextView backBtn        = view.findViewById(R.id.back_btn_reading);
        TextView titleHeader    = view.findViewById(R.id.title_header_reading);
        TextView fontSmaller    = view.findViewById(R.id.font_smaller);
        TextView fontBigger     = view.findViewById(R.id.font_bigger);
        TextView prevBtn        = view.findViewById(R.id.prev_btn);
        TextView nextBtn        = view.findViewById(R.id.next_btn);
        LinearLayout versesContainer = view.findViewById(R.id.verses_container);

        backBtn.setOnClickListener(v -> listener.onBackPressed());
        titleHeader.setText(book.getName() + " " + chapter);

        boolean hasPrev = chapter > 1;
        boolean hasNext = chapter < availableChapters.size();

        prevBtn.setTextColor(hasPrev ? COLOR_CYAN : COLOR_MUTED);
        nextBtn.setTextColor(hasNext ? COLOR_CYAN : COLOR_MUTED);

        if (hasPrev) prevBtn.setOnClickListener(v -> listener.onNavigateToPrevChapter());
        if (hasNext) nextBtn.setOnClickListener(v -> listener.onNavigateToNextChapter());

        verseViewModel.loadVerses(book.getId(), chapter);
        verseViewModel.getVerses().observe(lifecycleOwner, verses -> {
            if (verses != null) renderVerses(inflater, versesContainer, verses);
        });

        fontBigger.setOnClickListener(v -> {
            if (currentFontSize < 34) {
                currentFontSize += 2;
                listener.onFontSizeChanged(currentFontSize);
                rerenderIfLoaded(inflater, versesContainer);
            }
        });

        fontSmaller.setOnClickListener(v -> {
            if (currentFontSize > 12) {
                currentFontSize -= 2;
                listener.onFontSizeChanged(currentFontSize);
                rerenderIfLoaded(inflater, versesContainer);
            }
        });

        return view;
    }

    private void rerenderIfLoaded(LayoutInflater inflater, LinearLayout container) {
        List<Verse> current = verseViewModel.getVerses().getValue();
        if (current != null) renderVerses(inflater, container, current);
    }

    private void renderVerses(LayoutInflater inflater, LinearLayout container, List<Verse> verses) {
        container.removeAllViews();
        for (Verse verse : verses) {
            container.addView(buildVerseView(inflater, verse));
        }
    }

    private View buildVerseView(LayoutInflater inflater, Verse verse) {
        View view = inflater.inflate(R.layout.layout_verse_item, null);
        TextView sectionTitle = view.findViewById(R.id.verse_section_title);
        TextView text = view.findViewById(R.id.verse_text);

        if (verse.getSectionTitle() != null && !verse.getSectionTitle().isEmpty()) {
            sectionTitle.setVisibility(View.VISIBLE);
            sectionTitle.setText(verse.getSectionTitle());
            sectionTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentFontSize - 2);
        } else {
            sectionTitle.setVisibility(View.GONE);
        }

        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentFontSize);
        text.setHighlightColor(Color.TRANSPARENT);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        text.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onVerseLongPressed(verse);
                isLongPressActive = true;
                return true; // Retorna true para indicar que o evento foi consumido
            }
            return false;
        });

        List<WordToken> tokens = StrongParser.parseVerseText(verse.getRawText());
        verse.setTokens(tokens);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        String prefix = verse.getNumber() + "  ";
        builder.append(prefix);
        builder.setSpan(new ForegroundColorSpan(COLOR_CYAN), 0, prefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(Typeface.BOLD), 0, prefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        for (final WordToken token : tokens) {
            if (token.hasStrong()) {
                int startWord = builder.length();
                builder.append(token.getWord());
                int endWord = builder.length();

                builder.setSpan(new ForegroundColorSpan(COLOR_CYAN), startWord, endWord, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        if (isLongPressActive) {
                            isLongPressActive = false;
                            return;
                        }
                        listener.onStrongCodeClicked(new WordToken(token.getWord(), token.getStrongCodes()));
                    }
                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(COLOR_CYAN);
                        ds.setUnderlineText(true);
                    }
                }, startWord, endWord, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                builder.append(token.getWord());
            }
            builder.append(" ");
        }
        text.setText(builder);
        return view;
    }
}
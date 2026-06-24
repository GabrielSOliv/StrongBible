package com.devgabriel.bstrong.features.verses.presentation.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.*;
import android.text.method.LinkMovementMethod;
import android.text.style.*;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.LifecycleOwner;
import com.devgabriel.bstrong.R;
import com.devgabriel.bstrong.StrongParser;
import com.devgabriel.bstrong.features.books.domain.model.Book;
import com.devgabriel.bstrong.features.verses.domain.model.Verse;
import com.devgabriel.bstrong.features.verses.domain.model.WordToken;
import com.devgabriel.bstrong.features.verses.presentation.viewmodel.VerseViewModel;
import java.util.*;

public class VerseReaderView {

    private Book currentBook;
    private int currentChapter;
    private int targetVerse = -1;

    public interface Listener {
        void onBackPressed();
        void onNavigateToPrevChapter();
        void onNavigateToNextChapter();
        void onStrongCodeClicked(WordToken token);
        void onFontSizeChanged(int newSize);
        void onCommentaryRequested(List<Verse> selectedVerses);
    }

    private final LifecycleOwner lifecycleOwner;
    private final ViewGroup parent;
    private final VerseViewModel verseViewModel;
    private final Listener listener;
    private int currentFontSize;
    private boolean isLongPressActive = false;

    private int dynamicAccentColor;
    private int dynamicMutedColor;

    private boolean selectionModeActive = false;
    private final LinkedHashMap<Integer, Verse> selectedVerses = new LinkedHashMap<>();
    private final Map<Integer, View> verseRowViews = new HashMap<>();

    private View headerRow;
    private View chapterNavRow;
    private View selectionToolbar;
    private TextView selectionCountText;
    private ImageView selectionCancelBtn;
    private ImageView selectionCopyBtn;
    private ImageView selectionCommentsBtn;

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

    public View createView(Book book, int chapter, int targetVerse, List<Integer> availableChapters, int initialFontSize) {
        this.currentFontSize = initialFontSize;
        this.currentBook = book;
        this.currentChapter = chapter;
        this.targetVerse = targetVerse;

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.screen_reading, parent, false);

        this.dynamicAccentColor = getThemeColor(context, R.attr.appColorAccent);
        this.dynamicMutedColor = getThemeColor(context, R.attr.appTextColorSecondary);

        ImageView backBtn        = view.findViewById(R.id.back_btn_reading);
        TextView titleHeader    = view.findViewById(R.id.title_header_reading);
        TextView fontSmaller    = view.findViewById(R.id.font_smaller);
        TextView fontBigger     = view.findViewById(R.id.font_bigger);
        TextView prevBtn        = view.findViewById(R.id.prev_btn);
        TextView nextBtn        = view.findViewById(R.id.next_btn);
        LinearLayout versesContainer = view.findViewById(R.id.verses_container);

        headerRow            = view.findViewById(R.id.header_row_reading);
        chapterNavRow        = view.findViewById(R.id.chapter_nav_row_reading);
        selectionToolbar     = view.findViewById(R.id.selection_toolbar);
        selectionCountText   = view.findViewById(R.id.selection_count_text);
        selectionCancelBtn   = view.findViewById(R.id.selection_cancel_btn);
        selectionCopyBtn     = view.findViewById(R.id.selection_copy_btn);
        selectionCommentsBtn = view.findViewById(R.id.selection_comments_btn);

        selectionCancelBtn.setOnClickListener(v -> clearSelections());
        selectionCopyBtn.setOnClickListener(v -> copySelectedVerses());
        selectionCommentsBtn.setOnClickListener(v -> requestCommentaryForSelection());

        backBtn.setOnClickListener(v -> listener.onBackPressed());
        titleHeader.setText(book.getName() + " " + chapter);

        boolean hasPrev = chapter > 1;
        boolean hasNext = chapter < availableChapters.size();

        prevBtn.setTextColor(hasPrev ? dynamicAccentColor : dynamicMutedColor);
        nextBtn.setTextColor(hasNext ? dynamicAccentColor : dynamicMutedColor);

        if (hasPrev) prevBtn.setOnClickListener(v -> listener.onNavigateToPrevChapter());
        if (hasNext) nextBtn.setOnClickListener(v -> listener.onNavigateToNextChapter());

        verseViewModel.loadVerses(book.getId(), chapter);
        verseViewModel.getVerses().observe(lifecycleOwner, verses -> {
            if (verses != null) {
                renderVerses(inflater, versesContainer, verses);

                if (this.targetVerse != -1) {
                    scrollToTargetVerse(versesContainer, verses);
                }
            }
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

    private void scrollToTargetVerse(LinearLayout versesContainer, List<Verse> verses) {
        int targetIndex = -1;
        for (int i = 0; i < verses.size(); i++) {
            if (verses.get(i).getNumber() == this.targetVerse) {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex != -1 && targetIndex < versesContainer.getChildCount()) {
            View targetChild = versesContainer.getChildAt(targetIndex);

            ViewParent parent = versesContainer.getParent();
            while (parent != null && !(parent instanceof ScrollView) && !(parent instanceof NestedScrollView)) {
                parent = parent.getParent();
            }

            if (parent != null) {
                final View scrollView = (View) parent;
                scrollView.post(() -> {
                    if (scrollView instanceof ScrollView) {
                        ((ScrollView) scrollView).smoothScrollTo(0, targetChild.getTop());
                    } else if (scrollView instanceof NestedScrollView) {
                        ((NestedScrollView) scrollView).smoothScrollTo(0, targetChild.getTop());
                    }

                    int highlightColor = (dynamicAccentColor & 0x00FFFFFF) | 0x33000000;
                    targetChild.setBackgroundColor(highlightColor);
                    targetChild.postDelayed(() -> targetChild.setBackgroundColor(Color.TRANSPARENT), 1000);
                });
            }
        }
        this.targetVerse = -1;
    }

    private int getThemeColor(Context context, int attrColor) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrColor, typedValue, true);
        return typedValue.data;
    }

    private void rerenderIfLoaded(LayoutInflater inflater, LinearLayout container) {
        List<Verse> current = verseViewModel.getVerses().getValue();
        if (current != null) renderVerses(inflater, container, current);
    }

    private void renderVerses(LayoutInflater inflater, LinearLayout container, List<Verse> verses) {
        container.removeAllViews();
        verseRowViews.clear();
        int selectedColor = (dynamicAccentColor & 0x00FFFFFF) | 0x33000000;
        for (Verse verse : verses) {
            View row = buildVerseView(inflater, verse);
            verseRowViews.put(verse.getNumber(), row);
            if (selectionModeActive && selectedVerses.containsKey(verse.getNumber())) {
                row.setBackgroundColor(selectedColor);
            }
            container.addView(row);
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

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable highlightRunnable = () -> {
            int pressColor = (dynamicAccentColor & 0x00FFFFFF) | 0x26000000;
            view.setBackgroundColor(pressColor);
        };

        final boolean[] wasLongPress = {false};

        text.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isLongPressActive = false;
                    wasLongPress[0] = false;
                    if (!selectedVerses.containsKey(verse.getNumber())) {
                        handler.postDelayed(highlightRunnable, 3);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handler.removeCallbacks(highlightRunnable);
                    if (!wasLongPress[0] && !selectedVerses.containsKey(verse.getNumber())) {
                        view.setBackgroundColor(Color.TRANSPARENT);
                    }
                    break;
            }
            return false;
        });

        text.setOnClickListener(v -> {
            if (isLongPressActive) {
                isLongPressActive = false;
                return;
            }
            if (selectionModeActive) {
                toggleVerseSelection(verse, view);
            }
        });
        text.setOnLongClickListener(v -> {
            wasLongPress[0] = true;
            isLongPressActive = true;
            handler.removeCallbacks(highlightRunnable);
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            toggleVerseSelection(verse, view);
            return true;
        });

        List<WordToken> tokens = StrongParser.parseVerseText(verse.getRawText());
        verse.setTokens(tokens);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        String prefix = verse.getNumber() + "  ";
        builder.append(prefix);

        builder.setSpan(new ForegroundColorSpan(dynamicAccentColor), 0, prefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(Typeface.BOLD), 0, prefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        for (final WordToken token : tokens) {
            if (token.hasStrong()) {
                int startWord = builder.length();
                builder.append(token.getWord());
                int endWord = builder.length();

                builder.setSpan(new ForegroundColorSpan(dynamicAccentColor), startWord, endWord, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        if (isLongPressActive) {
                            isLongPressActive = false;
                            return;
                        }
                        if (selectionModeActive) {
                            toggleVerseSelection(verse, view);
                            return;
                        }
                        listener.onStrongCodeClicked(new WordToken(token.getWord(), token.getStrongCodes()));
                    }
                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(dynamicAccentColor);
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

    private void showSelectionToolbar() {
        if (headerRow != null) headerRow.setVisibility(View.GONE);
        if (chapterNavRow != null) chapterNavRow.setVisibility(View.GONE);
        if (selectionToolbar != null) selectionToolbar.setVisibility(View.VISIBLE);
    }

    private void toggleVerseSelection(Verse verse, View rowView) {
        if (!selectionModeActive) {
            selectionModeActive = true;
            showSelectionToolbar();
        }

        int key = verse.getNumber();
        int selectedColor = (dynamicAccentColor & 0x00FFFFFF) | 0x33000000;

        if (selectedVerses.containsKey(key)) {
            selectedVerses.remove(key);
            rowView.setBackgroundColor(Color.TRANSPARENT);
        } else {
            selectedVerses.put(key, verse);
            rowView.setBackgroundColor(selectedColor);
        }

        if (selectedVerses.isEmpty()) {
            clearSelections();
        } else {
            updateSelectionToolbar();
        }
    }

    private void updateSelectionToolbar() {
        if (selectionCountText == null) return;
        int count = selectedVerses.size();
        selectionCountText.setText(count + (count == 1 ? " versículo selecionado" : " versículos selecionados"));
    }

    private void copySelectedVerses() {
        if (selectedVerses.isEmpty()) return;

        List<Verse> ordered = new ArrayList<>(selectedVerses.values());
        Collections.sort(ordered, (a, b) -> Integer.compare(a.getNumber(), b.getNumber()));

        StringBuilder sb = new StringBuilder();
        if (currentBook != null) {
            sb.append(currentBook.getName()).append(" ").append(currentChapter);
            if (ordered.size() == 1) {
                sb.append(":").append(ordered.get(0).getNumber());
            } else {
                sb.append(":").append(ordered.get(0).getNumber())
                  .append("-").append(ordered.get(ordered.size() - 1).getNumber());
            }
            sb.append("\n");
        }

        for (Verse v : ordered) {
            sb.append(v.getNumber()).append(" ").append(buildPlainVerseText(v)).append("\n");
        }

        Context context = parent.getContext();
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Versículo", sb.toString().trim());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(
                    context,
                    ordered.size() == 1 ? "Versículo copiado" : "Versículos copiados",
                    Toast.LENGTH_SHORT
            ).show();
        }

        clearSelections();
    }

    private String buildPlainVerseText(Verse verse) {
        List<WordToken> tokens = verse.getTokens();
        if (tokens == null || tokens.isEmpty()) return verse.getRawText();
        StringBuilder text = new StringBuilder();
        for (WordToken token : tokens) {
            text.append(token.getWord()).append(" ");
        }
        return text.toString().trim();
    }

    private void requestCommentaryForSelection() {
        if (selectedVerses.isEmpty() || listener == null) return;
        List<Verse> verses = new ArrayList<>(selectedVerses.values());
        listener.onCommentaryRequested(verses);
        clearSelections();
    }

    public void clearSelections() {
        for (View row : verseRowViews.values()) {
            row.setBackgroundColor(Color.TRANSPARENT);
        }
        isLongPressActive = false;
        selectedVerses.clear();
        selectionModeActive = false;
        if (headerRow != null) headerRow.setVisibility(View.VISIBLE);
        if (chapterNavRow != null) chapterNavRow.setVisibility(View.VISIBLE);
        if (selectionToolbar != null) selectionToolbar.setVisibility(View.GONE);
        updateSelectionToolbar();
    }
}
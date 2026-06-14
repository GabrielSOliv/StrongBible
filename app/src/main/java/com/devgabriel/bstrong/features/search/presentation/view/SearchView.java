package com.devgabriel.bstrong.features.search.presentation.view;

import android.content.Context;
import android.graphics.Color;
import android.text.*;
import android.text.method.LinkMovementMethod;
import android.text.style.*;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import com.devgabriel.bstrong.R;
import com.devgabriel.bstrong.StrongParser;
import com.devgabriel.bstrong.features.books.domain.model.Book;
import com.devgabriel.bstrong.features.search.domain.model.SearchResult;
import com.devgabriel.bstrong.features.search.presentation.viewmodel.SearchViewModel;
import com.devgabriel.bstrong.features.verses.domain.model.WordToken;
import java.util.ArrayList;
import java.util.List;

public class SearchView {

    private static final int COLOR_CYAN = Color.parseColor("#33E1ED");

    public interface Listener {
        void onBackPressed();
        void onResultClicked(Book book, int chapter);
        void onStrongCodeClicked(WordToken token);
    }

    private final LifecycleOwner lifecycleOwner;
    private final ViewGroup parent;
    private final SearchViewModel viewModel;
    private final Listener listener;
    private final List<Book> allBooks;

    public SearchView(
            LifecycleOwner lifecycleOwner,
            ViewGroup parent,
            SearchViewModel viewModel,
            List<Book> allBooks,
            Listener listener
    ) {
        this.lifecycleOwner = lifecycleOwner;
        this.parent = parent;
        this.viewModel = viewModel;
        this.allBooks = allBooks;
        this.listener = listener;
    }

    public View createView() {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.screen_search, parent, false);

        TextView backBtn            = view.findViewById(R.id.back_btn_search);
        EditText searchInput        = view.findViewById(R.id.search_input);
        TextView labelResults       = view.findViewById(R.id.label_results);
        LinearLayout resultsContainer = view.findViewById(R.id.results_container);

        backBtn.setOnClickListener(v -> listener.onBackPressed());

        viewModel.getResults().observe(lifecycleOwner, matches -> {
            resultsContainer.removeAllViews();
            if (matches == null) return;

            labelResults.setText(matches.size() + " versículos encontrados.");

            for (final SearchResult match : matches) {
                View itemCard = inflater.inflate(R.layout.layout_search_item, resultsContainer, false);
                TextView referenceHeader = itemCard.findViewById(R.id.reference_header);
                TextView contentText     = itemCard.findViewById(R.id.content_text);

                referenceHeader.setText(
                    match.getBookShortName() + " " + match.getChapter() + ":" + match.getVerse()
                );

                contentText.setHighlightColor(Color.TRANSPARENT);
                contentText.setMovementMethod(LinkMovementMethod.getInstance());
                contentText.setText(buildSpannable(match, inflater));

                itemCard.setOnClickListener(v -> {
                    hideKeyboard(searchInput);

                    Book found = null;
                    for (Book b : allBooks) {
                        if (b.getId() == match.getBookId()) {
                            found = b;
                            break;
                        }
                    }
                    if (found != null) {
                        listener.onResultClicked(found, match.getChapter());
                    }
                });

                resultsContainer.addView(itemCard);
            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String q = s.toString().trim();
                if (q.length() < 2) {
                    labelResults.setText("Digite 2 ou mais letras...");
                    resultsContainer.removeAllViews();
                    return;
                }
                viewModel.loadSearch(q);
            }
        });

        searchInput.requestFocus();
        return view;
    }

    private SpannableStringBuilder buildSpannable(SearchResult match, LayoutInflater inflater) {
        List<WordToken> tokens = StrongParser.parseVerseText(match.getText());
        match.setTokens(tokens);

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (final WordToken token : tokens) {
            ssb.append(token.getWord());

            if (token.hasStrong()) {
                for (final String code : token.getStrongCodes()) {
                    ssb.append(" ");
                    int start = ssb.length();
                    ssb.append("<").append(code).append(">");
                    int end = ssb.length();

                    ssb.setSpan(new ForegroundColorSpan(COLOR_CYAN), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull android.view.View widget) {
                            List<String> tempCodes = new ArrayList<>();
                            tempCodes.add(code);
                            listener.onStrongCodeClicked(new WordToken(token.getWord(), tempCodes));
                        }
                        @Override
                        public void updateDrawState(@NonNull TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(COLOR_CYAN);
                            ds.setUnderlineText(true);
                        }
                    }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            ssb.append(" ");
        }
        return ssb;
    }

    private void hideKeyboard(android.view.View focusedView) {
        Context context = focusedView.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }
}
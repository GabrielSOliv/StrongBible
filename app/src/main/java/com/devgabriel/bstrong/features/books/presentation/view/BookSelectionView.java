package com.devgabriel.bstrong.features.books.presentation.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.devgabriel.bstrong.R;
import com.devgabriel.bstrong.features.books.domain.model.Book;
import java.util.List;

public class BookSelectionView {

    public interface BookSelectionListener {
        void onBookSelected(Book book);
        void onTabChanged(int testamentId);
        void onSearchRequested();
        void onThemeToggleRequested(); 
    }
    private final View root;
    private final LinearLayout booksContainer;
    private final TextView tabAt, tabNt;
    private final BookSelectionListener listener;

    public BookSelectionView(Context context, ViewGroup parent, BookSelectionListener listener) {
        this.listener = listener;
        this.root = LayoutInflater.from(context).inflate(R.layout.screen_home, parent, false);
        
        this.booksContainer = root.findViewById(R.id.books_container);
        this.tabAt = root.findViewById(R.id.tab_at);
        this.tabNt = root.findViewById(R.id.tab_nt);

        setupClickListeners();
    }
    private void setupClickListeners() {
        tabAt.setOnClickListener(v -> {
            tabAt.setSelected(true);
            tabNt.setSelected(false);
            listener.onTabChanged(1);
        });
        tabNt.setOnClickListener(v -> {
            tabNt.setSelected(true);
            tabAt.setSelected(false);
            listener.onTabChanged(2);
        });

        tabAt.setSelected(true);
        root.findViewById(R.id.search_icon_home).setOnClickListener(v -> listener.onSearchRequested());
        View themeBtn = root.findViewById(R.id.theme_toggle_btn);
        if (themeBtn != null) {
            themeBtn.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onThemeToggleRequested();
                }
            });
        }
    }
    public void updateBookList(List<Book> books) {
        booksContainer.removeAllViews();
        if (books == null) return;

        for (Book book : books) {
            View row = buildBookItemRow(book);
            booksContainer.addView(row);
        }
    }
    private View buildBookItemRow(Book book) {
        View row = LayoutInflater.from(root.getContext())
                .inflate(R.layout.layout_book_item, booksContainer, false);
        TextView badge = row.findViewById(R.id.book_badge);
        TextView name = row.findViewById(R.id.book_name);
        badge.setText(book.getShortName());
        name.setText(book.getName());
        row.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookSelected(book);
            }
        });
        return row;
    }
    public View getRoot() {
        return root;
    }
}
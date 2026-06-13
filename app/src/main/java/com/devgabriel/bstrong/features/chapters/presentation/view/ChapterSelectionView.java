package com.devgabriel.bstrong.features.chapters.presentation.view;

import android.app.Activity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import com.devgabriel.bstrong.R;

public class ChapterSelectionView {
    private final Activity activity;
    private View root;
    private TextView backBtn;
    private TextView title;
    private GridLayout chaptersGrid;

    public ChapterSelectionView(Activity activity) {
        this.activity = activity;
    }
   public View createView() {
        this.root = activity.getLayoutInflater().inflate(R.layout.screen_chapter_select, null, false);
        this.backBtn = root.findViewById(R.id.back_btn_chapters);
        this.title = root.findViewById(R.id.book_title_chapters);
        this.chaptersGrid = root.findViewById(R.id.chapters_grid);
        return this.root;
    }
    public View getRoot() {
        return root;
    }
    public TextView getBackBtn() {
        return backBtn;
    }
    public TextView getTitle() {
        return title;
    }
    public GridLayout getChaptersGrid() {
        return chaptersGrid;
    }
}

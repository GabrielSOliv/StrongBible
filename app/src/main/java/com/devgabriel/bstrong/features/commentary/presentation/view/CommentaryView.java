package com.devgabriel.bstrong.features.commentary.presentation.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devgabriel.bstrong.R;
import com.devgabriel.bstrong.features.commentary.domain.model.Commentary;

import java.util.List;

public class CommentaryView extends LinearLayout {

    private TextView backBtn;
    private TextView titleHeader;
    private TextView btnShowMore;
    private TextView prevBtn;
    private TextView nextBtn;
    private LinearLayout versesContainer;

    private OnCommentaryActionListener actionListener;

    public interface OnCommentaryActionListener {
        void onBackClicked();
        void onShowMoreClicked();
        void onPrevClicked();
        void onNextClicked();
    }

    public CommentaryView(Context context) {
        super(context);
        init(context);
    }

    public CommentaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommentaryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.screen_commentary_reading, this, true);
        
        setOrientation(LinearLayout.VERTICAL);

        backBtn = findViewById(R.id.back_btn_reading);
        titleHeader = findViewById(R.id.title_header_reading);
        btnShowMore = findViewById(R.id.font_smaller);
        prevBtn = findViewById(R.id.prev_btn);
        nextBtn = findViewById(R.id.next_btn);
        versesContainer = findViewById(R.id.verses_container);

        setupClickListeners();
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onBackClicked();
        });

        btnShowMore.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onShowMoreClicked();
        });

        prevBtn.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onPrevClicked();
        });

        nextBtn.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onNextClicked();
        });
    }

    public void setOnCommentaryActionListener(OnCommentaryActionListener listener) {
        this.actionListener = listener;
    }

    public void setHeaderTitle(String title) {
        if (titleHeader != null) {
            titleHeader.setText(title);
        }
    }

    public void setCommentaries(List<Commentary> commentaries) {
        if (versesContainer == null) return;

        versesContainer.removeAllViews();

        for (Commentary commentary : commentaries) {
            TextView textView = new TextView(getContext());

            String formattedText = "Versículo " + commentary.getVerse() + "\n" + commentary.getText();
            textView.setText(formattedText);
            
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(16f);
            textView.setPadding(0, 12, 0, 24);

            versesContainer.addView(textView);
        }
    }
}
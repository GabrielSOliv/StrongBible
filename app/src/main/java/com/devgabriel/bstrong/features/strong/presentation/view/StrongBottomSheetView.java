package com.devgabriel.bstrong.features.strong.presentation.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.lifecycle.Observer;
import com.devgabriel.bstrong.R;
import com.devgabriel.bstrong.features.strong.domain.model.StrongEntry;
import com.devgabriel.bstrong.features.strong.presentation.viewmodel.StrongViewModel;
import com.devgabriel.bstrong.features.verses.domain.model.WordToken;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class StrongBottomSheetView {

    public interface Listener {
        void onDismiss();
    }

    private final Context context;
    private final StrongViewModel viewModel;
    private Observer<StrongEntry> activeObserver;

    public StrongBottomSheetView(
            Context context,
            StrongViewModel viewModel
    ) {
        this.context = context;
        this.viewModel = viewModel;
    }

    public void show(WordToken token) {
        if (token == null || !token.hasStrong()) return;

        String code = token.getStrongCodes().get(0);

        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_strong_bottom_sheet, null);

        TextView badge       = view.findViewById(R.id.bottom_sheet_badge);
        TextView lang        = view.findViewById(R.id.bottom_sheet_lang);
        TextView unicode     = view.findViewById(R.id.bottom_sheet_unicode);
        TextView translit    = view.findViewById(R.id.bottom_sheet_translit);
        TextView meaning     = view.findViewById(R.id.bottom_sheet_meaning);
        TextView closeBtn    = view.findViewById(R.id.bottom_sheet_close_btn);

        if (activeObserver != null) {
            viewModel.getStrong().removeObserver(activeObserver);
        }

        activeObserver = entry -> {
            if (entry == null) {
                badge.setText(code);
                lang.setText("-");
                unicode.setText("-");
                translit.setText("-");
                meaning.setText("Código não encontrado.");
                return;
            }
            badge.setText(entry.getCode());
            lang.setText(entry.getLanguage());
            unicode.setText(entry.getOriginalWord());
            translit.setText(entry.getTransliteration());
            meaning.setText(entry.getMeaning());
        };

        viewModel.getStrong().observeForever(activeObserver);

        dialog.setOnDismissListener(d -> {
            if (activeObserver != null) {
                viewModel.getStrong().removeObserver(activeObserver);
                activeObserver = null;
            }
        });

        closeBtn.setOnClickListener(v -> dialog.dismiss());

        viewModel.loadStrong(code);

        dialog.setContentView(view);
        dialog.show();
    }
}
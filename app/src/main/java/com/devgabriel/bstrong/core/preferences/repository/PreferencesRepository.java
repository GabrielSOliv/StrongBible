package com.devgabriel.bstrong.core.preferences.repository;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesRepository {
    private static final String PREFS_NAME = "bstrong_preferences";
    private static final String KEY_THEME_INDEX = "selected_theme_index";
    private static final String KEY_FONT_SIZE = "preference_font_size";
    
    private final SharedPreferences prefs;

    public PreferencesRepository(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getThemeIndex() {
        return prefs.getInt(KEY_THEME_INDEX, 0);
    }

    public void saveThemeIndex(int index) {
        prefs.edit().putInt(KEY_THEME_INDEX, index).apply();
    }

    public int getFontSize() {
        return prefs.getInt(KEY_FONT_SIZE, 18);
    }

    public void saveFontSize(int size) {
        prefs.edit().putInt(KEY_FONT_SIZE, size).apply();
    }
}

package com.devgabriel.bstrong.features.verses.data.datasource;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import com.devgabriel.bstrong.core.database.BibleDatabaseHelper;
import com.devgabriel.bstrong.features.verses.domain.model.Verse;

public class VerseLocalDataSource {
    private final BibleDatabaseHelper databaseHelper;
    private static final String TAG = "VerseLocalDataSource";

    public VerseLocalDataSource(BibleDatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public List<Verse> getVerses(int bookId, int chapter) {
        List<Verse> list = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                "SELECT VER_VERSICULO, VER_TITULO, VER_TEXTO FROM VERSICULOS WHERE VER_LIV_ID = ? AND VER_CAPITULO = ? ORDER BY VER_VERSICULO",
                new String[]{String.valueOf(bookId), String.valueOf(chapter)}
            );
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int number = cursor.getInt(0);
                    String title = cursor.getString(1);
                    String rawText = cursor.getString(2);
                    list.add(new Verse(number, title, rawText));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading verses: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }
}

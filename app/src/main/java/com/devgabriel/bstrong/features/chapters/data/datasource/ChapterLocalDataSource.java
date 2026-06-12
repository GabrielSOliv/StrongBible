package com.devgabriel.bstrong.features.chapters.data.datasource;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import com.devgabriel.bstrong.core.database.BibleDatabaseHelper;

public class ChapterLocalDataSource {
    private final BibleDatabaseHelper databaseHelper;
    private static final String TAG = "ChapterLocalDataSource";
    public ChapterLocalDataSource(BibleDatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public List<Integer> getChapters(int bookId) {
        List<Integer> list = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                "SELECT DISTINCT VER_CAPITULO FROM VERSICULOS WHERE VER_LIV_ID = ? ORDER BY VER_CAPITULO",
                new String[]{String.valueOf(bookId)}
            );
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(cursor.getInt(0));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading chapters for book " + bookId + ": " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }
}
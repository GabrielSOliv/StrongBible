package com.devgabriel.bstrong;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import com.devgabriel.bstrong.core.database.BibleDatabaseHelper;

public class BibleRepository {

    private static final String TAG = "BibleRepository";
    private final BibleDatabaseHelper dbHelper;

    public BibleRepository(Context context) {
        this.dbHelper = new BibleDatabaseHelper(context);
    }

    public List<SearchResult> search(String query) {
        List<SearchResult> list = new ArrayList<>();
        if (query == null || query.trim().length() < 3) return list;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                "SELECT v.VER_LIV_ID, v.VER_CAPITULO, v.VER_VERSICULO, v.VER_TEXTO, l.LIV_NOME_CURTO " +
                "FROM VERSICULOS v JOIN LIVROS l ON v.VER_LIV_ID = l.LIV_ID " +
                "WHERE v.VER_TEXTO LIKE ? LIMIT 100",
                new String[]{"%" + query + "%"}
            );
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int bookId = cursor.getInt(0);
                    int chapter = cursor.getInt(1);
                    int verse = cursor.getInt(2);
                    String text = cursor.getString(3);
                    String bookShort = cursor.getString(4);
                    list.add(new SearchResult(bookId, bookShort, chapter, verse, text));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error searching: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    private int getBookChaptersCountFallback(int bookId) {
        switch (bookId) {
            case 1: return 50;
            case 2: return 40;
            case 19: return 150;
            case 43: return 21;
            default: return 28;
        }
    }
}

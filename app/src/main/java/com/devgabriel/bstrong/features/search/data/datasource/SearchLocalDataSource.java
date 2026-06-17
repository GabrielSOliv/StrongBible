package com.devgabriel.bstrong.features.search.data.datasource;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import com.devgabriel.bstrong.core.database.BibleDatabaseHelper;
import com.devgabriel.bstrong.features.search.domain.model.SearchResult;


public class SearchLocalDataSource {
    private static final String TAG = "SearchLocalDataSource";
    private final BibleDatabaseHelper databaseHelper;

    public SearchLocalDataSource(BibleDatabaseHelper databaseHelper){
        this.databaseHelper = databaseHelper;
    }

    public List<SearchResult> search(String query) {
        List<SearchResult> list = new ArrayList<>();
        if (query == null || query.trim().length() < 2) return list;

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
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
}
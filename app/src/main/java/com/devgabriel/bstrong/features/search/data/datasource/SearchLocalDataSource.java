package com.devgabriel.bstrong.features.search.data.datasource;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import com.devgabriel.bstrong.core.database.BibleDatabaseHelper;
import com.devgabriel.bstrong.features.search.domain.model.SearchResult;

public class SearchLocalDataSource {
    private static final String TAG = "SearchLocalDataSource";
    private static final int MAX_RESULTS = 300;

    private final BibleDatabaseHelper databaseHelper;

    public enum Testament {
        AT, NT
    }

    public SearchLocalDataSource(BibleDatabaseHelper databaseHelper){
        this.databaseHelper = databaseHelper;
    }

    public List<SearchResult> search(String query, Testament testament) {
        List<SearchResult> list = new ArrayList<>();
        String trimmed = query == null ? null : query.trim();
        if (trimmed == null || trimmed.length() < 2) return list;

        Pattern wordPattern = buildWholeWordPattern(trimmed);

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sqlQuery = "SELECT v.VER_LIV_ID, v.VER_CAPITULO, v.VER_VERSICULO, v.VER_TEXTO, l.LIV_NOME_CURTO " +
                              "FROM VERSICULOS v JOIN LIVROS l ON v.VER_LIV_ID = l.LIV_ID " +
                              "WHERE v.VER_TEXTO LIKE ?";

            if (testament == Testament.AT) {
                sqlQuery += " AND l.LIV_TES_ID = 1";
            } else if (testament == Testament.NT) {
                sqlQuery += " AND l.LIV_TES_ID = 2";
            }
            cursor = db.rawQuery(sqlQuery, new String[]{"%" + trimmed + "%"});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String text = cursor.getString(3);

                    if (text != null && wordPattern.matcher(text).find()) {
                        int bookId = cursor.getInt(0);
                        int chapter = cursor.getInt(1);
                        int verse = cursor.getInt(2);
                        String bookShort = cursor.getString(4);
                        list.add(new SearchResult(bookId, bookShort, chapter, verse, text));

                        if (list.size() >= MAX_RESULTS) break;
                    }
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

    private Pattern buildWholeWordPattern(String query) {
        String escaped = Pattern.quote(query);
        String letterClass = "A-Za-zÀ-ÖØ-öø-ÿ0-9";
        String regex = "(?<![" + letterClass + "])" + escaped + "(?![" + letterClass + "])";
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }
}
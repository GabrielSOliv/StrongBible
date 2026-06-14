package com.devgabriel.bstrong.features.strong.data.datasource;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.devgabriel.bstrong.core.database.BibleDatabaseHelper;
import com.devgabriel.bstrong.features.strong.domain.model.StrongEntry;

public class StrongLocalDataSource {
    private final BibleDatabaseHelper databaseHelper;
    private static final String TAG = "StrongLocalDataSource";
    public StrongLocalDataSource(BibleDatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public StrongEntry getStrong(String code) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                "SELECT CODIGO, IDIOMA, PALAVRA_ORIGINAL, TRADUCAO, SIGNIFICADO FROM STRONG WHERE CODIGO = ?",
                new String[]{code}
            );
            if (cursor != null && cursor.moveToFirst()) {
                String codeResult = cursor.getString(0);
                String language = cursor.getString(1);
                String original = cursor.getString(2);
                String translit = cursor.getString(3);
                String meaning = cursor.getString(4);
                return new StrongEntry(codeResult, language, original, translit, meaning);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error Loading Strong: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}
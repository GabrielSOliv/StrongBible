package com.devgabriel.bstrong.features.books.data.datasource;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import com.devgabriel.bstrong.core.database.BibleDatabaseHelper;
import com.devgabriel.bstrong.features.books.domain.model.Book;


public class BookLocalDataSource {
    private final BibleDatabaseHelper databaseHelper;
    private static final String TAG = "BookLocalDataSource";

    public BookLocalDataSource(BibleDatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    public List<Book> getAllBooks() {
        List<Book> list = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                "SELECT LIV_ID, LIV_TES_ID, LIV_NOME_LONGO, LIV_NOME_CURTO FROM LIVROS ORDER BY LIV_ID",
                null
            );
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(0);
                    int testamentId = cursor.getInt(1);
                    String name = cursor.getString(2);
                    String shortName = cursor.getString(3);
                    list.add(new Book(id, testamentId, name, shortName));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading books: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }


}

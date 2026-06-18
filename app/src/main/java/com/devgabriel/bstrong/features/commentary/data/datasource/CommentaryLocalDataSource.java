package com.devgabriel.bstrong.features.commentary.data.datasource;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

import com.devgabriel.bstrong.core.database.CommentaryDatabaseHelper;
import com.devgabriel.bstrong.features.commentary.domain.model.Commentary;

public class CommentaryLocalDataSource {

    private final CommentaryDatabaseHelper databaseHelper;
    private static final String TAG = "CommentaryLocalDataSource";

    public CommentaryLocalDataSource(CommentaryDatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public List<Commentary> getCommentariesForChapter(int bookId, int chapter) {
        List<Commentary> list = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT COM_LIV_ID, COM_CAPITULO, COM_VERSICULO, COM_TEXTO FROM COMENTARIOS " +
                    "WHERE COM_LIV_ID = ? AND COM_CAPITULO = ? ORDER BY COM_VERSICULO",
                    new String[]{ String.valueOf(bookId), String.valueOf(chapter) }
            );

            if (cursor != null && cursor.moveToFirst()) {
                int bookIdIndex = cursor.getColumnIndexOrThrow("COM_LIV_ID");
                int chapterIndex = cursor.getColumnIndexOrThrow("COM_CAPITULO");
                int verseIndex = cursor.getColumnIndexOrThrow("COM_VERSICULO");
                int textIndex = cursor.getColumnIndexOrThrow("COM_TEXTO");

                do {
                    int bId = cursor.getInt(bookIdIndex);
                    int chapId = cursor.getInt(chapterIndex);
                    int verseId = cursor.getInt(verseIndex);
                    String text = cursor.getString(textIndex);

                    list.add(new Commentary(bId, chapId, verseId, text));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao carregar comentários do capítulo: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }

        return list;
    }

    public Commentary getCommentaryForVerse(int bookId, int chapter, int verse) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        Commentary commentary = null;

        try {
            cursor = db.rawQuery(
                    "SELECT COM_LIV_ID, COM_CAPITULO, COM_VERSICULO, COM_TEXTO FROM COMENTARIOS " +
                    "WHERE COM_LIV_ID = ? AND COM_CAPITULO = ? AND COM_VERSICULO = ?",
                    new String[]{ String.valueOf(bookId), String.valueOf(chapter), String.valueOf(verse) }
            );

            if (cursor != null && cursor.moveToFirst()) {
                int bId = cursor.getInt(cursor.getColumnIndexOrThrow("COM_LIV_ID"));
                int chapId = cursor.getInt(cursor.getColumnIndexOrThrow("COM_CAPITULO"));
                int verseId = cursor.getInt(cursor.getColumnIndexOrThrow("COM_VERSICULO"));
                String text = cursor.getString(cursor.getColumnIndexOrThrow("COM_TEXTO"));

                commentary = new Commentary(bId, chapId, verseId, text);
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao carregar comentário do versículo: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }

        return commentary;
    }
}
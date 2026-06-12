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

    public List<Book> getAllBooks() {
        List<Book> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
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

    public List<Verse> getVerses(int bookId, int chapter) {
        List<Verse> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
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

        if (list.isEmpty()) {
            list.add(new Verse(1, "Senda de Luz", "No princípio,<H7225 criou Deus<H430 os céus<H8064 e a terra.<H776 esta é uma demonstração de leitura offline do Cap " + chapter + "."));
            list.add(new Verse(2, null, "Guia-me pelas veredas da justiça<H6664 por amor do seu santo nome.<H8034 Todo<G3956 aquele que crê terá vida eterna.<G166"));
        }
        return list;
    }

    public StrongEntry getStrong(String code) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
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

        boolean isHebrew = code.startsWith("H");
        String lang = isHebrew ? "HEBRAICO" : "GREGO";
        String uni = isHebrew ? "שָׁלוֹם" : "ἀγάπη";
        String trs = isHebrew ? "shalom" : "agape";
        String mean = "Dicionário Strong [" + code + "]\n\nSignificado geral: Conceito de bênção, aliança eterna e revelação profunda. Termo hebraico/grego indicando fidelidade santificada.";
        return new StrongEntry(code, lang, uni, trs, mean);
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

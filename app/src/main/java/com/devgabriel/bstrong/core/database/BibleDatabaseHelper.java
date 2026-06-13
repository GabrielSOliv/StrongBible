package com.devgabriel.bstrong.core.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class BibleDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "DB_BIBLE.db";
    private static final int DB_VERSION = 1;

    private final Context context;
    private final File dbFile;

    public BibleDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        this.dbFile = context.getDatabasePath(DB_NAME);

        initializeDatabase();
    }

    private void initializeDatabase() {

        if (dbFile.exists())
            return;

        dbFile.getParentFile().mkdirs();

        try (
                InputStream is = context.getAssets().open(DB_NAME);
                OutputStream os = new FileOutputStream(dbFile)
        ) {

            byte[] buffer = new byte[8192];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            os.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Banco já existe nos assets.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Futuras migrações.
    }

    public Cursor getBooks() {
        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM LIVROS ORDER BY LIV_ID",
                null
        );
    }

    public Cursor getChapter(int book, int chapter){

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM VERSICULOS " +
                        "WHERE VER_LIV_ID = ? " +
                        "AND VER_CAPITULO = ? " +
                        "ORDER BY VER_VERSICULO",
                new String[]{
                        String.valueOf(book),
                        String.valueOf(chapter)
                }
        );
    }

    public Cursor getVerse(int livro, int capitulo, int versiculo) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM VERSICULOS " +
                        "WHERE VER_LIV_ID = ? " +
                        "AND VER_CAPITULO = ? " +
                        "AND VER_VERSICULO = ?",
                new String[]{
                        String.valueOf(livro),
                        String.valueOf(capitulo),
                        String.valueOf(versiculo)
                }
        );
    }

    public Cursor getStrong(String codigo) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM STRONG WHERE CODIGO = ?",
                new String[]{codigo}
        );
    }

    public Cursor searchDictionary(String palavra) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM DICIONARIO " +
                        "WHERE PALAVRA LIKE ?",
                new String[]{"%" + palavra + "%"}
        );
    }

    public Cursor searchText(String texto) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM VERSICULOS " +
                        "WHERE VER_TEXTO LIKE ?",
                new String[]{"%" + texto + "%"}
        );
    }
}
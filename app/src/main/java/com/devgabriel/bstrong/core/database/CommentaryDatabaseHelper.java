package com.devgabriel.bstrong.core.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CommentaryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "DB_MHCC_COMMENT.db";
    private static final int DB_VERSION = 1;

    private final Context context;
    private final File dbFile;

    public CommentaryDatabaseHelper(Context context) {
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

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getCommentaryForVerse(int bookId, int chapter, int verse) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM COMENTARIOS " +
                        "WHERE COM_LIV_ID = ? " +
                        "AND COM_CAPITULO = ? " +
                        "AND COM_VERSICULO = ?",
                new String[]{
                        String.valueOf(bookId),
                        String.valueOf(chapter),
                        String.valueOf(verse)
                }
        );
    }

    public Cursor getCommentariesForChapter(int bookId, int chapter) {
        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM COMENTARIOS " +
                        "WHERE COM_LIV_ID = ? " +
                        "AND COM_CAPITULO = ? " +
                        "ORDER BY COM_VERSICULO",
                new String[]{
                        String.valueOf(bookId),
                        String.valueOf(chapter)
                }
        );
    }
}
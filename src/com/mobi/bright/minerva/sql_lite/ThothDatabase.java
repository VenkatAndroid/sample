package com.mobi.bright.minerva.sql_lite;

import android.database.sqlite.SQLiteDatabase;

public final class ThothDatabase {
// ------------------------------ FIELDS ------------------------------

    private static ThothDatabase sInstance = new ThothDatabase();

    SQLiteDatabase database;

// --------------------------- CONSTRUCTORS ---------------------------

    private ThothDatabase() {
        
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public synchronized  SQLiteDatabase getDatabase() {
        return database;
    }

// -------------------------- OTHER METHODS --------------------------

    public static ThothDatabase getInstance() {

            if (sInstance == null) {
                sInstance = new ThothDatabase();
            }

            sInstance.create();


        return sInstance;
    }

    private synchronized void create() {
        if (database == null) {
            database = SqlLiteHelper.openDatabase();
        }
    }
}

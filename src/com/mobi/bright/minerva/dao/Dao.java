package com.mobi.bright.minerva.dao;

import android.database.sqlite.SQLiteDatabase;
import com.mobi.bright.minerva.sql_lite.ThothDatabase;


/**
 * Convenience Base class for DataObjects to obtain connection to SQL LITE
 */
public class Dao {
// ------------------------------ FIELDS ------------------------------

    private static SQLiteDatabase sqLiteDb;

    public static void setSqLiteDb(SQLiteDatabase sqLiteDb) {
        Dao.sqLiteDb = sqLiteDb;
    }


    // --------------------------- CONSTRUCTORS ---------------------------

    public Dao() {

        getSqlLite();

    }

    public Dao(boolean createDb) {
        if (createDb) {
            getSqlLite();
        }
    }

    protected synchronized SQLiteDatabase getSqlLite() {
        if (sqLiteDb == null) {
           sqLiteDb = ThothDatabase.getInstance().getDatabase();
        }

        return sqLiteDb;
    }

    //protected void closeDb() {
    //  sqLiteDb = null;
    //}
}

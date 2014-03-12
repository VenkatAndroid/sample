package com.mobi.bright.minerva.dao;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.mobi.analytics.commom.json.MobiAnalyticsFromClient;
import com.mobi.bright.minerva.sql_lite.ThothDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class MobiAnalyticsOutgoing extends MobiAnalyticsFromClient{
// ------------------------------ FIELDS ------------------------------

    public static final String TABLE_NAME = "mobi_analytics";

// -------------------------- STATIC METHODS --------------------------

    public static boolean delete(final Long timestamp) {
        SQLiteDatabase sqLiteDb = ThothDatabase.getInstance()
                                               .getDatabase();
        return sqLiteDb.delete(TABLE_NAME,
                               ("TimeStamp" + "=" + timestamp),
                               null) > 0;
    }

    public static List<MobiAnalyticsOutgoing> getUnsent() {
        List<MobiAnalyticsOutgoing> list = new ArrayList<MobiAnalyticsOutgoing>();

        SQLiteDatabase sqLiteDb = ThothDatabase.getInstance()
                                               .getDatabase();

        String query =
                "SELECT TimeStamp,SessionId,Action,Json,customerId,ThothId FROM " + TABLE_NAME + " WHERE TimeStamp < ?";

        String timeCutOff = "" + (new Date().getTime() - (10 * 60 * 1000));

        Cursor cursor = sqLiteDb.rawQuery(query,
                                          new String[]{
                                                  timeCutOff
                                          });

        while (cursor.moveToNext()) {
            MobiAnalyticsOutgoing mobiAnalyticsOutgoing = new MobiAnalyticsOutgoing();

            mobiAnalyticsOutgoing.timestamp = cursor.getLong(0);
            mobiAnalyticsOutgoing.sessionId = cursor.getString(1);
            mobiAnalyticsOutgoing.action = cursor.getString(2);
            mobiAnalyticsOutgoing.json = cursor.getString(3);
            mobiAnalyticsOutgoing.customerId = cursor.getInt(4);
            mobiAnalyticsOutgoing.thothId = cursor.getInt(5);

            list.add(mobiAnalyticsOutgoing);
        }

        return list;
    }

// -------------------------- OTHER METHODS --------------------------

    public void insert() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("TimeStamp",
                          timestamp);
        contentValues.put("SessionId",
                          sessionId);
        contentValues.put("Action",
                          action);
        contentValues.put("Json",
                          json);
        contentValues.put("customerId",
                          customerId);
        contentValues.put("ThothId",
                          thothId);


        SQLiteDatabase sqLiteDb = ThothDatabase.getInstance()
                                               .getDatabase();

        sqLiteDb.insert(TABLE_NAME,
                        null,
                        contentValues);
    }
}

package com.mobi.bright.minerva.dao;


import android.database.Cursor;


public final class PageHtml extends Dao {
    public PageHtml() {
        super();
    }

    public String getHtmlFromDb(int id) {
        Cursor cursor = getSqlLite()
                .rawQuery("select Html " +
                          "from page_html  " +
                          "where  PageInstanceId = ? ",
                          new String[]{
                                  "" + id,
                          });

        try {
            if(cursor.moveToNext()) {

                return cursor.getString(0);
            }

            return "";
        }
        finally {
            cursor.close();
        }
    }
}

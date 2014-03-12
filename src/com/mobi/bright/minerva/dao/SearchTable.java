package com.mobi.bright.minerva.dao;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public final class SearchTable extends Dao {
    String id;
    String title;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public SearchTable() {
        super();
    }

    public List<SearchTable> search(String searchIn,
                                    boolean useWildCard) {

        List<SearchTable> list = new ArrayList<SearchTable>();

        if (searchIn == null) {
            return list;
        }

        searchIn = searchIn.trim();
        searchIn = searchIn.toLowerCase();

        StringBuilder builder = new StringBuilder();

        for (char c : searchIn.toCharArray()) {
            if (Character.isLetterOrDigit(c) || Character.isSpaceChar(c)) {
                builder.append(c);
            }
        }

        String wildCard = "";

        if (useWildCard) {
            wildCard = "*";
        }

        String query =
                "SELECT PageInstanceId,Title FROM search_table WHERE SearchData MATCH '" + builder + wildCard + "'";

        //MobiLog.l(this,
        //          query);

        Cursor cursor = getSqlLite().rawQuery(query,
                                          null);

        while (cursor.moveToNext()) {
            SearchTable page = new SearchTable();
            page.id = cursor.getString(0);
            page.title = cursor.getString(1);

            list.add(page);
        }

        return list;
    }
}

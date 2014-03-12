package com.mobi.bright.minerva.dao;


import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;


public final class PageTag extends Dao {
    public PageTag() {
        super();
    }

    public List<Integer> getTagIds(int pageInstanceId) {
        List<Integer> list = new ArrayList<Integer>();

        // MobiLog.l(this,
        //           "pageInstanceId=" + pageInstanceId);


        Cursor cursor = getSqlLite().rawQuery("Select TagsId from page_tag where PageInstanceId = ?",
                                          new String[]{
                                                  "" + pageInstanceId,
                                          });

        while (cursor.moveToNext()) {

            int id = cursor.getInt(0);

            list.add(id);
        }

        //MobiLog.l(this,
        //          "list.size()=" + list.size());

        return list;
    }
}

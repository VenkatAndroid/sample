package com.mobi.bright.minerva.dao;

import android.database.Cursor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Tags extends Dao implements Serializable {
    // ------------------------------ FIELDS ------------------------------
    Integer id = -1;
    String tag;
    List<PageInstance> pageInstances;
    /**
     * use to mark the last position for displaying lists
     */
    int lastListPageId = -1;

// --------------------------- CONSTRUCTORS ---------------------------

    public Tags() {
        super();
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public Integer getId() {
        return id;
    }

    public int getLastListPageId() {
        return lastListPageId;
    }

    public void setLastListPageId(int lastListPageId) {
        this.lastListPageId = lastListPageId;
    }

    public List<PageInstance> getPageInstances() {
        return pageInstances;
    }

    public void setPageInstances(List<PageInstance> pageInstances) {
        this.pageInstances = pageInstances;
    }

    public String getTag() {
        return tag;
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * Fetches all tags in database
     *
     * @return List of all the tags in database
     */
    public List<Tags> getListFromDb(int pageId) {
        List<Tags> list = new ArrayList<Tags>();

        Cursor cursor = getSqlLite().rawQuery("select TagsId, tag from tags where PagesId = ? ORDER BY orderNum ASC",
                                          new String[]{"" + pageId});

        while (cursor.moveToNext()) {
            Tags tags = new Tags();

            tags.id = cursor.getInt(0);
            tags.tag = cursor.getString(1);

            //MobiLog.l(this,
            //          tags.toString());

            list.add(tags);
        }

        cursor.close();

        return list;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Tags");
        sb.append("{id=")
          .append(id);
        sb.append(", tag='")
          .append(tag)
          .append('\'');
        sb.append('}');
        return sb.toString();
    }

    public List<TagProperties> getProperties() {
        if (id == null) {
            throw new RuntimeException("Tag has not been instantiated from database");
        }

        List<TagProperties> list = new ArrayList<TagProperties>();

        Cursor cursor = getSqlLite().rawQuery("select Name, value from properties where TagsId=?",
                                          new String[]{"" + id});

        while (cursor.moveToNext()) {
            list.add(new TagProperties(cursor.getString(0),
                                       cursor.getString(1)));
        }

        cursor.close();

        return list;
    }
}

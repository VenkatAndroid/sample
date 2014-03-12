package com.mobi.bright.minerva.dao;


import android.database.Cursor;
import android.util.Log;
import com.mobi.bright.util.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class PageInstance extends Dao implements Serializable {
// ------------------------------ FIELDS ------------------------------

    public static final String SELECT =
            "  p.PageInstanceId, p.OrderNum, p.Title, P.Image, p.Intro, p.Date, p.PagesId, p.Extra," +
            " " +
            "p.hasVideo, p.hasAudio, p.DateRead, p.LastUpdate, p.CurrentPercentage, " +
            "p.MaxPercentage ";
    public static final String TABLE_NAME = "page_instance";
    private static final String TAG = LogUtil.createTag(PageInstance.class);
    Integer id;
    Integer orderNum;
    String title;
    String image;
    String intro;
    Long date;
    Tags tag;
    Integer pageId;
    String extra;
    Integer hasVideo;
    Integer hasAudio;
    Long dateRead;
    Long lastUpdate;
    Integer currentPercentage;
    Integer maxPercentage;

// --------------------------- CONSTRUCTORS ---------------------------

    public PageInstance() {
        super();
    }

    //List<Tags> tags;
    private PageInstance(boolean flag) {
        super(flag);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public Integer getCurrentPercentage() {
        return currentPercentage;
    }

    public void setCurrentPercentage(Integer currentPercentage) {
        this.currentPercentage = currentPercentage;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getDateRead() {
        return dateRead;
    }

    public void setDateRead(Long dateRead) {
        this.dateRead = dateRead;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Integer getHasAudio() {
        return hasAudio;
    }

    public void setHasAudio(Integer hasAudio) {
        this.hasAudio = hasAudio;
    }

    public Integer getHasVideo() {
        return hasVideo;
    }

    public void setHasVideo(Integer hasVideo) {
        this.hasVideo = hasVideo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getMaxPercentage() {
        return maxPercentage;
    }

    public void setMaxPercentage(Integer maxPercentage) {
        this.maxPercentage = maxPercentage;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getPageId() {
        return pageId;
    }

    public void setPageId(Integer pageId) {
        this.pageId = pageId;
    }

    //  public List<Tags> getTags() {
    //     return tags;
    // }

    public Tags getTag() {
        return tag;
    }

    public void setTag(Tags tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

// -------------------------- OTHER METHODS --------------------------

    public static PageInstance getPageFromDb(int id) {

        PageInstance pageInstance = new PageInstance();

        Cursor cursor = pageInstance.getSqlLite()
                                    .rawQuery("select" + SELECT +
                                              "from page_instance p " +
                                              "where  p.PageInstanceId = ? ",
                                              new String[]{
                                                      "" + id,
                                              });
        try {
            if (cursor.moveToNext()) {
                pageInstance.processPage(cursor,
                                         pageInstance);
                return pageInstance;
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
    }

    private void processPage(Cursor cursor,
                             PageInstance pageInstance) {
        pageInstance.id = cursor.getInt(0);
        pageInstance.orderNum = cursor.getInt(1);
        pageInstance.title = cursor.getString(2);
        pageInstance.image = cursor.getString(3);
        pageInstance.intro = cursor.getString(4);

        pageInstance.date = cursor.getLong(5);
        pageInstance.pageId = cursor.getInt(6);
        pageInstance.extra = cursor.getString(7);
        pageInstance.hasVideo = cursor.getInt(8);
        pageInstance.hasAudio = cursor.getInt(9);
        pageInstance.dateRead = cursor.getLong(10);
        pageInstance.lastUpdate = cursor.getLong(11);
        pageInstance.currentPercentage = cursor.getInt(12);
        pageInstance.maxPercentage = cursor.getInt(13);
    }

    public PageInstance getPageFromDbObject(int id) {

        getSqlLite();

        Cursor cursor = getSqlLite()
                .rawQuery("select" + SELECT +
                          "from page_instance p " +
                          "where  p.PageInstanceId = ? ",
                          new String[]{
                                  "" + id,
                          });

        PageInstance pageInstance = new PageInstance(false);

        if (cursor.moveToNext()) {
            processPage(cursor,
                        pageInstance);
        }

        cursor.close();


        //closeDb();
        return pageInstance;
    }

    public List<PageInstance> getPages(int tagId,
                                       int pageId) {
        //CREATE TABLE page_instance (PageInstanceId INTEGER PRIMARY KEY ASC, OrderNum Integer,
        // Title Text, Html TEXT, Date Integer, PagesId Integer)

        Cursor cursor = getSqlLite()
                .rawQuery("select" + SELECT +
                          "from page_instance p, page_tag t " +
                          "where p.PagesId=? and t.TagsId = ? and t.PageInstanceId = p.PageInstanceId" +
                          " " +
                          "ORDER BY p.orderNum ASC",
                          new String[]{
                                  "" + pageId,
                                  "" + tagId
                          });

        return process(cursor);
    }

    private List<PageInstance> process(Cursor cursor) {
        List<PageInstance> list = new ArrayList<PageInstance>();

        while (cursor.moveToNext()) {
            PageInstance pageInstance = new PageInstance();

            processPage(cursor,
                        pageInstance);


            list.add(pageInstance);
        }

        cursor.close();
        return list;
    }

    public List<PageInstance> getPages(int tagId,
                                       int pageId,
                                       int lastId) {
        //CREATE TABLE page_instance (PageInstanceId INTEGER PRIMARY KEY ASC, OrderNum Integer,
        // Title Text, Html TEXT, Date Integer, PagesId Integer)

        //MobiLog.l(this,
        //         "lastId=" + lastId);

        Cursor cursor = getSqlLite()
                .rawQuery("select" + SELECT +
                          "from page_instance p, page_tag t " +
                          "where p.PagesId=? and t.TagsId = ? and t.PageInstanceId = p.PageInstanceId" +
                          " and p.orderNum > ?" +
                          "ORDER BY p.orderNum ASC",
                          new String[]{
                                  "" + pageId,
                                  "" + tagId,
                                  "" + lastId
                          });

        return process(cursor);
    }

    public List<PageInstance> getPagesFromFavorites() {
        //CREATE TABLE page_instance (PageInstanceId INTEGER PRIMARY KEY ASC, OrderNum Integer,
        // Title Text, Html TEXT, Date Integer, PagesId Integer)

        Cursor cursor = getSqlLite()
                .rawQuery("select" + SELECT +
                          "from page_instance p, favorites t " +
                          "where  t.PageInstanceId = p.PageInstanceId " +
                          "ORDER BY t.unixTime DESC",
                          null);

        return process(cursor);
    }

    public List<PageInstance> getPagesFromHistory() {
        //CREATE TABLE page_instance (PageInstanceId INTEGER PRIMARY KEY ASC, OrderNum Integer,
        // Title Text, Html TEXT, Date Integer, PagesId Integer)

        Cursor cursor = getSqlLite()
                .rawQuery("select" + SELECT +
                          "from page_instance p, history t " +
                          "where  t.PageInstanceId = p.PageInstanceId " +
                          "ORDER BY t.unixTime DESC",
                          null);

        return process(cursor);
    }

    public void update() {
        String strSQL = "UPDATE " + TABLE_NAME +
                        " set DateRead=?, LastUpdate=?, CurrentPercentage=?, " +
                        "MaxPercentage=? WHERE PageInstanceId=?";

        getSqlLite();

        getSqlLite().execSQL(strSQL,
                             new String[]{
                                     dateRead == null ? null : "" + dateRead,
                                     "" + lastUpdate,
                                     "" + currentPercentage,
                                     "" + maxPercentage,
                                     "" + id});

        //closeDb();


        Log.d(TAG,
              this.toString());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PageInstance{");
        sb.append("id=")
          .append(id);
        sb.append(", orderNum=")
          .append(orderNum);
        sb.append(", title='")
          .append(title)
          .append('\'');
        sb.append(", image='")
          .append(image)
          .append('\'');
        sb.append(", intro='")
          .append(intro)
          .append('\'');
        sb.append(", date=")
          .append(date);
        sb.append(", tag=")
          .append(tag);
        sb.append(", pageId=")
          .append(pageId);
        sb.append(", extra='")
          .append(extra)
          .append('\'');
        sb.append(", hasVideo=")
          .append(hasVideo);
        sb.append(", hasAudio=")
          .append(hasAudio);
        sb.append(", dateRead=")
          .append(dateRead);
        sb.append(", lastUpdate=")
          .append(lastUpdate);
        sb.append(", currentPercentage=")
          .append(currentPercentage);
        sb.append(", maxPercentage=")
          .append(maxPercentage);
        sb.append('}');
        return sb.toString();
    }
}

package com.mobi.bright.minerva.data;

import android.app.Activity;
import android.util.Log;
import com.mobi.bright.minerva.dao.Dao;
import com.mobi.bright.minerva.dao.Page;
import com.mobi.bright.minerva.dao.PageInstance;
import com.mobi.bright.minerva.dao.Tags;
import com.mobi.bright.minerva.dao.Title;
import com.mobi.bright.minerva.sql_lite.SqlLiteHelper;
import com.mobi.bright.minerva.sql_lite.ThothDatabase;
import com.mobi.bright.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class DataHolder {

    private static final String TAG = LogUtil.createTag(DataHolder.class);
    private static Map<Integer, List<PageInstance>> allPageInstancesMap;
    private static Map<Integer, List<Tags>> tagsListMap;
    private static Title title;

    public static List<PageInstance> getAllPageInstanceList() {
        List<PageInstance> all = new ArrayList<PageInstance>();

        for (Integer integer : allPageInstancesMap.keySet()) {
            //MobiLog.l(DataHolder.class,
            //          "integer=" + integer);
            all.addAll(allPageInstancesMap.get(integer));
        }

        return all;
    }

    public static void init(Activity activity,
                            String dataBaseName) {
        SqlLiteHelper databaseHelper = new SqlLiteHelper(activity,
                                                         dataBaseName);

        try {
            databaseHelper.createDataBase(activity);
        } catch (Exception e) {
            e.printStackTrace(); // todo show error
            Log.e(TAG,
                  e.getMessage(),
                  e);
        }

        //create the singleton
        ThothDatabase thothDatabase = ThothDatabase.getInstance();

        Dao.setSqLiteDb(thothDatabase.getDatabase());

        getTitle();
        getCollections();
    }

    private static void getCollections() {
        allPageInstancesMap = new TreeMap<Integer, List<PageInstance>>();
        tagsListMap = new HashMap<Integer, List<Tags>>();

        Page pageDb = new Page();
        Tags tags = new Tags();

        PageInstance pageInstance = new PageInstance();

        for (Page page : pageDb.getPages()) {
            List<PageInstance> allPageInstancesFromDb = new ArrayList<PageInstance>();
            List<Tags> tagsListFromDb = tags.getListFromDb(page.getId());
            tagsListMap.put(page.getId(),
                            tagsListFromDb);

            for (Tags tag : tagsListFromDb) {
                //tag.getProperties(); // todo
                List<PageInstance> pageInstances = pageInstance.getPages(tag.getId(),
                                                                         page.getId());

                for (PageInstance instance : pageInstances) {
                    instance.setTag(tag);
                }

                allPageInstancesFromDb.addAll(pageInstances);

                tag.setPageInstances(pageInstances);
            }

            allPageInstancesMap.put(page.getId(),
                                    allPageInstancesFromDb);
        } //page
    }

    public static Map<Integer, List<PageInstance>> getAllPageInstancesMap() {
        if (allPageInstancesMap == null) {
            getCollections();
        }

        return allPageInstancesMap;
    }

    public static Map<Integer, List<Tags>> getTagsListMap() {
        if (tagsListMap == null) {
            getCollections();
        }

        return tagsListMap;
    }

    public static Title getTitle() {
        if (title == null) {
            title = new Title();
        }

        return title;
    }
}

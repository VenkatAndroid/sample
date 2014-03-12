package com.mobi.bright.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.mobi.bright.activities.services.WebSocket;
import com.mobi.bright.minerva.dao.Page;
import com.mobi.bright.minerva.dao.Tags;
import com.mobi.bright.minerva.data.DataHolder;
import com.mobi.bright.util.BundleTags;
import com.mobi.bright.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CategoryActivity extends ListActivity {
// ------------------------------ FIELDS ------------------------------

    private static final String TAG = LogUtil.createTag(CategoryActivity.class);
    private int pageId = -1;

// -------------------------- OTHER METHODS --------------------------

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//    setContentView(R.layout.main);


        final Map<Integer, List<Tags>> tagsListMap = DataHolder.getTagsListMap();

        Log.d(TAG,
              "TAGS=" + tagsListMap.size());

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            pageId = extras.getInt(BundleTags.PAGE_INSTANCE_ID,
                                   -1);
        }


        if (pageId == -1) {
            Page page = new Page();
            pageId = page.findMinId();
        }


        final List<Tags> tagsList = DataHolder.getTagsListMap()
                                              .get(pageId);


        ArrayList<String> tagsArray = new ArrayList<String>(tagsList.size());


        for (Tags t : tagsList) {
            tagsArray.add(t.getTag());
        }

        setListAdapter(new ArrayAdapter<String>(this,
                                                R.layout.list_thoth,
                                                tagsArray));


        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {
                Tags tag = tagsList.get(position);

                WebSocket webSocket = WebSocket.getInstance();

                if (webSocket != null) {
                    webSocket.createTagEvent(tag);
                }


                Log.d(TAG,
                      "Size=" + tag.getPageInstances()
                                   .size());


                Intent intent = new Intent(CategoryActivity.this,
                                           TitlesActivity.class);
                intent.putExtra(Tag.class.getName(),
                                tag);


                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();


        WebSocket webSocket = WebSocket.getInstance();

        if (webSocket != null) {
            webSocket.createRestartEvent();
        }
    }

    @Override
    protected void onStop() {
        super.onDestroy();

        WebSocket webSocket = WebSocket.getInstance();

        if (webSocket != null) {
            webSocket.createPausedEvent();
        }
    }
}
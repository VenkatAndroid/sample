package com.mobi.bright.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.mobi.bright.minerva.dao.PageInstance;
import com.mobi.bright.minerva.dao.Tags;
import com.mobi.bright.util.LogUtil;

import java.util.ArrayList;
import java.util.List;


public class TitlesActivity extends ListActivity {

    private static final String TAG = LogUtil.createTag(MainActivity.class);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        Tags tag = (Tags) extras.getSerializable(Tag.class.getName());

        final List<PageInstance> pageInstances = tag.getPageInstances();


        ArrayList<String> titlesArray = new ArrayList<String>(pageInstances.size());

        for (PageInstance pageInstance : pageInstances) {
            titlesArray.add(pageInstance.getTitle());
        }

        setListAdapter(new ArrayAdapter<String>(this,
                                                R.layout.list_thoth,
                                                titlesArray));


        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {


                PageInstance pageInstance = pageInstances.get(position);

                Intent intent = new Intent(TitlesActivity.this,
                                           ContentWebViewActivity.class);
                intent.putExtra(PageInstance.class.getName(),
                                pageInstance);


                startActivity(intent);


            }
        });


    }


}
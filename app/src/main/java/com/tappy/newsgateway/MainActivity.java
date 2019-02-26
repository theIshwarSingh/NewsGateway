package com.tappy.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = ".MainActivity";
    private static String ACTION_NEWS_STORY="ACTION_NEWS_STORY";
    private static String ACTION_MSG_TO_SERVICE="ACTION_MSG_TO_SERVICE";

    private PgAdapter PgAdapter;
    private List<Fragment> frags;
    private ViewPager pgviewer;
    private DrawerLayout DLayout;
    private ListView ListView;
    private ActionBarDrawerToggle DrwToggle;
    private NewsReceiver NewsReceiver;
    ListAdapter lAdapter;
    private ArrayList<String> items = new ArrayList<>();
    private HashMap<String,Origin> map;
    private List optionsList = new ArrayList();
    Menu menuList;
    private String PresentNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        //start NewService service
        Intent i = new Intent(MainActivity.this, NewsService.class);
        startService(i);

        NewsReceiver =new NewsReceiver();
        IntentFilter Ifilter = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(NewsReceiver, Ifilter);

        //Drawer and View Pager setup
        DLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView = (ListView) findViewById(R.id.left_drawer);

        lAdapter = new ArrayAdapter<>(this, R.layout.dlistview, items);
        ListView.setAdapter(lAdapter);
        ListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> paraent, View view, int position, long id) {
                selectItem(position);

            }
        });
        DrwToggle = new ActionBarDrawerToggle(
                this,
                DLayout,
                R.string.dopen,
                R.string.dclose);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        frags = getFragments();
        PgAdapter = new PgAdapter(getSupportFragmentManager());
        pgviewer = (ViewPager) findViewById(R.id.viewpager);
        pgviewer.setAdapter(PgAdapter);

        map = new HashMap<>();
        new NewsSourceDownloader(this).execute("");
    }


    private void selectItem(int position) {
        Toast.makeText(this, "SELECTED "+items.get(position), Toast.LENGTH_SHORT).show();
        pgviewer.setBackground(null);
        setTitle(items.get(position));
        PresentNews = items.get(position);
        Intent presentnews = new Intent();
        presentnews.setAction(ACTION_MSG_TO_SERVICE);
        Origin src = map.get(items.get(position));
        presentnews.putExtra("SOURCE_OBJECT",src.getId());
        sendBroadcast(presentnews);
        DLayout.closeDrawer(ListView);
    }

    private void reDoFragments(ArrayList<Items> list) {
        int size = list.size();
       // Log.d(TAG, "reDoFragments: Start");
       // Toast.makeText(this, "REDO FRAGS", Toast.LENGTH_SHORT).show();
        for (int i = 0; i < PgAdapter.getCount(); i++)
            PgAdapter.notifyChangeInPosition(i);
        frags.clear();
        for (int i = 0; i < list.size(); i++) {
            Items a = list.get(i);
            frags.add(Frags.newInstance(this,a,i,size));
        }
        PgAdapter.notifyDataSetChanged();
        pgviewer.setCurrentItem(0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        DrwToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DrwToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuList=menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (DrwToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: DrwToggle " + item);
            return true;
        }
        new NewsSourceDownloader(this).execute(item.toString());
        Log.d(TAG, "onOptionsItemSelected: Options Menu: "+item.toString());
        return super.onOptionsItemSelected(item);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<>();
        return fList;
    }

    public void setSources(List<Origin> sourceList, List<String> catList) {
        if(!sourceList.isEmpty() ){
            map.clear();
            items.clear();

            for (int i=0;i<sourceList.size();i++){
                Origin s= sourceList.get(i);
                items.add(s.getSource());
                map.put(s.getSource(),s);
            }
        }

        if(catList.isEmpty()){
            optionsList.clear();
            optionsList.add("all");

        }
        else if(optionsList.isEmpty()){
            optionsList = catList;
            optionsList.add("all");
        }

        else if(!catList.isEmpty())
        {
            for (int k=0;k<catList.size();k++){
                if (!optionsList.contains(catList.get(k))){
                    Log.d(TAG,"cl:"+catList.get(k));
                    optionsList.add(catList.get(k));
                }
            }
        }

        menuList.clear();
        Log.d(TAG, "catS: "+ optionsList.size());
        for(int j = 0; j< optionsList.size(); j++){
            menuList.add(optionsList.get(j).toString());
            Log.d(TAG, "setSources: "+menuList.size());
        }
        lAdapter = new ArrayAdapter<>(this, R.layout.dlistview, items);
        ListView.setAdapter(lAdapter);
    }



    private class PgAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        public PgAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return frags.get(position);
        }

        @Override
        public int getCount() {
            return frags.size();
        }

        @Override
        public long getItemId(int position) {
            return baseId + position;
        }

        public void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }

    }

    public void openPicassoPhoto(String url, ImageView im) {
        final ImageView imageView = im;
        final String photUrl = url;
        if (url != null) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {

                    final String changedUrl = photUrl.replace("http:", "https:");
                    picasso.load(changedUrl)
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(imageView);
                }
            }).build();
            picasso.load(url)
                    .resize(360,1060)
                    .centerInside()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
            Log.d(TAG, "openPicassoPhoto: "+url);

        } else {
            Picasso.with(this).load(url)
                    .error(R.drawable.brokenimage)
                    .into(imageView);

        }
    }

    public class NewsReceiver  extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("ACTION_NEWS_STORY")){
                ArrayList<Items> array =  (ArrayList<Items>) intent.getSerializableExtra("articleList");
                //Log.d(TAG, "onReceive: NewsReceiver"+array.size());
                reDoFragments(array);
            }
            //Toast.makeText(context, " New Receiver Class", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        Intent intent1 = new Intent(MainActivity.this, NewsReceiver.class);
        stopService(intent1);
        unregisterReceiver(NewsReceiver);
        super.onDestroy();
    }
}

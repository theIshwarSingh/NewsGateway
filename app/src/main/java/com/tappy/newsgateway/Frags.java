package com.tappy.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Frags extends Fragment {

    public static final String TAG = ".NewsFragment";

    TextView origin,
            details,
            AuName,
            pubDate,
            place;

    ImageView imageView;
    static MainActivity ma;

    public static final Frags newInstance(MainActivity m, Items a, int pos, int size)
    {
        ma = m;
        Frags f = new Frags();
        Bundle b = new Bundle(1);
        b.putSerializable("Items", a);
        b.putInt("Position", pos);
        b.putInt("Total", size);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "NewsFragment: onCreateView: ");

        final Items it = (Items) getArguments().getSerializable("Items");
        int position  = getArguments().getInt("Position");
        int length  = getArguments().getInt("Total");


        View v = inflater.inflate(R.layout.frags_layout, container, false);
        origin =(TextView) v.findViewById(R.id.origin);
        details =(TextView) v.findViewById(R.id.details);
        imageView = (ImageView)v.findViewById( R.id.newsimg);

        AuName =(TextView) v.findViewById(R.id.auName);
        pubDate =(TextView) v.findViewById(R.id.pubDate);
        place =(TextView) v.findViewById(R.id.place);
        details.setMovementMethod(new ScrollingMovementMethod());

        ma.openPicassoPhoto(it.getUrlToImage(),imageView);
        origin.setText(it.getTitle());
        details.setText(it.getDesc());


        if(it.getAuthor().equals("null") || it.getAuthor().isEmpty())
            AuName.setText("");

        else
            AuName.setText(it.getAuthor());

        if(it.getPubDate().equals("null") || it.getPubDate().isEmpty())
            pubDate.setText("");

        else {

            SimpleDateFormat Dformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
            SimpleDateFormat FormatOut = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.ENGLISH);
            try{
                Date d = Dformat.parse(it.getPubDate());
                String formattedTime = FormatOut.format(d);
                pubDate.setText(formattedTime);
            }
            catch (ParseException e){
                e.printStackTrace();
            }

        }

        position=position+1;
        String pos = position +" of "+ length;
        place.setText(pos);


        origin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: origin:"+ origin.getText().toString() + it.getUrl());
                newslink(v, it.getUrl());

            }
        });

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: details:"+ origin.getText().toString() + it.getUrl());
                newslink(v, it.getUrl());

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: imageView:"+ origin.getText().toString() + it.getUrl());
                newslink(v, it.getUrl());

            }
        });

        return v;
    }
    public void newslink(View v, String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}

package com.tappy.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class NewsSourceDownloader extends AsyncTask<String, Void, String> {

    private MainActivity ma;
    private String category;
    private List<String> catList = new ArrayList<String>();
    private List<Origin> sourceList = new ArrayList<>();
    private String TAG = "NewsSourceDownloader";
    private String API_KEY = "eacaf5abc8ac474ba468e72c9f236d6c";
    private String URL = "http://newsapi.org/v1/sources?language=en&country=us";

    public NewsSourceDownloader(MainActivity ma) {
        this.ma = ma;
    }

    @Override
    protected String doInBackground(String[] params) {
        if(params[0].isEmpty() || params[0].equals("all"))
            this.category = "";
        else
            this.category =params[0];
        Uri.Builder buildURL = Uri.parse(URL).buildUpon();
        buildURL.appendQueryParameter("category", category);
        buildURL.appendQueryParameter("apiKey", API_KEY);
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            java.net.URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return "Exception";
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            //JSONArray json = new JSONArray(s);
            JSONObject object = new JSONObject(s);
            JSONArray sources = new JSONArray(object.getString("sources"));
            for(int i=0;i<sources.length();i++){
                JSONObject source = (JSONObject) sources.get(i);
                String name =  source.getString("name");
                String id = source.getString("id");
                String details = source.getString("description");
                String URLorigin = source.getString("url");
                String options = source.getString("category");
                sourceList.add(new Origin(id,name,details,URLorigin,options));
            }
            if (sourceList != null) {
                Origin temp = sourceList.get(0);
                catList.add(temp.getCategory());

                for (int j = 1; j < sourceList.size(); j++) {
                    Origin source = sourceList.get(j);
                    if (!catList.contains(source.getCategory())) {
                        catList.add(source.getCategory());
                    }
                }

                if (!catList.isEmpty() && !sourceList.isEmpty())
                    ma.setSources(sourceList,catList);
            }

        }
        catch(JSONException e){
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }

    }
}

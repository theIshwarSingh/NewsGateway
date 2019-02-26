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
public class NewsArticleDownloader extends AsyncTask<String, Void, String> {

    private NewsService Service;
    private String source;
    private ArrayList<Items> itemsarray = new ArrayList<Items>();
    private String TAG = "NewsArticleDownloader";
    private String API_KEY = "eacaf5abc8ac474ba468e72c9f236d6c";
    private String URL = "http://newsapi.org/v1/articles";

    public NewsArticleDownloader(NewsService Service) {
        this.Service = Service;
    }

    @Override
    protected String doInBackground(String[] params) {
        source =params[0];
        Uri.Builder buildURL = Uri.parse(URL).buildUpon();
        buildURL.appendQueryParameter("source", source);
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
        return sb.toString().replace("\n//","");
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject obj = new JSONObject(s);
            JSONArray items = (JSONArray) obj.get("articles");
            for (int i=0; i < items.length(); i++){
                JSONObject jObj = (JSONObject) items.get(i);
                String author = jObj.getString("author");
                String title = jObj.getString("title");
                String details = jObj.getString("description");
                String itemURL = jObj.getString("url");
                String ImgURL = jObj.getString("urlToImage");
                String publish = jObj.getString("publishedAt");
                itemsarray.add(new Items(author,title,details,itemURL,ImgURL,publish));
            }
            Log.d(TAG, "onPostExecute: itemsarray Size is "+ itemsarray.size());
            if(!itemsarray.isEmpty())
                Service.setArticles(itemsarray);

        } catch (JSONException e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
package com.example.aficionado.data;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by upopple on 1/18/14.
 */
public class ServerGet extends AsyncTask<String, Void, JSONObject> {
    private String GET_URL = "http://aficionado.herokuapp.com/pieces";
    Callback<MuseumItem> mCallback;

    public ServerGet(Callback<MuseumItem> callback) {
        mCallback = callback;
    }

    @Override
    protected JSONObject doInBackground(String... accession_numbers) {
        return RestClient.get(GET_URL + "?accession_number=" + accession_numbers[0]);
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                JSONArray jsonComments = jsonObject.getJSONArray("comments");
                String accessionNumber = jsonObject.getString("accession_number");
                Comment[] comments = new Comment[jsonComments.length()];
                for (int i = jsonComments.length() - 1; i >= 0; i--) {
                    JSONObject jsonComment = jsonComments.getJSONObject(i);
                    Comment comment = new Comment(accessionNumber,
                            jsonComment.getString("comment"),
                            jsonComment.getString("name"), null);
                    comments[i] = comment;
                }
                MuseumItem mi = new MuseumItem(jsonObject.getString("title"),
                        jsonObject.getString("image"), comments);
                mCallback.success(mi);
            } catch (JSONException e) {
                e.printStackTrace();
                mCallback.failure();
            }
        }
        else {
            mCallback.failure();
        }
    }
}

package com.example.aficionado.data;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by upopple on 1/18/14.
 */
public class ServerPost extends AsyncTask<Comment, Void, HttpResponse>{
    Callback<HttpResponse> mCallback;

    public ServerPost(Callback<HttpResponse> mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    protected HttpResponse doInBackground(Comment... comments) {
        List<NameValuePair> data = new ArrayList<NameValuePair>();
        for(Comment comment : comments) {
            data.add(new BasicNameValuePair("comment", comment.getComment()));
            data.add(new BasicNameValuePair("name", comment.getName()));
            data.add(new BasicNameValuePair("accession_number", comment.getAccessionName()));
        }

        return RestClient.post("http://aficionado.herokuapp.com/posts.json", data);
    }

    @Override
    protected void onPostExecute(HttpResponse httpResponse) {
        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
            mCallback.success(httpResponse);
        }
        else {
            mCallback.failure();
        }
    }
}

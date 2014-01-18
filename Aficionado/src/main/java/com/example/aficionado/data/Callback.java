package com.example.aficionado.data;

import org.json.JSONObject;

/**
 * Created by upopple on 1/18/14.
 */
public interface Callback<T>{
    public void success(T result);
    public void failure();
}

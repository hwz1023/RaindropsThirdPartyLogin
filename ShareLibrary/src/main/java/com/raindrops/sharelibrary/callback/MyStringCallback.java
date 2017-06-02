package com.raindrops.sharelibrary.callback;

import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * Created by huangweizhou on 2017/6/2.
 */

public abstract class MyStringCallback extends Callback<String> {

    @Override
    public String parseNetworkResponse(Response response, int id) throws Exception {
        return new String(response.body().toString().getBytes("ISO-8859-1"), "UTF-8");
    }
}

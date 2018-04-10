package com.raindrops.sharelibrary.callback;

/**
 * Created by huangweizhou on 2017/2/15.
 */

public interface IThirdPartyAuthCallback {

    void onComplete(String code);

    void onError(int code, int type, String message);

}

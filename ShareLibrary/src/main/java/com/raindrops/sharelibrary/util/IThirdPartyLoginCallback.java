package com.raindrops.sharelibrary.util;

/**
 * Created by huangweizhou on 2017/2/15.
 */

public interface IThirdPartyLoginCallback {

    void onComplete(String uid, String username, String type, String icon, String sex);

    void onError(int code, int type, String message);

}

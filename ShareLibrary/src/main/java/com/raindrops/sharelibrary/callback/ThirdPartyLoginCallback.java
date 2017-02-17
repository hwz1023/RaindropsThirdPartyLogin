package com.raindrops.sharelibrary.callback;

import com.raindrops.sharelibrary.AppManager;
import com.raindrops.sharelibrary.LoginShareActivity;

/**
 * Created by huangweizhou on 2017/2/17.
 */

public abstract class ThirdPartyLoginCallback implements IThirdPartyLoginCallback {

    @Override
    public void onError(int code, int type, String message) {
        onLoginError(code, type, message);
        AppManager.getAppManager().finishActivity(LoginShareActivity.class);
    }

    @Override
    public void onComplete(String uid, String username, String type, String icon, String sex) {
        onLoginSuccess(uid, username, type, icon, sex);
        AppManager.getAppManager().finishActivity(LoginShareActivity.class);
    }

    public abstract void onLoginSuccess(String uid, String username, String type, String icon,
                                           String sex);

    public abstract void onLoginError(int code, int type, String message);

}

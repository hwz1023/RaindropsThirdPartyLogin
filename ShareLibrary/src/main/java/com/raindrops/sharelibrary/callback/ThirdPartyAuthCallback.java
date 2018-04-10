package com.raindrops.sharelibrary.callback;

import com.raindrops.sharelibrary.AppManager;
import com.raindrops.sharelibrary.LoginShareActivity;

public abstract class ThirdPartyAuthCallback implements IThirdPartyAuthCallback {
    @Override
    public void onError(int code, int type, String message) {
        onLoginError(code, type, message);
        AppManager.getAppManager().finishActivity(LoginShareActivity.class);
    }

    @Override
    public void onComplete(String code) {
        onLoginSuccess(code);
        AppManager.getAppManager().finishActivity(LoginShareActivity.class);
    }

    public abstract void onLoginSuccess(String code);

    public abstract void onLoginError(int code, int type, String message);
}

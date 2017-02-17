package com.raindrops.sharelibrary.callback;

import com.raindrops.sharelibrary.AppManager;
import com.raindrops.sharelibrary.LoginShareActivity;

/**
 * Created by huangweizhou on 2017/2/17.
 */

public abstract class ThirdPartyShareCallback implements IThirdPartyShareCallback {
    @Override
    public void shareSuccess() {
        AppManager.getAppManager().finishActivity(LoginShareActivity.class);
        onShareSuccess();
    }

    @Override
    public void shareError() {
        AppManager.getAppManager().finishActivity(LoginShareActivity.class);
        onShareError();
    }

    public abstract void onShareSuccess();

    public abstract void onShareError();
}

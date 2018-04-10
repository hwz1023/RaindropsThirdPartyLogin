package com.raindrops.sharelibrary;

/**
 * Created by huangweizhou on 2017/2/21.
 */

public abstract class PayCallback implements IPayCallback {

    @Override
    public void success() {
        finish();
        paySuccess();
    }

    @Override
    public void error(String code, String msg) {
        finish();
        payError(code, msg);
    }

    private void finish() {
//        AppManager.getAppManager().finishActivity(PayActivity.class);
//        AppManager.getAppManager().finishActivity(WXPayEntryActivity.class);
//        AppManager.getAppManager().finishActivity(CallbackActivity.class);
    }

    public abstract void paySuccess();

    public abstract void payError(String code, String msg);

}

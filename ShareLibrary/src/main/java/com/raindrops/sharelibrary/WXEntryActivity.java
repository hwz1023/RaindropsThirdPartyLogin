package com.raindrops.sharelibrary;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.raindrops.sharelibrary.util.LoginUtil;
import com.raindrops.sharelibrary.util.ShareConfig;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate", "onCreate");
        api = WXAPIFactory.createWXAPI(this, ShareConfig.getInstance().wechatAPPID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        int result = 0;
        String msg = "";
        SendAuth.Resp resp = (SendAuth.Resp) baseResp;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //			result = R.string.errcode_success
                LoginUtil.getAccessToken(resp.code);
                msg = "授权成功";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL://用户取消
                msg = "用户取消授权";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                msg = "用户拒绝授权";
                break;
            default:
                msg = "授权失败";
                break;
        }
        Log.e("msg", msg);
        if (msg.length() > 0)
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        finish();
    }

}
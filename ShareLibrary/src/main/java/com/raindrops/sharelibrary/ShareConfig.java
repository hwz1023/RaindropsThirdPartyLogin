package com.raindrops.sharelibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.raindrops.sharelibrary.callback.ThirdPartyLoginCallback;
import com.raindrops.sharelibrary.callback.ThirdPartyShareCallback;
import com.raindrops.sharelibrary.callback.IThirdPartyLoginCallback;
import com.raindrops.sharelibrary.callback.IThirdPartyShareCallback;

/**
 * Created by huangweizhou on 2016/12/30.
 */

public class ShareConfig {

    public String wechatAPPID;

    public String wechatSecret;

    public String qqAPPID;

    public String weiboKey;

    public String weiboRedirectUrl;

    public String weiboScope;

    private IThirdPartyLoginCallback thirdPartyLoginCallback;

    private IThirdPartyShareCallback thirdPartyShareCallback;

    private int thirdPartyType;


    private static class ShareConfigSignle {
        private static ShareConfig instance = new ShareConfig();
    }

    public static ShareConfig getInstance() {
        return ShareConfigSignle.instance;
    }

    public ShareConfig initWechatAPPID(String appId, String wechatSecret) {
        this.wechatAPPID = appId;
        this.wechatSecret = wechatSecret;
        return this;
    }

    public ShareConfig initQQAPPID(String qqAPPID) {
        this.qqAPPID = qqAPPID;
        return this;
    }

    public ShareConfig initWeibo(String weiboKey, String redirectUrl, String scope) {
        this.weiboKey = weiboKey;
        this.weiboRedirectUrl = redirectUrl;
        this.weiboScope = scope;
        return this;
    }

    public ShareConfig bindLoginCallback(ThirdPartyLoginCallback thirdPartyLoginCallback) {
        this.thirdPartyLoginCallback = thirdPartyLoginCallback;
        return this;
    }

    public IThirdPartyLoginCallback getLoginCallBack() {
        return thirdPartyLoginCallback;
    }

    public ShareConfig bindShareCallback(ThirdPartyShareCallback thirdPartyShareCallback) {
        this.thirdPartyShareCallback = thirdPartyShareCallback;
        return this;
    }

    public IThirdPartyShareCallback getShareCallBack() {
        return thirdPartyShareCallback;
    }

    public static void newInstance(Context mContext, Bundle bundle) {
        Intent intent = new Intent(mContext, LoginShareActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    /**
     * 判断shareCallback是否注册
     *
     * @return
     */
    public static boolean isShareCallBack() {
        return ShareConfig.getInstance().getShareCallBack() != null;
    }

    public int getThirdPartyType() {
        return thirdPartyType;
    }

    public void setThirdPartyType(int thirdPartyType) {
        this.thirdPartyType = thirdPartyType;
    }
}

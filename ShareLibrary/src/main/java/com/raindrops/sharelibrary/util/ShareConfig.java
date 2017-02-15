package com.raindrops.sharelibrary.util;

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

}

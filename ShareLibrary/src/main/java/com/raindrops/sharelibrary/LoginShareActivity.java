package com.raindrops.sharelibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.raindrops.sharelibrary.util.LoginUtil;
import com.raindrops.sharelibrary.util.ShareUtil;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.constant.WBConstants;

import static com.raindrops.sharelibrary.ShareIntentStaticCode.THIDR_PARTY_SHARE_IMAGEURL;

public class LoginShareActivity extends Activity implements IWeiboHandler.Response {
    private LoginUtil loginUtil;
    private ShareUtil shareUtil;

    private int thirdPartyType;

    private int thirdPartyPlatForm;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        bundle = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        thirdPartyPlatForm = bundle.getInt(ShareIntentStaticCode.THIDR_PARTY_PLATFORM);
        thirdPartyType = bundle.getInt(ShareIntentStaticCode.THIDR_PARTY_TYPE);
        ShareConfig.getInstance().setThirdPartyType(thirdPartyType);
        if (thirdPartyType == ShareIntentStaticCode.THIDR_PARTY_LOGIN) {
            loginUtil = LoginUtil.getInstance();
            loginUtil.initLoginUtil(this, ShareConfig.getInstance().getLoginCallBack());
            login();
        } else if (thirdPartyType == ShareIntentStaticCode.THIDR_PARTY_SHARE) {
            shareUtil = new ShareUtil(this);
            if (savedInstanceState != null)
                shareUtil.weiboResponse(getIntent(), this);
            share();
        }
    }

    /**
     * 登录
     */
    private void login() {
        switch (thirdPartyPlatForm) {
            case ShareIntentStaticCode.THIDR_PARTY_QQ:
                loginUtil.loginToQQ();
                break;
            case ShareIntentStaticCode.THIDR_PARTY_WECHAT:
                loginUtil.loginToWechat();
                break;
            case ShareIntentStaticCode.THIDR_PARTY_WEIBO:
                loginUtil.loginWeibo();
                break;
        }
    }

    /**
     * 分享
     */
    private void share() {
        String webUrl = bundle.getString(ShareIntentStaticCode.THIDR_PARTY_SHARE_WEBURL);
        String title = bundle.getString(ShareIntentStaticCode.THIDR_PARTY_SHARE_TITLE);
        String des = bundle.getString(ShareIntentStaticCode.THIDR_PARTY_SHARE_DESCRIPTION);
        String imageUrl = "";
        if (bundle.containsKey(THIDR_PARTY_SHARE_IMAGEURL))
            imageUrl = bundle.getString(THIDR_PARTY_SHARE_IMAGEURL);
        switch (thirdPartyPlatForm) {
            case ShareIntentStaticCode.THIDR_PARTY_QQ_ZONE:
            case ShareIntentStaticCode.THIDR_PARTY_QQ:
                shareUtil.shareToQQ(webUrl, title, des, imageUrl, thirdPartyPlatForm);
                break;
            case ShareIntentStaticCode.THIDR_PARTY_WECHAT_FRIEND_CIRCLE:
            case ShareIntentStaticCode.THIDR_PARTY_WECHAT:
                shareUtil.shareToWechat(webUrl, title, des, imageUrl, thirdPartyPlatForm);
                break;
            case ShareIntentStaticCode.THIDR_PARTY_WEIBO:
                shareUtil.shareToWeibo(webUrl, title, des, imageUrl);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putAll(getIntent().getExtras());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (thirdPartyType == ShareIntentStaticCode.THIDR_PARTY_SHARE && shareUtil != null)
            shareUtil.weiboResponse(intent, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (thirdPartyType == ShareIntentStaticCode.THIDR_PARTY_LOGIN && loginUtil != null) {
            loginUtil.onActivityResult(requestCode, resultCode, data);
        } else if (thirdPartyType == ShareIntentStaticCode.THIDR_PARTY_SHARE && shareUtil != null) {
            shareUtil.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        Log.e("BaseResponse", "BaseResponse");
        Log.e("BaseResponse", "baseResponse.errCode" + baseResponse.errCode + "\n"
                + "baseResponse.errMsg" + baseResponse.errMsg);
        if (baseResponse.errCode == WBConstants.ErrorCode.ERR_OK) {
            if (ShareConfig.isShareCallBack())
                ShareConfig.getInstance().getShareCallBack().shareSuccess();
        } else {
            if (ShareConfig.isShareCallBack())
                ShareConfig.getInstance().getShareCallBack().shareError();
        }
    }

    @Override
    protected void onDestroy() {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
    }
}
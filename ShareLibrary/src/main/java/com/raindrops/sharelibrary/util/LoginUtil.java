package com.raindrops.sharelibrary.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.raindrops.sharelibrary.ShareConfig;
import com.raindrops.sharelibrary.ShareIntentStaticCode;
import com.raindrops.sharelibrary.callback.IThirdPartyLoginCallback;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * Created by huangweizhou on 2016/12/30.
 */

public class LoginUtil {

    private Context mContext;
    /**
     * 微信
     */
    private IWXAPI api;
    /**
     * QQ
     */
    private Tencent mTencent;
    /**
     * 微博
     */
    private AuthInfo authInfo;
    /**
     * 微博授权
     */
    private SsoHandler mSsoHandler;

    private IThirdPartyLoginCallback iThirdPartyLoginCallback;

    private IUiListener uiListener;
    private Oauth2AccessToken mAccessToken;

    private LoginUtil() {
    }

    public static LoginUtil getInstance() {
        return SingleInstance.instance;
    }

    public void initLoginUtil(Context mContext, final IThirdPartyLoginCallback
            iThirdPartyLoginCallback) {
        this.iThirdPartyLoginCallback = iThirdPartyLoginCallback;
        this.mContext = mContext;
        api = WXAPIFactory.createWXAPI(mContext, ShareConfig.getInstance().wechatAPPID);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
        mTencent = Tencent.createInstance(ShareConfig.getInstance().qqAPPID, mContext
                .getApplicationContext());
        authInfo = new AuthInfo(mContext, ShareConfig.getInstance().weiboKey, ShareConfig
                .getInstance().weiboRedirectUrl,
                ShareConfig.getInstance().weiboScope);
        uiListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject jsonObject = (JSONObject) o;
                try {
                    if (jsonObject.getInt("ret") == 0) {
                        initOpenidAndToken(jsonObject);
                        getQQUserInfo();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                if (iThirdPartyLoginCallback != null)
                    iThirdPartyLoginCallback.onError(-100, ShareIntentStaticCode.THIDR_PARTY_QQ,
                            "授权失败");
            }

            @Override
            public void onCancel() {
                if (iThirdPartyLoginCallback != null)
                    iThirdPartyLoginCallback.onError(-100, ShareIntentStaticCode.THIDR_PARTY_QQ,
                            "授权失败");
            }
        };
    }

    private static class SingleInstance {
        public static final LoginUtil instance = new LoginUtil();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResult", "onActivityResult");
        Tencent.onActivityResultData(requestCode, resultCode, data, uiListener);

        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    public void loginWeibo() {

        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            getWeiboUserInfo(mAccessToken.getToken(), mAccessToken.getUid());
            return;
        }

        if (mSsoHandler == null)
            mSsoHandler = new SsoHandler((Activity) mContext, authInfo);
        mSsoHandler.authorize(new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
                if (mAccessToken.isSessionValid()) {
                    // 保存 Token 到 SharedPreferences
                    getWeiboUserInfo(mAccessToken.getToken(), mAccessToken.getUid());
                } else {
                    // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                    if (iThirdPartyLoginCallback != null)
                        iThirdPartyLoginCallback.onError(-100, ShareIntentStaticCode
                                .THIDR_PARTY_WEIBO, "授权失败");
                }

            }

            @Override
            public void onWeiboException(WeiboException e) {
                if (iThirdPartyLoginCallback != null)
                    iThirdPartyLoginCallback.onError(-100, ShareIntentStaticCode
                            .THIDR_PARTY_WEIBO, "授权失败");
            }

            @Override
            public void onCancel() {
                if (iThirdPartyLoginCallback != null)
                    iThirdPartyLoginCallback.onError(-100, ShareIntentStaticCode
                            .THIDR_PARTY_WEIBO, "授权失败");
            }
        });
    }

    /**
     * 获取微博用户信息
     *
     * @param access_token
     * @param uid
     */
    public void getWeiboUserInfo(String access_token, final String uid) {
        OkHttpUtils.get().url("https://api.weibo.com/2/users/show.json")
                .addParams("access_token", access_token)
                .addParams("uid", uid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (iThirdPartyLoginCallback != null)
                            iThirdPartyLoginCallback.onError(-100, ShareIntentStaticCode
                                    .THIDR_PARTY_WEIBO, "授权失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.isNull("error_code")) {
                                if (iThirdPartyLoginCallback != null)
                                    iThirdPartyLoginCallback.onError(-100, ShareIntentStaticCode
                                            .THIDR_PARTY_WEIBO, "授权失败");
                            } else {
                                if (iThirdPartyLoginCallback != null)
                                    iThirdPartyLoginCallback.onComplete(
                                            jsonObject.getString("id"), jsonObject.getString
                                                    ("screen_name")
                                            , "2", jsonObject.getString("profile_image_url"),
                                            jsonObject.getString("gender"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (iThirdPartyLoginCallback != null)
                                iThirdPartyLoginCallback.onError(-100, ShareIntentStaticCode
                                        .THIDR_PARTY_WEIBO, "授权失败");
                        }
                    }
                });
    }

    public void loginToWechat() {
        // send oauth request
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_auth";
        api.sendReq(req);
    }

    public void loginToQQ() {
        if (!mTencent.isSessionValid()) {
            mTencent.login((Activity) mContext, "get_simple_userinfo", uiListener);
        } else {
            getQQUserInfo();
        }
    }

    /**
     * 初始化QQTOKEN
     *
     * @param jsonObject
     */
    public void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 获取QQ信息
     */
    public void getQQUserInfo() {
        UserInfo userInfo = new UserInfo(mContext, mTencent.getQQToken());
        userInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object o) {
                Log.e("getQQUserInfo e", o.toString());
                try {
                    JSONObject jsonObject = (JSONObject) o;
                    int ret = jsonObject.getInt("ret");
                    if (ret == 0) {
                        if (iThirdPartyLoginCallback != null)
                            iThirdPartyLoginCallback.onComplete(
                                    mTencent.getQQToken().getOpenId(), jsonObject.getString("nickname"),
                                    "3", jsonObject.getString("figureurl_qq_2"), jsonObject.getString("gender")
                                            .equals("女") ? "f" : "m"
                            );
                    } else {
                        if (iThirdPartyLoginCallback != null)
                            iThirdPartyLoginCallback.onError(ret, ShareIntentStaticCode
                                    .THIDR_PARTY_QQ, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                if (iThirdPartyLoginCallback != null)
                    iThirdPartyLoginCallback.onError(uiError.errorCode, ShareIntentStaticCode
                            .THIDR_PARTY_QQ, uiError.errorMessage);
            }

            @Override
            public void onCancel() {
                if (iThirdPartyLoginCallback != null)
                    iThirdPartyLoginCallback.onError(-100, ShareIntentStaticCode
                            .THIDR_PARTY_QQ, "授权失败");
            }
        });
    }

    public boolean isInstallWechat() {
        return api.isWXAppInstalled();
    }

    /**
     * 获取微信登录token
     *
     * @param code
     */
    public static void getAccessToken(String code) {
        Log.e("getAccessToken", "getAccessToken");
        OkHttpUtils.post().url("https://api.weixin.qq.com/sns/oauth2/access_token")
                .addParams("appid", ShareConfig.getInstance().wechatAPPID)
                .addParams("secret", ShareConfig.getInstance().wechatSecret)
                .addParams("code", code)
                .addParams("grant_type", "authorization_code")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (LoginUtil.getInstance().iThirdPartyLoginCallback != null)
                            LoginUtil.getInstance().iThirdPartyLoginCallback.onError(-100,
                                    ShareIntentStaticCode
                                            .THIDR_PARTY_WECHAT,
                                    "授权失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.isNull("errcode")) {
                                if (LoginUtil.getInstance().iThirdPartyLoginCallback != null)
                                    LoginUtil.getInstance().iThirdPartyLoginCallback.onError(-100,
                                            ShareIntentStaticCode
                                                    .THIDR_PARTY_WECHAT,
                                            "授权失败");
                            } else {
                                getUserInfo(jsonObject.getString("access_token"), jsonObject
                                        .getString("openid"));
                            }
                        } catch (JSONException e) {
                            if (LoginUtil.getInstance().iThirdPartyLoginCallback != null)
                                LoginUtil.getInstance().iThirdPartyLoginCallback.onError(-100,
                                        ShareIntentStaticCode
                                                .THIDR_PARTY_WECHAT,
                                        "授权失败");
                            e.printStackTrace();
                        }
                    }
                });

    }

    /**
     * 获取微信用户信息
     *
     * @param access_token
     * @param openid
     */
    public static void getUserInfo(String access_token, String openid) {
        Log.e("access_token", access_token);
        OkHttpUtils.get().url("https://api.weixin.qq.com/sns/userinfo")
                .addParams("access_token", access_token)
                .addParams("openid", openid)
                .addParams("lang", "zh_CN")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (LoginUtil.getInstance().iThirdPartyLoginCallback != null)
                            LoginUtil.getInstance().iThirdPartyLoginCallback.onError(-100,
                                    ShareIntentStaticCode
                                            .THIDR_PARTY_WECHAT,
                                    "授权失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            Log.e("getUserInfo", response);
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.isNull("errcode")) {
                                if (LoginUtil.getInstance().iThirdPartyLoginCallback != null)
                                    LoginUtil.getInstance().iThirdPartyLoginCallback.onError(-100,
                                            ShareIntentStaticCode
                                                    .THIDR_PARTY_WECHAT,
                                            "授权失败");
                            } else {
                                if (LoginUtil.getInstance().iThirdPartyLoginCallback != null)
                                    LoginUtil.getInstance().iThirdPartyLoginCallback.onComplete(
                                            jsonObject.getString("openid"),
                                            jsonObject.getString("nickname"),
                                            "1", jsonObject.getString("headimgurl"),
                                            jsonObject.getInt("sex") == 1 ? "m" :
                                                    jsonObject.getInt("sex") == 2 ? "f" : "n");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (LoginUtil.getInstance().iThirdPartyLoginCallback != null)
                                LoginUtil.getInstance().iThirdPartyLoginCallback.onError(-100,
                                        ShareIntentStaticCode
                                                .THIDR_PARTY_WECHAT,
                                        "授权失败");
                        }
                    }
                });
    }
}

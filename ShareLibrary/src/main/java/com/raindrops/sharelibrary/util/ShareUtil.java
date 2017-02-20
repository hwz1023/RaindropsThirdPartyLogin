package com.raindrops.sharelibrary.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.raindrops.sharelibrary.ShareConfig;
import com.raindrops.sharelibrary.ShareIntentStaticCode;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * Created by huangweizhou on 2017/2/16.
 */

public class ShareUtil {

    private IWXAPI api;
    /**
     * QQ
     */
    private Tencent mTencent;

    private Context mContext;

    private IUiListener uiListener;

    /**
     * 微博微博分享接口实例
     */
    private IWeiboShareAPI mWeiboShareAPI = null;

    public ShareUtil(Context mContext) {
        this.mContext = mContext;
        api = WXAPIFactory.createWXAPI(mContext, ShareConfig.getInstance().wechatAPPID);
        // 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mContext, ShareConfig.getInstance().weiboKey);
        mWeiboShareAPI.registerApp();
        mTencent = Tencent.createInstance(ShareConfig.getInstance().qqAPPID, mContext
                .getApplicationContext());
        uiListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                if (ShareConfig.isShareCallBack())
                    ShareConfig.getInstance().getShareCallBack().shareSuccess();
            }

            @Override
            public void onError(UiError uiError) {
                if (ShareConfig.isShareCallBack())
                    ShareConfig.getInstance().getShareCallBack().shareError();
            }

            @Override
            public void onCancel() {
                if (ShareConfig.isShareCallBack())
                    ShareConfig.getInstance().getShareCallBack().shareError();
            }
        };
    }

    /**
     * @param webUrl
     * @param title
     * @param description
     * @param imageUrl
     * @param type        朋友圈 好友
     */

    public void shareToWechat(final String webUrl, final String title, final String description,
                              String imageUrl, final int type) {
        Glide.with(mContext)
                .load(imageUrl).asBitmap()
                .toBytes()
                .into(new SimpleTarget<byte[]>(120, 120) {
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if (ShareConfig.isShareCallBack())
                            ShareConfig.getInstance().getShareCallBack().shareError();
                    }

                    @Override
                    public void onResourceReady(byte[] resource, GlideAnimation<? super
                            byte[]> glideAnimation) {
                        WXWebpageObject webpageObject = new WXWebpageObject();
                        webpageObject.webpageUrl = webUrl;
                        WXMediaMessage msg = new WXMediaMessage(webpageObject);
                        msg.title = title;
                        msg.description = description;
                        msg.thumbData = resource;
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = "web" + System.currentTimeMillis();
                        req.message = msg;
                        req.scene = type == ShareIntentStaticCode
                                .THIDR_PARTY_WECHAT_FRIEND_CIRCLE ? SendMessageToWX
                                .Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                        api.sendReq(req);
                    }
                });
    }

    public void shareToQQ(String webUrl, String title, String description, String imageUrl, int
            type) {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, webUrl);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        if (type == ShareIntentStaticCode.THIDR_PARTY_QQ_ZONE)
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        mTencent.shareToQQ((Activity) mContext, params, uiListener);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, uiListener);
    }

    public void shareToWeibo(final String webUrl, final String title, final String description,
                             String imageUrl) {
        Glide.with(mContext)
                .load(imageUrl).asBitmap()
                .toBytes()
                .into(new SimpleTarget<byte[]>(120, 120) {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if (ShareConfig.isShareCallBack())
                            ShareConfig.getInstance().getShareCallBack().shareError();
                    }

                    @Override
                    public void onResourceReady(byte[] resource, GlideAnimation<? super
                            byte[]> glideAnimation) {
                        // 1. 初始化微博的分享消息
                        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
                        WebpageObject mediaObject = new WebpageObject();
                        mediaObject.identify = Utility.generateGUID();
                        mediaObject.title = title;
                        mediaObject.description = description;
                        mediaObject.thumbData = resource;
                        mediaObject.actionUrl = webUrl;
                        mediaObject.defaultText = "分享";
                        weiboMessage.mediaObject = mediaObject;
                        // 2. 初始化从第三方到微博的消息请求
                        final SendMultiMessageToWeiboRequest request = new
                                SendMultiMessageToWeiboRequest();
                        // 用transaction唯一标识一个请求
                        request.transaction = String.valueOf(System.currentTimeMillis());
                        request.multiMessage = weiboMessage;
                        // 3. 发送请求消息到微博，唤起微博分享界面
                        final AuthInfo authInfo = new AuthInfo(mContext, ShareConfig.getInstance
                                ().weiboKey,
                                ShareConfig.getInstance().weiboRedirectUrl, ShareConfig
                                .getInstance().weiboScope);
                        mWeiboShareAPI.sendRequest((Activity) mContext, request, authInfo,
                                "", new WeiboAuthListener() {
                                    @Override
                                    public void onWeiboException(WeiboException arg0) {
                                    }

                                    @Override
                                    public void onComplete(Bundle bundle) {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void onCancel() {
                                    }
                                });
                    }
                });
    }

    public void weiboResponse(Intent intent, IWeiboHandler.Response handler) {
        mWeiboShareAPI.handleWeiboResponse(intent, handler);
    }


}

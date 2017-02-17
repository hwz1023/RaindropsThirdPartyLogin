package com.raindrops.sharelibrary;

/**
 * Created by huangweizhou on 2016/12/30.
 */

public interface ShareIntentStaticCode {

    int AUTH_GET_CODE = 100010;

    int AUTH_ERROR = 100011;

    int AUTH_RESULT = 100012;

    /**
     * QQ
     */
    int THIDR_PARTY_QQ = 1000001;
    /**
     * 微信
     */
    int THIDR_PARTY_WECHAT = 1000002;
    /**
     * 微博
     */
    int THIDR_PARTY_WEIBO = 1000003;
    /**
     * 朋友圈
     */
    int THIDR_PARTY_WECHAT_FRIEND_CIRCLE = 1000004;

    /**
     * 登陆
     */
    int THIDR_PARTY_LOGIN = 1000005;

    /**
     * 分享
     */
    int THIDR_PARTY_SHARE = 1000006;

    /**
     * 类型 分享或者登陆
     */
    String THIDR_PARTY_TYPE = "THIDR_PARTY_TYPE";
    /**
     * 平台 QQ 微信 微博等
     */
    String THIDR_PARTY_PLATFORM = "THIDR_PARTY_PLATFORM";

    /**
     * 分享网页地址
     */
    String THIDR_PARTY_SHARE_WEBURL = "THIDR_PARTY_SHARE_WEBURL";

    /**
     * 分享标题
     */
    String THIDR_PARTY_SHARE_TITLE = "THIDR_PARTY_SHARE_TITLE";

    /**
     * 分享描述
     */
    String THIDR_PARTY_SHARE_DESCRIPTION = "THIDR_PARTY_SHARE_DESCRIPTION";

    /**
     * 分享图片地址
     */
    String THIDR_PARTY_SHARE_IMAGEURL = "THIDR_PARTY_SHARE_IMAGEURL";

    /**
     * 分享图片内容
     */
    String THIDR_PARTY_SHARE_IMAGEBYTE = "THIDR_PARTY_SHARE_IMAGEBYTE";

}

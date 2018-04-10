package com.raindrops.sharelibrary.util;

import android.app.Activity;
import android.content.Context;

import com.alipay.sdk.app.PayTask;
import com.raindrops.sharelibrary.PayCallback;
import com.raindrops.sharelibrary.alipay.PayResult;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by huangweizhou on 2017/2/21.
 */

public class ThirdPartyPayUtils {

    public String wxAppId;

    public String qqAppId;

    public String qqCallbackScheme;

    private PayCallback callback;


    public static ThirdPartyPayUtils getInstance() {
        return SingleInstance.instance;
    }

    private static class SingleInstance {
        private static ThirdPartyPayUtils instance = new ThirdPartyPayUtils();
    }

    public ThirdPartyPayUtils initWx(String wxAppId) {
        this.wxAppId = wxAppId;
        return this;
    }

    public ThirdPartyPayUtils initQQ(String qqAppId) {
        this.qqAppId = qqAppId;
        this.qqCallbackScheme = "qwallet" + qqAppId;
        return this;
    }

    public static void doAliPay(final Context context, final String orderInfo) {
        Observable.create(new Observable.OnSubscribe<PayResult>() {
            @Override
            public void call(Subscriber<? super PayResult> subscriber) {
                PayTask alipay = new PayTask((Activity) context);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                PayResult payResult = new PayResult(result);
                subscriber.onNext(payResult);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PayResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getInstance().getCallback() != null)
                            getInstance().getCallback().payError("-1", e.getMessage());
                    }

                    @Override
                    public void onNext(PayResult payResult) {
                        if (getInstance().getCallback() == null)
                            return;
                        if (payResult.getResultStatus().equals("9000")) {
                            getInstance().getCallback().paySuccess();
                        } else {
                            getInstance().getCallback().payError(payResult.getResultStatus(), payResult.getResult());
                        }
                    }
                });

    }

    public static void doWechatPay(Context context, String partnerId, String prepayId, String
            packageValue
            , String nonceStr, String timeStamp, String sign) {
        final IWXAPI msgApi = WXAPIFactory.createWXAPI(context, null);
        msgApi.registerApp(ThirdPartyPayUtils.getInstance().wxAppId);
        PayReq request = new PayReq();
        request.appId = ThirdPartyPayUtils.getInstance().wxAppId;
        request.partnerId = partnerId;
        request.prepayId = prepayId;
        request.packageValue = packageValue;
        request.nonceStr = nonceStr;
        request.timeStamp = timeStamp;
        request.sign = sign;
        msgApi.sendReq(request);
    }


    public ThirdPartyPayUtils bindCallback(PayCallback callback) {
        this.callback = callback;
        return this;
    }

    public PayCallback getCallback() {
        return callback;
    }
}

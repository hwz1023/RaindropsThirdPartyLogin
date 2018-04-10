package raindrops.hwz.shareutil;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.longene.mashangwan.R;
import com.raindrops.sharelibrary.ShareConfig;
import com.raindrops.sharelibrary.ShareIntentStaticCode;
import com.raindrops.sharelibrary.callback.ThirdPartyLoginCallback;
import com.raindrops.sharelibrary.callback.ThirdPartyShareCallback;
import com.raindrops.sharelibrary.util.LoginUtil;
import com.raindrops.sharelibrary.util.ShareUtil;

public class MainActivity extends Activity {
    private LoginUtil loginUtil;
    ShareUtil shareUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShareConfig.getInstance().initWechatAPPID("xx",
                "xx")
                .initQQAPPID("xx")
                .initWeibo("xx", "http://www.xx.com", "");

        ShareConfig.getInstance().bindLoginCallback(new ThirdPartyLoginCallback() {
            @Override
            public void onLoginSuccess(String uid, String username, String type, String icon,
                                       String sex) {
                Log.e("onLoginSuccess", "uid" + uid + "\n" +
                        "username" + username + "\n" +
                        "type" + type + "\n" +
                        "icon" + icon + "\n" +
                        "sex" + sex + "\n");
            }

            @Override
            public void onLoginError(int code, int type, String message) {
                Log.e("onError", "code" + code + "\n" +
                        "type" + type + "\n" +
                        "message" + message + "\n");
            }
        });


        ShareConfig.getInstance().bindShareCallback(new ThirdPartyShareCallback() {
            @Override
            public void onShareSuccess() {
                Log.e("onShareSuccess", "onShareSuccess");
            }

            @Override
            public void onShareError() {
                Log.e("onShareError", "onShareError");
            }
        });

        Bundle bundle = new Bundle();
        bundle.putInt(ShareIntentStaticCode.THIDR_PARTY_PLATFORM, ShareIntentStaticCode
                .THIDR_PARTY_QQ);
        bundle.putInt(ShareIntentStaticCode.THIDR_PARTY_TYPE, ShareIntentStaticCode
                .THIDR_PARTY_SHARE);
        bundle.putString(ShareIntentStaticCode.THIDR_PARTY_SHARE_WEBURL, "http://www.xx.com");
        bundle.putString(ShareIntentStaticCode.THIDR_PARTY_SHARE_TITLE, "测试标题");
        bundle.putString(ShareIntentStaticCode.THIDR_PARTY_SHARE_DESCRIPTION, "测试");
        bundle.putString(ShareIntentStaticCode.THIDR_PARTY_SHARE_IMAGEURL, "http://www.xx" +
                ".com/xx/xx.png");
        ShareConfig.newInstance(this, bundle);
    }
}

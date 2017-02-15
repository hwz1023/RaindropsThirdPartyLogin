package raindrops.hwz.shareutil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.raindrops.sharelibrary.util.IThirdPartyLoginCallback;
import com.raindrops.sharelibrary.util.LoginUtil;
import com.raindrops.sharelibrary.util.ShareConfig;

public class MainActivity extends AppCompatActivity {
    private LoginUtil loginUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ShareConfig.getInstance().initWechatAPPID("xx",
                "xx")
                .initQQAPPID("xx")
                .initWeibo("xx", "http://www.xx.com", "");

        loginUtil = LoginUtil.getInstance();
        loginUtil.initLoginUtil(this, new IThirdPartyLoginCallback() {
            @Override
            public void onComplete(String uid, String username, String type, String icon, String
                    sex) {
                Log.e("onComplete", "uid" + uid + "\n" +
                        "username" + username + "\n" +
                        "type" + type + "\n" +
                        "icon" + icon + "\n" +
                        "sex" + sex + "\n");
            }

            @Override
            public void onError(int code, int type, String message) {
                Log.e("onError", "code" + code + "\n" +
                        "type" + type + "\n" +
                        "message" + message + "\n");
            }
        });
        loginUtil.loginWeibo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loginUtil.onActivityResult(requestCode, resultCode, data);
    }
}

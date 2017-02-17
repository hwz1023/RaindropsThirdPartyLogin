package raindrops.hwz.shareutil;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by huangweizhou on 2017/2/16.
 */

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}

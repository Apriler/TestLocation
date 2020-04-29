package cn.com.test.testlocation;

import android.app.Application;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

public class BaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UMConfigure.init(this, "5ea8e08ddbc2ec0782da95cc", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }
}

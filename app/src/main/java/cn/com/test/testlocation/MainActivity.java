package cn.com.test.testlocation;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnReset;
    private Button mBtnMap;
    private Button mBtnWifi;
    private Button mBtnGps;
    private TextView mTvLat;
    private TextView mTvLatInput;
    private TextView mTvLongitude;
    private TextView mTvLongitudeInput;
    private TextView mTvAccuracy;
    private TextView mTvAccuracyInput;
    private TextView mTvProvider;
    private TextView mTvProviderInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mBtnReset = (Button) findViewById(R.id.btn_reset);
        mBtnMap = (Button) findViewById(R.id.btn_map);
        mBtnWifi = (Button) findViewById(R.id.btn_wifi);
        mBtnGps = (Button) findViewById(R.id.btn_gps);
        mTvLat = (TextView) findViewById(R.id.tv_lat);
        mTvLatInput = (TextView) findViewById(R.id.tv_lat_input);
        mTvLongitude = (TextView) findViewById(R.id.tv_longitude);
        mTvLongitudeInput = (TextView) findViewById(R.id.tv_longitude_input);
        mTvAccuracy = (TextView) findViewById(R.id.tv_accuracy);
        mTvAccuracyInput = (TextView) findViewById(R.id.tv_accuracy_input);
        mTvProvider = (TextView) findViewById(R.id.tv_provider);
        mTvProviderInput = (TextView) findViewById(R.id.tv_provider_input);

        mBtnReset.setOnClickListener(this);
        mBtnMap.setOnClickListener(this);
        mBtnWifi.setOnClickListener(this);
        mBtnGps.setOnClickListener(this);
//        openGPS(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset:
                showToast(mBtnReset.getText().toString());
                break;
            case R.id.btn_map:
                showToast(mBtnMap.getText().toString());
                break;
            case R.id.btn_wifi:
                getLocation(LocationManager.NETWORK_PROVIDER);
                showToast(mBtnWifi.getText().toString());
                break;
            case R.id.btn_gps:
//                openGPSSettings();
                getLocation(LocationManager.GPS_PROVIDER);
                showToast(mBtnGps.getText().toString());
                break;
        }
    }

    public void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void openGPSSettings() {
        LocationManager alm = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, " GPS模块正常 ", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        Toast.makeText(this, " 请开启GPS！ ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        startActivityForResult(intent, 0); // 此为设置完成后返回到获取界面

    }

    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void getLocation(String locationType) {
// 获取位置管理服务
//        LocationManager locationManager;
//        String serviceName = Context.LOCATION_SERVICE;
//        locationManager = (LocationManager) this.getSystemService(serviceName);
//
//// 查找到服务信息
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(true);
//        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
//
//        String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
////                ActivityCompat#requestPermissions
////             here to request the missing permissions, and then overriding
////               public void onRequestPermissionsResult(int requestCode, String[] permissions,
////                                                      int[] grantResults)
////             to handle the case where the user grants the permission. See the documentation
////             for ActivityCompat#requestPermissions for more details.
//            return;
//        }



        //提供位置定位服务的位置管理器对象,中枢控制系统
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //位置提供器，也就是实际上来定位的对象，这里选择的是GPS定位
        String locationProvider = locationType;
        //获取手机中开启的位置提供器
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            Log.e("a","provider --->"+provider );
        }
        Log.e("a","---------------provider ------------------" );
        //开始定位,获取当前位置对象
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        }
        final Location location = locationManager.getLastKnownLocation(locationProvider);

        //每1s监听一次位置信息，如果位置距离改变超过1m。就执行onLocationChanged方法
        //如果第一次打开没有显示位置信息，可以退出程序重新进入，就会显示
        locationManager.requestLocationUpdates("gps", 1000,1,new locationListener());
//        Log.e("a","location is null  ----- ");



//        Log.e("a","provider --->"+provider);
//        Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
//        Log.e("a","location --->"+location.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateToNewLocation(location);
            }
        });

// 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
//        locationManager.requestLocationUpdates(provider, 100 * 1000 , 500 ,
//                locationListener);
    }

    private void updateToNewLocation(Location location) {

//        TextView tv1;

//        tv1 = (TextView) this .findViewById(R.id.tv1);
        if (location != null ) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.e("a"," 维度： " + latitude + " \n经度 " + longitude);
            mTvLatInput.setText(String.valueOf(latitude));
            mTvLongitudeInput.setText(String.valueOf(longitude));
//            tv1.setText( " 维度： " + latitude + " \n经度 " + longitude);
        } else {
//            tv1.setText(  );
            mTvLatInput.setText(String.valueOf(" 无法获取地理信息 "));
            mTvLongitudeInput.setText(String.valueOf(" 无法获取地理信息 "));
        }

    }


}

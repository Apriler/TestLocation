package cn.com.test.testlocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
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
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

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
    private TextView mTvQuene;
    private ExecutorService mExecutor = null;
    public volatile String locationType = LocationManager.GPS_PROVIDER;
    public LimitQueue<LonLat> LonLatQueue = new LimitQueue<>(8);

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
//        mTvLat = (TextView) findViewById(R.id.tv_lat);
//        mTvLatInput = (TextView) findViewById(R.id.tv_lat_input);
//        mTvLongitude = (TextView) findViewById(R.id.tv_longitude);
//        mTvLongitudeInput = (TextView) findViewById(R.id.tv_longitude_input);
//        mTvAccuracy = (TextView) findViewById(R.id.tv_accuracy);
//        mTvAccuracyInput = (TextView) findViewById(R.id.tv_accuracy_input);
//        mTvProvider = (TextView) findViewById(R.id.tv_provider);
//        mTvProviderInput = (TextView) findViewById(R.id.tv_provider_input);
        mTvQuene = (TextView) findViewById(R.id.tv_quene);

        mExecutor = Executors.newSingleThreadExecutor();
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

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_NETWORK_STATE}, 1);

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset:
                mTvLatInput.setText("");
                mTvLongitudeInput.setText("");
                mTvAccuracyInput.setText("");
                mTvProviderInput.setText("");
                break;
            case R.id.btn_map:
                showToast(mBtnMap.getText().toString());
                LocationProcessor locationProcessorNetwork = new LocationProcessor(this);
                locationProcessorNetwork.addDownloadTask();
                break;
            case R.id.btn_wifi:
//                getLocation(LocationManager.NETWORK_PROVIDER);
                locationType = LocationManager.NETWORK_PROVIDER;


//                LocationTask callNetwork = new LocationTask(this, );
//                FutureTask<Boolean> fuNetwork=new FutureTask<Boolean>(callNetwork);
//                Thread thNetwork=new Thread(fuNetwork,"我是fu线程");
//                thNetwork.start();

                showToast(mBtnWifi.getText().toString());
                break;
            case R.id.btn_gps:
//                openGPSSettings();
//                getLocation(LocationManager.GPS_PROVIDER);
//                LocationTask callGps = new LocationTask(this, LocationManager.GPS_PROVIDER);
                locationType = LocationManager.GPS_PROVIDER;
//                LocationProcessor locationProcessorGps =new LocationProcessor(this);
//                locationProcessorGps.addDownloadTask();
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

    /**
     * 获取定位信息
     * @param locationType 采取定位的方式
     */
    private void getLocation(final String locationType) {
// 获取位置管理服务
//        LocationProcessor locationManager;
//        String serviceName = Context.LOCATION_SERVICE;
//        locationManager = (LocationProcessor) this.getSystemService(serviceName);
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


//
//        //提供位置定位服务的位置管理器对象,中枢控制系统
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        //位置提供器，也就是实际上来定位的对象，这里选择的是GPS定位
//        String locationProvider = locationType;
//        //获取手机中开启的位置提供器
//        List<String> providers = locationManager.getProviders(true);
//        for (String provider : providers) {
//            Log.e("a","provider --->"+provider );
//        }
//        Log.e("a","--------------------------------" );
//        //开始定位,获取当前位置对象
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//
//        }
        //每1s监听一次位置信息，如果位置距离改变超过1m。就执行onLocationChanged方法
        //如果第一次打开没有显示位置信息，可以退出程序重新进入，就会显示

//        final Location location = locationManager.getLastKnownLocation(locationProvider);
//        if (location == null) {
//            Log.e("a","location is null------");
//            locationManager.requestLocationUpdates(locationType, 1000, 1, new locationListener());
//        }

//        Log.e("a","provider --->"+provider);
//        Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
//        Log.e("a","location --->"+location.toString());
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                updateToNewLocation(location);
//            }
//        });

// 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
//        locationManager.requestLocationUpdates(provider, 100 * 1000 , 500 ,
//                locationListener);
    }

    public void updateToNewLocation(Location location,String locationType) {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = sdf.format(date);
        if (location != null ) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            float accuracy = location.getAccuracy();
            Log.e("luo"," 维度： " + latitude + " \n经度 " + longitude+ " \n精准度 " + accuracy);
            LonLatQueue.offer(new LonLat(latitude,longitude,accuracy,nowTime,locationType,true));
//            mTvLatInput.setText(String.valueOf(latitude));
//            mTvLongitudeInput.setText(String.valueOf(longitude));
//            mTvAccuracyInput.setText(String.valueOf(accuracy));
//            mTvProviderInput.setText(locationType);
//            tv1.setText( " 维度： " + latitude + " \n经度 " + longitude);
        } else {
//            tv1.setText(  );
            LonLatQueue.offer(new LonLat(nowTime,locationType,false));
//            mTvLatInput.setText(String.valueOf(" 无法获取地理信息 "));
//            mTvLongitudeInput.setText(String.valueOf(" 无法获取地理信息 "));
//            mTvAccuracyInput.setText("无法获取经度信息");

        }
        mTvQuene.setText(LonLatQueue.toString());

    }




    class LocationTask implements Runnable {
        private LocationTask mTask = this;

        public locationListener mListener1;

        private Context mContext;
//        private String locationType;
        private LocationManager locationManager;
        private LocationProvider locationProvider;
        private LocationProcessor locationProcessor;

        public LocationTask(Context mContext,LocationProcessor locationProcessor) {
//        mListener = listener;
            this.mContext =mContext;
//            this.locationType =locationType;
//            locationProcessor = new LocationProcessor(mContext,locationType);
            this.locationProcessor =locationProcessor;
            init();
        }


        @Override
        @SuppressLint("MissingPermission")
        public void run() {
            try {
                while (true){
                    Log.e("luo","locationType is ------" +locationType);
                    final Location location = locationManager.getLastKnownLocation(locationType);
                    if (location != null) {
                        Log.e("luo","location is null------");
//                        locationManager.requestLocationUpdates(locationType, 1000, 1, new locationListener() {
//                            @Override
//                            public void onLocationChanged(Location location) {
//                                super.onLocationChanged(location);
//                            }
//
//                            @Override
//                            public void onStatusChanged(String provider, int status, Bundle extras) {
//                                super.onStatusChanged(provider, status, extras);
//                                //不可用的状态
//                                if (status != LocationProvider.AVAILABLE){
//                                    switch (status){
//                                        case LocationProvider.TEMPORARILY_UNAVAILABLE:
//                                            Log.e("luo","-------------provider: "+provider+"  --->  onStatusChanged  ,status --->TEMPORARILY_UNAVAILABLE" );
//                                            break;
//                                        case LocationProvider.OUT_OF_SERVICE:
//                                            Log.e("luo","-------------provider: "+provider+"  --->  onStatusChanged  ,status --->OUT_OF_SERVICE" );
//                                            break;
//                                    }
//
//                                    locationProcessor.removeTask();
//                                }
//                                Log.e("luo","-------------provider: "+provider+"  --->  onStatusChanged  ,status --->AVAILABLE" );
//                            }
//
//                            @Override
//                            public void onProviderEnabled(String provider) {
//                                super.onProviderEnabled(provider);
//                                Log.e("luo","-------------provider: "+provider+"  --->  onProviderEnabled");
//                            }
//
//                            @Override
//                            public void onProviderDisabled(String provider) {
//                                super.onProviderDisabled(provider);
//                            }
//                        });
//                        Log.e("luo","--------------" );
//

                    }
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("luo","--------------locationType--->:"+locationType );
                            updateToNewLocation(location,locationType);
                        }
                    });
                    Thread.sleep(1000);
//                    this.wait(1000);
                }

            } catch (Exception e) {
                Log.e("luo","--------------e:"+e );
            }
        }

        private void init() {
            //提供位置定位服务的位置管理器对象,中枢控制系统
            locationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
            //位置提供器，也就是实际上来定位的对象，这里选择的是GPS定位
//            String locationProvider = locationType;
//            //获取手机中开启的位置提供器
//            List<String> providers = locationManager.getProviders(true);


        }
    }

    class LocationProcessor {


        private Context mContext;
//        private String locationType;

        public LocationProcessor(Context mContext) {

            this.mContext =mContext;
//            this.locationType =locationType;

        }



        /**
         * 添加下载任务到下载队列里,同时将下载任务放到线程池中
         */
        public boolean addDownloadTask() {

            Thread th = new Thread(new LocationTask(mContext,this));
            th.start();


            return true;
        }

        /**
         * 删除下载任务
         */
        public void removeTask() {

        }
    }



}

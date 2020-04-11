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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.CellIdentityGsm;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Queue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnReset;
    private Button mBtnStart;
    private Button mBtnWifi;
    private Button mBtnGps;


    private TextView mTvQuene;
    private TextView mTvSignalStrength;

    private ExecutorService mExecutor = null;
    public static volatile String locationType = LocationManager.GPS_PROVIDER;
    public LimitQueue<LonLat> LonLatQueue = new LimitQueue<>(8);
    private LocationManager locationManager;
    private MainActivity.locationListener1 locationListener1;
    private String log_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/location.txt";
    // 是否 暂停标志位
    public boolean isPause = false;
    //是否 第一次启动 标志位
    public boolean isFirstStart = true;
    //gps获取定位的次数
    public volatile int gpsCount = 0;
    //wifi获取定位的次数
    public volatile int wifiCount = 0;

    LocationTask locationTask;
    Thread th;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mBtnReset = (Button) findViewById(R.id.btn_reset);
        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnWifi = (Button) findViewById(R.id.btn_wifi);
        mBtnGps = (Button) findViewById(R.id.btn_gps);
        mTvSignalStrength = findViewById(R.id.tv_signal_strength);
        mTvQuene = (TextView) findViewById(R.id.tv_quene);
        locationListener1 = new locationListener1();
        mExecutor = Executors.newSingleThreadExecutor();
        mBtnReset.setOnClickListener(this);
        mBtnStart.setOnClickListener(this);
        mBtnWifi.setOnClickListener(this);
        mBtnGps.setOnClickListener(this);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        openGPS(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }



    }

    @Override
    @SuppressLint("MissingPermission")
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_reset:
                isPause = true;
                mTvQuene.setText("");
                LonLatQueue.clear();
                th.interrupt();
                isFirstStart = true;
                mBtnStart.setText("start");
                clearCount();
                break;
            case R.id.btn_start:
                if (isFirstStart) {
                    locationTask = new LocationTask(this);
                    isPause = false;
                    th = new Thread(locationTask);
                    showToast("start!");
                    th.start();
                    isFirstStart = false;
                    mBtnStart.setText("|| pause");
                } else {
                    if (isPause) {
                        showToast("continue!");
                        locationTask.toResume();
                        mBtnStart.setText("|| pause");
                    } else {
                        showToast("pause!");
                        locationTask.toSuspend();
                        mBtnStart.setText("▶ continue");
                    }
                }
                break;
            case R.id.btn_wifi:
                //先清除计数
                clearCount();
//                getLocation(LocationManager.NETWORK_PROVIDER);
                locationManager.removeUpdates(locationListener1);
                locationType = LocationManager.NETWORK_PROVIDER;
                locationManager.requestLocationUpdates(locationType, 1000, 0, locationListener1);

//                LocationTask callNetwork = new LocationTask(this, );
//                FutureTask<Boolean> fuNetwork=new FutureTask<Boolean>(callNetwork);
//                Thread thNetwork=new Thread(fuNetwork,"我是fu线程");
//                thNetwork.start();

                showToast(mBtnWifi.getText().toString());

                break;
            case R.id.btn_gps:
                clearCount();
//                openGPSSettings();
//                getLocation(LocationManager.GPS_PROVIDER);
//                LocationTask callGps = new LocationTask(this, LocationManager.GPS_PROVIDER);
                locationManager.removeUpdates(locationListener1);
                locationType = LocationManager.GPS_PROVIDER;
                locationManager.requestLocationUpdates(locationType, 1000, 0, locationListener1);
//                locationManager.requestLocationUpdates(locationType, 1000, 0, new locationListener1());
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
     *
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void updateToNewLocation(WifiInfo wifiInfo, SignalStrength signalStrength, Location location, String locationType) {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = sdf.format(date);
        //获取gsm信号强度
        int dbm = 0;
        dbm = signalStrength.getGsmSignalStrength();
        //获取wifi信号强度
        int rssi = 0;
        rssi = wifiInfo.getRssi();

        mTvSignalStrength.setText(String.format("signal  strength： GSM %d          WIFI %d",dbm,rssi));
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            float accuracy = location.getAccuracy();
            Log.e("luo", " 维度： " + latitude + " \n经度 " + longitude + " \n精准度 " + accuracy + " \ngsm信号强度 " + dbm + " \nwifi信号强度 " + rssi);
            LonLat lonLat = new LonLat(latitude, longitude, accuracy, nowTime, locationType, true);
            this.outToFile(lonLat);
            LonLatQueue.offer(lonLat);
//            mTvLatInput.setText(String.valueOf(latitude));
//            mTvLongitudeInput.setText(String.valueOf(longitude));
//            mTvAccuracyInput.setText(String.valueOf(accuracy));
//            mTvProviderInput.setText(locationType);
//            tv1.setText( " 维度： " + latitude + " \n经度 " + longitude);
        } else {
//            tv1.setText(  );

            LonLatQueue.offer(new LonLat(nowTime, locationType, false));
//            mTvLatInput.setText(String.valueOf(" 无法获取地理信息 "));
//            mTvLongitudeInput.setText(String.valueOf(" 无法获取地理信息 "));
//            mTvAccuracyInput.setText("无法获取经度信息");

        }


    }
    public void clearCount(){
        this.wifiCount = 0;
        this.gpsCount = 0;
    }


    class LocationTask implements Runnable {


        private Context mContext;
        private LocationManager locationManager;


        public LocationTask(Context mContext) {
//        mListener = listener;
            this.mContext = mContext;

            init();
        }

        public synchronized void toSuspend() {//同步方法
            isPause = true;
        }

        public synchronized void toResume() {//不执行wait，并唤醒暂停的线程

            isPause = false;
//                locationTask.notifyAll();
            locationTask.notify();//当前等待的进程，继续执行（唤醒线程）

        }


        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        @SuppressLint("MissingPermission")
        public void run() {
            try {
                while (true) {
                    //同步代码块，加锁
                    synchronized (this) {
                        Log.e("luo", "isPause is ------" + isPause);
                        while (isPause) {
                            try {
                                //线程进入等待状态
                                wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Log.e("luo", "locationType is ------" + locationType);
//                    locationManager.requestLocationUpdates(locationType, 1000, 1, new locationListener1());
                    final Location location = locationManager.getLastKnownLocation(locationType);
                    if (location != null) {
                        Log.e("luo", "location is null------");

                    }
                    TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    final SignalStrength signalStrength = mTelephonyManager.getSignalStrength();
                    final List<CellInfo> allCellInfo = mTelephonyManager.getAllCellInfo();
                    WifiManager wifi_service = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    final WifiInfo wifiInfo = wifi_service.getConnectionInfo();

                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                            Log.e("luo", "locationType--->:" + locationType);
                            mTvQuene.setText(LonLatQueue.toString());
                            if (wifiCount == 5){
                                mBtnGps.performClick();
                                return;
                            }else if (gpsCount == 3){
                                mBtnWifi.performClick();
                                return;
                            }
                            updateToNewLocation(wifiInfo, signalStrength, location, locationType);
                            if (LocationManager.GPS_PROVIDER.equals(locationType)){
                                gpsCount++;
                            }else if (LocationManager.NETWORK_PROVIDER.equals(locationType)){
                                wifiCount++;
                            }
                        }
                    });
//                    }
                    Thread.sleep(3000);
                }


            } catch (Exception e) {
                Log.e("luo", "--------------e:" + e);
            }
        }

        private void init() {
            //提供位置定位服务的位置管理器对象,中枢控制系统
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            //位置提供器，也就是实际上来定位的对象，这里选择的是GPS定位
//            String locationProvider = locationType;
//            //获取手机中开启的位置提供器
//            List<String> providers = locationManager.getProviders(true);


        }
    }


    class locationListener1 implements LocationListener {
        @Override
        public void onLocationChanged(final Location location) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e("luo", "--------------locationType--->:" + locationType);
//                    updateToNewLocation(location, locationType);
//                }
//            });
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
//        不可用的状态
            if (status != LocationProvider.AVAILABLE) {
                switch (status) {
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.e("luo", "-------------provider: " + provider + "  --->  onStatusChanged  ,status --->TEMPORARILY_UNAVAILABLE");
                        break;
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.e("luo", "-------------provider: " + provider + "  --->  onStatusChanged  ,status --->OUT_OF_SERVICE");
                        break;
                }

            }
            Log.e("luo", "-------------provider: " + provider + "  --->  onStatusChanged  ,status --->AVAILABLE");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("位置提供器：", "启用");
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void outToFile(LonLat lonLat) {
        FileOutputStream out = null;
        BufferedWriter write = null;
        try {
            File file = new File(log_file);
            // 判断文件是否存在
            if (!file.exists()) {
                File path = new File(file.getParent());
                if (!path.exists() && !path.mkdirs()) {   // 判断文件夹是否存在，不存在则创建文件夹
                    Toast.makeText(getApplicationContext(), "文件夹创建失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!file.createNewFile()) {    // 创建文件
                    Toast.makeText(getApplicationContext(), "文件创建失败", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            out = new FileOutputStream(file, true);
            write = new BufferedWriter(new OutputStreamWriter(out));
            //获取电池电量
            BatteryManager manager = (BatteryManager) getSystemService(BATTERY_SERVICE);
            int intProperty = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            //写入到文件
            write.write(lonLat.tofileString(intProperty));
            write.close();
        } catch (IOException e) {
            Log.e("luo", "log locaton msg error :" + e);
        }
    }


}

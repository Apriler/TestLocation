package cn.com.test.testlocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;



import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Log.e("a","aaaa");
        //提供位置定位服务的位置管理器对象,中枢控制系统
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //位置提供器，也就是实际上来定位的对象，这里选择的是GPS定位
        String locationProvider = LocationManager.PASSIVE_PROVIDER;
        //获取手机中开启的位置提供器
        List<String> providers = locationManager.getProviders(true);
        //开始定位,获取当前位置对象
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            AndPermission.with(this)
//                    .permission(Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION)
//                    .onGranted(new Action<List<String>>() {
//                        @Override
//                        public void onAction(List<String> data) {
//
//                        }
//                    })
//                    .onDenied(new Action<List<String>>() {
//                        @Override
//                        public void onAction(List<String> data) {
////                            Uri packageURI = Uri.parse("package:" + MyApplication.context.getPackageName());
////                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
////                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                            startActivity(intent);
////                            Toast.makeText(LoginAc.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
//                        }
//                    }).start();

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        while (location == null) {
            //每1s监听一次位置信息，如果位置距离改变超过1m。就执行onLocationChanged方法
            //如果第一次打开没有显示位置信息，可以退出程序重新进入，就会显示
            locationManager.requestLocationUpdates("gps", 1000,1,new locationListener());
        }
        //获取纬度
        Double latitude = location.getLatitude();
        //获取经度
        Double longitude = location.getLongitude();
        Log.e("Latitude", String.valueOf(latitude));
        Log.e("Longitude", String.valueOf(longitude));
    }
}

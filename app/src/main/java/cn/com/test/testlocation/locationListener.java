package cn.com.test.testlocation;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public  class locationListener implements LocationListener {
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e("位置提供器：", "启用");
    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
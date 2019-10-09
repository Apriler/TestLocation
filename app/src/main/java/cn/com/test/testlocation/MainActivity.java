package cn.com.test.testlocation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
                showToast(mBtnWifi.getText().toString());
                break;
            case R.id.btn_gps:
                showToast(mBtnGps.getText().toString());
                break;
        }
    }

    public  void  showToast(String msg){
        if (TextUtils.isEmpty(msg)){
            return;
        }
        Toast.makeText(this.getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }


}

package com.bomeans.irreader;

import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bomeans.IRKit.BIRReader;
import com.bomeans.usbserial.IRUARTCommand;

import java.util.ArrayList;
import java.util.List;

public class LearnFullActivity extends AppCompatActivity implements BomeansUSBDongle.IBomeansUSBDongleCallback {

    private BomeansUSBDongle mUsbDongle;
    private BIRReader mIrReader;
    private Boolean mIsCompressedFormat = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_full);

        // enable the back button on the action bar
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mIsCompressedFormat = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("pref_use_compressed_format", true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mUsbDongle = ((BomeansIrReaderApp) getApplication()).getUsbDongle();
        mIrReader = ((BomeansIrReaderApp) getApplication()).getIrReader();

        if (null != mUsbDongle) {
            mUsbDongle.registerCallback(this);
            updateDeviceStatus(mUsbDongle.isAttached());
        }
    }

    @Override
    protected void onPause() {

        if (null != mUsbDongle) {
            mUsbDongle.unregisterCallback(this);
        }
        super.onPause();
    }

    @Override
    public void onDeviceStatusChanged(Boolean attached) {
        updateDeviceStatus(attached);
    }

    private void updateDeviceStatus(Boolean attached) {
        this.setTitle(
                String.format("%s (%s)", getResources().getString(R.string.app_name),
                        attached ? getResources().getString(R.string.connected) : getResources().getString(R.string.disconnected))
        );

    }


}

package com.bomeans.irreader;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.bomeans.IRKit.*;
import com.splunk.mint.Mint;

/**
 *
 * Created by ray on 16/6/3.
 */
public class BomeansIrReaderApp extends Application {

    // apply your api key and paste it below
    private String API_KEY = "";
    private String MINT_API_KEY = "c4d2eaab";

    private BIRReader mIrReader = null;
    private BomeansUSBDongle mUsbDongle = null;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        Mint.initAndStartSession(this, MINT_API_KEY);

        IRKit.setup(API_KEY, getApplicationContext());

        Boolean useChinaServer = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("pref_select_china_server", false);
        IRKit.setUseChineseServer(useChinaServer);

        /*
        IRKit.createIRReader(false, new BIRReaderCallback() {
            @Override
            public void onReaderCreated(BIRReader birReader) {
                if (null != birReader) {

                    mIrReader = birReader;
                }
            }

            @Override
            public void onReaderCreateFailed() {
                mIrReader = null;
            }
        });*/

        mUsbDongle = new BomeansUSBDongle(this);

        mContext = this.getApplicationContext();
    }

    public BIRReader getIrReader() {
        return mIrReader;
    }
    public void setIrReader(BIRReader irReader) {
        mIrReader = irReader;
    }

    public BomeansUSBDongle getUsbDongle() {
        return mUsbDongle;
    }


    public static Context getContext() {
        return mContext;
    }

    public static String getAppVersion() {

        String appVersion = "";
        PackageManager manager = mContext.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            appVersion = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return appVersion;
    }
}

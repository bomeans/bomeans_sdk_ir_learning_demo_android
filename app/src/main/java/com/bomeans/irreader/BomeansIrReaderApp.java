package com.bomeans.irreader;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.bomeans.IRKit.*;
import com.splunk.mint.Mint;

import java.util.HashMap;

/**
 *
 * Created by ray on 16/6/3.
 */
public class BomeansIrReaderApp extends Application {

    // apply your api key and paste it below
    private String API_KEY = "";    // contact Bomeans Design for a valid API key
    private String MINT_API_KEY = "";   // for crash report, can be ignored.

    private BIRReader mIrReader = null;
    private BomeansUSBDongle mUsbDongle = null;
    private static Context mContext;

    private TypeItemEx[] mTypes = new TypeItemEx[0];
    private HashMap<String, BrandItemEx[]> mBrands = new HashMap<>();

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

    public void setTypes(TypeItem[] types) {
        mTypes = new TypeItemEx[types.length];
        for (int i = 0; i < types.length; i++) {
            mTypes[i] = new TypeItemEx(types[i]);
        }
    }

    public TypeItemEx[] getTypes() { return mTypes; }

    public void setBrands(String typeId, BrandItem[] brands) {
        if (mBrands.containsKey(typeId)) {
            mBrands.remove(typeId);
        }

        BrandItemEx[] brandsEx = new BrandItemEx[brands.length];
        for (int i = 0; i < brands.length; i++) {
            brandsEx[i] = new BrandItemEx(brands[i]);
        }
        mBrands.put(typeId, brandsEx);
    }

    public BrandItemEx[] getBrands(String typeId) {
        return mBrands.get(typeId);
    }
}

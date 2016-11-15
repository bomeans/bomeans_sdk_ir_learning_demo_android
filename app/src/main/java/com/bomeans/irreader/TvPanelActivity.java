package com.bomeans.irreader;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bomeans.IRKit.BIRReader;
import com.bomeans.IRKit.BIRRemote;
import com.bomeans.IRKit.ConstValue;
import com.bomeans.IRKit.IRKit;
import com.bomeans.IRKit.IRemoteCreateCallBack;
import com.bomeans.IRKit.IWebAPICallBack;
import com.bomeans.IRKit.KeyName;
import com.bomeans.usbserial.IRUARTCommand;

import java.util.Locale;

public class TvPanelActivity extends AppCompatActivity implements BomeansUSBDongle.IBomeansUSBDongleCallback {

    private BomeansUSBDongle mUsbDongle;

    private BIRRemote mBirRemote;

    private String mTypeId = null;
    private String mBrandId = null;
    private String mRemoteId = null;

    private ScrollView mScrollView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_panel);

        // enable the back button on the action bar
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            if (getIntent().hasExtra("TYPE_ID")) {
                mTypeId = getIntent().getStringExtra("TYPE_ID");
            }
            if (getIntent().hasExtra("BRAND_ID")) {
                mBrandId = getIntent().getStringExtra("BRAND_ID");
            }
            if (getIntent().hasExtra("REMOTE_ID")) {
                mRemoteId = getIntent().getStringExtra("REMOTE_ID");
            }
        }

        mScrollView = (ScrollView) findViewById(R.id.main_scroll_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }



    @Override
    protected void onResume() {
        super.onResume();

        mUsbDongle = ((BomeansIrReaderApp) getApplication()).getUsbDongle();

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

    private void updateDeviceStatus(Boolean attached) {

        this.setTitle(
                String.format("%s (%s)", mRemoteId == null ? getResources().getString(R.string.app_name) : mRemoteId,
                        mUsbDongle.isAttached() ? getResources().getString(R.string.connected) : getResources().getString(R.string.disconnected))
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        final Activity thisActivity = this;
        if ((null != mTypeId) && (null != mBrandId) && (null != mRemoteId)) {
            IRKit.createRemote(mTypeId, mBrandId, mRemoteId, false, new IRemoteCreateCallBack() {
                @Override
                public void onCreateResult(Object remote, int result) {

                    if (result == ConstValue.BIRNoError) {

                        mBirRemote = (BIRRemote) remote;

                        thisActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createButtons();
                            }
                        });
                    } else {

                        LinearLayout linearLayout = new LinearLayout(thisActivity);
                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        TextView msg = new TextView(thisActivity);
                        Boolean isChinaServer = PreferenceManager
                                .getDefaultSharedPreferences(getApplicationContext())
                                .getBoolean("pref_select_china_server", true);
                        msg.setText(String.format("ERROR LOADING REMOTE!\nIRKit.createRemote(TypeId=%s, BrandId=%s, RemoteId=%s) FAILED!\nServer:%s",
                                mTypeId, mBrandId, mRemoteId, isChinaServer ? "China" : "International"));
                        linearLayout.addView(msg);
                        mScrollView.removeAllViews();
                        mScrollView.addView(linearLayout);
                        mProgressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onPreCreate() {

                }

                @Override
                public void onProgressUpdate(Integer... integers) {

                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void createButtons()
    {
        if ((null == mBirRemote) || (null == mScrollView)) {
            return;
        }

        mScrollView.removeAllViews();

        final Activity thisActivity = this;
        IRKit.webGetKeyName(mTypeId, Locale.getDefault().getLanguage(), false, new IWebAPICallBack() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(Object o, int i) {
                KeyName[] keyNames = (KeyName[]) o;

                LinearLayout linearLayout = new LinearLayout(thisActivity);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                String[] keyIdList = mBirRemote.getAllKeys();
                Button button;
                for (String keyId : keyIdList) {
                    button = new Button(thisActivity);

                    button.setText(keyId);
                    for (KeyName keyName : keyNames) {
                        if (keyName.keyId.equalsIgnoreCase(keyId)) {
                            button.setText(keyName.name);
                            break;
                        }
                    }

                    final String thisKeyId = keyId;
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBirRemote.transmitIR(thisKeyId, null);
                        }
                    });
                    linearLayout.addView(button);
                }

                mScrollView.removeAllViews();
                mScrollView.addView(linearLayout);

                mProgressBar.setVisibility(View.GONE);

            }

            @Override
            public void onProgressUpdate(Integer... integers) {

            }
        });




    }


    @Override
    public void onDeviceStatusChanged(Boolean attached) {
        updateDeviceStatus(attached);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

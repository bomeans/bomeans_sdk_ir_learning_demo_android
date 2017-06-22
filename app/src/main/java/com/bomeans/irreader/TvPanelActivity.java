package com.bomeans.irreader;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ProgressBar;

import com.bomeans.irreader.panel.ACRemotePanel;
import com.bomeans.irreader.panel.IRemotePanel;
import com.bomeans.irreader.panel.IRemotePanelLoadedCallback;
import com.bomeans.irreader.panel.TVRemotePanel;

public class TvPanelActivity extends AppCompatActivity implements BomeansUSBDongle.IBomeansUSBDongleCallback {

    private BomeansUSBDongle mUsbDongle;

    private String mTypeId = null;
    private String mBrandId = null;
    private String mRemoteId = null;
    private Boolean mRefresh = false;

    private ProgressBar mProgressBar;

    private IRemotePanel mRemotePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
            if (getIntent().hasExtra("REFRESH")) {
                mRefresh = getIntent().getBooleanExtra("REFRESH", false);
            }
        }

        if (mTypeId.equalsIgnoreCase("2"/*"AC"*/)) {
            setContentView(R.layout.panel_ac);

            final GridLayout keyPanelView = (GridLayout) findViewById(R.id.remote_keys_layout);
            final GridLayout displayPanelView = (GridLayout) findViewById(R.id.ac_display_panel_layout);

            mRemotePanel = new ACRemotePanel(this, displayPanelView, keyPanelView);

            ViewTreeObserver vto = keyPanelView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    keyPanelView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    if (null != mRemotePanel) {
                        mRemotePanel.loadRemote(mTypeId, mBrandId, mRemoteId, mRefresh, new IRemotePanelLoadedCallback() {

                            @Override
                            public void onPanelLoaded(boolean succeeded) {
                                if (null != mProgressBar) {
                                    mProgressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
            });

        } else {
            setContentView(R.layout.panel_tv);// activity_tv_panel);

            final GridLayout keyPanelView = (GridLayout) findViewById(R.id.remote_keys_layout);

            switch (mTypeId)
            {
                case "1"://"TV":
                    mRemotePanel = new TVRemotePanel(this, keyPanelView);
                    break;

                case "3"://"SETTOP":
                    mRemotePanel = new TVRemotePanel(this, keyPanelView);
                    break;

                case "6"://"MP3":
                    mRemotePanel = new TVRemotePanel(this, keyPanelView);
                    break;

                case "4"://"AMP":
                    mRemotePanel = new TVRemotePanel(this, keyPanelView);
                    break;

                case "5"://"DVD":
                    mRemotePanel = new TVRemotePanel(this, keyPanelView);
                    break;

                case "7"://"GAME":
                    mRemotePanel = new TVRemotePanel(this, keyPanelView);
                    break;

                case "8"://"FAN":
                    mRemotePanel = new TVRemotePanel(this, keyPanelView);
                    break;

                case "9"://"PJ"
                    mRemotePanel = new TVRemotePanel(this, keyPanelView);
                    break;

                case "10"://"ROBOT":
                    mRemotePanel = new TVRemotePanel(this, keyPanelView);
                    break;

                case "11"://"LIGHTING":
                    mRemotePanel = new TVRemotePanel(this, keyPanelView);
                    break;

                default:
                    mRemotePanel = new TVRemotePanel(this, keyPanelView);
                    break;
            }

            ViewTreeObserver vto = keyPanelView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    keyPanelView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    if (null != mRemotePanel) {
                        mRemotePanel.loadRemote(mTypeId, mBrandId, mRemoteId, mRefresh, new IRemotePanelLoadedCallback() {

                            @Override
                            public void onPanelLoaded(boolean succeeded) {
                                if (null != mProgressBar) {
                                    mProgressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
            });
        }

        // enable the back button on the action bar
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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
        return super.onCreateOptionsMenu(menu);
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

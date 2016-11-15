package com.bomeans.irreader;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bomeans.IRKit.BIRReadFirmwareVersionCallback;
import com.bomeans.IRKit.BIRReader;
import com.bomeans.IRKit.BIRReaderCallback;
import com.bomeans.IRKit.IRKit;
import com.bomeans.usbserial.IRUARTCommand;

public class MainActivity extends AppCompatActivity implements BomeansUSBDongle.IBomeansUSBDongleCallback {

    private BomeansUSBDongle mUsbDongle;
    private TextView mMessageText;
    private TextView mVersionInfo;
    private Button mLearnAndRecognizeButton;
    private Button mReloadFormatsButton;
    private ProgressBar mProgressBar;

    private boolean mDebugFunctions = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mVersionInfo = (TextView) findViewById(R.id.version_text);
        mVersionInfo.setText(String.format("%s: %s",
                getResources().getString(R.string.sw_ver), BomeansIrReaderApp.getAppVersion()));

        mMessageText = (TextView) findViewById(R.id.text_device_status);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (null != fab) {

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }*/

        // broadcast receiver for USB device detached
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);

        // learn and transmit
        Button learnAndTestButton = (Button) findViewById(R.id.learn_n_test_button);
        if (null != learnAndTestButton) {
            learnAndTestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, LearnAndSendActivity.class);
                    MainActivity.this.startActivity(intent);
                }
            });
        }

        // learn and recognize
        mLearnAndRecognizeButton = (Button) findViewById(R.id.learn_n_recognize_button);
        if (null != mLearnAndRecognizeButton) {

            mLearnAndRecognizeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, LearnAndRecognizeActivity.class);
                    MainActivity.this.startActivity(intent);
                }
            });
        }
        mLearnAndRecognizeButton.setEnabled(false);

        // test function
        TextView testButtonDesc = (TextView) findViewById(R.id.test_button_desc);
        Button testButton = (Button) findViewById(R.id.test_button);
        if (null != testButton) {
            testButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, TestActivity.class);
                    MainActivity.this.startActivity(intent);
                }
            });
        }
        testButtonDesc.setVisibility(mDebugFunctions ? View.VISIBLE : View.GONE);
        testButton.setVisibility(mDebugFunctions ? View.VISIBLE : View.GONE);


        // reload formats
        mReloadFormatsButton = (Button) findViewById(R.id.button_reload);
        if (null != mReloadFormatsButton) {
            mReloadFormatsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadIrReader(true);
                }
            });
        }

        mUsbDongle = ((BomeansIrReaderApp) getApplication()).getUsbDongle();
        IRKit.setIRHW(mUsbDongle);

        loadIrReader(false);

    }

    private void loadIrReader(Boolean forceReload) {
        BIRReader irReader = ((BomeansIrReaderApp) getApplication()).getIrReader();
        if (null == irReader || forceReload) {
            mMessageText.setText(getResources().getString(R.string.loading_pls_wait));
            mMessageText.setTextColor(Color.RED);
            mProgressBar.setVisibility(View.VISIBLE);
            mReloadFormatsButton.setVisibility(View.GONE);
            mLearnAndRecognizeButton.setEnabled(false);
            IRKit.createIRReader(forceReload, new BIRReaderCallback() {
                @Override
                public void onReaderCreated(BIRReader birReader) {
                    if (null != birReader) {

                        ((BomeansIrReaderApp) getApplication()).setIrReader(birReader);
                        mMessageText.setText(getResources().getString(R.string.load_ok));
                        mMessageText.setTextColor(Color.BLACK);
                        mLearnAndRecognizeButton.setEnabled(true);
                        mProgressBar.setVisibility(View.GONE);
                        mReloadFormatsButton.setVisibility(View.VISIBLE);
                        
                        /*// debug
                        byte[] data = {
                                (byte)0xFF, 0x61, 0x00, (byte)0x8D, 0x00, (byte)0x92, (byte)0xCF, (byte)0x92,
                                0x56, 0x06, 0x26, 0x46, 0x30, 0x23, 0x3C, 0x02,
                                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                0x00, 0x00, 0x00, 0x00, (byte)0x94, 0x11, (byte)0x94, 0x06,
                                0x2C, 0x02, 0x03, (byte)0x94, 0x00, 0x00, 0x00, 0x00,
                                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                0x00, 0x00, 0x00, 0x00, 0x00, 0x11, 0x21, 0x31,
                                (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                                (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, 0x66, 0x00, 0x10, 0x21,
                                0x22, 0x22, 0x11, 0x12, 0x12, 0x12, 0x22, 0x22,
                                0x12, 0x22, 0x11, 0x11, 0x21, 0x11, 0x32, 0x10,
                                0x21, 0x22, 0x22, 0x11, 0x12, 0x12, 0x12, 0x22,
                                0x22, 0x12, 0x22, 0x11, 0x11, 0x21, 0x11, 0x32,
                                0x10, 0x21, 0x22, 0x22, 0x11, 0x12, 0x12, 0x12,
                                0x22, 0x22, 0x12, 0x22, 0x11, 0x11, 0x21, 0x11,
                                0x32, (byte)0xC8, (byte)0xF0};
                        birReader.load(data, false, true);
                        birReader.getFrequency();*/
                    }
                }

                @Override
                public void onReaderCreateFailed() {
                    mMessageText.setText(getResources().getString(R.string.load_failed));
                    mMessageText.setTextColor(Color.RED);
                    mLearnAndRecognizeButton.setEnabled(false);
                    mProgressBar.setVisibility(View.GONE);
                    mReloadFormatsButton.setVisibility(View.VISIBLE);
                }
            });
        } else {
            mMessageText.setText(getResources().getString(R.string.load_ok));
            mMessageText.setTextColor(Color.BLACK);
            mLearnAndRecognizeButton.setEnabled(true);
            mProgressBar.setVisibility(View.GONE);
            mReloadFormatsButton.setVisibility(View.VISIBLE);
        }
    }

    private void updateUsbState() {

        if (null != mUsbDongle) {
            mUsbDongle.rescan();

            this.setTitle(
                    String.format("%s (%s)", getResources().getString(R.string.app_name),
                            mUsbDongle.isAttached() ? getResources().getString(R.string.connected) : getResources().getString(R.string.disconnected))
            );

            if (mUsbDongle.isAttached()) {
                mUsbDongle.registerCallback(this);
                sendVersionCommand();
            }
        }
    }

    private Boolean sendVersionCommand() {
        if (null != mUsbDongle && mUsbDongle.isAttached()) {

            final Activity thisActivity = this;
            IRKit.getIrBlasterFirmwareVersion(new BIRReadFirmwareVersionCallback() {
                @Override
                public void onFirmwareVersionReceived(final String version) {

                    thisActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String info = String.format("%s: %s\n%s: %s",
                                        getResources().getString(R.string.sw_ver),
                                        BomeansIrReaderApp.getAppVersion(),
                                        getResources().getString(R.string.fw_ver),
                                        version);
                                mVersionInfo.setText(info);
                            }
                        });
                }
            });

            /*IRUARTCommand uartCmd =
                    new IRUARTCommand(IRUARTCommand.IR_UART_COMMAND_FIRMWARE_VERSION, null);
            return mUsbDongle.sendCommand(uartCmd);*/
        }
        return false;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


        updateUsbState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // BroadcastReceiver when remove the device USB plug from a USB port
    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                updateUsbState();
            }
        }
    };

    /*
    @Override
    public void onCommandReceived(IRUARTCommand uartCommand) {

        if (null != uartCommand && uartCommand.isValid()) {
            byte cmdId = uartCommand.getCommandID();

            if (cmdId == IRUARTCommand.IR_UART_COMMAND_FIRMWARE_VERSION_RESPONSE) {

                byte[] payload = uartCommand.getPayload();
                if (null != payload && payload.length > 0) {
                    final String version = new String(payload);

                    switchLearningFormatDependsOnVersionNumber(version);

                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String info = String.format("%s: %s\n%s: %s",
                                    getResources().getString(R.string.sw_ver),
                                    BomeansIrReaderApp.getAppVersion(),
                                    getResources().getString(R.string.fw_ver),
                                    version);
                            mVersionInfo.setText(info);
                        }
                    });
                }
            }
        }
    }

    private void switchLearningFormatDependsOnVersionNumber(String version) {
        PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putBoolean("pref_use_compressed_format", version.startsWith("IR Reader_c") || version.startsWith("IR Readr_c"))
                .commit();
    }*/

    @Override
    public void onDeviceStatusChanged(Boolean attached) {

        mVersionInfo.setText(String.format("%s: %s",
                getResources().getString(R.string.sw_ver), BomeansIrReaderApp.getAppVersion()));

        if (attached) {
            this.setTitle(
                    String.format("%s (%s)",
                            getResources().getString(R.string.app_name),
                            getResources().getString(R.string.connected))
            );
        } else {
            this.setTitle(
                    String.format("%s (%s)",
                            getResources().getString(R.string.app_name),
                            getResources().getString(R.string.disconnected))
            );
        }

    }
}

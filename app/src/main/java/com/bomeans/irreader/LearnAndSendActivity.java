package com.bomeans.irreader;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bomeans.IRKit.BIRReader;

import java.util.Timer;
import java.util.TimerTask;

public class LearnAndSendActivity extends AppCompatActivity implements BomeansUSBDongle.IBomeansUSBDongleCallback {

    private BomeansUSBDongle mUsbDongle;
    private BIRReader mIrReader;

    private Button mSendButton;
    private Button mLearnButton;
    private Button mSaveButton;
    private TextView mLearnResultText;
    private ProgressBar mProgressBar;
    private Timer mLearningTimer;

    private byte[] mLearnedDataForSending;  // ir signal data wrapping in Bomeans UART command format

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_and_send);

        // enable the back button on the action bar
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mLearnResultText = (TextView) findViewById(R.id.learn_result);

        mLearnButton = (Button) findViewById(R.id.learn_button);
        mLearnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sendLearningCommand()) {
                    mLearnResultText.setText(R.string.waiting_for_signal);
                } else {
                    mLearnResultText.setText(R.string.failed_retry);
                }

                mSendButton.setEnabled(false);
            }
        });

        mSendButton = (Button) findViewById(R.id.transmit_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mLearnedDataForSending != null) {

                    if (null != mIrReader) {
                        mIrReader.sendLearningData(mLearnedDataForSending);
                    }
                }
            }
        });

        mSaveButton = (Button) findViewById(R.id.remember_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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

        mLearnButton.setEnabled(attached);
        mSendButton.setEnabled(attached);
        mSaveButton.setEnabled(attached);
    }

    private Boolean sendLearningCommand() {
        if (null != mUsbDongle && mUsbDongle.isAttached()) {

            // show progress bar
            mProgressBar.setVisibility(View.GONE);
            if (null != mLearningTimer) {
                mLearningTimer.cancel();
            }

            mLearnedDataForSending = null;

            final Activity thisActivity = this;
            if (null != mIrReader) {

                mProgressBar.setVisibility(View.VISIBLE);
                mLearningTimer = new Timer(true);
                mLearningTimer.schedule(new LearningTimerTask(), 0, 1000);

                mIrReader.stopLearning();

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mIrReader.startLearningAndGetData(BIRReader.PREFER_REMOTE_TYPE.Auto, new BIRReader.BIRReaderFormatMatchCallback() {
                    @Override
                    public void onFormatMatchSucceeded(final BIRReader.ReaderMatchResult readerMatchResult) {

                        thisActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String info = mLearnResultText.getText() + "\n";

                                if (readerMatchResult.isAc()) {
                                    info += String.format("AC: %s\n",
                                            readerMatchResult.formatId);
                                } else {
                                    info += String.format("TV: %s, C: 0x%X, K: 0x%X\n",
                                            readerMatchResult.formatId,
                                            readerMatchResult.customCode,
                                            readerMatchResult.keyCode);
                                }

                                mLearnResultText.setText(info);
                            }
                        });
                    }

                    @Override
                    public void onFormatMatchFailed(BIRReader.FormatParsingErrorCode errorCode) {

                        thisActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String info = mLearnResultText.getText() + "\n";
                                info += getResources().getString(R.string.uncognized_format);
                                mLearnResultText.setText(info);
                            }
                        });
                    }

                    @Override
                    public void onLearningDataReceived(final byte[] learningDataBytes) {

                        mLearnedDataForSending = learningDataBytes;

                        final int waveCount = mIrReader.getWaveCount();
                        final int frequency = mIrReader.getFrequency();

                        thisActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                mProgressBar.setVisibility(View.GONE);
                                mLearningTimer.cancel();
                                mSendButton.setEnabled(true);

                                String info = getResources().getString(R.string.learn_ok) + "\n";
                                info += String.format("signal count: %d (%dHz)", waveCount, frequency);
                                mLearnResultText.setText(info);
                            }
                        });
                    }

                    @Override
                    public void onLearningDataFailed(BIRReader.LearningErrorCode errorCode) {

                        thisActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mProgressBar.setVisibility(View.GONE);
                                mLearningTimer.cancel();
                                mSendButton.setEnabled(false);

                                mLearnResultText.setText(R.string.learn_fail);
                            }
                        });
                    }
                });

                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private Boolean saveBytesToFile(String fileName, byte[] data) {
        return true;
    }

    private byte[] readBytesFromFile(String fileName) {
        return null;
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

    class LearningTimerTask extends TimerTask {

        int mSecCount = 0;

        @Override
        public void run() {
            mProgressBar.setProgress(mSecCount);
            mSecCount++;
            if (mSecCount > 15) {
                this.cancel();
            }
        }
    }
}

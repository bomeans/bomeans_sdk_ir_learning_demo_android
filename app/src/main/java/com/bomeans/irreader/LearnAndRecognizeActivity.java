package com.bomeans.irreader;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bomeans.IRKit.BIRReader;
import com.bomeans.IRKit.IRKit;
import com.bomeans.IRKit.IRReader;

import java.util.List;
import java.util.Locale;

public class LearnAndRecognizeActivity extends AppCompatActivity implements BomeansUSBDongle.IBomeansUSBDongleCallback {

    private BomeansUSBDongle mUsbDongle;
    private BIRReader mIrReader;

    private TextView mMessageText;
    private Button mLearningButton;
    private ScrollView mScrollView;
    private RadioGroup mTypeRadioGroup;

    private Boolean mIsLearning = true;//false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_and_recognize);

        // enable the back button on the action bar
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mTypeRadioGroup = (RadioGroup) findViewById(R.id.type_group);
        mTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                initLearning();
            }
        });

        mMessageText = (TextView) findViewById(R.id.text_message_view);
        mScrollView = (ScrollView) findViewById(R.id.scroll_view);
        mLearningButton = (Button) findViewById(R.id.start_learning_button);
        mLearningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initLearning();
            }
        });
    }

    private void initLearning() {

        if (null != mIrReader) {
            mIrReader.reset();
        }

        //sendStopLearningCommand();

        mMessageText.setText("");
        mScrollView.removeAllViews();

        /*try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        mIsLearning = true;
        updateLearningState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mUsbDongle = ((BomeansIrReaderApp) getApplication()).getUsbDongle();
        mIrReader = ((BomeansIrReaderApp) getApplication()).getIrReader();

        if (null != mIrReader) {
            mIrReader.reset();
        }

        if (null != mUsbDongle) {
            mUsbDongle.registerCallback(this);

            updateDeviceStatus(mUsbDongle.isAttached());
        }
    }

    @Override
    protected void onPause() {

        mIsLearning = false;

        sendStopLearningCommand();

        if (null != mUsbDongle) {
            mUsbDongle.unregisterCallback(this);
        }

        super.onPause();
    }

    private void updateLearningState() {

        if (mIsLearning) {

            if (sendLearningCommand()) {
                mLearningButton.setText(R.string.button_restart_learning);
            } else {
                mLearningButton.setText(R.string.button_start_learning);
            }

        } else {

            sendStopLearningCommand();

            mMessageText.setText("");
            mLearningButton.setText(R.string.button_start_learning);
        }
    }

    private Boolean sendLearningCommand() {
        if (null != mUsbDongle && mUsbDongle.isAttached()) {

            final Activity thisActivity = this;

            BIRReader.PREFER_REMOTE_TYPE preferRemoteType;
            // get the current selected type
            int selectedTypeId = mTypeRadioGroup.getCheckedRadioButtonId();
            switch (selectedTypeId) {
                case R.id.type_ac:
                    preferRemoteType = BIRReader.PREFER_REMOTE_TYPE.AC;
                    break;
                case R.id.type_tv:
                    preferRemoteType = BIRReader.PREFER_REMOTE_TYPE.TV;
                    break;
                case R.id.type_auto:
                default:
                    preferRemoteType = BIRReader.PREFER_REMOTE_TYPE.Auto;
                    break;
            }


            mIrReader.startLearningAndSearchCloud(false, preferRemoteType, new BIRReader.BIRReaderRemoteMatchCallback() {
                @Override
                public void onRemoteMatchSucceeded(final List<BIRReader.RemoteMatchResult> list) {

                    thisActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            showMatchResult(list);
                        }
                    });

                }

                @Override
                public void onRemoteMatchFailed(BIRReader.CloudMatchErrorCode errorCode) {

                    thisActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            showMatchResult(null);
                        }
                    });
                }

                @Override
                public void onFormatMatchSucceeded(final List<BIRReader.ReaderMatchResult> list) {

                    if (mIsLearning) {
                        sendLearningCommand();
                    }

                    thisActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            showUsedResults(list);

                            LinearLayout linearLayout = new LinearLayout(thisActivity);
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            ProgressBar progBar = new ProgressBar(thisActivity);
                            linearLayout.addView(progBar);
                            mScrollView.removeAllViews();
                            mScrollView.addView(linearLayout);
                        }
                    });
                }

                @Override
                public void onFormatMatchFailed(final BIRReader.FormatParsingErrorCode errorCode) {

                    if (mIsLearning) {
                        sendLearningCommand();
                    }

                    thisActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (errorCode == BIRReader.FormatParsingErrorCode.UnrecognizedFormat) {
                                mMessageText.setText(R.string.uncognized_format);
                            }/* else {
                                mMessageText.setText(R.string.learn_fail);
                            }*/
                        }
                    });
                }
            });

            return true;
        }
        return false;
    }

    private Boolean sendStopLearningCommand() {

        if (null != mIrReader) {
            int result = mIrReader.stopLearning();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return (IRKit.BIROK == result);
        }

        return false;
    }

    private void showMatchResult(final List<BIRReader.RemoteMatchResult> matchResultList) {

        final Activity thisActivity = this;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mScrollView.removeAllViews();

                LinearLayout linearLayout = new LinearLayout(thisActivity);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                if ((null != matchResultList) && (matchResultList.size() > 0)) {

                    TextView textView = new TextView(thisActivity);
                    textView.setText(String.format(Locale.US, "%s: %d", getResources().getString(R.string.match_remotes), matchResultList.size()));
                    linearLayout.addView(textView);

                    for (BIRReader.RemoteMatchResult result : matchResultList) {
                        Button button = new Button(thisActivity);
                        button.setText(result.modelId);
                        final String remoteId = result.modelId;
                        final String typeId = result.typeId;
                        final String brandId = result.brandId;
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(thisActivity, TvPanelActivity.class);
                                intent.putExtra("TYPE_ID", typeId);
                                intent.putExtra("BRAND_ID", brandId);
                                intent.putExtra("REMOTE_ID", remoteId);
                                startActivity(intent);
                            }
                        });
                        linearLayout.addView(button);
                    }
                } else {

                    TextView textView = new TextView(thisActivity);
                    textView.setText(R.string.no_remote_match);
                    linearLayout.addView(textView);
                }

                mScrollView.removeAllViews();
                mScrollView.addView(linearLayout);

            }
        });
    }

    private void showUsedResults(List<IRReader.ReaderMatchResult> resultList) {
        String info = "Learn OK\n";
        for (IRReader.ReaderMatchResult result : resultList) {
            if (result.isAc()) {
                info += String.format("%s (AC)\n", result.formatId);
            } else {
                info += String.format("%s, C:%X, K:%X\n", result.formatId, result.customCode, result.keyCode);
            }
        }

        mMessageText.setText(info);
    }

    private void updateDeviceStatus(Boolean attached) {

        this.setTitle(
        String.format("%s (%s)", getResources().getString(R.string.app_name),
                attached ? getResources().getString(R.string.connected) : getResources().getString(R.string.disconnected))
        );

        mIsLearning = attached;
        mLearningButton.setEnabled(mIsLearning);
        updateLearningState();
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

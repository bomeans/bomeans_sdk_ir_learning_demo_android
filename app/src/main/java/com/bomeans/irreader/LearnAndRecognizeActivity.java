package com.bomeans.irreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
    private CheckBox mChkFilter;
    private TextView mTextType;
    private TextView mTextBrand;

    private TypeItemEx mSelectedType = null;
    private BrandItemEx mSelectedBrand = null;

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

        mChkFilter = (CheckBox) findViewById(R.id.check_filter);
        mTextType = (TextView) findViewById(R.id.text_category);
        mTextBrand = (TextView) findViewById(R.id.text_brand);

        mChkFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextType.setEnabled(mChkFilter.isChecked());
                mTextBrand.setEnabled(mChkFilter.isChecked());
            }
        });

        final Activity thisActivity = this;
        mTextType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mChkFilter.isChecked()) {
                    return;
                }

                TypeItemEx[] types = ((BomeansIrReaderApp) getApplication()).getTypes();
                if ((null == types) || (types.length == 0)) {
                    Toast.makeText(thisActivity, "Types are not available!", Toast.LENGTH_SHORT).show();
                    return;
                }

                showTypeDialog(types);
            }
        });
        mTextBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mChkFilter.isChecked()) {
                    return;
                }

                if (null == mSelectedType) {
                    Toast.makeText(thisActivity, "Select type first!", Toast.LENGTH_SHORT).show();
                    return;
                }

                BrandItemEx[] brands = ((BomeansIrReaderApp) getApplication()).getBrands(mSelectedType.typeId);
                if ((null == brands) || (brands.length == 0)) {
                    Toast.makeText(thisActivity, "No brands are available!", Toast.LENGTH_SHORT).show();
                    return;
                }

                showBrandDialog(brands);
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

                // filtering?
                if ((null != matchResultList) && (matchResultList.size() > 0)) {
                    if (mChkFilter.isChecked()) {

                        // filter type
                        if (null != mSelectedType) {
                            for (int i = matchResultList.size() - 1; i >= 0; i--) {
                                if (!matchResultList.get(i).typeID.equals(mSelectedType.typeId)) {
                                    matchResultList.remove(i);
                                }
                            }
                        }

                        // filter brand
                        if (null != mSelectedBrand) {
                            for (int i = matchResultList.size() - 1; i >= 0; i--) {
                                if (!matchResultList.get(i).brandID.equals(mSelectedBrand.brandId)) {
                                    matchResultList.remove(i);
                                }
                            }
                        }
                    }
                }

                if ((null != matchResultList) && (matchResultList.size() > 0)) {

                    TextView textView = new TextView(thisActivity);
                    textView.setText(String.format(Locale.US, "%s: %d", getResources().getString(R.string.match_remotes), matchResultList.size()));
                    linearLayout.addView(textView);

                    for (BIRReader.RemoteMatchResult result : matchResultList) {
                        Button button = new Button(thisActivity);
                        button.setText(result.modelID);
                        final String remoteId = result.modelID;
                        final String typeId = result.typeID;
                        final String brandId = result.brandID;
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(thisActivity, TvPanelActivity.class);
                                intent.putExtra("TYPE_ID", typeId);
                                intent.putExtra("BRAND_ID", brandId);
                                intent.putExtra("REMOTE_ID", remoteId);
                                intent.putExtra("REFRESH", false);
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

    private void showTypeDialog(final TypeItemEx[] types) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.select_type_dialog, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle(R.string.type);
        ListView lv = (ListView) convertView.findViewById(R.id.list_view);
        final AlertDialog dialog = alertDialog.show();

        ArrayAdapter<TypeItemEx> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, types);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mSelectedType = types[position];
                mTextType.setText(String.format("%s: %s", getResources().getString(R.string.type), mSelectedType.toString()));

                if (mSelectedType.name.equalsIgnoreCase("AC")) {
                    mTypeRadioGroup.check(R.id.type_ac);
                } else {
                    mTypeRadioGroup.check(R.id.type_tv);
                }
                dialog.dismiss();
            }
        });

    }

    private void showBrandDialog(final BrandItemEx[] brands) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.select_brand_dialog, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle(R.string.brand);
        ListView lv = (ListView) convertView.findViewById(R.id.list_view);
        EditText et = (EditText) convertView.findViewById(R.id.text_filter);
        final AlertDialog dialog = alertDialog.show();

        final ArrayAdapter<BrandItemEx> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brands);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mSelectedBrand = (BrandItemEx) parent.getItemAtPosition(position);;
                mTextBrand.setText(String.format("%s: %s", getResources().getString(R.string.brand), mSelectedBrand.toString()));
                dialog.dismiss();
            }
        });

        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}

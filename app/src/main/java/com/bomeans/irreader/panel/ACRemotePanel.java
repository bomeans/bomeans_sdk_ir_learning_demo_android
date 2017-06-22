package com.bomeans.irreader.panel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bomeans.IRKit.BIRGUIFeature;
import com.bomeans.IRKit.BIRRemote;
import com.bomeans.IRKit.ConstValue;
import com.bomeans.IRKit.IRKit;
import com.bomeans.IRKit.IRemoteCreateCallBack;
import com.bomeans.IRKit.IWebAPICallBack;
import com.bomeans.IRKit.KeyName;
import com.bomeans.irreader.R;
import com.bomeans.irreader.panel.keydef.DefaultACKeys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by ray on 2017/6/21.
 */

public class ACRemotePanel extends BaseRemotePanel {

    private GridLayout mDisplayPanelView;
    private GridLayout mKeyPanelView;
    private ACDisplay mAcDisplay;

    private static AbstractDefaultKeys mPredefinedKeys = null;

    private BIRRemote mBirRemote;
    private KeyName[] mKeyNames;

    private String mTypeId;
    private String mBrandId;
    private String mRemoteId;

    private int mOnTimerHour = 0;
    private int mOnTimerMinute = 0;
    private int mOffTimerHour = 0;
    private int mOffTimerMinute = 0;

    private IRemotePanelLoadedCallback mCallback = null;

    private enum TimerType {
        ON_TIMER,
        OFF_TIMER
    }

    public ACRemotePanel(Context context,
                         GridLayout displayPanelView,
                         GridLayout keyPanelView) {
        super(context);

        mDisplayPanelView = displayPanelView;
        mKeyPanelView = keyPanelView;
    }

    @Override
    protected AbstractDefaultKeys getDefaultKeys() {
        return new DefaultACKeys(getContext(), mKeyNames);
    }

    private String getLanguageCode() {

        return Locale.getDefault().getCountry().toLowerCase(Locale.US);
    }

    @Override
    public boolean loadRemote(String typeId, String brandId, String remoteId, boolean getNew, IRemotePanelLoadedCallback callback) {

        mTypeId = typeId;
        mBrandId = brandId;
        mRemoteId = remoteId;
        mCallback = callback;

        loadKeyNames(getNew);

        return true;
    }

    private void loadKeyNames(final boolean getNew) {

        IRKit.webGetKeyName("2", getLanguageCode(), getNew, new IWebAPICallBack() {

            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(Object o, int i) {
                mKeyNames = (KeyName[]) o;

                _loadRemote(mTypeId, mBrandId, mRemoteId, getNew);
            }

            @Override
            public void onProgressUpdate(Integer... integers) {

            }
        });
    }

    private boolean _loadRemote(String typeId, String brandId, String remoteId, boolean getNew) {

        if ((null != typeId) && (null != brandId) && (null != remoteId)) {
            IRKit.createRemote(typeId, brandId, remoteId, getNew, new IRemoteCreateCallBack() {
                @Override
                public void onCreateResult(Object remote, int result) {

                    if (result == ConstValue.BIRNoError) {

                        mBirRemote = (BIRRemote) remote;

                        ((Activity)getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createRemoteLayoutCallback();

                                if (null != mCallback) {
                                    mCallback.onPanelLoaded(true);
                                }
                            }
                        });
                    } else {
                        if (null != mCallback) {
                            mCallback.onPanelLoaded(false);
                        }
                    }
                }

                @Override
                public void onPreCreate() {

                }

                @Override
                public void onProgressUpdate(Integer... integers) {

                }
            });

            return true;
        }

        return false;
    }

    @Override
    public void createRemoteLayoutCallback() {

        super.createRemoteLayoutCallback();

        if (null == mBirRemote) {
            return;
        }

        if (null == mPredefinedKeys) {
            mPredefinedKeys = getDefaultKeys();
        }

        if (mBirRemote.getGuiFeature().displayType == BIRGUIFeature.BIRGuiDisplayType_NO) {
            mDisplayPanelView.setVisibility(View.GONE);
        } else {
            addDisplayPanelComponents(mDisplayPanelView);
            mDisplayPanelView.setVisibility(View.VISIBLE);
        }

        addKeyPanelComponents(mKeyPanelView);
    }

    private void addDisplayPanelComponents(GridLayout layout) {

        if (null == mBirRemote) {
            return;
        }

        String[] keyIdList = mBirRemote.getAllKeys();

        if (null == mAcDisplay) {
            mAcDisplay = new ACDisplay(layout, mPredefinedKeys, mBirRemote);
        }
        mAcDisplay.init(keyIdList);

        updateACDisplay();

    }

    private void addKeyPanelComponents(GridLayout layout) {

        if (null == mBirRemote) {
            return;
        }

        ArrayList<String> keyIdArray = new ArrayList<> (Arrays.asList(mBirRemote.getAllKeys()));

        boolean bFound;
        String keyId;
        RemoteKey guiKey;
        for (int i = 0; i < mPredefinedKeys.getCount(); i++) {
            bFound = false;
            guiKey = mPredefinedKeys.getAt(i);
            keyId = guiKey.getKeyId();
            for (String tmpKeyId : keyIdArray) {
                if (tmpKeyId.equalsIgnoreCase(keyId)) {
                    addKeyButton(keyId, guiKey, layout);
                    keyIdArray.remove(tmpKeyId);

                    bFound = true;
                    break;
                }
            }

            if (!bFound) {
                // add a dummy button
                addDummyKeyButton(guiKey.getKeyName(), layout);
            }
        }

        // reminding keys
        for (String tmpKeyId : keyIdArray) {
            addKeyButton(tmpKeyId, mPredefinedKeys.getKeyById(tmpKeyId), layout);
        }
    }

    private void addDummyKeyButton(String keyName, GridLayout layout) {

        TextView newButton = new TextView(getContext());
        newButton.setTextSize(1);
        newButton.setText(R.string.dummy);
        newButton.setPadding(0, 0, 0, 0);

        newButton.setLayoutParams(getLayoutParams(layout));

        layout.addView(newButton);
    }

    private void addKeyButton(
            final String keyId,
            final RemoteKey guiKey,
            GridLayout layout) {

        Button newButton = new Button(getContext(), null, R.style.key_button);
        newButton.setBackgroundResource(R.drawable.key_button_ac);

        // not to wrap the text
        newButton.setHorizontallyScrolling(true);

        //newButton.setText(keyId);
        // set key name text / image
        boolean bIsUnknownKey = false;
        if (null != guiKey) {

            newButton.setText(guiKey.getKeyName());
			/*
			if (guiKey.hasDrawable()) {
				newButton.setCompoundDrawablesWithIntrinsicBounds(
						mFragment.getResources().getDrawable(guiKey.getDrawableId()),
						null,
						null,
						null);
			}
			*/
        } else {
            String keyName = keyId;

            // get the key name, if any
            for (KeyName tmpKeyName : mKeyNames) {
                if (tmpKeyName.keyId.equalsIgnoreCase(keyId)) {
                    keyName = tmpKeyName.name;
                    break;
                }
            }

            if (keyName.contains("IR_ACKEY_")) {
                keyName = keyName.substring(9);
            }

            newButton.setText(keyName);
            bIsUnknownKey = true;
        }

        newButton.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
        newButton.setShadowLayer(2, -1, -1, R.color.Black);
        if (bIsUnknownKey) {
            newButton.setTextColor(getContext().getResources().getColor(R.color.Red));
        } else {
            newButton.setTextColor(getContext().getResources().getColor(R.color.DimGray));
        }
        newButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        newButton.setPadding(0, 0, 0, 0);

        // set click listener
        newButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null == mBirRemote) {
                    return;
                }

                sendIR(keyId, null);
            }

        });

        // set long click listener
        newButton.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                if (keyId.equalsIgnoreCase("IR_ACKEY_ONTIMER") ||
                        keyId.equalsIgnoreCase("IR_ACKEY_OFFTIMER") ||
                        keyId.equalsIgnoreCase("IR_ACKEY_TIMER")) {
                    showTimerKeyOptions(keyId, guiKey);
                }
				/*
				if (keyId.equalsIgnoreCase("IR_ACKEY_ONTIMER")) {
					showTimerPickerDialog(TimerType.ON_TIMER);
					return true;

				} else if (keyId.equalsIgnoreCase("IR_ACKEY_OFFTIMER")) {
					showTimerPickerDialog(TimerType.OFF_TIMER);
					return true;

				} else if (keyId.equalsIgnoreCase("IR_ACKEY_TIMER")) {
					// IR_ACKEY_TIMER is a combo key with 3 options: ON_TIMER_ON, OFF_TIMER_ON, and TIMER_OFF

					showTimerPickerDialog(TimerType.ON_TIMER);
					showTimerPickerDialog(TimerType.OFF_TIMER);
					return true;

				}*/ else {

                    // show key options
                    showKeyOptions(keyId, guiKey);
                }

                return true;
            }

        });

        // button size
        /*
        RelativeLayout.LayoutParams lprams = new RelativeLayout.LayoutParams(
               RelativeLayout.LayoutParams.WRAP_CONTENT,
               mDefaultButtonHeight);
        lprams.width = (layout.getWidth() / layout.getColumnCount()) - lprams.rightMargin - lprams.leftMargin;
        */
        newButton.setLayoutParams(getLayoutParams(layout));

        layout.addView(newButton);
    }

    private GridLayout.LayoutParams getLayoutParams(GridLayout layout) {

        GridLayout.LayoutParams lprams = new GridLayout.LayoutParams();
        lprams.width = (layout.getWidth() / (layout.getColumnCount()) - 10);
        lprams.height = lprams.width / 3;
        lprams.topMargin = 20;
        lprams.bottomMargin = 20;
        lprams.leftMargin = 5;
        lprams.rightMargin = 5;

        return lprams;
    }

    private void showTimerKeyOptions(final String keyId, RemoteKey guiKey) {

        boolean bHasOnTimer = false;
        boolean bHasOffTimer = false;

        if (keyId.equalsIgnoreCase("IR_ACKEY_ONTIMER")) {
            bHasOnTimer = true;

        } else if (keyId.equalsIgnoreCase("IR_ACKEY_OFFTIMER")) {
            bHasOffTimer = true;

        } else if (keyId.equalsIgnoreCase("IR_ACKEY_TIMER")) {
            // IR_ACKEY_TIMER is a combo key with 3 options: ON_TIMER_ON, OFF_TIMER_ON, and TIMER_OFF
            bHasOnTimer = true;
            bHasOffTimer = true;

        }

        ArrayList<String> options = new ArrayList<>(Arrays.asList(mBirRemote.getKeyOption(keyId).options));
        ArrayList<String> newOptions = new ArrayList<>(Arrays.asList(getOptionNames(mBirRemote.getKeyOption(keyId).options, guiKey)));

        if (bHasOnTimer) {
            options.add("TMP_SET_ONTIMER_TIME");
            newOptions.add(getResourceString(R.string.SetOnTimerTime));
        }

        if (bHasOffTimer) {
            options.add("TMP_SET_OFFTIMER_TIME");
            newOptions.add(getResourceString(R.string.SetOffTimerTime));
        }

        // get the title (key name)
        String optionTitle;
        if (null != guiKey) {
            optionTitle = guiKey.getKeyName();
        } else {
            optionTitle = keyId;
        }

        final String[] optionNames = newOptions.toArray(new String[newOptions.size()]);
        final String[] optionValues = options.toArray(new String[options.size()]);

        // show the pop-up (single selection alert dialog)
        new AlertDialog.Builder(getContext())
                .setTitle(optionTitle)
                .setPositiveButton(
                        android.R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }


                        })
                .setSingleChoiceItems(
                        optionNames,
                        mBirRemote.getKeyOption(keyId).currentOption,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String selectedOption = optionValues[which];
                                if (selectedOption.equalsIgnoreCase("TMP_SET_ONTIMER_TIME")) {
                                    showTimerPickerDialog(TimerType.ON_TIMER);
                                } else if (selectedOption.equalsIgnoreCase("TMP_SET_OFFTIMER_TIME")) {
                                    showTimerPickerDialog(TimerType.OFF_TIMER);
                                } else {
                                    sendIR(keyId, selectedOption);
                                }

                                dialog.dismiss();
                            }
                        })
                .show();
    }

    /**
     * general long press key: show available options
     */
    private void showKeyOptions(final String keyId, RemoteKey guiKey) {

        final String[] options = mBirRemote.getKeyOption(keyId).options;

        // get the option names
        final String[] newOptions = getOptionNames(options, guiKey);

        // get the title (key name)
        String optionTitle;
        if (null != guiKey) {
            optionTitle = guiKey.getKeyName();
        } else {
            optionTitle = keyId;
        }

        // show the pop-up (single selection alert dialog)
        new AlertDialog.Builder(getContext())
                .setTitle(optionTitle)
                .setPositiveButton(
                        android.R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }


                        })
                .setSingleChoiceItems(
                        newOptions,
                        mBirRemote.getKeyOption(keyId).currentOption,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String selectedOption = options[which];
                                sendIR(keyId, selectedOption);
                                dialog.dismiss();
                            }
                        })
                .show();
    }

    private String[] getOptionNames(String[] options, RemoteKey guiKey) {

        String[] newOptions = new String[options.length];

        if (null != guiKey) {
            RemoteKey guiKeyOption;
            for (int i = 0; i < options.length; i++) {
                guiKeyOption = guiKey.getOptionById(options[i]);
                if (null != guiKeyOption) {
                    newOptions[i] = guiKeyOption.getKeyName();
                } else {

                    // handle the special case
                    // temp: IR_ACSTATE_TEMP_XX
                    String optionId =  options[i].toUpperCase(Locale.US);
                    if (optionId.startsWith("IR_ACSTATE_TEMP_")) {
                        newOptions[i] = options[i].substring("IR_ACSTATE_TEMP_".length());
                        if (newOptions[i].startsWith("P")) {
                            newOptions[i] = newOptions[i].replace("P", "+");
                        } else if (newOptions[i].startsWith("N")) {
                            newOptions[i] = newOptions[i].replace("N",  "-");
                        }
                    } else if (optionId.startsWith("IR_ACOPT_TEMP_")) {
                        String temp = optionId.substring("IR_ACOPT_TEMP_".length());
                        if (temp.startsWith("P")) {
                            newOptions[i] = temp.replace("P", "+");
                        } else if (temp.startsWith("N")) {
                            newOptions[i] = temp.replace("N", "-");
                        } else {
                            newOptions[i] = temp;
                        }

                    } else if (optionId.endsWith("_ON")) {
                        newOptions[i] = getResourceString(R.string.IR_ACOPT_ON);
                    } else if (optionId.endsWith("_OFF")) {
                        newOptions[i] = getResourceString(R.string.IR_ACOPT_OFF);
                    } else {
                        newOptions[i] = options[i];
                    }
                }
            }
        } else {
            System.arraycopy(options, 0, newOptions, 0, options.length);
        }

        return newOptions;
    }

    private void showTimerPickerDialog(final TimerType timerType) {

        int currentHourOfDay = 0;
        int currentMinute = 0;

        if (TimerType.ON_TIMER == timerType) {
            currentHourOfDay = mOnTimerHour;
            currentMinute = mOnTimerMinute;
        } else if (TimerType.OFF_TIMER == timerType) {
            currentHourOfDay = mOffTimerHour;
            currentMinute = mOffTimerMinute;
        }

        final TimePicker timePicker = new TimePicker(getContext());
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(currentHourOfDay);
        timePicker.setCurrentMinute(currentMinute);

        new AlertDialog.Builder(getContext())
                .setTitle(TimerType.ON_TIMER == timerType ? R.string.IR_ACKEY_ONTIMER : R.string.IR_ACKEY_OFFTIMER)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int hourOfDay;
                                if (Build.VERSION.SDK_INT >= 23 )
                                    hourOfDay = timePicker.getHour();
                                else
                                    hourOfDay = timePicker.getCurrentHour();

                                int minute;
                                if (Build.VERSION.SDK_INT >= 23 ) {
                                    minute = timePicker.getMinute();
                                } else {
                                    minute = timePicker.getCurrentMinute();
                                }

                                //int hourOfDay = timePicker.getCurrentHour();
                                //int minute = timePicker.getCurrentMinute();

                                if (timerType == TimerType.ON_TIMER) {

                                    mOnTimerHour = hourOfDay;
                                    mOnTimerMinute = minute;
                                    mBirRemote.setOnTime(mOnTimerHour, mOnTimerMinute, 0);

                                } else if (timerType == TimerType.OFF_TIMER) {

                                    mOffTimerHour = hourOfDay;
                                    mOffTimerMinute = minute;
                                    mBirRemote.setOffTime(mOffTimerHour, mOffTimerMinute, 0);

                                } else {
                                    return;
                                }

                                // update the GUI display
                                updateACDisplay();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setView(timePicker)
                .show();

    }

    private String getResourceString(int resId) {
        return getContext().getResources().getString(resId);
    }

    private void updateACDisplay() {

        if (null != mAcDisplay) {

            // get current active keys
            String[] keyIDs = mBirRemote.getActiveKeys();

            // update the on/off timer time
            mAcDisplay.setOnTimerTime(mOnTimerHour, mOnTimerMinute);
            mAcDisplay.setOffTimerTime(mOffTimerHour, mOffTimerMinute);

            // update the display now
            mAcDisplay.updateDisplay(keyIDs);
        }
    }

    private boolean sendIR(String keyId, String optionId) {

        boolean bOK;
        if (null == mBirRemote) {
            return false;
        }

        bOK = mBirRemote.transmitIR(keyId, optionId) == IRKit.BIRNoError;

        // update the current state to the GUI
        updateACDisplay();

        return bOK;
    }
}

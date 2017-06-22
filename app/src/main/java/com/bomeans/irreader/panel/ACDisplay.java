package com.bomeans.irreader.panel;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.bomeans.IRKit.BIRKeyOption;
import com.bomeans.IRKit.BIRRemote;
import com.bomeans.irreader.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ray on 2017/6/21.
 */

public class ACDisplay {

    protected GridLayout mLayout;
    protected AbstractDefaultKeys mPredefinedACKeys;

    //private int mDefaultRowHeight;
    private static final int DEFAULT_ROW_HEIGHT = 50;
    private static final int DEFAULT_TEXT_SIZE = 20;
    private static final int DEFAULT_TEXT_COLOR_RESID = R.color.ac_lcd_text;
    private static final String DEFAULT_INIT_TEXT = "---";

    private View mTempView;
    private TextView mFanSpeedView;
    private TextView mModeView;
    private TextView mVerticalAirSwingView;
    private TextView mHorizontalAirSwingView;
    private TextView mAirSwapView;

    private TextView mRTCView;
    private Timer mRTCTimer;
    private Boolean mShowRTCDot = true; // toggle the dot

    private TextView mOnTimerView;
    private TextView mOffTimerView;
    private TextView mTimerView;	// for combo timer key (ontimer/offtimer/timer off)
    private Hashtable<String, TextView> mGeneralOnOffView = new Hashtable<String, TextView> ();

    private int mOnTimerHour = 0;
    private int mOnTimerMinute = 0;
    private int mOffTimerHour = 0;
    private int mOffTimerMinute = 0;

    private BIRRemote mBirRemote;

    public enum TimerType {
        ON_TIMER,
        OFF_TIMER
    }

    public ACDisplay(GridLayout layout, AbstractDefaultKeys predefinedKeys, BIRRemote birRemote) {

        mLayout = layout;
        mPredefinedACKeys = predefinedKeys;
        mBirRemote = birRemote;
        mLayout.setBackgroundResource(getLCDBackgroundOnColor());

    }

    protected String[] getDefaultDisplaySequence() {
        return new String[] {
                "IR_ACKEY_MODE",
                "IR_ACKEY_TEMP",
                "IR_ACKEY_FANSPEED",
                "IR_ACKEY_AIRSWING_UD",
                "IR_ACKEY_AIRSWING_LR",
                "IR_ACKEY_SLEEP",
                "IR_ACKEY_ONTIMER",
                "IR_ACKEY_OFFTIMER"
        };
    }

    protected Context getContext() {
        return mLayout.getContext();
    }

    protected int getCellHeight() {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_ROW_HEIGHT,
                getContext().getResources().getDisplayMetrics());
    }

    protected int getTextSize() {
        return DEFAULT_TEXT_SIZE;
    }

    protected View getTempView() {
        return getNewDisplayView();
    }

    private void addView(String keyId) {

        if (keyId.equalsIgnoreCase("IR_ACKEY_POWER")) {

        } else if (keyId.equalsIgnoreCase("IR_ACKEY_MODE")) {
            mModeView = getNewDisplayView();

        } else if (keyId.equalsIgnoreCase("IR_ACKEY_TEMP")) {
            mTempView = getTempView();

        } else if (keyId.equalsIgnoreCase("IR_ACKEY_FANSPEED")) {
            mFanSpeedView = getNewDisplayView();

        } else if (keyId.equalsIgnoreCase("IR_ACKEY_AIRSWING_UD")) {
            mVerticalAirSwingView = getNewDisplayView();

        } else if (keyId.equalsIgnoreCase("IR_ACKEY_AIRSWING_LR")) {
            mHorizontalAirSwingView = getNewDisplayView();

        } else if (keyId.equalsIgnoreCase("IR_ACKEY_ONTIMER")) {
            mOnTimerView = getNewDisplayView();

        } else if (keyId.equalsIgnoreCase("IR_ACKEY_OFFTIMER")) {
            mOffTimerView = getNewDisplayView();

        } else if (keyId.equalsIgnoreCase("IR_ACKEY_TIMER")) {
            mTimerView = getNewDisplayView();

        } else if (keyId.equalsIgnoreCase("IR_ACKEY_AIRSWAP")) {
            mAirSwapView = getNewDisplayView();

        } else {
            // key id use all upper case
            mGeneralOnOffView.put(keyId.toUpperCase(Locale.US), getNewDisplayView());
        }
    }

    public void init(String[] keyIdList) {

        mLayout.removeAllViews();

        // RTC: always have it (?)
        mRTCView = getRTCView();

        ArrayList<String> newKeyIdList = new ArrayList<>(Arrays.asList(keyIdList));
        for (String keyId : getDefaultDisplaySequence()) {

            for (int i = 0; i < newKeyIdList.size(); i++) {
                if (newKeyIdList.get(i).equalsIgnoreCase(keyId)) {

                    addView(keyId);

                    newKeyIdList.remove(i);	// remove this entry
                    break;
                }
            }
        }

        for (String keyId : newKeyIdList) {
            addView(keyId);
        }

    }

    private boolean isPowerOn(String[] activeKeyIDs) {

        for (String keyId : activeKeyIDs) {

            if (keyId.equalsIgnoreCase("IR_ACKEY_POWER")) {
                BIRKeyOption keyOption = mBirRemote.getKeyOption(keyId);
                if (null == keyOption) {
                    continue;
                }
                String optionId = keyOption.options[keyOption.currentOption];

                if (null != optionId) {
                    optionId = optionId.toUpperCase(Locale.US);
                    if (optionId.equalsIgnoreCase("IR_ACOPT_POWER_ON")) {
                        return true;
                    } else if (optionId.equalsIgnoreCase("IR_ACOPT_POWER_OFF")) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void updateDisplay(String[] activeKeyIDs) {

        if (isPowerOn(activeKeyIDs)) {
            mLayout.setBackgroundResource(getLCDBackgroundOnColor());

            for (String keyId : activeKeyIDs) {

                if (keyId.equalsIgnoreCase("IR_ACKEY_TEMP")) {
                    setTempDisplay(mTempView, mBirRemote.getKeyOption(keyId));

                } else if (keyId.equalsIgnoreCase("IR_ACKEY_FANSPEED")) {
                    setViewDisplay(mFanSpeedView, keyId);

                } else if (keyId.equalsIgnoreCase("IR_ACKEY_MODE")) {
                    setViewDisplay(mModeView, keyId);

                } else if (keyId.equalsIgnoreCase("IR_ACKEY_AIRSWING_UD")) {
                    setViewDisplay(mVerticalAirSwingView, keyId);

                } else if (keyId.equalsIgnoreCase("IR_ACKEY_AIRSWING_LR")) {
                    setViewDisplay(mHorizontalAirSwingView, keyId);

                } else if (keyId.equalsIgnoreCase("IR_ACKEY_ONTIMER")) {
                    setTimerDisplay(mOnTimerView, keyId);

                } else if (keyId.equalsIgnoreCase("IR_ACKEY_OFFTIMER")) {
                    setTimerDisplay(mOffTimerView, keyId);

                } else if (keyId.equalsIgnoreCase("IR_ACKEY_TIMER")) {
                    setTimerDisplay(mTimerView, keyId);

                } else if (keyId.equalsIgnoreCase("IR_ACKEY_AIRSWAP")) {
                    setViewDisplay(mAirSwapView, keyId);

                } else {
                    setGeneralOnOffDisplay(keyId);
                }
            }

        } else { // power off

            mLayout.setBackgroundResource(getLCDBackgroundOffColor());

            setPowerOffDisplay(activeKeyIDs);
        }
    }

    protected void setPowerOffDisplay(String[] activeKeyIDs) {

        View childView;
        TextView textView;
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            childView = mLayout.getChildAt(i);
            //if ((childView instanceof TextView) && !(childView instanceof TextClock)) {
            if (childView instanceof TextView) {
                textView = (TextView) childView;
                textView.setText(DEFAULT_INIT_TEXT);
                if (isIndicatorIconOnTop()) {
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, getResourceDrawable(getDrawableDummyResId()), null, null);
                } else {
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResourceDrawable(getDrawableDummyResId()));
                }
            }
        }
    }

    protected int getDrawableDummyResId() {
        return R.drawable.ac_dummy;
    }

    protected int getLCDBackgroundOnColor() {
        return R.color.ac_lcd;
    }

    protected int getLCDBackgroundOffColor() {
        return R.color.ac_lcd_off;
    }

    private void setGeneralOnOffDisplay(String keyId) {

        if (null == keyId) {
            return;
        }

        // get the GUI key (RemoteKey) for the readable key name and icon
        RemoteKey guiKey = mPredefinedACKeys.getKeyById(keyId);

        BIRKeyOption keyOption = mBirRemote.getKeyOption(keyId);

        // get the target view which was previously stored in hashtable
        TextView targetView = mGeneralOnOffView.get(keyId);
        if (null == targetView) {
            return;
        }

        // get the key name
        String keyName;
        if (null == guiKey) {
            keyName = keyId;
            if (keyName.startsWith("IR_ACKEY_")) {
                keyName = keyName.substring("IR_ACKEY_".length());
            }
        } else {
            keyName = guiKey.getKeyName();
        }

        Drawable icon = null;
        String text = "";

        // get the current key option
        if ((null != keyOption) && (keyOption.options.length > 0)) {
            String currentOptionId = keyOption.options[keyOption.currentOption];

            boolean bMatched = false;

            if (null != currentOptionId) {

                // is it a ON?
                if (currentOptionId.toUpperCase(Locale.US).endsWith("_ON")) {
                    text = String.format("%s:%s",
                            keyName,
                            getResourceString(R.string.IR_ACOPT_ON));
                    bMatched = true;

                    // if there is a icon associated with this key, get it
                    if (null != guiKey && guiKey.hasDrawable()) {
                        icon = getResourceDrawable(guiKey.getDrawableId());
                    } else {
                        // if not, get the dummy icon
                        icon = getResourceDrawable(R.drawable.ac_dummy);
                    }

                } else if (currentOptionId.toUpperCase(Locale.US).endsWith("_OFF")) {
                    text = String.format("%s:%s",
                            keyName,
                            getResourceString(R.string.IR_ACOPT_OFF));
                    bMatched = true;

                    // draw the dummy
                    icon = getResourceDrawable(R.drawable.ac_dummy);
                }
            }

            if (!bMatched) {
                text = String.format("%s:?", keyName);
            }

        } else {
            text = keyName;
        }

        targetView.setText(text);
        targetView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, icon);

    }

    private void setTimerDisplay(TextView targetView, String keyId) {

        if (null == targetView) {
            return;
        }

        RemoteKey guiKey = mPredefinedACKeys.getKeyById(keyId);
        BIRKeyOption keyOption = mBirRemote.getKeyOption(keyId);

        String keyOptionId = keyOption.options[keyOption.currentOption];
        String keyName = keyId;
        if (null != guiKey) {
            keyName = guiKey.getKeyName();
        }

        String text = "";
        if (keyId.equalsIgnoreCase("IR_ACKEY_ONTIMER")) {

            text = String.format("%s:%s\n%02d:%02d",
                    keyName,
                    keyOptionId.endsWith("_ON") ? getResourceString(R.string.IR_ACOPT_ON) : getResourceString(R.string.IR_ACOPT_OFF),
                    mOnTimerHour,
                    mOnTimerMinute);

        } else if (keyId.equalsIgnoreCase("IR_ACKEY_OFFTIMER")) {

            text = String.format("%s:%s\n%02d:%02d",
                    keyName,
                    keyOptionId.endsWith("_ON") ? getResourceString(R.string.IR_ACOPT_ON) : getResourceString(R.string.IR_ACOPT_OFF),
                    mOffTimerHour,
                    mOffTimerMinute);

        } else if (keyId.equalsIgnoreCase("IR_ACKEY_TIMER")) {

            if (keyOptionId.equalsIgnoreCase("IR_ACOPT_ONTIMER_ON")) {

                text = String.format("%s:%s\n%d:%d",
                        getResourceString(R.string.IR_ACKEY_ONTIMER),
                        getResourceString(R.string.IR_ACOPT_ON),
                        mOnTimerHour,
                        mOnTimerMinute);

            } else if (keyOptionId.equalsIgnoreCase("IR_ACOPT_OFFTIMER_ON")) {

                text = String.format("%s:%s\n%d:%d",
                        getResourceString(R.string.IR_ACKEY_OFFTIMER),
                        getResourceString(R.string.IR_ACOPT_ON),
                        mOffTimerHour,
                        mOffTimerMinute);
            } else { // IR_ACOPT_TIMER_OFF
                text = String.format("%s:%s\n--:--",
                        keyName,
                        getResourceString(R.string.IR_ACOPT_OFF));
            }
        }

        targetView.setText(text);

        // no icon
        targetView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    /**
     *
     * @return true if the indicator icon is on top of the text, false if at bottom
     */
    protected Boolean isIndicatorIconOnTop() {
        return false;
    }

    protected void setViewDisplay(TextView targetView, String keyId) {

        if (null == targetView || null == keyId) {
            return;
        }

        RemoteKey guiKey = mPredefinedACKeys.getKeyById(keyId);
        BIRKeyOption keyOpton = mBirRemote.getKeyOption(keyId);

        String text = "";
        if (guiKey.getOptionCount() > 0) {
            String currentOptionId = keyOpton.options[keyOpton.currentOption];

            boolean bMatched = false;

            if (null != currentOptionId) {
                for (RemoteKey option : guiKey.getOptionList()) {
                    if (currentOptionId.equalsIgnoreCase(option.getKeyId())) {
                        if (guiKey.getKeyId().equalsIgnoreCase("IR_ACKEY_MODE")) {
                            text = option.getKeyName();
                        } else {
                            text = String.format("%s:%s",
                                    guiKey.getKeyName(),
                                    option.getKeyName());
                        }

                        // icon?
                        if (option.hasDrawable()) {
                            Drawable icon = getResourceDrawable(option.getDrawableId());
                            if (isIndicatorIconOnTop()) {
                                targetView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
                            } else {
                                targetView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, icon);
                            }
                        }

                        bMatched = true;
                        break;
                    }
                }
            }

            if (!bMatched) {
                text = String.format("%s:?",
                        guiKey.getKeyName());
            }
        } else {
            text = guiKey.getKeyName();
        }

        targetView.setText(text);
    }

    private String getResourceString(int resId) {
        return getContext().getResources().getString(resId);
    }

    protected Drawable getResourceDrawable(int resId) {
        return getContext().getResources().getDrawable(resId);
    }

    protected void setTempDisplay(View tempView, BIRKeyOption keyOption) {

        if (tempView instanceof TextView) {
            TextView targetView = (TextView) tempView;
            String optionId = keyOption.options[keyOption.currentOption];

            if (null == optionId) {
                targetView.setText("");
            } else {
                if (optionId.startsWith("IR_ACOPT_TEMP_")) {
                    String temp = optionId.substring("IR_ACOPT_TEMP_".length());

                    if (temp.startsWith("P")) {
                        temp.replace("P", "+");
                    } else if (temp.startsWith("N")) {
                        temp.replace("N", "-");
                    }

                    targetView.setText(temp);

                } else if (optionId.startsWith("IR_ACSTATE_TEMP_")) {
                    // generated items by underlying lib, prefixed with "STATE_NAME"_XXX,
                    // for temperature, its IR_ACSTATE_TEMP_12, for example
                    String temp = optionId.substring("IR_ACSTATE_TEMP_".length());

                    if (temp.startsWith("P")) {
                        temp = temp.replace("P", "+");
                    } else if (temp.startsWith("N")) {
                        temp = temp.replace("N", "-");
                    }

                    targetView.setText(temp);
                } else {
                    targetView.setText(optionId);
                }

            }

            // icon?
            Drawable icon = getResourceDrawable(getTempIconResId());
            if (isIndicatorIconOnTop()) {
                targetView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
            } else {
                targetView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, icon);
            }
        }

    }

    protected int getTempIconResId() {
        return R.drawable.ir_ackey_temp;
    }

    private TextView getRTCView() {

        TextView textClock = new TextView(getContext());
        textClock.setText(getCurrentTimeString());

        /*
        TextClock textClock = new TextClock(getContext());
		textClock.setFormat12Hour(null);
		textClock.setFormat24Hour("HH:mm");
	    */
        textClock.setTextSize(getTextSize());
        textClock.setTextColor(getContext().getResources().getColor(DEFAULT_TEXT_COLOR_RESID));
        textClock.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        textClock.setPadding(0, 0, 0, 0);

        GridLayout.LayoutParams lprams = new GridLayout.LayoutParams();
        lprams.width = (mLayout.getWidth() / mLayout.getColumnCount());
        lprams.height = getCellHeight();
        textClock.setLayoutParams(lprams);

        mLayout.addView(textClock);

        mRTCTimer = new Timer();
        mRTCTimer.schedule(new RTCTimerTask(), 0, 500);

        return textClock;
    }

    private class RTCTimerTask extends TimerTask {

        @Override
        public void run() {

            ((Activity) getContext()).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mRTCView.setText(getCurrentTimeString());
                }
            });
        }
    }

    private String getCurrentTimeString() {
        Calendar cal = Calendar.getInstance();
        String formatStr = mShowRTCDot ? "%02d:%02d" :"%02d %02d";
        mShowRTCDot = !mShowRTCDot;
        return String.format(formatStr,
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }

    protected TextView getNewDisplayView() {

        TextView newView = new TextView(getContext());

        newView.setTextSize(getTextSize());
        newView.setTextColor(getContext().getResources().getColor(DEFAULT_TEXT_COLOR_RESID));
        newView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        newView.setPadding(0, 0, 0, 0);

        GridLayout.LayoutParams lprams = new GridLayout.LayoutParams();
        lprams.width = (mLayout.getWidth() / mLayout.getColumnCount());
        lprams.height = GridLayout.LayoutParams.WRAP_CONTENT;//getCellHeight();
        lprams.setGravity(Gravity.CENTER_VERTICAL);
        newView.setLayoutParams(lprams);

        newView.setText(DEFAULT_INIT_TEXT);

        mLayout.addView(newView);

        return newView;
    }

    public void setOnTimerTime(int hourOfDay, int minute) {
        mOnTimerHour = hourOfDay;
        mOnTimerMinute = minute;
    }

    public void setOffTimerTime(int hourOfDay, int minute) {
        mOffTimerHour = hourOfDay;
        mOffTimerMinute = minute;
    }
}

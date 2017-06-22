package com.bomeans.irreader.panel;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bomeans.IRKit.BIRRemote;
import com.bomeans.IRKit.ConstValue;
import com.bomeans.IRKit.IRKit;
import com.bomeans.IRKit.IRemoteCreateCallBack;
import com.bomeans.IRKit.IWebAPICallBack;
import com.bomeans.IRKit.KeyName;
import com.bomeans.irreader.R;
import com.bomeans.irreader.panel.keydef.DefaultTVKeys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by ray on 2017/6/19.
 */

public class TVRemotePanel extends BaseRemotePanel {

    private Boolean FANNING_STYLE = false;

    private GridLayout mKeyLayoutView;
    //private boolean mRefresh = false;
    private BIRRemote mBirRemote;
    private KeyName[] mKeyNames;

    private String mTypeId;
    private String mBrandId;
    private String mRemoteId;

    private IRemotePanelLoadedCallback mCallback = null;

    public TVRemotePanel(Context context, GridLayout keyLayoutView) {

        super(context);

        mKeyLayoutView = keyLayoutView;
    }

    protected Boolean keySupportsLongPress(String keyId) {
        return (keyId.equalsIgnoreCase("IR_KEY_VOLUME_UP") ||
                keyId.equalsIgnoreCase("IR_KEY_VOLUME_DOWN"));
    }

    protected int getColumnCount() {
        return 3;
    }

    private String getLanguageCode() {

        String languageCode = Locale.getDefault().getCountry().toLowerCase(Locale.US);
        return languageCode;
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

        IRKit.webGetKeyName("0", getLanguageCode(), getNew, new IWebAPICallBack() {

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


    public boolean _loadRemote(String typeId, String brandId, String remoteId, boolean getNew) {

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
    protected AbstractDefaultKeys getDefaultKeys() {
        return new DefaultTVKeys(getContext(), mKeyNames);
    }

    protected boolean sortKeyList() {
        return true;
    }

    @Override
    public void createRemoteLayoutCallback() {

        super.createRemoteLayoutCallback();

        if ((null != mKeyLayoutView) && (null != mBirRemote)) {

            String[] keyIds = mBirRemote.getAllKeys();

            if (sortKeyList()) {
                Arrays.sort(keyIds);
            }

            ArrayList<String> keyIdList = new ArrayList<String>(Arrays.asList(keyIds));
            addAllKeys(keyIdList, mKeyLayoutView);
        }
    }

    private void addAllKeys(ArrayList<String> keyIdList, GridLayout layout) {

        AbstractDefaultKeys defaultKeys = getDefaultKeys();

        boolean bFound;
        String guiKeyId;
        RemoteKey guiKey;

        int colCount = getColumnCount();
        RemoteKey[] cachedGuiKey = new RemoteKey[colCount];
        String[] cachedKeyId = new String[colCount];

        boolean bDone;

        for (int i = 0; i < defaultKeys.getCount(); i++) {

            bDone = false;

            guiKey = defaultKeys.getAt(i);
            if (null == guiKey) {
                bDone = true;
            }

            guiKeyId = guiKey.getKeyId();

            if (!bDone) {
                bFound = false;
                if (guiKeyId.equalsIgnoreCase("IR_KEY_TV_AV")) {
                    for (String tmpKeyId : keyIdList) {
                        if (tmpKeyId.equalsIgnoreCase("IR_KEY_TV_AV") || tmpKeyId.equalsIgnoreCase("IR_KEY_INPUT")) {
                            cachedGuiKey[i % colCount] = guiKey;
                            cachedKeyId[i % colCount] = tmpKeyId;
                            keyIdList.remove(tmpKeyId);
                            bFound = true;
                            break;
                        }
                    }

                    if (bFound) {
                        bDone = true;
                    }
                }
            }

            if (!bDone) {
                bFound = false;
                if (guiKeyId.equalsIgnoreCase("IR_KEY_PLAY")) {
                    for (String tmpKeyId : keyIdList) {
                        if (tmpKeyId.equalsIgnoreCase("IR_KEY_PLAY") || tmpKeyId.equalsIgnoreCase("IR_KEY_PLAY_PAUSE")) {
                            cachedGuiKey[i % colCount] = guiKey;
                            cachedKeyId[i % colCount] = tmpKeyId;
                            keyIdList.remove(tmpKeyId);
                            bFound = true;
                            break;
                        }
                    }

                    if (bFound) {
                        bDone = true;
                    }
                }
            }

            if (!bDone) {
                bFound = false;
                for (String tmpKeyId : keyIdList) {
                    if (tmpKeyId.equalsIgnoreCase(guiKeyId)) {
                        cachedGuiKey[i % colCount] = guiKey;
                        cachedKeyId[i % colCount] = tmpKeyId;
                        keyIdList.remove(tmpKeyId);

                        bFound = true;
                        break;
                    }
                }

                if (!bFound) {
                    cachedGuiKey[i % colCount] = guiKey;
                }
            }

            // here we handle the key padding
            if ((i > 0) && ((i + 1) % colCount == 0)) {
                // check if it's an empty row
                boolean bNotEmpty = false;
                for (int idx = 0; idx < colCount; idx++) {
                    if (cachedKeyId[idx] != null) {
                        bNotEmpty = true;
                        break;
                    }
                }

                if (bNotEmpty) {
                    for (int idx = 0; idx < colCount; idx++) {
                        if (null != cachedKeyId[idx]) {
                            addKeyButton(mBirRemote, cachedKeyId[idx], cachedGuiKey[idx], layout);
                        } else if (null != cachedGuiKey[idx]) {
                            addDummyKeyButton(cachedGuiKey[idx].getKeyId(), layout);
                        }
                    }
                }

                // reset the cached keys
                for (int idx = 0; idx < colCount; idx++) {
                    cachedGuiKey[idx] = null;
                    cachedKeyId[idx] = null;
                }
            }
        }

        // reminding keys
        for (int i = 0; i < keyIdList.size(); i++) {
            addKeyButton(mBirRemote, keyIdList.get(i), defaultKeys.getKeyById(keyIdList.get(i)), layout);
        }
    }

    private void addKeyButton(
            final BIRRemote tvRemote,
            final String keyId,
            final RemoteKey guiKey,
            GridLayout layout) {

        View newButton = getButtonView(keyId, guiKey);
        if (null == newButton) {
            return;
        }


        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvRemote.transmitIR(keyId, null);
            }
        });

        newButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (keySupportsLongPress(keyId)) {
                    // start long press
                    tvRemote.beginTransmitIR(keyId);
                } else {
                    tvRemote.transmitIR(keyId, null);
                }
                return true;
            }
        });

        newButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //tvRemote.beginTransmitIR(keyId);
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        tvRemote.endTransmitIR();
                        break;
                }

                return false;
            }

        });


        GridLayout.LayoutParams lprams = new GridLayout.LayoutParams();
        lprams.width = (layout.getWidth() / getColumnCount());
        lprams.height = lprams.width;
        newButton.setLayoutParams(lprams);

        layout.addView(newButton);
    }

    private void addDummyKeyButton(String keyName, GridLayout layout) {

        Button newButton = new Button(getContext(), null, R.style.key_button);

        if (FANNING_STYLE) {
            newButton.setBackgroundColor(getContext().getResources().getColor(R.color.Transparent));
        } else {
            newButton.setBackgroundColor(getContext().getResources().getColor(R.color.Transparent));
        }

        // not to wrap the text
        newButton.setHorizontallyScrolling(true);

        // set key name text
        newButton.setText(keyName);
        newButton.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);

        if (FANNING_STYLE) {
            newButton.setTextColor(getContext().getResources().getColor(R.color.Transparent));
        } else {
            newButton.setTextColor(getContext().getResources().getColor(R.color.Transparent));
            //newButton.setShadowLayer(1, -1, -1, R.color.Silver);
        }

        newButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        newButton.setPadding(0, 0, 0, 0);

        GridLayout.LayoutParams lprams = new GridLayout.LayoutParams();
        lprams.width = (layout.getWidth() / getColumnCount());
        lprams.height = lprams.width;
        newButton.setLayoutParams(lprams);

        layout.addView(newButton);
    }

    protected int getKeyButtonBackgroundResId() {
        if (FANNING_STYLE) {
            return R.drawable.key_button_fanning;
        } else {
            return R.drawable.key_button_round_black;
        }
    }

    protected View getKeyButtonWithDrawable(int imageResId, RemoteKey guiKey) {

        if (null == guiKey || !(guiKey.hasDrawable() || guiKey.hasDrawablePath())) {
            return null;
        }

        Button newButton = new CenteredIconButton(getContext(), null, R.style.key_button);
        newButton.setBackgroundResource(imageResId);

        if (guiKey.hasDrawable()) {

            Drawable buttonDrawable;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                buttonDrawable = getContext().getResources().getDrawable(guiKey.getDrawableId(), getContext().getTheme());
            } else {
                buttonDrawable = getContext().getResources().getDrawable(guiKey.getDrawableId());
            }

            newButton.setCompoundDrawablesWithIntrinsicBounds(
                    buttonDrawable,
                    null,
                    null,
                    null);

        } else if (guiKey.hasDrawablePath()) {
            String path = guiKey.getDrawablePath();
            if (!path.startsWith("http://")) {
                newButton.setCompoundDrawablesWithIntrinsicBounds(
                        Drawable.createFromPath(path),
                        null,
                        null,
                        null);
            }
        }

        newButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        newButton.setPadding(0, 0, 0, 0);

        return newButton;
    }

    protected View getKeyButtonWithTextOnly(int imageResId, String keyName) {

        View newButton = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.remote_text_key_button, null);
        ImageView buttonImage = (ImageView) newButton.findViewById(R.id.button_image);
        TextView text = (TextView) newButton.findViewById(R.id.text_key_name);

        buttonImage.setImageResource(imageResId);
        text.setHorizontallyScrolling(true);
        text.setText(keyName);

        text.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
        text.setTextColor(getContext().getResources().getColor(R.color.LightGray));
        text.setShadowLayer(2, -2, -2, R.color.Black);

        //text.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        //newButton.setPadding(0, 0, 0, 0);

        return newButton;
    }

    protected View getKeyButtonDigits(String keyId, RemoteKey guiKey) {

        String keyName;
        if (null != guiKey) {
            keyName = guiKey.getKeyName();
        } else {
            keyName = keyId;
            if (keyName.startsWith("IR_KEY_DIG_")) {
                keyName = keyName.substring(10);
            }
        }

        return getKeyButtonWithCenteredText(R.drawable.key_button_round_black, keyName, R.color.LightGray);

    }

    protected View getKeyButtonWithCenteredText(int bgResId, String text, int textColor) {

        Button newButton = new CenteredIconButton(getContext(), null, R.style.key_button);
        newButton.setBackgroundResource(bgResId);
        newButton.setText(text);

        newButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, newButton.getTextSize() + 50);
        newButton.setShadowLayer(3, -3, -3, R.color.Black);
        newButton.setTextColor(getContext().getResources().getColor(textColor));

        newButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        newButton.setPadding(0, 0, 0, 0);

        return newButton;
    }

    protected View getButtonView(String keyId, RemoteKey guiKey) {
        View newButton;

        // handle special key views
        switch (keyId) {
            case "IR_KEY_RED":
                return getKeyButtonWithTextOnly(R.drawable.key_button_square_red, guiKey == null ? keyId : guiKey.getKeyName());
            case "IR_KEY_GREEN":
                return getKeyButtonWithTextOnly(R.drawable.key_button_square_green, guiKey == null ? keyId : guiKey.getKeyName());
            case "IR_KEY_BLUE":
                return getKeyButtonWithTextOnly(R.drawable.key_button_square_blue, guiKey == null ? keyId : guiKey.getKeyName());
            case "IR_KEY_YELLOW":
                return getKeyButtonWithTextOnly(R.drawable.key_button_square_yellow, guiKey == null ? keyId : guiKey.getKeyName());
            case "IR_KEY_POWER_TOGGLE":
            case "IR_KEY_FAN_POWER":
                return getKeyButtonWithDrawable(R.drawable.key_button_round_red, guiKey);
            case "IR_KEY_CHANNEL_UP":
            case "IR_KEY_VOLUME_UP":
                return getKeyButtonWithDrawable(R.drawable.key_button_squareround_up_black, guiKey);
            case "IR_KEY_CHANNEL_DOWN":
            case "IR_KEY_VOLUME_DOWN":
                return getKeyButtonWithDrawable(R.drawable.key_button_squareround_down_black, guiKey);
            case "IR_KEY_CURSOR_UP":
                return getKeyButtonWithDrawable(R.drawable.key_button_arrow_up_gray, guiKey);
            case "IR_KEY_CURSOR_DOWN":
                return getKeyButtonWithDrawable(R.drawable.key_button_arrow_down_gray, guiKey);
            case "IR_KEY_CURSOR_RIGHT":
                return getKeyButtonWithDrawable(R.drawable.key_button_arrow_right_gray, guiKey);
            case "IR_KEY_CURSOR_LEFT":
                return getKeyButtonWithDrawable(R.drawable.key_button_arrow_left_gray, guiKey);
            case "IR_KEY_OK":
                return getKeyButtonWithCenteredText(
                        R.drawable.key_button_round_gray,
                        guiKey == null ? guiKey.getKeyName() : "OK",
                        R.color.Black);
            default:
                if (keyId.startsWith("IR_KEY_DIG_")) {
                    return getKeyButtonDigits(keyId, guiKey);
                }
                break;
        }

        if (null != guiKey) {
            if (guiKey.hasDrawable() || guiKey.hasDrawablePath()) {
                newButton = getKeyButtonWithDrawable(getKeyButtonBackgroundResId(), guiKey);
            } else {
                newButton = getKeyButtonWithTextOnly(R.drawable.key_button_square_black, guiKey.getKeyName());
            }
        } else {
            Boolean bFound = false;
            /*if (null != mKeyList) {
                for (KeyCode keyCode : mKeyList) {
                    if (keyId.equalsIgnoreCase(keyCode.id)) {
                        keyId = keyCode.name;
                        bFound = true;
                        break;
                    }
                }
            }*/

            if (!bFound) {
                if (keyId.contains("IR_KEY_")) {
                    keyId = keyId.substring(7);
                }
            }

            newButton = getKeyButtonWithTextOnly(R.drawable.key_button_square_black, keyId);
        }

        return newButton;
    }
}

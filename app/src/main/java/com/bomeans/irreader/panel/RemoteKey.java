package com.bomeans.irreader.panel;

import java.util.ArrayList;

/**
 * Created by ray on 2017/6/19.
 */

public class RemoteKey {

    private String mKeyId;
    private String mKeyName;
    private int mDrawableId;
    private String mDrawablePath;

    private ArrayList<RemoteKey> mOptionList = null;	// for AC type keys

    public static final int INVALID_DRAWABLE_ID = -1;


    public RemoteKey (String keyId, String keyName, int drawableId, ArrayList<RemoteKey> optionList) {
        mKeyId = keyId;
        mKeyName = keyName;
        mDrawableId = drawableId;
        mDrawablePath = null;
        mOptionList = optionList;
    }

    public RemoteKey(String keyId, String keyName, String drawablePath, ArrayList<RemoteKey> optionList) {

        mKeyId = keyId;
        mKeyName = keyName;
        mDrawableId = INVALID_DRAWABLE_ID;
        mDrawablePath = drawablePath;
        mOptionList = optionList;
    }

    public RemoteKey (String keyId, int drawableId, ArrayList<RemoteKey> optionList) {
        this(keyId, "", drawableId, optionList);
    }

    public RemoteKey(String keyId, String keyName, int drawableId) {
        this(keyId, keyName, drawableId, null);
    }

    public RemoteKey(String keyId, int drawableId) {
        this(keyId, "", drawableId, null);
    }

    public RemoteKey(String keyId, String keyName) {
        this(keyId, keyName, INVALID_DRAWABLE_ID, null);
    }

    public RemoteKey(String keyId) {
        this(keyId, "", INVALID_DRAWABLE_ID, null);
    }

    public String getKeyId() {
        return mKeyId;
    }

    public int getDrawableId() {
        return mDrawableId;
    }

    public String getDrawablePath() {
        return mDrawablePath;
    }

    public boolean hasDrawable() {
        return mDrawableId != INVALID_DRAWABLE_ID;
    }

    public Boolean hasDrawablePath() {
        return (null != mDrawablePath);
    }

    public ArrayList<RemoteKey> getOptionList() {
        return mOptionList;
    }

    public void setOptionList(ArrayList<RemoteKey> optionList) {
        mOptionList = optionList;
    }

    public RemoteKey getOptionById(String optionId) {
        if (null == mOptionList) {
            return null;
        }

        for (RemoteKey option : mOptionList) {
            if (option.getKeyId().equalsIgnoreCase(optionId)) {
                return option;
            }
        }

        return null;
    }

    public RemoteKey getOptionByIndex(int index) {
        if (null == mOptionList) {
            return null;
        }

        if (index < mOptionList.size()) {
            return mOptionList.get(index);
        }

        return null;
    }

    public int getOptionCount() {
        if (null != mOptionList) {
            return mOptionList.size();
        }

        return 0;
    }

    public void setKeyName(String keyName) {
        mKeyName = keyName;
    }

    public String getKeyName() {
        return mKeyName;
    }
}

package com.bomeans.irreader.panel;

import android.content.Context;

import com.bomeans.IRKit.KeyName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by ray on 2017/6/19.
 */

public abstract class AbstractDefaultKeys {

    protected String[] mDefaultKeySequence;

    protected HashMap<String, RemoteKey> mKeyMapping = new HashMap<>();
    private KeyName[] mAllKeyNames;

    public AbstractDefaultKeys() {
        mDefaultKeySequence = new String[0];
    }

    public AbstractDefaultKeys(Context context, KeyName[] keyNames) {

        mAllKeyNames = keyNames;

        initKeyNameMapping(context, mKeyMapping);

        mappingKeyNames();

        mDefaultKeySequence = initKeys();
    }

    /**
     * Assigning all keys to the input keyMapping
     * @param context activity context
     * @param keyMapping map for storing the keys
     */
    protected abstract void initKeyNameMapping(Context context, HashMap<String, RemoteKey> keyMapping);

    protected abstract String[] initKeys();

    public RemoteKey getAt(int index) {
        if (index < mDefaultKeySequence.length) {
            return mKeyMapping.get(mDefaultKeySequence[index]);
        } else {
            return null;
        }
    }

    public int getCount() {
        return mDefaultKeySequence.length;
    }

    public RemoteKey getKeyById(String keyId) {
        return mKeyMapping.get(keyId.toUpperCase(Locale.US));
    }

    public HashMap<String, RemoteKey> getAllKeys() {
        return mKeyMapping;
    }

    protected void mappingKeyNames() {

        if (mAllKeyNames.length == 0) {
            return;
        }

        RemoteKey remoteKey;
        ArrayList<RemoteKey> keysToBeAdded = new ArrayList<> ();
        for (KeyName keyName : mAllKeyNames) {

            remoteKey = mKeyMapping.get(keyName.keyId);
            if (null != remoteKey) {
                // if the key already exist in the mKeyMapping, update the key name
                remoteKey.setKeyName(keyName.name);
            } else {
                // if not exists, create a new one for adding to the table later
                remoteKey = new RemoteKey(keyName.keyId, keyName.name);
                keysToBeAdded.add(remoteKey);
            }
        }

        // added the keys not exists in the mKeyMaping table
        for (RemoteKey newKey : keysToBeAdded) {
            mKeyMapping.put(newKey.getKeyId(), newKey);
        }
    }
}

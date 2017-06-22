package com.bomeans.irreader.panel.keydef;

import android.content.Context;

import com.bomeans.IRKit.KeyName;

/**
 * Created by ray on 2017/6/20.
 */

public class DefaultROBOTKeys extends DefaultTVKeys {

    public DefaultROBOTKeys(Context context, KeyName[] keyNames) {
        super(context, keyNames);
    }

    @Override
    protected String[] initKeys() {
        return new String[] {

                "IR_KEY_POWER_TOGGLE",
                "IR_KEY_CURSOR_UP",
                "IR_KEY_DUMMY",

                "IR_KEY_CURSOR_LEFT",
                "IR_KEY_OK",
                "IR_KEY_CURSOR_RIGHT",

                "IR_KEY_DUMMY",
                "IR_KEY_CURSOR_DOWN",
                "IR_KEY_STOP"
        };
    }
}

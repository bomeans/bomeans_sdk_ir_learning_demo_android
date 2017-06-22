package com.bomeans.irreader.panel.keydef;

import android.content.Context;

import com.bomeans.IRKit.KeyName;
import com.bomeans.irreader.panel.RemoteKey;

import java.util.HashMap;

/**
 * Created by ray on 2017/6/20.
 */

public class DefaultMP3Keys extends DefaultTVKeys {

    public DefaultMP3Keys(Context context, KeyName[] keyNames) {
        super(context, keyNames);
    }

    @Override
    protected void initKeyNameMapping(Context context, HashMap<String, RemoteKey> keyMapping) {

        super.initKeyNameMapping(context, keyMapping);

        keyMapping.put("IR_KEY_PHOTO_PREVIEW", new RemoteKey("IR_KEY_PHOTO_PREVIEW"));
        keyMapping.put("IR_KEY_PHOTO_SLIDE", new RemoteKey("IR_KEY_PHOTO_SLIDE"));
        keyMapping.put("IR_KEY_PHOTO_DELETE", new RemoteKey("IR_KEY_PHOTO_DELETE"));
        keyMapping.put("IR_KEY_PHOTO_COPY", new RemoteKey("IR_KEY_PHOTO_COPY"));
        keyMapping.put("IR_KEY_PHOTO_ROTATE", new RemoteKey("IR_KEY_PHOTO_ROTATE"));
        keyMapping.put("IR_KEY_PHOTO_RATIO", new RemoteKey("IR_KEY_PHOTO_RATIO"));
        keyMapping.put("IR_KEY_PHOTO_MUSIC", new RemoteKey("IR_KEY_PHOTO_MUSIC"));
        keyMapping.put("IR_KEY_PHOTO_DATE", new RemoteKey("IR_KEY_PHOTO_DATE"));
        keyMapping.put("IR_KEY_PHOTO_MODE", new RemoteKey("IR_KEY_PHOTO_MODE"));

    }

    @Override
    protected String[] initKeys() {
        return new String[] {
                "IR_KEY_POWER_TOGGLE",
                "IR_KEY_MENU",
                "IR_KEY_PHOTO_MODE",

                "IR_KEY_PHOTO_PREVIEW",
                "IR_KEY_PHOTO_SLIDE",
                "IR_KEY_PHOTO_MUSIC",

                "IR_KEY_INFO",
                "IR_KEY_CURSOR_UP",
                "IR_KEY_PLAY",

                "IR_KEY_CURSOR_LEFT",
                "IR_KEY_OK",
                "IR_KEY_CURSOR_RIGHT",

                "IR_KEY_EXIT",
                "IR_KEY_CURSOR_DOWN",
                "IR_KEY_PHOTO_DELETE",

                "IR_KEY_VOLUME_UP",
                "IR_KEY_VOLUME_DOWN"
        };
    }

}

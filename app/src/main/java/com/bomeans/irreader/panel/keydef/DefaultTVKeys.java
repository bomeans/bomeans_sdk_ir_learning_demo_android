package com.bomeans.irreader.panel.keydef;

import android.content.Context;

import com.bomeans.IRKit.KeyName;
import com.bomeans.irreader.R;
import com.bomeans.irreader.panel.AbstractDefaultKeys;
import com.bomeans.irreader.panel.RemoteKey;

import java.util.HashMap;

/**
 * Created by ray on 2017/6/20.
 */

public class DefaultTVKeys extends AbstractDefaultKeys {

    public DefaultTVKeys(Context context, KeyName[] keyNames) {
        super(context, keyNames);
    }

    @Override
    protected String[] initKeys() {

        return new String[] {

                // main
                "IR_KEY_POWER_TOGGLE",
                "IR_KEY_TV_AV",
                "IR_KEY_ASPECT_RATIO",

                "IR_KEY_VOLUME_UP",
                "IR_KEY_MUTING",
                "IR_KEY_CHANNEL_UP",

                "IR_KEY_VOLUME_DOWN",
                "IR_KEY_LAST_CHANNEL",
                "IR_KEY_CHANNEL_DOWN",


                // menu / directions
                "IR_KEY_HOME",
                "IR_KEY_CURSOR_UP",
                "IR_KEY_MENU",

                "IR_KEY_CURSOR_LEFT",
                "IR_KEY_OK",
                "IR_KEY_CURSOR_RIGHT",

                "IR_KEY_BACK",
                "IR_KEY_CURSOR_DOWN",
                "IR_KEY_EXIT",


                // digits
                "IR_KEY_DIG_1",
                "IR_KEY_DIG_2",
                "IR_KEY_DIG_3",

                "IR_KEY_DIG_4",
                "IR_KEY_DIG_5",
                "IR_KEY_DIG_6",

                "IR_KEY_DIG_7",
                "IR_KEY_DIG_8",
                "IR_KEY_DIG_9",

                "IR_KEY_DIG_100",
                "IR_KEY_DIG_0",
                "IR_KEY_RETURN",


                // others
                "IR_KEY_DISPLAY",
                "IR_KEY_SLEEP",
                "IR_KEY_RED",

                "IR_KEY_GREEN",
                "IR_KEY_YELLOW",
                "IR_KEY_BLUE",

                // multi0media
                "IR_KEY_SKIP_REVERSE",
                "IR_KEY_PLAY",
                "IR_KEY_SKIP_FORWARD",

                "IR_KEY_PREVIOUS",
                "IR_KEY_PAUSE",
                "IR_KEY_NEXT",

                "IR_KEY_RECORD",
                "IR_KEY_STOP",
                "IR_KEY_DUMMY"
        };
    }

    @Override
    protected void initKeyNameMapping(Context context, HashMap<String, RemoteKey> keyMapping) {
        keyMapping.put("IR_KEY_POWER_TOGGLE", new RemoteKey("IR_KEY_POWER_TOGGLE", R.drawable.ir_key_power_toggle));
        keyMapping.put("IR_KEY_CHANNEL_UP", new RemoteKey("IR_KEY_CHANNEL_UP", R.drawable.ir_key_channel_up));
        keyMapping.put("IR_KEY_CHANNEL_DOWN", new RemoteKey("IR_KEY_CHANNEL_DOWN", R.drawable.ir_key_channel_down));

        keyMapping.put("IR_KEY_VOLUME_UP", new RemoteKey("IR_KEY_VOLUME_UP", R.drawable.ir_key_volume_up));
        keyMapping.put("IR_KEY_VOLUME_DOWN", new RemoteKey("IR_KEY_VOLUME_DOWN", R.drawable.ir_key_volume_down));
        keyMapping.put("IR_KEY_MUTING", new RemoteKey("IR_KEY_MUTING", R.drawable.ir_key_muting));

        keyMapping.put("IR_KEY_CURSOR_UP", new RemoteKey("IR_KEY_CURSOR_UP", R.drawable.ir_key_cursor_up));
        keyMapping.put("IR_KEY_CURSOR_DOWN", new RemoteKey("IR_KEY_CURSOR_DOWN", R.drawable.ir_key_cursor_down));
        keyMapping.put("IR_KEY_CURSOR_LEFT", new RemoteKey("IR_KEY_CURSOR_LEFT", R.drawable.ir_key_cursor_left));
        keyMapping.put("IR_KEY_CURSOR_RIGHT", new RemoteKey("IR_KEY_CURSOR_RIGHT", R.drawable.ir_key_cursor_right));
        keyMapping.put("IR_KEY_OK", new RemoteKey("IR_KEY_OK", R.drawable.ir_key_ok));
        keyMapping.put("IR_KEY_BACK", new RemoteKey("IR_KEY_BACK", R.drawable.ir_key_back));
        keyMapping.put("IR_KEY_EXIT", new RemoteKey("IR_KEY_EXIT", R.drawable.ir_key_exit));


        //keyMapping.put("IR_KEY_RED", new RemoteKey("IR_KEY_RED", R.drawable.ir_key_red));
        //keyMapping.put("IR_KEY_GREEN", new RemoteKey("IR_KEY_GREEN", R.drawable.ir_key_green));
        //keyMapping.put("IR_KEY_YELLOW", new RemoteKey("IR_KEY_YELLOW", R.drawable.ir_key_yellow));
        //keyMapping.put("IR_KEY_BLUE", new RemoteKey("IR_KEY_BLUE", R.drawable.ir_key_blue));

        keyMapping.put("IR_KEY_LAST_CHANNEL", new RemoteKey("IR_KEY_LAST_CHANNEL", R.drawable.ir_key_last_channel));
        keyMapping.put("IR_KEY_RETURN", new RemoteKey("IR_KEY_RETURN", R.drawable.ir_key_return));
        keyMapping.put("IR_KEY_INPUT", new RemoteKey("IR_KEY_INPUT", R.drawable.ir_key_input));
        keyMapping.put("IR_KEY_TV_AV", new RemoteKey("IR_KEY_TV_AV", R.drawable.ir_key_input));
        keyMapping.put("IR_KEY_HOME", new RemoteKey("IR_KEY_HOME", R.drawable.ir_key_home));
        keyMapping.put("IR_KEY_MENU", new RemoteKey("IR_KEY_MENU", R.drawable.ir_key_menu));
        keyMapping.put("IR_KEY_ASPECT_RATIO", new RemoteKey("IR_KEY_ASPECT_RATIO", R.drawable.ir_key_aspect_ratio));

        keyMapping.put("IR_KEY_PLAY", new RemoteKey("IR_KEY_PLAY", R.drawable.ir_key_play));
        keyMapping.put("IR_KEY_PLAY_PAUSE", new RemoteKey("IR_KEY_PLAY_PAUSE", R.drawable.ir_key_play_pause));
        keyMapping.put("IR_KEY_PAUSE", new RemoteKey("IR_KEY_PAUSE", R.drawable.ir_key_pause));
        keyMapping.put("IR_KEY_STOP", new RemoteKey("IR_KEY_STOP", R.drawable.ir_key_stop));
        keyMapping.put("IR_KEY_SKIP_FORWARD", new RemoteKey("IR_KEY_SKIP_FORWARD", R.drawable.ir_key_skip_forward));
        keyMapping.put("IR_KEY_SKIP_REVERSE", new RemoteKey("IR_KEY_SKIP_REVERSE", R.drawable.ir_key_skip_reverse));
        keyMapping.put("IR_KEY_PREVIOUS", new RemoteKey("IR_KEY_PREVIOUS", R.drawable.ir_key_previous));
        keyMapping.put("IR_KEY_NEXT", new RemoteKey("IR_KEY_NEXT", R.drawable.ir_key_next));
        keyMapping.put("IR_KEY_RECORD", new RemoteKey("IR_KEY_RECORD", R.drawable.ir_key_record));
    }
}

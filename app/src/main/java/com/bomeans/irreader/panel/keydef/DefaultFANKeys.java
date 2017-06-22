package com.bomeans.irreader.panel.keydef;

import android.content.Context;

import com.bomeans.IRKit.KeyName;
import com.bomeans.irreader.R;
import com.bomeans.irreader.panel.RemoteKey;

import java.util.HashMap;

/**
 * Created by ray on 2017/6/20.
 */

public class DefaultFANKeys extends DefaultTVKeys {

    public DefaultFANKeys(Context context, KeyName[] keyNames) {
        super(context, keyNames);
    }

    @Override
    protected void initKeyNameMapping(Context context, HashMap<String, RemoteKey> keyMapping) {
        super.initKeyNameMapping(context, keyMapping);

        keyMapping.put("IR_KEY_FAN_POWER", new RemoteKey("IR_KEY_FAN_POWER", R.drawable.ir_key_fan_power));
        keyMapping.put("IR_KEY_FAN_SPEED_UP", new RemoteKey("IR_KEY_FAN_SPEED_UP", R.drawable.ir_key_fan_speed_up));
        keyMapping.put("IR_KEY_FAN_SPEED_DOWN", new RemoteKey("IR_KEY_FAN_SPEED_DOWN", R.drawable.ir_key_fan_speed_down));
        keyMapping.put("IR_KEY_FAN_SWING", new RemoteKey("IR_KEY_FAN_SWING", R.drawable.ir_key_fan_swing_lr));
        keyMapping.put("IR_KEY_FAN_SWING_UD", new RemoteKey("IR_KEY_FAN_SWING_UD", R.drawable.ir_key_fan_swing_ud));
        keyMapping.put("IR_KEY_FAN_SWING_LR", new RemoteKey("IR_KEY_FAN_SWING_LR", R.drawable.ir_key_fan_swing_lr));
        keyMapping.put("IR_KEY_FAN_TIMER", new RemoteKey("IR_KEY_FAN_TIMER", R.drawable.ir_key_fan_timer));
        keyMapping.put("IR_KEY_FAN_RHYTHM", new RemoteKey("IR_KEY_FAN_RHYTHM", R.drawable.ir_key_fan_rythm));
        keyMapping.put("IR_KEY_FAN_LIGHT", new RemoteKey("IR_KEY_FAN_TIMER", R.drawable.ir_key_fan_eco));
        keyMapping.put("IR_KEY_FAN_MODE", new RemoteKey("IR_KEY_FAN_MODE", R.drawable.ir_key_fan_mode));
        keyMapping.put("IR_KEY_FAN_SLEEP", new RemoteKey("IR_KEY_FAN_SLEEP", R.drawable.ir_key_fan_sleep));
    }

    @Override
    protected String[] initKeys() {
        return new String[] {
                "IR_KEY_FAN_POWER",
                "IR_KEY_FAN_SPEED_DOWN",
                "IR_KEY_FAN_SPEED_UP",

                "IR_KEY_FAN_SWING_LR",
                "IR_KEY_FAN_SWING_UD",
                "IR_KEY_FAN_SWING",

                "IR_KEY_FAN_TIMER",
                "IR_KEY_FAN_RHYTHM",
                "IR_KEY_FAN_MODE"

                /*
				"IR_KEY_FAN_POWER",
				"IR_KEY_FAN_SPEED_UP",
				"IR_KEY_FAN_SPEED_DOWN",

				"IR_KEY_FAN_SWING",
				"IR_KEY_FAN_MODE",
				"IR_KEY_FAN_LIGHT",

				"IR_KEY_FAN_TIMER"
				*/
        };
    }
}

package com.bomeans.irreader.panel.keydef;

import android.content.Context;
import android.content.res.Resources;

import com.bomeans.IRKit.KeyName;
import com.bomeans.irreader.R;
import com.bomeans.irreader.panel.AbstractDefaultKeys;
import com.bomeans.irreader.panel.RemoteKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by ray on 2017/6/20.
 */

public class DefaultACKeys extends AbstractDefaultKeys {

    public DefaultACKeys(Context context, KeyName[] keyNames) {
        super(context, keyNames);
    }

    @Override
    protected void initKeyNameMapping(Context context, HashMap<String, RemoteKey> keyMapping) {
        Resources res = context.getResources();

        ArrayList<RemoteKey> options;

        // power
        options = new ArrayList<>(Arrays.asList(
                new RemoteKey[] {
                        new RemoteKey("IR_ACOPT_POWER_ON",	res.getString(R.string.IR_ACOPT_POWER_ON),	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_POWER_OFF",	res.getString(R.string.IR_ACOPT_POWER_OFF),	R.drawable.ac_dummy) }));
        keyMapping.put("IR_ACKEY_POWER", new RemoteKey("IR_ACKEY_POWER", res.getString(R.string.IR_ACKEY_POWER), R.drawable.ac_dummy, options));

        // mode
        options = new ArrayList<>(Arrays.asList(
                new RemoteKey[] {
                        new RemoteKey("IR_ACOPT_MODE_AUTO",	res.getString(R.string.IR_ACOPT_MODE_AUTO),	R.drawable.ac_mode_auto),
                        new RemoteKey("IR_ACOPT_MODE_COOL",	res.getString(R.string.IR_ACOPT_MODE_COOL),	R.drawable.ac_mode_cool),
                        new RemoteKey("IR_ACOPT_MODE_WARM",	res.getString(R.string.IR_ACOPT_MODE_WARM),	R.drawable.ac_mode_warm),
                        new RemoteKey("IR_ACOPT_MODE_DRY",	res.getString(R.string.IR_ACOPT_MODE_DRY),	R.drawable.ac_mode_dry),
                        new RemoteKey("IR_ACOPT_MODE_FAN",	res.getString(R.string.IR_ACOPT_MODE_FAN),	R.drawable.ac_mode_fan) }));
        keyMapping.put("IR_ACKEY_MODE", new RemoteKey("IR_ACKEY_MODE", res.getString(R.string.IR_ACKEY_MODE), R.drawable.ac_dummy, options));

        // temp
        keyMapping.put("IR_ACKEY_TEMP", 		new RemoteKey("IR_ACKEY_TEMP", 		res.getString(R.string.IR_ACKEY_TEMP), 		R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_TEMP_UP", 		new RemoteKey("IR_ACKEY_TEMP_UP", 	res.getString(R.string.IR_ACKEY_TEMP_UP), 	R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_TEMP_DOWN", 	new RemoteKey("IR_ACKEY_TEMP_DOWN",	res.getString(R.string.IR_ACKEY_TEMP_DOWN), R.drawable.ac_dummy));

        // air swing up/down
        options = new ArrayList<>(Arrays.asList(
                new RemoteKey[] {
                        new RemoteKey("IR_ACOPT_AIRSWING_UD_A",		res.getString(R.string.IR_ACOPT_AIRSWING_UD_A), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_UD_OFF",	res.getString(R.string.IR_ACOPT_AIRSWING_UD_OFF), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_UD_1", 	res.getString(R.string.IR_ACOPT_AIRSWING_UD_1), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_UD_2", 	res.getString(R.string.IR_ACOPT_AIRSWING_UD_2), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_UD_3", 	res.getString(R.string.IR_ACOPT_AIRSWING_UD_3), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_UD_4", 	res.getString(R.string.IR_ACOPT_AIRSWING_UD_4), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_UD_5", 	res.getString(R.string.IR_ACOPT_AIRSWING_UD_5), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_UD_6", 	res.getString(R.string.IR_ACOPT_AIRSWING_UD_6), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_UD_7", 	res.getString(R.string.IR_ACOPT_AIRSWING_UD_7), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_UD_8", 	res.getString(R.string.IR_ACOPT_AIRSWING_UD_8), 	R.drawable.ac_dummy) }));
        keyMapping.put("IR_ACKEY_AIRSWING_UD", new RemoteKey("IR_ACKEY_AIRSWING_UD", res.getString(R.string.IR_ACKEY_AIRSWING_UD), 	R.drawable.ac_dummy,	options));

        // air swing left/right
        options = new ArrayList<>(Arrays.asList(
                new RemoteKey[] {
                        new RemoteKey("IR_ACOPT_AIRSWING_LR_A", 	res.getString(R.string.IR_ACOPT_AIRSWING_LR_A), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_LR_OFF", 	res.getString(R.string.IR_ACOPT_AIRSWING_LR_OFF), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_LR_1", 	res.getString(R.string.IR_ACOPT_AIRSWING_LR_1), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_LR_2", 	res.getString(R.string.IR_ACOPT_AIRSWING_LR_2), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_LR_3", 	res.getString(R.string.IR_ACOPT_AIRSWING_LR_3), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_LR_4", 	res.getString(R.string.IR_ACOPT_AIRSWING_LR_4), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_LR_5", 	res.getString(R.string.IR_ACOPT_AIRSWING_LR_5), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_LR_6", 	res.getString(R.string.IR_ACOPT_AIRSWING_LR_6), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_LR_7", 	res.getString(R.string.IR_ACOPT_AIRSWING_LR_7), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWING_LR_8", 	res.getString(R.string.IR_ACOPT_AIRSWING_LR_8), 	R.drawable.ac_dummy) }));
        keyMapping.put("IR_ACKEY_AIRSWING_LR", new RemoteKey("IR_ACKEY_AIRSWING_LR", res.getString(R.string.IR_ACKEY_AIRSWING_LR), R.drawable.ac_dummy, options));

        // fan speed
        options = new ArrayList<>(Arrays.asList(
                new RemoteKey[] {
                        new RemoteKey("IR_ACOPT_FANSPEED_A", 	res.getString(R.string.IR_ACOPT_FANSPEED_A), 	R.drawable.ac_fanspeed_auto),
                        new RemoteKey("IR_ACOPT_FANSPEED_L", 	res.getString(R.string.IR_ACOPT_FANSPEED_L), 	R.drawable.ac_fanspeed_l),
                        new RemoteKey("IR_ACOPT_FANSPEED_M", 	res.getString(R.string.IR_ACOPT_FANSPEED_M), 	R.drawable.ac_fanspeed_m),
                        new RemoteKey("IR_ACOPT_FANSPEED_H", 	res.getString(R.string.IR_ACOPT_FANSPEED_H), 	R.drawable.ac_fanspeed_h),
                        new RemoteKey("IR_ACOPT_FANSPEED_H1", 	res.getString(R.string.IR_ACOPT_FANSPEED_H1), 	R.drawable.ac_fanspeed_h1),
                        new RemoteKey("IR_ACOPT_FANSPEED_H2", 	res.getString(R.string.IR_ACOPT_FANSPEED_H2), 	R.drawable.ac_fanspeed_h2),
                        new RemoteKey("IR_ACOPT_FANSPEED_H3", 	res.getString(R.string.IR_ACOPT_FANSPEED_H3), 	R.drawable.ac_fanspeed_h3) }));
        keyMapping.put("IR_ACKEY_FANSPEED", new RemoteKey("IR_ACKEY_FANSPEED", res.getString(R.string.IR_ACKEY_FANSPEED), R.drawable.ac_dummy, options));

        // air swap
        options = new ArrayList<>(Arrays.asList(
                new RemoteKey[] {
                        new RemoteKey("IR_ACOPT_AIRSWAP_ON", 	res.getString(R.string.IR_ACOPT_AIRSWAP_ON), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWAP_OFF", 	res.getString(R.string.IR_ACOPT_AIRSWAP_OFF), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWAP_1", 	res.getString(R.string.IR_ACOPT_AIRSWAP_1), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWAP_2", 	res.getString(R.string.IR_ACOPT_AIRSWAP_2), 	R.drawable.ac_dummy),
                        new RemoteKey("IR_ACOPT_AIRSWAP_3", 	res.getString(R.string.IR_ACOPT_AIRSWAP_3), 	R.drawable.ac_dummy)
                }));
        keyMapping.put("IR_ACKEY_AIRSWAP", new RemoteKey("IR_ACKEY_AIRSWAP", res.getString(R.string.IR_ACKEY_AIRSWAP), R.drawable.ac_dummy, options));

        // others (on/off)
        keyMapping.put("IR_ACKEY_SLEEP", new RemoteKey("IR_ACKEY_SLEEP", 		res.getString(R.string.IR_ACKEY_SLEEP), 	R.drawable.ir_ackey_sleep));
        keyMapping.put("IR_ACKEY_TURBO", new RemoteKey("IR_ACKEY_TURBO", 		res.getString(R.string.IR_ACKEY_TURBO), 	R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_QUICK", new RemoteKey("IR_ACKEY_QUICK", 		res.getString(R.string.IR_ACKEY_QUICK), 	R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_AIRCLEAN", new RemoteKey("IR_ACKEY_AIRCLEAN", 	res.getString(R.string.IR_ACKEY_AIRCLEAN), 	R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_WARMUP", new RemoteKey("IR_ACKEY_WARMUP", 		res.getString(R.string.IR_ACKEY_WARMUP), 	R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_DISPLAY", new RemoteKey("IR_ACKEY_DISPLAY", 	res.getString(R.string.IR_ACKEY_DISPLAY), 	R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_ANIMDISPLAY", new RemoteKey("IR_ACKEY_ANIMDISPLAY", res.getString(R.string.IR_ACKEY_ANIMDISPLAY), R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_LOCK", new RemoteKey("IR_ACKEY_LOCK", 			res.getString(R.string.IR_ACKEY_LOCK), 		R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_DEMOLD", new RemoteKey("IR_ACKEY_DEMOLD", 		res.getString(R.string.IR_ACKEY_DEMOLD), 	R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_POWERSAVING", new RemoteKey("IR_ACKEY_POWERSAVING", res.getString(R.string.IR_ACKEY_POWERSAVING), R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_SILENT", new RemoteKey("IR_ACKEY_SILENT", 		res.getString(R.string.IR_ACKEY_SILENT), 	R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_RHYTHM", new RemoteKey("IR_ACKEY_RHYTHM", 		res.getString(R.string.IR_ACKEY_RHYTHM), 	R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_FUZZY", new RemoteKey("IR_ACKEY_FUZZY", 		res.getString(R.string.IR_ACKEY_FUZZY), 	R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_SENSING", new RemoteKey("IR_ACKEY_SENSING", 	res.getString(R.string.IR_ACKEY_SENSING), 	R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_LIGHT", new RemoteKey("IR_ACKEY_LIGHT", 		res.getString(R.string.IR_ACKEY_LIGHT), 	R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_PRESET", new RemoteKey("IR_ACKEY_PRESET", 		res.getString(R.string.IR_ACKEY_PRESET), 	R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_MOTION_SENSING", new RemoteKey("IR_ACKEY_MOTION_SENSING", res.getString(R.string.IR_ACKEY_MOTION_SENSING),	R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_LIGHT_SENSING", new RemoteKey("IR_ACKEY_LIGHT_SENSING", res.getString(R.string.IR_ACKEY_LIGHT_SENSING), 	R.drawable.ac_dummy));

        // timer
        keyMapping.put("IR_ACKEY_RTC", new RemoteKey("IR_ACKEY_RTC", 			res.getString(R.string.IR_ACKEY_RTC), 		R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_ONTIMER", new RemoteKey("IR_ACKEY_ONTIMER", 	res.getString(R.string.IR_ACKEY_ONTIMER), 	R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_OFFTIMER", new RemoteKey("IR_ACKEY_OFFTIMER", 	res.getString(R.string.IR_ACKEY_OFFTIMER), 	R.drawable.ac_dummy));
        keyMapping.put("IR_ACKEY_TIMER", new RemoteKey("IR_ACKEY_TIMER", 		res.getString(R.string.IR_ACKEY_TIMER), 	R.drawable.ac_dummy));

    }

    @Override
    protected String[] initKeys() {
        return new String[] {

                "IR_ACKEY_POWER",
                "IR_ACKEY_MODE",

                "IR_ACKEY_TEMP",
                "IR_ACKEY_FANSPEED",

                "IR_ACKEY_AIRSWING_UD",
                "IR_ACKEY_AIRSWING_LR",

                "IR_ACKEY_ONTIMER",
                "IR_ACKEY_OFFTIMER",

                "IR_ACKEY_SLEEP"
        };
    }
}

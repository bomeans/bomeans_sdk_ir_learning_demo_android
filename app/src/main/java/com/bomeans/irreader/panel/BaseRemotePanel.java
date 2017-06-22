package com.bomeans.irreader.panel;

import android.content.Context;

/**
 * Created by ray on 2017/6/19.
 */

public abstract class BaseRemotePanel implements IRemotePanel {

    private Context mContext;
    private boolean mLayoutCreated = false;
    private boolean mActivityIsRunning = true;

    public BaseRemotePanel(Context context) {
        mContext = context;
    }

    protected Context getContext() { return mContext; }

    protected abstract AbstractDefaultKeys getDefaultKeys();

    @Override
    public void createRemoteLayoutCallback() {
        mLayoutCreated = true;
    }

    @Override
    public void saveStates() {

    }

    @Override
    public boolean keyLayoutCreated() {
        return mLayoutCreated;
    }

    @Override
    public void setActivityIsRunning(Boolean isRunning) {
        mActivityIsRunning = isRunning;
    }

    public boolean isActivityRunning() { return mActivityIsRunning; }
}

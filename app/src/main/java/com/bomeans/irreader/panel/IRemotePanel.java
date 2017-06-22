package com.bomeans.irreader.panel;

/**
 * Created by ray on 2017/6/19.
 */

public interface IRemotePanel {

    boolean loadRemote(String typeId, String brandId, String remoteId, boolean getNew, IRemotePanelLoadedCallback callback);

    void createRemoteLayoutCallback();

    void saveStates();

    //String getCategoryString();

    boolean keyLayoutCreated();

    void setActivityIsRunning(Boolean isRunning);
}

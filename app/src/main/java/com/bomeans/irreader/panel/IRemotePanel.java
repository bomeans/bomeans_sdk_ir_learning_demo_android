package com.bomeans.irreader;

/**
 * Created by ray on 2017/6/19.
 */

public interface IRemotePanel {

    /**
     * Get the associate remote XML file from the server or the local database
     * (if down-loaded before.)
     * @param remote remote instance
     * @param refreshMenuItem refresh menu item
     * @param bIsRefresh is triggered by refresh
     * @return true if succeeded, false otherwise
     */
    //boolean getRemoteXML(AbstractFavRemote remote, MenuItem refreshMenuItem, boolean bIsRefresh);

    /**
     * dynamically create the GUI layout corresponding to the remote XML content.
     */
    void createRemoteLayoutCallback();

    void saveStates();

    String getCategoryString();

    boolean keyLayoutCreated();

    void setActivityIsRunning(Boolean isRunning);
}

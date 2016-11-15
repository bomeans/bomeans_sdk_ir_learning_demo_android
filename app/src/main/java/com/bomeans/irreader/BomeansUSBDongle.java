package com.bomeans.irreader;

import android.content.Context;

import com.bomeans.IRKit.BIRIRBlaster;
import com.bomeans.IRKit.BIRReceiveDataCallback;
import com.bomeans.IRKit.IRKit;
import com.bomeans.usbserial.BMSUsbSerial;
import com.bomeans.usbserial.IBMSUsbSerialCallback;
import com.bomeans.usbserial.IRUARTCommand;

import java.util.ArrayList;

/**
 * Created by ray on 16/8/30.
 */
public class BomeansUSBDongle implements IBMSUsbSerialCallback, BIRIRBlaster {

    private BMSUsbSerial mUsbSerial;
    private Context mContext;
    final private ArrayList<IBomeansUSBDongleCallback> mCallbackList = new ArrayList<>();
    private Boolean mDeviceAttached = false;
    private BIRReceiveDataCallback mReceiveDataCallback = null;

    public BomeansUSBDongle(Context context) {
        mContext = context;
    }

    public void registerCallback(IBomeansUSBDongleCallback callback) {
        for (IBomeansUSBDongleCallback tmpCallback : mCallbackList) {
            if (tmpCallback.equals(callback)) {
                return;
            }
        }

        mCallbackList.add(callback);
    }

    public void unregisterCallback(IBomeansUSBDongleCallback callback) {
        for (IBomeansUSBDongleCallback tmpCallback : mCallbackList) {
            if (tmpCallback.equals(callback)) {
                mCallbackList.remove(tmpCallback);
                return;
            }
        }
    }

    public void rescan() {
        mDeviceAttached = _rescan();

        for (IBomeansUSBDongleCallback callback : mCallbackList) {
            callback.onDeviceStatusChanged(mDeviceAttached);
        }
    }

    private Boolean _rescan() {

        mUsbSerial = new BMSUsbSerial(mContext);

        mUsbSerial.registerCallback(this);

        if (mUsbSerial.isValid()) {
            mUsbSerial.createDeviceList();
            if (mUsbSerial.getDeviceCount() > 0) {
                if (mUsbSerial.connect(
                        0,
                        38400,
                        BMSUsbSerial.UARTDataBits.BITS_8,
                        BMSUsbSerial.UARTStopBits.BITS_1,
                        BMSUsbSerial.UARTParity.NONE,
                        BMSUsbSerial.UARTFlowControl.NONE)) {

                    return true;
                }
            }
        }

        mUsbSerial = null;
        return false;
    }

    @Override
    public void onCommandReceived(IRUARTCommand uartCommand) {

        // FIXME temp removed
        /*
        // just relay the callback
        for (IBomeansUSBDongleCallback callback : mCallbackList) {
            callback.onCommandReceived(uartCommand);
        }*/

        if (null != mReceiveDataCallback) {
            mReceiveDataCallback.onDataReceived(uartCommand.getCommandBytes());
        }
    }

    public Boolean isAttached() {
        return mDeviceAttached;
    }

    /*
    public Boolean sendCommand(IRUARTCommand uartCmd) {
        if (null != mUsbSerial && mUsbSerial.isOpen()) {
            return mUsbSerial.sendCommand(uartCmd);
        }

        return false;
    }*/

    @Override
    public int sendData(byte[] bytes) {

        /*
        // debug
        String info = String.format("%2X, %2X,...%2X", bytes[0], bytes[1], bytes[bytes.length-1]);
        Toast.makeText(mContext, info, Toast.LENGTH_SHORT).show();*/

        if (null != mUsbSerial) {
            if (mUsbSerial.sendDataBytes(bytes)) {
                return IRKit.BIROK;
            }
        }

        return IRKit.BIRTransmitFail;
    }

    @Override
    public int isConnection() {
        if ((null != mUsbSerial) && mUsbSerial.isOpen()) {
            return IRKit.BIROK;
        } else {
            return IRKit.BIRNotConnectToNetWork;
        }
    }

    @Override
    public void setReceiveDataCallback(BIRReceiveDataCallback birReceiveDataCallback) {
        mReceiveDataCallback = birReceiveDataCallback;
    }

    public interface IBomeansUSBDongleCallback {

        //void onCommandReceived(IRUARTCommand uartCommand);
        void onDeviceStatusChanged(Boolean attached);
    }
}



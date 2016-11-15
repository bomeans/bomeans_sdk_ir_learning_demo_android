package com.bomeans.usbserial;

import android.content.Context;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

import java.util.ArrayList;


/**
 * Created by ray on 16/7/7.
 */
public class BMSUsbSerial {

    enum DeviceStatus {
        DEV_NOT_CONNECT,
        DEV_NOT_CONFIG,
        DEV_CONFIG
    }

    public enum UARTDataBits {
        BITS_7,
        BITS_8
    }

    public enum UARTStopBits {
        BITS_1,
        BITS_2
    }

    public enum UARTFlowControl {
        NONE,
        RTS_CTS,
        DTR_DSR,
        XON_XOFF
    }

    public enum UARTParity {
        NONE,
        ODD,
        EVEN,
        MARK,
        SPACE
    }

    private final byte XON = 0x11;  // Resume transmission
    private final byte XOFF = 0x13; // Pause transmission

    private Context mContext;

    private Boolean mUartConfigured = false;
    private Boolean mEnableReadThread = false;
    private UARTReadThread mReadThread = null;
    private final int MAX_NUM_BYTES = 65536;

    private DataBuffer mDataBuffer = new DataBuffer(MAX_NUM_BYTES);

    // j2xx
    private static D2xxManager mFtD2xx = null;
    private FT_Device mFtDev;

    private int mDeviceCount = 0;
    private int mCurrentPortIndex = -1; // 0-base port index

    final private ArrayList<IBMSUsbSerialCallback> mCallbackList = new ArrayList<>();

    public BMSUsbSerial(Context context) {

        mContext = context;

        try {
            mFtD2xx = D2xxManager.getInstance(context);
        } catch (D2xxManager.D2xxException e) {
            mFtD2xx = null;
            e.printStackTrace();
        }
    }

    public void registerCallback(IBMSUsbSerialCallback callback) {
        for (IBMSUsbSerialCallback tmpCallback : mCallbackList) {
            if (tmpCallback.equals(callback)) {
                return;
            }
        }

        mCallbackList.add(callback);
    }

    public void unregisterCallback(IBMSUsbSerialCallback callback) {
        for (IBMSUsbSerialCallback tmpCallback : mCallbackList) {
            if (tmpCallback.equals(callback)) {
                mCallbackList.remove(tmpCallback);
                return;
            }
        }
    }

    public Boolean isValid() {
        return null != mFtD2xx;
    }

    public void setPortNumber(int portNumber)
    {
        mCurrentPortIndex = portNumber;
    }

    public void createDeviceList()
    {
        int tempDevCount = mFtD2xx.createDeviceInfoList(mContext);

        if (tempDevCount > 0)
        {
            if( mDeviceCount != tempDevCount )
            {
                mDeviceCount = tempDevCount;
            }
        }
        else
        {
            mDeviceCount = 0;
            mCurrentPortIndex = -1;
        }
    }

    public int getDeviceCount() {
        return mDeviceCount;
    }

    public Boolean isOpen() {
        if (null != mFtDev) {
            return mFtDev.isOpen();
        }

        return false;
    }

    public Boolean connect(
            int portNumber,
            int baudRate,
            UARTDataBits dataBits,
            UARTStopBits stopBits,
            UARTParity parity,
            UARTFlowControl flowControl) {

        if (!connect(portNumber)) {
            return false;
        }

        setConfig(baudRate, dataBits, stopBits, parity, flowControl);

        return true;
    }

    private Boolean connect(int portNumber) {
        if (portNumber > mDeviceCount) {
            portNumber = 0;
        }

        if (mCurrentPortIndex == portNumber
                && mFtDev != null
                && true == mFtDev.isOpen() )
        {
            return true;    // already connected
        }

        if (mEnableReadThread)
        {
            mEnableReadThread = false;
            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e) {e.printStackTrace();}
        }

        mFtDev = mFtD2xx.openByIndex(mContext, portNumber);
        mUartConfigured = false;

        if (mFtDev == null) {
            return false;
        }

        if (!mFtDev.isOpen()) {
            return false;
        }

        mCurrentPortIndex = portNumber;

        if (!mEnableReadThread)
        {
            mReadThread = new UARTReadThread();
            mReadThread.start();
        }

        return true;
    }

    public DeviceStatus checkDevice()
    {
        if (null == mFtDev || !mFtDev.isOpen()) {
            return DeviceStatus.DEV_NOT_CONNECT;
        } else if (false == mUartConfigured) {
            return DeviceStatus.DEV_NOT_CONFIG;
        }

        return DeviceStatus.DEV_CONFIG;
    }

    private byte getParityValue(UARTParity parity) {
        switch (parity) {
            case NONE:
                return D2xxManager.FT_PARITY_NONE;
            case ODD:
                return D2xxManager.FT_PARITY_ODD;
            case EVEN:
                return D2xxManager.FT_PARITY_EVEN;
            case MARK:
                return D2xxManager.FT_PARITY_MARK;
            case SPACE:
                return D2xxManager.FT_PARITY_SPACE;
            default:
                return D2xxManager.FT_PARITY_NONE;
        }
    }

    private short getFlowControlValue(UARTFlowControl flowControl) {
        switch (flowControl) {
            case NONE:
                return D2xxManager.FT_FLOW_NONE;
            case RTS_CTS:
                return D2xxManager.FT_FLOW_RTS_CTS;
            case DTR_DSR:
                return D2xxManager.FT_FLOW_DTR_DSR;
            case XON_XOFF:
                return D2xxManager.FT_FLOW_XON_XOFF;
            default:
                return D2xxManager.FT_FLOW_NONE;
        }
    }

    private byte getDataBitsValue(UARTDataBits dataBits) {
        switch (dataBits) {
            case BITS_7:
                return D2xxManager.FT_DATA_BITS_7;
            case BITS_8:
                return D2xxManager.FT_DATA_BITS_8;
            default:
                return D2xxManager.FT_DATA_BITS_8;
        }
    }

    private byte getStopBitsValue(UARTStopBits stopBits) {
        switch (stopBits) {
            case BITS_1:
                return D2xxManager.FT_STOP_BITS_1;
            case BITS_2:
                return D2xxManager.FT_STOP_BITS_2;
            default:
                return D2xxManager.FT_STOP_BITS_1;
        }
    }

    void setConfig(
            int baud,
            UARTDataBits dataBits,
            UARTStopBits stopBits,
            UARTParity parity,
            UARTFlowControl flowControl) {

        // configure port
        // reset to UART mode for 232 devices
        mFtDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

        // baud rate
        mFtDev.setBaudRate(baud);

        mFtDev.setDataCharacteristics(
                getDataBitsValue(dataBits),
                getStopBitsValue(stopBits),
                getParityValue(parity));

        // set flow control
        mFtDev.setFlowControl(getFlowControlValue(flowControl), XON, XOFF);

        mUartConfigured = true;
    }

    public void disconnect() {

        mDeviceCount = 0;
        mCurrentPortIndex = -1;

        // terminate read thread
        mEnableReadThread = false;
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (null != mFtDev) {
            if (mFtDev.isOpen()) {
                mFtDev.close();
            }
        }
    }

    public Boolean sendCommand(IRUARTCommand uartCmd) {

        if (uartCmd != null && uartCmd.isValid()) {
            return sendDataBytes(uartCmd.getCommandBytes());
        }

        return false;
    }

    public Boolean sendDataBytes(byte[] buffer) {

        // reset the receving data buffer
        clearBuffer();

        if (null == mFtDev || !mFtDev.isOpen()) {
            return false;
        }

        if (null == buffer || buffer.length == 0) {
            return false;
        }

        return mFtDev.write(buffer, buffer.length) > 0;
    }

    public void clearBuffer() {
        mDataBuffer.reset();
    }

    class UARTReadThread extends Thread {

        private final int USB_DATA_BUFFER = 8192;

        UARTReadThread() {
            this.setPriority(MAX_PRIORITY);
        }

        public void run() {
            byte[] usbData = new byte[USB_DATA_BUFFER];
            int readCount = 0;
            mEnableReadThread = true;

            while (mEnableReadThread) {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                readCount = mFtDev.getQueueStatus();

                if (readCount > 0) {
                    if (readCount > USB_DATA_BUFFER) {
                        readCount = USB_DATA_BUFFER;
                    }
                    mFtDev.read(usbData, readCount);

                    /*
                    // debug
                    String msg = "";
                    for (int count = 0; count < readCount; count++) {
                        msg += String.format("0x%02X, ", usbData[count]);
                    }

                    final String msg2 = msg;
                    ((AppCompatActivity)mContext).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(mContext, msg2, Toast.LENGTH_SHORT).show();
                        }
                    });*/

                    mDataBuffer.writeData(usbData, readCount);


                    /*
                    byte[] tmp = mDataBuffer.readData(false);
                    // debug
                    String msg = "";
                    for (int count = 0; count < tmp.length; count++) {
                        msg += String.format("0x%02X, ", tmp[count]);
                    }

                    final String msg2 = msg;
                    ((AppCompatActivity)mContext).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(mContext, msg2, Toast.LENGTH_SHORT).show();
                        }
                    });*/


                    // check if complete package arrived
                    IRUARTCommand uartCmd;
                    while ((uartCmd = mDataBuffer.getUartCommand()) != null) {
                        for (IBMSUsbSerialCallback callback : mCallbackList) {
                            try {
                                callback.onCommandReceived(uartCmd);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }


            }

        }
    }


}

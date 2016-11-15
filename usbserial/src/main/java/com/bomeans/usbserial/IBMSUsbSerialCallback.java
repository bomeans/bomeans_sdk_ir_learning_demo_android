package com.bomeans.usbserial;

/**
 * Created by ray on 16/7/12.
 */
public interface IBMSUsbSerialCallback {

    void onCommandReceived(IRUARTCommand uartCommand);
}

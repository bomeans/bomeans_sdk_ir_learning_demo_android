package com.bomeans.usbserial;

/**
 * Created by ray on 16/7/11.
 */
public class DataBuffer {

    private int mBufferSize;
    private byte[] mBuffer;
    private int mWriteIndex;    // next index for writing
    private int mReadIndex;     // next index for reading



    public DataBuffer(int bufferSize) {

        mBufferSize = bufferSize;
        if (mBufferSize < 2048) {
            mBufferSize = 2048;
        }
        mBuffer = new byte[mBufferSize];
        mWriteIndex = 0;
        mReadIndex = 0;
    }

    public synchronized void reset() {
        mWriteIndex = 0;
        mReadIndex = 0;
    }

    public synchronized void writeData(byte[] dataBytes, int byteCount) {
        if (byteCount > dataBytes.length) {
            byteCount = dataBytes.length;
        }

        for (int i = 0; i < byteCount; i++) {
            mBuffer[mWriteIndex] = dataBytes[i];
            mWriteIndex++;
            mWriteIndex %= mBufferSize;
        }

        // drop all data before the package prefix
        while (mBuffer[mReadIndex] != (byte)0xFF && getDataCount() > 0) {
            mReadIndex++;
        }
    }

    public synchronized IRUARTCommand getUartCommand() {

        byte[] dataBytes = readData(false);

        if (dataBytes.length < 8) {
            return null;
        }

        int startIndex = 0;
        while ((dataBytes[startIndex] != (byte)0xFF) && (dataBytes[startIndex + 1] != (byte)0x61)) {
            startIndex++;

            if (startIndex >= dataBytes.length - 1) {
                reset();
                return null;
            }
        }

        if (startIndex != 0) {
            byte[] newDataBytes = new byte[dataBytes.length - startIndex];
            System.arraycopy(dataBytes, startIndex, newDataBytes, 0, dataBytes.length - startIndex);
            dataBytes = newDataBytes;

            // advance the read index (skipped bytes)
            mReadIndex += startIndex;
            mReadIndex %= mBufferSize;
        }

        IRUARTCommand uartCmd = new IRUARTCommand(dataBytes);
        if (uartCmd.isValid()) {

            // advance the read index
            mReadIndex += uartCmd.getCommandBytes().length;
            mReadIndex %= mBufferSize;

            return uartCmd;
        }

        return null;
    }

    private int getDataCount() {
        if (mWriteIndex >= mReadIndex) {
            return mWriteIndex - mReadIndex;
        } else {
            return (mBufferSize - mReadIndex) + mWriteIndex;
        }
    }

    private byte[] readData(Boolean clearAfterRead) {

        byte[] dataBytes;
        int dataCount;
        int readIdx;
        int bufferIdx;

        if (mWriteIndex >= mReadIndex) {
            dataCount = mWriteIndex - mReadIndex;
            dataBytes = new byte[dataCount];

            bufferIdx = 0;
            for (readIdx = mReadIndex; readIdx < mWriteIndex; readIdx++) {
                dataBytes[bufferIdx] = mBuffer[readIdx];
                bufferIdx++;
            }
        } else {
            dataCount = (mBufferSize - mReadIndex) + mWriteIndex;
            dataBytes = new byte[dataCount];

            for (readIdx = mReadIndex, bufferIdx = 0; readIdx < mBufferSize; readIdx++, bufferIdx++) {
                dataBytes[bufferIdx] = mBuffer[readIdx];
            }
            for (readIdx = 0; readIdx < mWriteIndex; readIdx++, bufferIdx++) {
                dataBytes[bufferIdx] = mBuffer[readIdx];
            }
        }

        if (clearAfterRead) {
            mReadIndex += dataCount;
            mReadIndex %= mBufferSize;
        }

        return dataBytes;
    }
}

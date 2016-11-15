package com.bomeans.usbserial;

public class IRUARTCommand {
	
	private static byte PREFIX0_CODE = (byte)0xFF;
    private static byte PREFIX1_CODE = (byte)0x61;
    private static byte VERSION_CODE = (byte)0x00;
    private static byte POSTFIX_CODE = (byte)0xF0;

    private static byte UNKNOWN_COMMAND_ID = (byte)0xFF;

    private byte fCommandID = UNKNOWN_COMMAND_ID;
    private byte[] fPayload = null;
    private byte[] fCmdBytes = null;
    private byte fVersionCode = VERSION_CODE;

    public IRUARTCommand(byte[] cmdBytes)
    {
        if (cmdBytes.length < 8)
        {
            fCommandID = UNKNOWN_COMMAND_ID;
            return;
        }

        // prefix & postfix
        if ((cmdBytes[0] != PREFIX0_CODE) || (cmdBytes[1] != PREFIX1_CODE) /*|| (cmdBytes[cmdBytes.length - 1] != POSTFIX_CODE)*/)
        {
            fCommandID = UNKNOWN_COMMAND_ID;
            return;
        }

        // version
        fVersionCode = cmdBytes[2];

        // length
        int length = (cmdBytes[3] & 0xff) + (((int)(cmdBytes[4] & 0xff)) << 8);   // command (1-byte) + data length + checksum (1-byte)
        if (length + 6 > cmdBytes.length)//if (length + 6 != cmdBytes.length)   // allow passed in data has more bytes than a full package
        {
            fCommandID = UNKNOWN_COMMAND_ID;
            return;
        }
        int payloadLength = length - 2; // data length

        // command ID
        fCommandID = cmdBytes[5];

        // payload data
        if (payloadLength > 0)
        {
            fPayload = new byte[payloadLength];
            System.arraycopy(cmdBytes, 6, fPayload, 0, payloadLength);
        }

        // checksum & postfix
        int checksum = 0;
        for (int i = 2; i < 6 + payloadLength; i++)
        {
            checksum += cmdBytes[i];
        }
        checksum &= 0xFF;
        if ((cmdBytes[6 + payloadLength] != (byte)checksum) ||
                (cmdBytes[7 + payloadLength] != POSTFIX_CODE))
        {
            fCommandID = UNKNOWN_COMMAND_ID;
            fPayload = null;
            fVersionCode = 0;
            return;
        }

        // copy the data bytes
        fCmdBytes = new byte[8 + payloadLength];
        System.arraycopy(cmdBytes, 0, fCmdBytes, 0, fCmdBytes.length);
    }

    public Boolean isValid() {
        return fCommandID != UNKNOWN_COMMAND_ID;
    }

    public byte getCommandID()
    {
        return fCommandID;
    }

    /**
     * payload is the command data without the prefix, postfix, version code, command id, etc.
     * @return
     */
    public byte[] getPayload()
    {
        return fPayload;
    }

    /**
     * return the actual byte array (for sending)
     * @return command byte array
     */
    public byte[] getCommandBytes()
    {
        return fCmdBytes;
    }

    public byte getVersionCode()
    {
    	return fVersionCode;
    }
}

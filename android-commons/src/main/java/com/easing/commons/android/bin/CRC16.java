package com.easing.commons.android.bin;

import com.github.snksoft.crc.CRC;

public class CRC16 {

    //通过CRC16-MODBUS算法生成字节校验码
    public static String MODBUS(String hex) {
        byte[] bin = ByteUtil.hexToByteArray(hex);
        long longCRC = CRC.calculateCRC(new CRC.Parameters(16, 0x8005, 0xFFFF, true, true, 0x0000), bin);
        return ByteUtil.longToHex(longCRC, 2);
    }

    //通过CRC16-IBM算法生成字节校验码
    public static String IBM(String hex) {
        byte[] bin = ByteUtil.hexToByteArray(hex);
        long longCRC = CRC.calculateCRC(new CRC.Parameters(16, 0x8005, 0x0000, true, true, 0x0000), bin);
        return ByteUtil.longToHex(longCRC, 2);
    }

    public static String reversedMODBUS(String hex) {
        return ByteUtil.reverse(MODBUS(hex));
    }

    public static String reversedIBM(String hex) {
        return ByteUtil.reverse(IBM(hex));
    }
}

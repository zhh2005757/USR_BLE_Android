package com.usr.usrsimplebleassistent;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    private final byte array[]={0x4D,0x44,0x00,0x00,0x00,0x25, (byte) 0xFB,0x3E, (byte) 0xEB, (byte) 0x90};

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        System.out.println(calculate(array));
    }

    public String calculate(byte[] buffer){
        String res="";
        double speed= (double)(buffer[2] & 0xFF *256+buffer[3]& 0xFF)/100; //保留两位小数
        double timestamp=(double)(buffer[4] & 0xFF *(int)Math.pow(2,24)+buffer[5]& 0xFF*(int)Math.pow(2,16)+buffer[6]& 0xFF*(int)Math.pow(2,8)+buffer[7]& 0xFF)/100; //保留三位小数
        res=String.format(Locale.US,"Speed: %.2f km/h\nTimestamp: %.3f s",speed,timestamp);
        return res;
    }
}
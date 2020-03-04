/*
 * Copyright Cypress Semiconductor Corporation, 2014-2015 All rights reserved.
 * 
 * This software, associated documentation and materials ("Software") is
 * owned by Cypress Semiconductor Corporation ("Cypress") and is
 * protected by and subject to worldwide patent protection (UnitedStates and foreign), United States copyright laws and international
 * treaty provisions. Therefore, unless otherwise specified in a separate license agreement between you and Cypress, this Software
 * must be treated like any other copyrighted material. Reproduction,
 * modification, translation, compilation, or representation of this
 * Software in any other form (e.g., paper, magnetic, optical, silicon)
 * is prohibited without Cypress's express written permission.
 * 
 * Disclaimer: THIS SOFTWARE IS PROVIDED AS-IS, WITH NO WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO,
 * NONINFRINGEMENT, IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE. Cypress reserves the right to make changes
 * to the Software without notice. Cypress does not assume any liability
 * arising out of the application or use of Software or any product or
 * circuit described in the Software. Cypress does not authorize its
 * products for use as critical components in any products where a
 * malfunction or failure may reasonably be expected to result in
 * significant injury or death ("High Risk Product"). By including
 * Cypress's product in a High Risk Product, the manufacturer of such
 * system or application assumes all risk of such use and in doing so
 * indemnifies Cypress against all liability.
 * 
 * Use of this Software may be limited by and subject to the applicable
 * Cypress software license agreement.
 * 
 * 
 */

package com.usr.usrsimplebleassistent.BLEProfileDataParserClasses;

import android.bluetooth.BluetoothGattCharacteristic;

import com.usr.usrsimplebleassistent.DataModelClasses.GlucoseDataModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Class used for parsing Glucose related information
 */
public class GlucoseParser {

    private static final int FIRST_BITMASK = 0x01;
    public static final int SECOND_BITMASK = FIRST_BITMASK << 1;
    public static final int THIRD_BITMASK = FIRST_BITMASK << 2;
    public static final int FOURTH_BITMASK = FIRST_BITMASK << 3;
    public static final int FIFTH_BITMASK = FIRST_BITMASK << 4;
    public static final int SIXTH_BITMASK = FIRST_BITMASK << 5;
    public static final int SEVENTH_BITMASK = FIRST_BITMASK << 6;
    public static final int EIGTH_BITMASK = FIRST_BITMASK << 7;

    /**
     * Get the Health Glucose
     *
     * @param characteristic
     * @return ArrayList<String>
     */
    public static ArrayList<String> getGlucoseHealth(
            BluetoothGattCharacteristic characteristic) {
        ArrayList<String> glucoseInfo = new ArrayList<String>();
        GlucoseDataModel record = new GlucoseDataModel();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();

        int l2 = characteristic.getIntValue(17, 0).intValue();
        int i3 = 1;
        boolean flag7, flag9, flag8, flag10;
        int j3, k3, l3, i4, j4, k4, l4, i5, j5;

        if ((l2 & 1) > 0) {
            flag7 = true;
        } else {
            flag7 = false;
        }
        if ((l2 & 2) > 0) {
            flag8 = true;
        } else {
            flag8 = false;
        }
        if ((l2 & 4) > 0) {
            j3 = 1;
        } else {
            j3 = 0;
        }
        if ((l2 & 8) > 0) {
            flag9 = true;
        } else {
            flag9 = false;
        }
        if ((l2 & 0x10) > 0) {
            flag10 = true;
        } else {
            flag10 = false;
        }
        k3 = i3 + 2;
        l3 = characteristic.getIntValue(18, 3).intValue();
        i4 = characteristic.getIntValue(17, 5).intValue();
        j4 = characteristic.getIntValue(17, 6).intValue();
        k4 = characteristic.getIntValue(17, 7).intValue();
        l4 = characteristic.getIntValue(17, 8).intValue();
        i5 = characteristic.getIntValue(17, 9).intValue();
        j5 = k3 + 7;

        calendar.set(l3, i4, j4, k4, l4, i5);
        record.time = calendar;
        if (flag7) {
            record.timeOffset = characteristic.getIntValue(34, j5).intValue();
            j5 += 2;
        }
        if (flag8) {
            String glucoseConctrn = " "
                    + characteristic.getFloatValue(50, j5).floatValue();
            String glucoseConctrnUnit;
            record.unit = j3;
            if (record.unit == 1) {
                glucoseConctrnUnit = "mol/L";
            } else {
                glucoseConctrnUnit = "kg/L";
            }
            int k5 = characteristic.getIntValue(17, j5 + 2).intValue();
            record.type = (k5 & 0xf0) >> 4;
            record.sampleLocation = k5 & 0xf;
            String glucoseType = "";
            String glucoseSampleLocation = "";
            glucoseType = glucoseType(record.type);
            glucoseSampleLocation = glucoseSampleLocation(record.sampleLocation);
            glucoseInfo.add(0, glucoseConctrn);
            glucoseInfo.add(1, glucoseType);
            glucoseInfo.add(2, glucoseSampleLocation);
            glucoseInfo.add(3, sdf.format(calendar.getTime()));
            glucoseInfo.add(4, glucoseConctrnUnit);
            j5 += 3f;
        }
        if (flag9) {
            record.status = characteristic.getIntValue(18, j5).intValue();
            j5 += 2;
        }
        return glucoseInfo;
    }

    /**
     * Get the glucose type
     *
     * @param lowNibbleTtype
     * @return
     */
    private static String glucoseType(int lowNibbleTtype) {
        String glucoseType;
        switch (lowNibbleTtype) {
            case 0:
                glucoseType = "Reserved for future use";
                break;
            case 1:
                glucoseType = "Capillary Whole blood";
                break;
            case 2:
                glucoseType = "Capillary Plasma";
                break;
            case 3:
                glucoseType = "Venous Whole blood";
                break;
            case 4:
                glucoseType = "Venous Plasma";
                break;
            case 5:
                glucoseType = "Arterial Whole blood";
                break;
            case 6:
                glucoseType = "Arterial Plasma";
                break;
            case 7:
                glucoseType = "Undetermined Whole blood";
                break;
            case 8:
                glucoseType = "Undetermined Plasma";
                break;
            case 9:
                glucoseType = "Interstitial Fluid (ISF)";
                break;
            case 10:
                glucoseType = "Control Solution";
                break;
            default:
                glucoseType = "Reserved for future use";
                break;
        }
        return glucoseType;
    }

    /**
     * Get the glucose sample location
     *
     * @param highNibbleSampleLocation
     * @return
     */
    private static String glucoseSampleLocation(int highNibbleSampleLocation) {
        String glucoseSampleLocation;
        switch (highNibbleSampleLocation) {
            case 0:
                glucoseSampleLocation = "Reserved for future use";
                break;
            case 1:
                glucoseSampleLocation = "Finger";
                break;
            case 2:
                glucoseSampleLocation = "Alternate Site Test (AST)";
                break;
            case 3:
                glucoseSampleLocation = "Earlobe";
                break;
            case 4:
                glucoseSampleLocation = "Control solution";
                break;
            case 15:
                glucoseSampleLocation = "Sample Location value not available";
                break;
            default:
                glucoseSampleLocation = "Reserved for future use";
                break;

        }
        return glucoseSampleLocation;
    }

}

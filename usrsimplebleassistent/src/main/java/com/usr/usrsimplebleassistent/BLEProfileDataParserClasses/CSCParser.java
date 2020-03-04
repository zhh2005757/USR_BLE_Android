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

import java.util.ArrayList;

/**
 * Class used for parsing Cycling speed and cadence related information
 */
public class CSCParser {

    private static ArrayList<String> CSCInfo = new ArrayList<String>();
    private static String cyclingExtraDistance;
    private static String cyclingExtraSpeed;
    private static String cyclingDistance;
    private static String cyclingCadence;
    private static String gearRatio;
    static String wheelCadence;
    private static int mFirstWheelRevolutions = -1;
    private static int mLastWheelRevolutions = -1;
    private static int mLastWheelEventTime = -1;
    private static float mWheelCadence = -1F;
    private static int mLastCrankRevolutions = -1;
    private static int mLastCrankEventTime = -1;
    private static final int WHEEL_CONST = 65535;

    /**
     * Get the Running Speed and Cadence
     *
     * @param characteristic
     * @return ArrayList<String>
     */
    public static ArrayList<String> getCyclingSpeednCadence(
            BluetoothGattCharacteristic characteristic) {
        boolean flag = true;
        byte byte0 = characteristic.getValue()[0];
        int i = 1;
        boolean flag1;
        if ((byte0 & 1) > 0) {
            flag1 = flag;
        } else {
            flag1 = false;
        }
        if ((byte0 & 2) <= 0) {
            flag = false;
        }
        if (flag1) {
            int i1 = characteristic.getIntValue(20, i).intValue();
            int j1 = i + 4;
            int k1 = characteristic.getIntValue(18, j1).intValue();
            i = j1 + 2;
            onWheelMeasurementReceived(i1, k1);
        }
        if (flag) {
            int j = characteristic.getIntValue(18, i).intValue();
            int k = i + 2;
            int l = characteristic.getIntValue(18, k).intValue();
            onCrankMeasurementReceived(j, l);
        }
        return CSCInfo;
    }

    private static void onWheelMeasurementReceived(int i, int j) {
        int WHEEL_CIRCUMFERENCE = 2100;
        if (mFirstWheelRevolutions < 0) {
            mFirstWheelRevolutions = i;
        }
        if (mLastWheelEventTime == j) {
            return;
        }
        if (mLastWheelRevolutions >= 0) {
            float f;
            float f1;
            float f2;
            float f3;
            float f4;
            if (j < mLastWheelEventTime) {
                f = (float) ((WHEEL_CONST + j) - mLastWheelEventTime) / 1024F;
            } else {
                f = (float) (j - mLastWheelEventTime) / 1024F;
            }
            f1 = (float) (WHEEL_CIRCUMFERENCE * (i - mLastWheelRevolutions)) / 1000F;
            f2 = ((float) i * (float) WHEEL_CIRCUMFERENCE) / 1000000F;
            f3 = ((float) (i - mFirstWheelRevolutions) * (float) WHEEL_CIRCUMFERENCE) / 1000F;
            f4 = f1 / f;
            mWheelCadence = (60F * (float) (i - mLastWheelRevolutions)) / f;
            cyclingDistance = "" + f2;
            CSCInfo.add(0, cyclingDistance);
            cyclingExtraSpeed = "" + f4;
            CSCInfo.add(3, cyclingExtraSpeed);
            cyclingExtraDistance = "" + f3;
            CSCInfo.add(4, cyclingExtraDistance);
        }
        mLastWheelRevolutions = i;
        mLastWheelEventTime = j;
    }

    private static void onCrankMeasurementReceived(int i, int j) {
        if (mLastCrankEventTime == j) {
            return;
        }
        if (mLastCrankRevolutions >= 0) {
            float f;
            float f1;
            if (j < mLastCrankEventTime) {
                f = (float) ((WHEEL_CONST + j) - mLastCrankEventTime) / 1024F;
            } else {
                f = (float) (j - mLastCrankEventTime) / 1024F;
            }
            f1 = (60F * (float) (i - mLastCrankRevolutions)) / f;
            if (f1 > 0.0F) {
                float f2 = mWheelCadence / f1;
                gearRatio = "" + f2;
                CSCInfo.add(2, gearRatio);
                cyclingCadence = "" + (int) f1;
                CSCInfo.add(1, cyclingCadence);
            }
        }
        mLastCrankRevolutions = i;
        mLastCrankEventTime = j;
    }

}

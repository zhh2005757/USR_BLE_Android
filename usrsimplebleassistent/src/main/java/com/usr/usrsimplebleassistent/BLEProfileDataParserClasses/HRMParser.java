package com.usr.usrsimplebleassistent.BLEProfileDataParserClasses;
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

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.ArrayList;

/**
 * Parser class for parsing the data related to HRM Profile
 */
public class HRMParser {
    private static final int FIRST_BITMASK = 0x01;
    private static final int FOURTH_BITMASK = FIRST_BITMASK << 3;
    private static final int FIFTH_BITMASK = FIRST_BITMASK << 4;
    /**
     * Getting the heart rate
     *
     * @param characteristic
     * @return String
     */
    public static String getHeartRate(BluetoothGattCharacteristic characteristic) {

          int format = -1;
        if (isHeartRateInUINT16(characteristic.getValue()[0])) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
        }
        final int heartRate = characteristic.getIntValue(format, 1);
        return String.valueOf(heartRate);
    }
    /**
     * Getting the Energy Expended
     *
     * @param characteristic
     * @return String
     */
    public static String getEnergyExpended(
            BluetoothGattCharacteristic characteristic) {
        int eeval = 0;

        if (isEEpresent(characteristic.getValue()[0])) {
            if (isHeartRateInUINT16(characteristic.getValue()[0])) {
                eeval = characteristic.getIntValue(
                        BluetoothGattCharacteristic.FORMAT_UINT16, 3);

            } else {
                eeval = characteristic.getIntValue(
                        BluetoothGattCharacteristic.FORMAT_UINT16, 2);

            }
        }
        return String.valueOf(eeval);
    }

    /**
     * Getting the RR-Interval
     *
     * @param characteristic
     * @return ArrayList
     */

    public static ArrayList<Integer> getRRInterval(
            BluetoothGattCharacteristic characteristic) {
        ArrayList<Integer> rrinterval = new ArrayList<Integer>();
        int length = characteristic.getValue().length;
        if (isEEpresent(characteristic.getValue()[0])) {
            if (isHeartRateInUINT16(characteristic.getValue()[0])) {
                if (isRRintpresent(characteristic.getValue()[0])) {
                    int startoffset = 5;
                    for (int i = startoffset; i < length; i += 2) {
                        rrinterval.add(characteristic.getIntValue(
                                BluetoothGattCharacteristic.FORMAT_UINT16, i));
                    }
                }
            } else {
                if (isRRintpresent(characteristic.getValue()[0])) {
                    int startoffset = 4;
                    for (int i = startoffset; i < length; i += 2) {
                        rrinterval.add(characteristic.getIntValue(
                                BluetoothGattCharacteristic.FORMAT_UINT16, i));
                    }
                }
            }
        } else {
            if (isHeartRateInUINT16(characteristic.getValue()[0])) {
                if (isRRintpresent(characteristic.getValue()[0])) {
                    int startoffset = 3;
                    for (int i = startoffset; i < length; i += 2) {
                        rrinterval.add(characteristic.getIntValue(
                                BluetoothGattCharacteristic.FORMAT_UINT16, i));
                    }
                }
            } else {
                if (isRRintpresent(characteristic.getValue()[0])) {
                    int startoffset = 2;
                    for (int i = startoffset; i < length; i += 2) {
                        rrinterval.add(characteristic.getIntValue(
                                BluetoothGattCharacteristic.FORMAT_UINT16, i));
                    }
                }
            }

        }
        return rrinterval;
    }

    /**
     * Checking the RR-Interval Flag
     *
     * @param flags
     * @return boolean
     */
    private static boolean isRRintpresent(byte flags) {
        if ((flags & FIFTH_BITMASK) != 0)
            return true;
        return false;
    }

    /**
     * Checking the Energy Expended Flag
     *
     * @param flags
     * @return boolean
     */
    private static boolean isEEpresent(byte flags) {
        if ((flags & FOURTH_BITMASK) != 0)
            return true;
        return false;
    }

    /**
     * Checking the Heart rate value format Flag
     *
     * @param flags
     * @return boolean
     */
    private static boolean isHeartRateInUINT16(byte flags) {
        return (flags & 1) != 0;
    }

    public static String getBodySensorLocation(
            BluetoothGattCharacteristic characteristic) {
        String body_sensor_location = "";
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            int body_sensor = Integer.valueOf(stringBuilder.toString().trim());
            switch (body_sensor) {
                case 0:
                    body_sensor_location = "Other";
                    break;
                case 1:
                    body_sensor_location = "Chest";
                    break;
                case 2:
                    body_sensor_location = "Wrist";
                    break;
                case 3:
                    body_sensor_location = "Finger";
                    break;
                case 4:
                    body_sensor_location = "Hand";
                    break;
                case 5:
                    body_sensor_location = "Ear Lobe";
                    break;
                case 6:
                    body_sensor_location = "Foot";
                    break;

                default:
                    body_sensor_location = "Reserved for future use";
                    break;
            }

        }
        return body_sensor_location;
    }
}

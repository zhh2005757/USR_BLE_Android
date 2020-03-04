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
 * Class used for parsing Health temperature related information
 */
public class HTMParser {

    private static ArrayList<String> tempInfo = new ArrayList<String>();

    /**
     * Get the thermometer reading
     *
     * @param characteristic
     * @return
     */
    public static ArrayList<String> getHealthThermo(
            BluetoothGattCharacteristic characteristic) {
        String tempUnit = "";
        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            byte flagByte = data[0];
            if ((flagByte & 0x01) != 0) {
                tempUnit = "°F";
            } else {
                tempUnit = "°C";
            }
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
        }
        final float temperature = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 1);


        tempInfo.add(0, "" + temperature);
        tempInfo.add(1, tempUnit);
        return tempInfo;
    }

    /**
     * Get the thermometer sensor location
     *
     * @param characteristic
     * @return
     */
    public static String getHealthThermoSensorLocation(
            BluetoothGattCharacteristic characteristic) {
        String health_thermo_sensor_location = "";
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            int health_body_sensor = Integer.valueOf(stringBuilder.toString()
                    .trim());

            switch (health_body_sensor) {
                case 1:
                    health_thermo_sensor_location = "Armpit";
                    break;
                case 2:
                    health_thermo_sensor_location = "Body (general)";
                    break;
                case 3:
                    health_thermo_sensor_location = "Ear (usually ear lobe)";
                    break;
                case 4:
                    health_thermo_sensor_location = "Finger";
                    break;
                case 5:
                    health_thermo_sensor_location = "Gastro-intestinal Tract";
                    break;
                case 6:
                    health_thermo_sensor_location = "Mouth";
                    break;
                case 7:
                    health_thermo_sensor_location = "Rectum";
                    break;
                case 8:
                    health_thermo_sensor_location = "Tympanum (ear drum)";
                    break;
                case 9:
                    health_thermo_sensor_location = "Toe";
                    break;
                case 10:
                    health_thermo_sensor_location = "Toe";
                    break;
                default:
                    health_thermo_sensor_location = "Reserved for future use";
                    break;
            }

        }
        return health_thermo_sensor_location;
    }
}

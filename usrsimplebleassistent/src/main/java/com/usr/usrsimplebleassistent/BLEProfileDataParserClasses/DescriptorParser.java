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

import android.bluetooth.BluetoothGattDescriptor;

import com.usr.usrsimplebleassistent.Utils.Constants;
import com.usr.usrsimplebleassistent.Utils.GattAttributes;
import com.usr.usrsimplebleassistent.Utils.ReportAttributes;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Parser class for parsing the descriptor value
 */
public class DescriptorParser {

    private static String notificationEnabled = "Notifications enabled";
    private static String notificationDisabled = "Notifications disabled";

    private static String indicationEnabled = "Indications enabled";
    private static String indicationDisabled = "Indications disabled";

    private static String reliableWriteEnabled = "Reliable Write enabled";
    private static String reliableWriteDisabled = "Reliable Write disabled";

    private static String writableAuxillaryEnabled = "Writable Auxiliaries enabled";
    private static String writableAuxillaryDisabled = "Writable Auxiliaries disabled";

    private static String broadcastEnabled = "Broadcasts enabled";
    private static String broadcastDisabled = "Broadcasts disabled";


    public static String getClientCharacteristicConfiguration(BluetoothGattDescriptor descriptor) {
        String valueConverted = "";
        byte[] array = descriptor.getValue();
        StringBuffer sb = new StringBuffer();
        for (byte byteChar : array) {
            sb.append(String.format("%02x", byteChar));
        }
        if (sb.toString().equalsIgnoreCase("0000")) {
            valueConverted = notificationDisabled + "\n" + indicationDisabled;
        } else if (sb.toString().equalsIgnoreCase("0100")) {
            valueConverted = notificationEnabled;
        } else if (sb.toString().equalsIgnoreCase("0200")) {
            valueConverted = indicationEnabled;
        }
        return valueConverted;
    }

    public static HashMap<String, String> getCharacteristicExtendedProperties(BluetoothGattDescriptor descriptor) {
        HashMap<String, String> valuesMap = new HashMap<String, String>();

        String reliableWriteStatus;
        String writableAuxillaryStatus;
        byte reliableWriteBit = descriptor.getValue()[0];
        byte writableAuxillaryBit = descriptor.getValue()[1];

        if ((reliableWriteBit & 0x01) != 0) {
            reliableWriteStatus = reliableWriteEnabled;
        } else {
            reliableWriteStatus = reliableWriteDisabled;
        }
        if ((writableAuxillaryBit & 0x01) != 0) {
            writableAuxillaryStatus = writableAuxillaryEnabled;
        } else {
            writableAuxillaryStatus = writableAuxillaryDisabled;
        }
        valuesMap.put(Constants.firstBitValueKey, reliableWriteStatus);
        valuesMap.put(Constants.secondBitValueKey, writableAuxillaryStatus);
        return valuesMap;
    }

    public static String getCharacteristicUserDescription(BluetoothGattDescriptor descriptor) {
        Charset UTF8_CHARSET = Charset.forName("UTF-8");
        byte[] valueEncoded = descriptor.getValue();
        return new String(valueEncoded, UTF8_CHARSET);
    }

    public static String getServerCharacteristicConfiguration(BluetoothGattDescriptor descriptor) {
        byte firstBit = descriptor.getValue()[0];
        String broadcastStatus;
        if ((firstBit & 0x01) != 0) {
            broadcastStatus = broadcastEnabled;
        } else {
            broadcastStatus = broadcastDisabled;
        }
        return broadcastStatus;
    }

    public static ArrayList<String> getReportReference(BluetoothGattDescriptor descriptor) {
        ArrayList<String> reportReferencevalues = new ArrayList<String>(2);
        byte[] array = descriptor.getValue();
        String reportReferenceID = "";
        String reportTYpe = "";
        if (array != null && array.length == 2) {
            reportReferenceID = ReportAttributes.lookupReportReferenceID("" + array[0]);
            reportTYpe = ReportAttributes.lookupReportReferenceType("" + array[1]);
            reportReferencevalues.add(reportReferenceID);
            reportReferencevalues.add(reportTYpe);
        }
        return reportReferencevalues;
    }

    public static String getCharacteristicPresentationFormat(BluetoothGattDescriptor descriptor) {
        String value = "";
        String formatKey = String.valueOf(descriptor.getValue()[0]);
        String formatValue = GattAttributes.lookCharacteristicPresentationFormat(formatKey);
        String exponentValue = String.valueOf(descriptor.getValue()[1]);
        byte unit1 = descriptor.getValue()[2];
        byte unit2 = descriptor.getValue()[3];
        String unitValue = String.valueOf(((unit1 & 0xFF) | unit2 << 8));
        String namespaceValue = String.valueOf(descriptor.getValue()[4]);
        if (namespaceValue.equalsIgnoreCase("1")) {
            namespaceValue = "Bluetooth SIG Assigned Numbers";
        } else {
            namespaceValue = "Reserved for future use";
        }
        String descriptionValue = String.valueOf(descriptor.getValue()[5]);
        value = "Format : " + formatValue + "\n" +
                "Exponent : " + exponentValue + "\n" +
                "Unit : " + unitValue + "\n" +
                "Namespace : " + namespaceValue + "\n" +
                "Description : " + descriptionValue;
        return value;
    }

}

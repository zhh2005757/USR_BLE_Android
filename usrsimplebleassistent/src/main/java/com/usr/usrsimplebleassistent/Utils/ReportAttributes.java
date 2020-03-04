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
package com.usr.usrsimplebleassistent.Utils;


import java.util.HashMap;

/**
 * Report and Report Reference class for the states of the Report
 */
public class ReportAttributes {

    //Report References id
    public static String MOUSE_REPORT_REFERENCE = "1";
    public static final String MOUSE_REPORT_REFERENCE_STRING = "Report ID: MOUSE_REPORT_REFERENCE";
    public static String KEYBOARD_REPORT_REFERENCE = "2";
    public static final String KEYBOARD_REPORT_REFERENCE_STRING = "Report ID: KEYBOARD_REPORT_REFERENCE";
    public static String MULTIMEDIA_REPORT_REFERENCE = "3";
    public static final String MULTIMEDIA_REPORT_REFERENCE_STRING = "Report ID: MULTIMEDIA_REPORT_REFERENCE";
    public static String POWER_REPORT_REFERENCE = "4";
    public static final String POWER_REPORT_REFERENCE_STRING = "Report ID: POWER_REPORT_REFERENCE";
    public static String AUDIO_REPORT_REFERENCE_CONTROL = "31";
    public static final String AUDIO_REPORT_REFERENCE_CONTROL_STRING = "Report ID: AUDIO_REPORT_REFERENCE_CONTROL";
    public static String AUDIO_REPORT_REFERENCE_DATA = "30";
    public static final String AUDIO_REPORT_REFERENCE_DATA_STRING = "Report ID: AUDIO_REPORT_REFERENCE_DATA";

    //Report Reference Types
    public static String INPUT_REPORT_TYPE = "1";
    public static final String INPUT_REPORT_TYPE_STRING = "Report Type: Input Report";
    public static String OUTPUT_REPORT_TYPE = "2";
    public static final String OUTPUT_REPORT_TYPE_STRING = "Report Type: Output Report";
    public static String FEATURE_REPORT_TYPE = "3";
    public static final String FEATURE_REPORT_TYPE_STRING = "Report Type: Feature Report";

    //Report Values String Compare
    public static String POWER = "3000";
    public static String VOLUME_PLUS = "e900";
    public static String VOLUME_MINUS = "ea00";
    public static String CHANNEL_PLUS = "9c00";
    public static String CHANNEL_MINUS = "9d00";
    public static String MICROPHONE = "ff01";
    public static String MICROPHONE_UP = "ff00";
    public static String MICROPHONE_SYNC = "FE";
    public static String RETURN = "00009e0000000000";
    public static String RETURN_UP = "0000000000000000";
    public static String SOURCE = "8700";
    public static String LEFT_CLICK_DOWN = "0100000000";
    public static String LEFT_RIGHT_CLICK_UP = "0000000000";
    public static String RIGHT_CLICK_DOWN = "0200000000";
    public static String GESTURE_ON = "0000FF0000";


    private static HashMap<String, String> referenceAttributes = new HashMap<String, String>();
    private static HashMap<String, String> referenceAttributesType = new HashMap<String, String>();
    private static HashMap<String, Integer> reportvalues = new HashMap<String, Integer>();

    static {

        referenceAttributes.put(MOUSE_REPORT_REFERENCE, MOUSE_REPORT_REFERENCE_STRING);
        referenceAttributes.put(KEYBOARD_REPORT_REFERENCE, KEYBOARD_REPORT_REFERENCE_STRING);
        referenceAttributes.put(MULTIMEDIA_REPORT_REFERENCE, MULTIMEDIA_REPORT_REFERENCE_STRING);
        referenceAttributes.put(POWER_REPORT_REFERENCE, POWER_REPORT_REFERENCE_STRING);
        referenceAttributes.put(AUDIO_REPORT_REFERENCE_CONTROL, AUDIO_REPORT_REFERENCE_CONTROL_STRING);
        referenceAttributes.put(AUDIO_REPORT_REFERENCE_DATA, AUDIO_REPORT_REFERENCE_DATA_STRING);

        referenceAttributesType.put(INPUT_REPORT_TYPE, INPUT_REPORT_TYPE_STRING);
        referenceAttributesType.put(OUTPUT_REPORT_TYPE, OUTPUT_REPORT_TYPE_STRING);
        referenceAttributesType.put(FEATURE_REPORT_TYPE, FEATURE_REPORT_TYPE_STRING);

        reportvalues.put(POWER, 101);
        reportvalues.put(VOLUME_PLUS, 102);
        reportvalues.put(VOLUME_MINUS, 103);
        reportvalues.put(CHANNEL_PLUS, 104);
        reportvalues.put(CHANNEL_MINUS, 105);
        reportvalues.put(MICROPHONE, 106);
        reportvalues.put(LEFT_CLICK_DOWN, 107);
        reportvalues.put(RIGHT_CLICK_DOWN, 108);
        reportvalues.put(RETURN, 109);
        reportvalues.put(SOURCE, 110);
        reportvalues.put(MICROPHONE_UP, 201);
        reportvalues.put(LEFT_RIGHT_CLICK_UP, 202);
        reportvalues.put(RETURN_UP, 203);

    }

    public static String lookupReportReferenceID(String reference) {
        String name = referenceAttributes.get(reference);
        return name == null ? "" + reference : name;
    }

    public static String lookupReportReferenceType(String referenceType) {
        String name = referenceAttributesType.get(referenceType);
        return name == null ? "Reserved for future use" : name;
    }

    public static int lookupReportValues(String reportValue) {
        int returnValueDefault = 0;
        Integer value = reportvalues.get(reportValue);
        if (value != null) {
            return value;
        } else {
            return returnValueDefault;
        }

    }
}

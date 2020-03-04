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

import java.util.UUID;

/**
 * This class will store the UUID of the GATT services and characteristics
 */
public class UUIDDatabase {
    /**
     * Heart rate related UUID
     */
    public final static UUID UUID_HEART_RATE_SERVICE = UUID
            .fromString(GattAttributes.HEART_RATE_SERVICE);
    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID
            .fromString(GattAttributes.HEART_RATE_MEASUREMENT);
    public final static UUID UUID_BODY_SENSOR_LOCATION = UUID
            .fromString(GattAttributes.BODY_SENSOR_LOCATION);
    /**
     * Device information related UUID
     */
    public final static UUID UUID_DEVICE_INFORMATION_SERVICE = UUID
            .fromString(GattAttributes.DEVICE_INFORMATION_SERVICE);
    public final static UUID UUID_SYSTEM_ID = UUID
            .fromString(GattAttributes.SYSTEM_ID);
    public static final UUID UUID_MANUFATURE_NAME_STRING = UUID
            .fromString(GattAttributes.MANUFACTURER_NAME_STRING);
    public static final UUID UUID_MODEL_NUMBER_STRING = UUID
            .fromString(GattAttributes.MODEL_NUMBER_STRING);
    public static final UUID UUID_SERIAL_NUMBER_STRING = UUID
            .fromString(GattAttributes.SERIAL_NUMBER_STRING);
    public static final UUID UUID_HARDWARE_REVISION_STRING = UUID
            .fromString(GattAttributes.HARDWARE_REVISION_STRING);
    public static final UUID UUID_FIRWARE_REVISION_STRING = UUID
            .fromString(GattAttributes.FIRMWARE_REVISION_STRING);
    public static final UUID UUID_SOFTWARE_REVISION_STRING = UUID
            .fromString(GattAttributes.SOFTWARE_REVISION_STRING);
    public static final UUID UUID_PNP_ID = UUID
            .fromString(GattAttributes.PNP_ID);
    public static final UUID UUID_IEEE = UUID
            .fromString(GattAttributes.IEEE);

    /**
     * Health thermometer related UUID
     */
    public final static UUID UUID_HEALTH_SERVICE = UUID
            .fromString(GattAttributes.HEALTH_TEMP_SERVICE);
    public final static UUID UUID_HEALTH_THERMOMETER = UUID
            .fromString(GattAttributes.HEALTH_TEMP_MEASUREMENT);
    public final static UUID UUID_HEALTH_THERMOMETER_SENSOR_LOCATION = UUID
            .fromString(GattAttributes.TEMPERATURE_TYPE);
    /**
     * Battery Level related uuid
     */
    public final static UUID UUID_BATTERY_SERVICE = UUID
            .fromString(GattAttributes.BATTERY_SERVICE);
    public final static UUID UUID_BATTERY_LEVEL = UUID
            .fromString(GattAttributes.BATTERY_LEVEL);

    /**
     * Find me related uuid
     */
    public final static UUID UUID_IMMEDIATE_ALERT_SERVICE = UUID
            .fromString(GattAttributes.IMMEDIATE_ALERT_SERVICE);
    public final static UUID UUID_TRANSMISSION_POWER_SERVICE = UUID
            .fromString(GattAttributes.TRANSMISSION_POWER_SERVICE);
    public final static UUID UUID_ALERT_LEVEL = UUID
            .fromString(GattAttributes.ALERT_LEVEL);
    public final static UUID UUID_TRANSMISSION_POWER_LEVEL = UUID
            .fromString(GattAttributes.TRANSMISSION_POWER_LEVEL);

    /**
     * CapSense related uuid
     */
    public final static UUID UUID_CAPSENSE_PROXIMITY = UUID
            .fromString(GattAttributes.CAPSENSE_PROXIMITY);
    public final static UUID UUID_CAPSENSE_SLIDER = UUID
            .fromString(GattAttributes.CAPSENSE_SLIDER);
    public final static UUID UUID_CAPSENSE_BUTTONS = UUID
            .fromString(GattAttributes.CAPSENSE_BUTTONS);
    public final static UUID UUID_CAPSENSE_PROXIMITY_CUSTOM = UUID
            .fromString(GattAttributes.CAPSENSE_PROXIMITY_CUSTOM);
    public final static UUID UUID_CAPSENSE_SLIDER_CUSTOM = UUID
            .fromString(GattAttributes.CAPSENSE_SLIDER_CUSTOM);
    public final static UUID UUID_CAPSENSE_BUTTONS_CUSTOM = UUID
            .fromString(GattAttributes.CAPSENSE_BUTTONS_CUSTOM);
    /**
     * RGB LED related uuid
     */
    public final static UUID UUID_RGB_LED_SERVICE = UUID
            .fromString(GattAttributes.RGB_LED_SERVICE);
    public final static UUID UUID_RGB_LED = UUID
            .fromString(GattAttributes.RGB_LED);
    public final static UUID UUID_RGB_LED_SERVICE_CUSTOM = UUID
            .fromString(GattAttributes.RGB_LED_SERVICE_CUSTOM);
    public final static UUID UUID_RGB_LED_CUSTOM = UUID
            .fromString(GattAttributes.RGB_LED_CUSTOM);

    /**
     * GlucoseService related uuid
     */
    public final static UUID UUID_GLUCOSE = UUID
            .fromString(GattAttributes.GLUCOSE_COCNTRN);
    /**
     * Blood pressure related uuid
     */
    public final static UUID UUID_BLOOD_PRESSURE_SERVICE = UUID
            .fromString(GattAttributes.BLOOD_PRESSURE_SERVICE);
    public final static UUID UUID_BLOOD_PRESSURE_MEASUREMENT = UUID
            .fromString(GattAttributes.BLOOD_PRESSURE_MEASUREMENT);

    /**
     * Running Speed & Cadence related uuid
     */
    public final static UUID UUID_RSC_MEASURE = UUID
            .fromString(GattAttributes.RSC_MEASUREMENT);

    /**
     * Cycling Speed & Cadence related uuid
     */
    public final static UUID UUID_CSC_MEASURE = UUID
            .fromString(GattAttributes.CSC_MEASUREMENT);
    /**
     * Barometer related uuid
     */
    public final static UUID UUID_BAROMETER_SERVICE = UUID
            .fromString(GattAttributes.BAROMETER_SERVICE);
    public final static UUID UUID_BAROMETER_DIGITAL_SENSOR = UUID
            .fromString(GattAttributes.BAROMETER_DIGITAL_SENSOR);
    public final static UUID UUID_BAROMETER_SENSOR_SCAN_INTERVAL = UUID
            .fromString(GattAttributes.BAROMETER_SENSOR_SCAN_INTERVAL);
    public final static UUID UUID_BAROMETER_THRESHOLD_FOR_INDICATION = UUID
            .fromString(GattAttributes.BAROMETER_THRESHOLD_FOR_INDICATION);
    public final static UUID UUID_BAROMETER_DATA_ACCUMULATION = UUID
            .fromString(GattAttributes.BAROMETER_DATA_ACCUMULATION);
    public final static UUID UUID_BAROMETER_READING = UUID
            .fromString(GattAttributes.BAROMETER_READING);
    /**
     * Accelerometer related uuid
     */
    public final static UUID UUID_ACCELEROMETER_SERVICE = UUID
            .fromString(GattAttributes.ACCELEROMETER_SERVICE);
    public final static UUID UUID_ACCELEROMETER_ANALOG_SENSOR = UUID
            .fromString(GattAttributes.ACCELEROMETER_ANALOG_SENSOR);
    public final static UUID UUID_ACCELEROMETER_DATA_ACCUMULATION = UUID
            .fromString(GattAttributes.ACCELEROMETER_DATA_ACCUMULATION);
    public final static UUID UUID_ACCELEROMETER_READING_X = UUID
            .fromString(GattAttributes.ACCELEROMETER_READING_X);
    public final static UUID UUID_ACCELEROMETER_READING_Y = UUID
            .fromString(GattAttributes.ACCELEROMETER_READING_Y);
    public final static UUID UUID_ACCELEROMETER_READING_Z = UUID
            .fromString(GattAttributes.ACCELEROMETER_READING_Z);
    public final static UUID UUID_ACCELEROMETER_SENSOR_SCAN_INTERVAL = UUID
            .fromString(GattAttributes.ACCELEROMETER_SENSOR_SCAN_INTERVAL);
    /**
     * Analog temperature  related uuid
     */
    public final static UUID UUID_ANALOG_TEMPERATURE_SERVICE = UUID
            .fromString(GattAttributes.ANALOG_TEMPERATURE_SERVICE);
    public final static UUID UUID_TEMPERATURE_ANALOG_SENSOR = UUID
            .fromString(GattAttributes.TEMPERATURE_ANALOG_SENSOR);
    public final static UUID UUID_TEMPERATURE_READING = UUID
            .fromString(GattAttributes.TEMPERATURE_READING);
    public final static UUID UUID_TEMPERATURE_SENSOR_SCAN_INTERVAL = UUID
            .fromString(GattAttributes.TEMPERATURE_SENSOR_SCAN_INTERVAL);

    /**
     * RDK related UUID
     */
    public final static UUID UUID_REP0RT = UUID
            .fromString(GattAttributes.REP0RT);

    /**
     * OTA related UUID
     */
    public final static UUID UUID_OTA_UPDATE_SERVICE = UUID
            .fromString(GattAttributes.OTA_UPDATE_SERVICE);
    public final static UUID UUID_OTA_UPDATE_CHARACTERISTIC = UUID
            .fromString(GattAttributes.OTA_CHARACTERISTIC);

    /**
     * Descriptor UUID
     */
    public final static UUID UUID_CLIENT_CHARACTERISTIC_CONFIG = UUID
            .fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG);
    public final static UUID UUID_CHARACTERISTIC_EXTENDED_PROPERTIES = UUID
            .fromString(GattAttributes.CHARACTERISTIC_EXTENDED_PROPERTIES);
    public final static UUID UUID_CHARACTERISTIC_USER_DESCRIPTION = UUID
            .fromString(GattAttributes.CHARACTERISTIC_USER_DESCRIPTION);
    public final static UUID UUID_SERVER_CHARACTERISTIC_CONFIGURATION = UUID
            .fromString(GattAttributes.SERVER_CHARACTERISTIC_CONFIGURATION);
    public final static UUID UUID_REPORT_REFERENCE = UUID
            .fromString(GattAttributes.REPORT_REFERENCE);
    public final static UUID UUID_CHARACTERISTIC_PRESENTATION_FORMAT = UUID
            .fromString(GattAttributes.CHARACTERISTIC_PRESENTATION_FORMAT);


}

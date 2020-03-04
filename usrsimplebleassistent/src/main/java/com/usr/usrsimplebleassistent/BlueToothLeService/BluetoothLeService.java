/*
 * Copyright Cypress Semiconductor Corporation, 2014-2014-2015 All rights reserved.
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

package com.usr.usrsimplebleassistent.BlueToothLeService;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import com.usr.usrsimplebleassistent.BLEProfileDataParserClasses.BloodPressureParser;
import com.usr.usrsimplebleassistent.BLEProfileDataParserClasses.CSCParser;
import com.usr.usrsimplebleassistent.BLEProfileDataParserClasses.CapSenseParser;
import com.usr.usrsimplebleassistent.BLEProfileDataParserClasses.DescriptorParser;
import com.usr.usrsimplebleassistent.BLEProfileDataParserClasses.GlucoseParser;
import com.usr.usrsimplebleassistent.BLEProfileDataParserClasses.HRMParser;
import com.usr.usrsimplebleassistent.BLEProfileDataParserClasses.HTMParser;
import com.usr.usrsimplebleassistent.BLEProfileDataParserClasses.RGBParser;
import com.usr.usrsimplebleassistent.BLEProfileDataParserClasses.RSCParser;
import com.usr.usrsimplebleassistent.BLEProfileDataParserClasses.SensorHubParser;
import com.usr.usrsimplebleassistent.Utils.Constants;
import com.usr.usrsimplebleassistent.Utils.GattAttributes;
import com.usr.usrsimplebleassistent.Utils.UUIDDatabase;
import com.usr.usrsimplebleassistent.Utils.Utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given BlueTooth LE device.
 */
public class BluetoothLeService extends Service {

    /**
     * GATT Status constants
     */
    public final static String ACTION_GATT_CONNECTED =
            "com.usr.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.usr.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_DISCONNECTED_CAROUSEL =
            "com.usr.bluetooth.le.ACTION_GATT_DISCONNECTED_CAROUSEL";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.usr.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.usr.bluetooth.le.ACTION_DATA_AVAILABLE";

    public final static String ACTION_GATT_CHARACTERISTIC_ERROR =
            "com.usr.bluetooth.le.ACTION_GATT_CHARACTERISTIC_ERROR";

    /**
     * add by usr_ljq
     */
    public final static String ACTION_GATT_DESCRIPTORWRITE_RESULT =
            "com.usr.bluetooth.le.ACTION_GATT_DESCRIPTORWRITE_RESULT";
    public final static String ACTION_GATT_CHARACTERISTIC_WRITE_SUCCESS =
            "com.usr.bluetooth.le.ACTION_GATT_CHARACTERISTIC_SUCCESS";

    /**
     * Connection status Constants
     */
    public static final int STATE_DISCONNECTED = 0;
    private final static String ACTION_GATT_DISCONNECTING =
            "com.usr.bluetooth.le.ACTION_GATT_DISCONNECTING";
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_DISCONNECTING = 4;

    /**
     * BluetoothAdapter for handling connections
     * 连接蓝牙都需要，用来管理手机上的蓝牙
     */
    public static BluetoothAdapter mBluetoothAdapter;

    /**
     * a) BluetoothGattServer作为周边来提供数据；BluetoothGattServerCallback返回周边的状态。
     * b) BluetoothGatt作为中央来使用和处理数据；BluetoothGattCallback返回中央的状态和周边提供的数据
     */
    public static BluetoothGatt mBluetoothGatt;
    private static int mConnectionState = STATE_DISCONNECTED;
    /**
     * Device address
     */
    private static String mBluetoothDeviceAddress;
    private static String mBluetoothDeviceName;
    private static Context mContext;
    /**
     * Implements callback methods for GATT events that the app cares about. For
     * example,connection change and services discovered.
     * 连接状态  已连接、断开等等
     */
    private final static BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {

            String intentAction;
            // GATT Server connected
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                System.out.println("---------------------------->已经连接");
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastConnectionUpdate(intentAction);

            }
            // GATT Server disconnected
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                System.out.println("---------------------------->连接断开");
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                broadcastConnectionUpdate(intentAction);

            }
            // GATT Server disconnected
            else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                System.out.println("---------------------------->正在连接");
//                intentAction = ACTION_GATT_DISCONNECTING;
//                mConnectionState = STATE_DISCONNECTING;
//                broadcastConnectionUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // GATT Services discovered
            //发现新的服务
            if (status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println("---------------------------->发现服务");
                broadcastConnectionUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {

            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                      int status) {


            if (status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println("onDescriptorWrite GATT_SUCCESS------------------->SUCCESS");
            } else if (status == BluetoothGatt.GATT_FAILURE){
                System.out.println("onDescriptorWrite GATT_FAIL------------------->FAIL");
                Intent intent = new Intent(ACTION_GATT_DESCRIPTORWRITE_RESULT);
                intent.putExtra(Constants.EXTRA_DESCRIPTOR_WRITE_RESULT, status);
                mContext.sendBroadcast(intent);
            }

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                     int status) {


            System.out.println("onDescriptorRead ------------------->GATT_SUCC");

            if (status == BluetoothGatt.GATT_SUCCESS) {
                UUID descriptorUUID = descriptor.getUuid();
                final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                Bundle mBundle = new Bundle();
                // Putting the byte value read for GATT Db
                mBundle.putByteArray(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE,
                        descriptor.getValue());


                mBundle.putString(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_UUID,
                        descriptor.getUuid().toString());
                mBundle.putString(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_CHARACTERISTIC_UUID,
                        descriptor.getCharacteristic().getUuid().toString());
                if (descriptorUUID.equals(UUIDDatabase.UUID_CLIENT_CHARACTERISTIC_CONFIG)) {
                    String valueReceived = DescriptorParser
                            .getClientCharacteristicConfiguration(descriptor);
                    mBundle.putString(Constants.EXTRA_DESCRIPTOR_VALUE, valueReceived);
                }
                if (descriptorUUID.equals(UUIDDatabase.UUID_CHARACTERISTIC_EXTENDED_PROPERTIES)) {
                    HashMap<String, String> receivedValuesMap = DescriptorParser
                            .getCharacteristicExtendedProperties(descriptor);
                    String reliableWriteStatus = receivedValuesMap.get(Constants.firstBitValueKey);
                    String writeAuxillaryStatus = receivedValuesMap.get(Constants.secondBitValueKey);
                    mBundle.putString(Constants.EXTRA_DESCRIPTOR_VALUE, reliableWriteStatus + "\n"
                            + writeAuxillaryStatus);
                }
                if (descriptorUUID.equals(UUIDDatabase.UUID_CHARACTERISTIC_USER_DESCRIPTION)) {
                    String description = DescriptorParser
                            .getCharacteristicUserDescription(descriptor);
                    mBundle.putString(Constants.EXTRA_DESCRIPTOR_VALUE, description);
                }
                if (descriptorUUID.equals(UUIDDatabase.UUID_SERVER_CHARACTERISTIC_CONFIGURATION)) {
                    String broadcastStatus = DescriptorParser.
                            getServerCharacteristicConfiguration(descriptor);
                    mBundle.putString(Constants.EXTRA_DESCRIPTOR_VALUE, broadcastStatus);
                }
                if (descriptorUUID.equals(UUIDDatabase.UUID_REPORT_REFERENCE)) {
                    ArrayList<String> reportReferencealues = DescriptorParser.getReportReference(descriptor);
                    String reportReference;
                    String reportReferenceType;
                    if (reportReferencealues.size() == 2) {
                        reportReference = reportReferencealues.get(0);
                        reportReferenceType = reportReferencealues.get(1);
                        mBundle.putString(Constants.EXTRA_DESCRIPTOR_REPORT_REFERENCE_ID, reportReference);
                        mBundle.putString(Constants.EXTRA_DESCRIPTOR_REPORT_REFERENCE_TYPE, reportReferenceType);
                        mBundle.putString(Constants.EXTRA_DESCRIPTOR_VALUE, reportReference + "\n" +
                                reportReferenceType);
                    }

                }
                if (descriptorUUID.equals(UUIDDatabase.UUID_CHARACTERISTIC_PRESENTATION_FORMAT)) {
                    String value = DescriptorParser.getCharacteristicPresentationFormat(descriptor);
                    mBundle.putString(Constants.EXTRA_DESCRIPTOR_VALUE,
                            value);
                }
                intent.putExtras(mBundle);
                /**
                 * Sending the broad cast so that it can be received on
                 * registered receivers
                 */
                mContext.sendBroadcast(intent);
            } else {
                System.out.println("onDescriptorRead ------------------->GATT_FAIL");
            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status) {
            //write操作会调用此方法
            if (status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println("onCharacteristicWrite ------------------->write success");
                Intent intent = new Intent(ACTION_GATT_CHARACTERISTIC_WRITE_SUCCESS);
                mContext.sendBroadcast(intent);
            } else {
                Intent intent = new Intent(ACTION_GATT_CHARACTERISTIC_ERROR);
                intent.putExtra(Constants.EXTRA_CHARACTERISTIC_ERROR_MESSAGE, "" + status);
                mContext.sendBroadcast(intent);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {

            System.out.println("onCharacteristicWrite ------------------->read");
            // GATT Characteristic read (读操作会调用该方法)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                UUID charUuid = characteristic.getUuid();
                final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                Bundle mBundle = new Bundle();
                // Putting the byte value read for GATT Db
                mBundle.putByteArray(Constants.EXTRA_BYTE_VALUE,
                        characteristic.getValue());
                mBundle.putString(Constants.EXTRA_BYTE_UUID_VALUE,
                        characteristic.getUuid().toString());

                System.out.println("onCharacteristicRead------------------->GATT_SUCC");

                // Body sensor location read value
                if (charUuid.equals(UUIDDatabase.UUID_BODY_SENSOR_LOCATION)) {
                    mBundle.putString(Constants.EXTRA_BSL_VALUE,
                            HRMParser.getBodySensorLocation(characteristic));
                }
                // Manufacture name read value
                else if (charUuid
                        .equals(UUIDDatabase.UUID_MANUFATURE_NAME_STRING)) {
                    mBundle.putString(Constants.EXTRA_MNS_VALUE,
                            Utils.getManufacturerNameString(characteristic));
                }
                // Model number read value
                else if (charUuid.equals(UUIDDatabase.UUID_MODEL_NUMBER_STRING)) {
                    mBundle.putString(Constants.EXTRA_MONS_VALUE,
                            Utils.getModelNumberString(characteristic));
                }
                // Serial number read value
                else if (charUuid
                        .equals(UUIDDatabase.UUID_SERIAL_NUMBER_STRING)) {
                    mBundle.putString(Constants.EXTRA_SNS_VALUE,
                            Utils.getSerialNumberString(characteristic));
                }
                // Hardware revision read value
                else if (charUuid
                        .equals(UUIDDatabase.UUID_HARDWARE_REVISION_STRING)) {
                    mBundle.putString(Constants.EXTRA_HRS_VALUE,
                            Utils.getHardwareRevisionString(characteristic));
                }
                // Firmware revision read value
                else if (charUuid
                        .equals(UUIDDatabase.UUID_FIRWARE_REVISION_STRING)) {
                    mBundle.putString(Constants.EXTRA_FRS_VALUE,
                            Utils.getFirmwareRevisionString(characteristic));
                }
                // Software revision read value
                else if (charUuid
                        .equals(UUIDDatabase.UUID_SOFTWARE_REVISION_STRING)) {
                    mBundle.putString(Constants.EXTRA_SRS_VALUE,
                            Utils.getSoftwareRevisionString(characteristic));
                }
                // Battery level read value
                else if (charUuid.equals(UUIDDatabase.UUID_BATTERY_LEVEL)) {
                    mBundle.putString(Constants.EXTRA_BTL_VALUE,
                            Utils.getBatteryLevel(characteristic));
                }
                // PNP ID read value
                else if (charUuid.equals(UUIDDatabase.UUID_PNP_ID)) {
                    mBundle.putString(Constants.EXTRA_PNP_VALUE,
                            Utils.getPNPID(characteristic));
                }
                // System ID read value
                else if (charUuid.equals(UUIDDatabase.UUID_SYSTEM_ID)) {
                    mBundle.putString(Constants.EXTRA_SID_VALUE,
                            Utils.getSYSID(characteristic));
                }
                // Regulatory data read value
                else if (charUuid.equals(UUIDDatabase.UUID_IEEE)) {
                    mBundle.putString(Constants.EXTRA_RCDL_VALUE,
                            Utils.ByteArraytoHex(characteristic.getValue()));
                }
                // Health thermometer sensor location read value
                else if (charUuid
                        .equals(UUIDDatabase.UUID_HEALTH_THERMOMETER_SENSOR_LOCATION)) {
                    mBundle.putString(Constants.EXTRA_HSL_VALUE, HTMParser
                            .getHealthThermoSensorLocation(characteristic));
                }
                // CapSense proximity read value
                else if (charUuid.equals(UUIDDatabase.UUID_CAPSENSE_PROXIMITY) ||
                        charUuid.equals(UUIDDatabase.UUID_CAPSENSE_PROXIMITY_CUSTOM)) {
                    mBundle.putInt(Constants.EXTRA_CAPPROX_VALUE,
                            CapSenseParser.getCapSenseProximity(characteristic));
                }
                // CapSense slider read value
                else if (charUuid.equals(UUIDDatabase.UUID_CAPSENSE_SLIDER) ||
                        charUuid.equals(UUIDDatabase.UUID_CAPSENSE_SLIDER_CUSTOM)) {
                    mBundle.putInt(Constants.EXTRA_CAPSLIDER_VALUE,
                            CapSenseParser.getCapSenseSlider(characteristic));
                }
                // CapSense buttons read value
                else if (charUuid.equals(UUIDDatabase.UUID_CAPSENSE_BUTTONS) ||
                        charUuid.equals(UUIDDatabase.UUID_CAPSENSE_BUTTONS_CUSTOM)) {
                    mBundle.putIntegerArrayList(
                            Constants.EXTRA_CAPBUTTONS_VALUE,
                            CapSenseParser.getCapSenseButtons(characteristic));
                }
                // Alert level read value
                else if (charUuid.equals(UUIDDatabase.UUID_ALERT_LEVEL)) {
                    mBundle.putString(Constants.EXTRA_ALERT_VALUE,
                            Utils.getAlertLevel(characteristic));
                }
                // Transmission power level read value
                else if (charUuid
                        .equals(UUIDDatabase.UUID_TRANSMISSION_POWER_LEVEL)) {
                    mBundle.putInt(Constants.EXTRA_POWER_VALUE,
                            Utils.getTransmissionPower(characteristic));
                }
                // RGB Led read value
                else if (charUuid.equals(UUIDDatabase.UUID_RGB_LED) ||
                        charUuid.equals(UUIDDatabase.UUID_RGB_LED_CUSTOM)) {
                    mBundle.putString(Constants.EXTRA_RGB_VALUE,
                            RGBParser.getRGBValue(characteristic));
                }
                // Glucose read value
                else if (charUuid.equals(UUIDDatabase.UUID_GLUCOSE)) {
                    mBundle.putStringArrayList(Constants.EXTRA_GLUCOSE_VALUE,
                            GlucoseParser.getGlucoseHealth(characteristic));
                }
                // Running speed read value
                else if (charUuid.equals(UUIDDatabase.UUID_RSC_MEASURE)) {
                    mBundle.putStringArrayList(Constants.EXTRA_RSC_VALUE,
                            RSCParser.getRunningSpeednCadence(characteristic));
                }
                // Accelerometer X read value
                else if (charUuid
                        .equals(UUIDDatabase.UUID_ACCELEROMETER_READING_X)) {
                    mBundle.putInt(Constants.EXTRA_ACCX_VALUE, SensorHubParser
                            .getAcceleroMeterXYZReading(characteristic));
                }
                // Accelerometer Y read value
                else if (charUuid
                        .equals(UUIDDatabase.UUID_ACCELEROMETER_READING_Y)) {
                    mBundle.putInt(Constants.EXTRA_ACCY_VALUE, SensorHubParser
                            .getAcceleroMeterXYZReading(characteristic));
                }
                // Accelerometer Z read value
                else if (charUuid
                        .equals(UUIDDatabase.UUID_ACCELEROMETER_READING_Z)) {
                    mBundle.putInt(Constants.EXTRA_ACCZ_VALUE, SensorHubParser
                            .getAcceleroMeterXYZReading(characteristic));
                }
                // Temperature read value
                else if (charUuid.equals(UUIDDatabase.UUID_TEMPERATURE_READING)) {
                    mBundle.putFloat(Constants.EXTRA_STEMP_VALUE,
                            SensorHubParser
                                    .getThermometerReading(characteristic));
                }
                // Barometer read value
                else if (charUuid.equals(UUIDDatabase.UUID_BAROMETER_READING)) {
                    mBundle.putInt(Constants.EXTRA_SPRESSURE_VALUE,
                            SensorHubParser.getBarometerReading(characteristic));
                }
                // Accelerometer scan interval read value
                else if (charUuid
                        .equals(UUIDDatabase.UUID_ACCELEROMETER_SENSOR_SCAN_INTERVAL)) {
                    mBundle.putInt(
                            Constants.EXTRA_ACC_SENSOR_SCAN_VALUE,
                            SensorHubParser
                                    .getSensorScanIntervalReading(characteristic));
                }
                // Accelerometer analog sensor read value
                else if (charUuid
                        .equals(UUIDDatabase.UUID_ACCELEROMETER_ANALOG_SENSOR)) {
                    mBundle.putInt(Constants.EXTRA_ACC_SENSOR_TYPE_VALUE,
                            SensorHubParser
                                    .getSensorTypeReading(characteristic));
                }
                // Accelerometer data accumulation read value
                else if (charUuid
                        .equals(UUIDDatabase.UUID_ACCELEROMETER_DATA_ACCUMULATION)) {
                    mBundle.putInt(Constants.EXTRA_ACC_FILTER_VALUE,
                            SensorHubParser
                                    .getFilterConfiguration(characteristic));
                }
                // Temperature sensor scan read value
                else if (charUuid
                        .equals(UUIDDatabase.UUID_TEMPERATURE_SENSOR_SCAN_INTERVAL)) {
                    mBundle.putInt(
                            Constants.EXTRA_STEMP_SENSOR_SCAN_VALUE,
                            SensorHubParser
                                    .getSensorScanIntervalReading(characteristic));
                }
                // Temperature analog sensor read value
                else if (charUuid
                        .equals(UUIDDatabase.UUID_TEMPERATURE_ANALOG_SENSOR)) {
                    mBundle.putInt(Constants.EXTRA_STEMP_SENSOR_TYPE_VALUE,
                            SensorHubParser
                                    .getSensorTypeReading(characteristic));
                }
                // Barometer sensor scan interval read value
                else if (charUuid
                        .equals(UUIDDatabase.UUID_BAROMETER_SENSOR_SCAN_INTERVAL)) {
                    mBundle.putInt(
                            Constants.EXTRA_SPRESSURE_SENSOR_SCAN_VALUE,
                            SensorHubParser
                                    .getSensorScanIntervalReading(characteristic));
                }
                // Barometer digital sensor
                else if (charUuid
                        .equals(UUIDDatabase.UUID_BAROMETER_DIGITAL_SENSOR)) {
                    mBundle.putInt(Constants.EXTRA_SPRESSURE_SENSOR_TYPE_VALUE,
                            SensorHubParser
                                    .getSensorTypeReading(characteristic));
                }
                // Barometer threshold for indication read value
                else if (charUuid
                        .equals(UUIDDatabase.UUID_BAROMETER_THRESHOLD_FOR_INDICATION)) {
                    mBundle.putInt(Constants.EXTRA_SPRESSURE_THRESHOLD_VALUE,
                            SensorHubParser.getThresholdValue(characteristic));
                }
                intent.putExtras(mBundle);

                /**
                 * Sending the broad cast so that it can be received on
                 * registered receivers
                 */

                mContext.sendBroadcast(intent);

            } else {

            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            /**
             * Android 底层貌似做了限制只能接受20个字节
             * There are four basic operations for moving data in BLE: read, write, notify, and indicate.
             * The BLE protocol specification requires that the maximum data payload size for these
             * operations is 20 bytes, or in the case of read operations, 22 bytes.
             * BLE is built for low power consumption, for infrequent short-burst data transmissions.
             * Sending lots of data is possible, but usually ends up being less efficient than classic
             * Bluetooth when trying to achieve maximum throughput.
             */
            System.out.println("onCharacteristicChanged -------------------> changed");
            //notify 会回调用此方法
            broadcastNotifyUpdate(characteristic);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
//            super.onMtuChanged(gatt, mtu, status);
            System.out.println("onMtuChanged-------------------->size:"+mtu);
            if (status == BluetoothGatt.GATT_SUCCESS){
                System.out.println("onMtuChanged-------------------->设置成功");
            }
        }
    };
    private final IBinder mBinder = new LocalBinder();
    /**
     * Flag to check the mBound status
     */
    public boolean mBound;
    /**
     * BlueTooth manager for handling connections
     */
    private BluetoothManager mBluetoothManager;

    public static String getmBluetoothDeviceAddress() {
        return mBluetoothDeviceAddress;
    }

    public static String getmBluetoothDeviceName() {
        return mBluetoothDeviceName;
    }

    private static void broadcastConnectionUpdate(final String action) {
        final Intent intent = new Intent(action);
        mContext.sendBroadcast(intent);
    }

    private static void broadcastNotifyUpdate(final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(BluetoothLeService.ACTION_DATA_AVAILABLE);
        Bundle mBundle = new Bundle();
        mBundle.putByteArray(Constants.EXTRA_BYTE_VALUE,
                characteristic.getValue());
        mBundle.putString(Constants.EXTRA_BYTE_UUID_VALUE, characteristic.getUuid().toString());
        // Heart rate Measurement notify value
        if (UUIDDatabase.UUID_HEART_RATE_MEASUREMENT.equals(characteristic
                .getUuid())) {
            String heart_rate = HRMParser.getHeartRate(characteristic);
            String energy_expended = HRMParser
                    .getEnergyExpended(characteristic);
            ArrayList<Integer> rrintervel = HRMParser
                    .getRRInterval(characteristic);
            mBundle.putString(Constants.EXTRA_HRM_VALUE, heart_rate);
            mBundle.putString(Constants.EXTRA_HRM_EEVALUE, energy_expended);
            mBundle.putIntegerArrayList(Constants.EXTRA_HRM_RRVALUE, rrintervel);
        }
        // Health thermometer notify value
        if (UUIDDatabase.UUID_HEALTH_THERMOMETER.equals(characteristic
                .getUuid())) {
            ArrayList<String> health_temp = HTMParser.getHealthThermo(characteristic);
            mBundle.putStringArrayList(Constants.EXTRA_HTM_VALUE, health_temp);
        }

        // CapSense Proximity notify value
        if (UUIDDatabase.UUID_CAPSENSE_PROXIMITY.equals(characteristic.getUuid()) ||
                UUIDDatabase.UUID_CAPSENSE_PROXIMITY_CUSTOM.equals(characteristic.getUuid())) {
            int capsense_proximity = CapSenseParser
                    .getCapSenseProximity(characteristic);
            mBundle.putInt(Constants.EXTRA_CAPPROX_VALUE, capsense_proximity);
        }
        // CapSense slider notify value
        if (UUIDDatabase.UUID_CAPSENSE_SLIDER.equals(characteristic.getUuid()) ||
                UUIDDatabase.UUID_CAPSENSE_SLIDER_CUSTOM.equals(characteristic.getUuid())) {
            int capsense_slider = CapSenseParser
                    .getCapSenseSlider(characteristic);
            mBundle.putInt(Constants.EXTRA_CAPSLIDER_VALUE, capsense_slider);

        }
        // CapSense buttons notify value
        if (UUIDDatabase.UUID_CAPSENSE_BUTTONS.equals(characteristic.getUuid()) ||
                UUIDDatabase.UUID_CAPSENSE_BUTTONS_CUSTOM.equals(characteristic.getUuid())) {
            ArrayList<Integer> capsense_buttons = CapSenseParser
                    .getCapSenseButtons(characteristic);
            mBundle.putIntegerArrayList(Constants.EXTRA_CAPBUTTONS_VALUE,
                    capsense_buttons);

        }
        // Glucose notify value
        if (UUIDDatabase.UUID_GLUCOSE.equals(characteristic.getUuid())) {
            ArrayList<String> glucose_values = GlucoseParser
                    .getGlucoseHealth(characteristic);
            mBundle.putStringArrayList(Constants.EXTRA_GLUCOSE_VALUE,
                    glucose_values);

        }
        // Blood pressure measurement notify value
        if (UUIDDatabase.UUID_BLOOD_PRESSURE_MEASUREMENT.equals(characteristic
                .getUuid())) {
            String blood_pressure_systolic = BloodPressureParser
                    .getSystolicBloodPressure(characteristic);
            String blood_pressure_diastolic = BloodPressureParser
                    .getDiaStolicBloodPressure(characteristic);
            String blood_pressure_systolic_unit = BloodPressureParser
                    .getSystolicBloodPressureUnit(characteristic, mContext);
            String blood_pressure_diastolic_unit = BloodPressureParser
                    .getDiaStolicBloodPressureUnit(characteristic, mContext);
            mBundle.putString(
                    Constants.EXTRA_PRESURE_SYSTOLIC_UNIT_VALUE,
                    blood_pressure_systolic_unit);
            mBundle.putString(
                    Constants.EXTRA_PRESURE_DIASTOLIC_UNIT_VALUE,
                    blood_pressure_diastolic_unit);
            mBundle.putString(
                    Constants.EXTRA_PRESURE_SYSTOLIC_VALUE,
                    blood_pressure_systolic);
            mBundle.putString(
                    Constants.EXTRA_PRESURE_DIASTOLIC_VALUE,
                    blood_pressure_diastolic);

        }
        // Running speed measurement notify value
        if (UUIDDatabase.UUID_RSC_MEASURE.equals(characteristic.getUuid())) {
            ArrayList<String> rsc_values = RSCParser
                    .getRunningSpeednCadence(characteristic);
            mBundle.putStringArrayList(Constants.EXTRA_RSC_VALUE, rsc_values);

        }
        // Cycling speed Measurement notify value
        if (UUIDDatabase.UUID_CSC_MEASURE.equals(characteristic.getUuid())) {
            ArrayList<String> csc_values = CSCParser
                    .getCyclingSpeednCadence(characteristic);
            mBundle.putStringArrayList(Constants.EXTRA_CSC_VALUE, csc_values);

        }
        // Accelerometer x notify value
        if (UUIDDatabase.UUID_ACCELEROMETER_READING_X.equals(characteristic
                .getUuid())) {
            mBundle.putInt(Constants.EXTRA_ACCX_VALUE,
                    SensorHubParser.getAcceleroMeterXYZReading(characteristic));

        }
        // Accelerometer Y notify value
        if (UUIDDatabase.UUID_ACCELEROMETER_READING_Y.equals(characteristic
                .getUuid())) {
            mBundle.putInt(Constants.EXTRA_ACCY_VALUE,
                    SensorHubParser.getAcceleroMeterXYZReading(characteristic));
        }
        // Accelerometer Z notify value
        if (UUIDDatabase.UUID_ACCELEROMETER_READING_Z.equals(characteristic
                .getUuid())) {
            mBundle.putInt(Constants.EXTRA_ACCZ_VALUE,
                    SensorHubParser.getAcceleroMeterXYZReading(characteristic));

        }
        // Temperature notify value
        if (UUIDDatabase.UUID_TEMPERATURE_READING.equals(characteristic
                .getUuid())) {
            mBundle.putFloat(Constants.EXTRA_STEMP_VALUE,
                    SensorHubParser.getThermometerReading(characteristic));

        }
        // Barometer notify value
        if (UUIDDatabase.UUID_BAROMETER_READING
                .equals(characteristic.getUuid())) {
            mBundle.putInt(Constants.EXTRA_SPRESSURE_VALUE,
                    SensorHubParser.getBarometerReading(characteristic));
        }
        // Battery level read value
        if (UUIDDatabase.UUID_BATTERY_LEVEL
                .equals(characteristic.getUuid())) {
            mBundle.putString(Constants.EXTRA_BTL_VALUE,
                    Utils.getBatteryLevel(characteristic));
        }
        //RDK characteristic
        if (UUIDDatabase.UUID_REP0RT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor
                    (UUIDDatabase.UUID_REPORT_REFERENCE);
            if (descriptor != null) {
                BluetoothLeService.readDescriptor(characteristic.getDescriptor(
                        UUIDDatabase.UUID_REPORT_REFERENCE));
                ArrayList<String> reportReferenceValues = DescriptorParser.getReportReference(characteristic.
                        getDescriptor(UUIDDatabase.UUID_REPORT_REFERENCE));
                if (reportReferenceValues.size() == 2) {
                    mBundle.putString(Constants.EXTRA_DESCRIPTOR_REPORT_REFERENCE_ID,
                            reportReferenceValues.get(0));
                    mBundle.putString(Constants.EXTRA_DESCRIPTOR_REPORT_REFERENCE_TYPE,
                            reportReferenceValues.get(1));
                }


            }

        }
        //case for OTA characteristic received
        if (UUIDDatabase.UUID_OTA_UPDATE_CHARACTERISTIC
                .equals(characteristic.getUuid())) {
            //do noting now
        }


        intent.putExtras(mBundle);
        /**
         * Sending the broad cast so that it can be received on registered
         * receivers
         */

        mContext.sendBroadcast(intent);
    }



    /**
     * Connects to the GATT server hosted on the BlueTooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The
     * connection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public static void connect(final String address, final String devicename, Context context) {
        mContext = context;
        if (mBluetoothAdapter == null || address == null) {
            return;
        }

        BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            return;
        }
        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
        //  refreshDeviceCache(mBluetoothGatt);
        mBluetoothDeviceAddress = address;
        mBluetoothDeviceName = devicename;

        mConnectionState = STATE_CONNECTING;
    }

    public static boolean refreshDeviceCache(BluetoothGatt gatt) {
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh");
            if (localMethod != null) {
                return (Boolean) localMethod.invoke(localBluetoothGatt);
            }
        } catch (Exception localException) {
            System.out.println("An exception occured while refreshing device");
        }
        return false;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The
     * disconnection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public static void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }

        if (mConnectionState == STATE_CONNECTED){
            //  Logger.datalog(mContext.getResources().getString(R.string.dl_device_connecting));
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
    }

    public static void discoverServices() {
        // Logger.datalog(mContext.getResources().getString(R.string.dl_service_discover_request));
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        } else {
            mBluetoothGatt.discoverServices();
        }

    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read
     * result is reported asynchronously through the
     * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public static void readCharacteristic( BluetoothGattCharacteristic characteristic) {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Request a read on a given {@code BluetoothGattDescriptor }.
     *
     * @param descriptor The descriptor to read from.
     */
    public static void readDescriptor(
            BluetoothGattDescriptor descriptor) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        //Logger.datalog(mContext.getResources().getString(R.string.dl_descriptor_read_request));
        mBluetoothGatt.readDescriptor(descriptor);

    }



    private static String getHexValue(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (byte byteChar : array) {
            sb.append(String.format("%02x", byteChar));
        }
        return "" + sb;
    }

    /**
     * Request a write on a given {@code BluetoothGattCharacteristic}.
     *
     * @param characteristic
     * @param byteArray
     */

    public static void writeCharacteristicGattDb(
            BluetoothGattCharacteristic characteristic, byte[] byteArray) {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        } else {
            byte[] valueByte = byteArray;
            characteristic.setValue(valueByte);
            mBluetoothGatt.writeCharacteristic(characteristic);
        }
    }



    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification. False otherwise.
     */
    public static void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        if (characteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG)) != null) {
            if (enabled == true) {
                BluetoothGattDescriptor descriptor = characteristic
                        .getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            } else {
                BluetoothGattDescriptor descriptor = characteristic
                        .getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }



    /**
     * Enables or disables indications on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable indications. False otherwise.
     */
    public static void setCharacteristicIndication(BluetoothGattCharacteristic characteristic, boolean enabled) {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }

        if (characteristic.getDescriptor(UUID
                .fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG)) != null) {
            if (enabled == true) {
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
                        .fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            } else {
                BluetoothGattDescriptor descriptor = characteristic
                        .getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }


    /**
     * 改变BLE默认的单次发包、收包的最大长度,用于android 5.0及以上版本
     * @param mtu
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean requestMtu(int mtu){
        if (mBluetoothGatt != null) {
            return mBluetoothGatt.requestMtu(mtu);
        }
        return false;
    }



    /**
     * Retrieves a list of supported GATT services on the connected device. This
     * should be invoked only after {@code BluetoothGatt#discoverServices()}
     * completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public static List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null)
            return null;

        return mBluetoothGatt.getServices();
    }

    public static int getConnectionState() {

        return mConnectionState;
    }

    public static boolean getBondedState() {
        Boolean bonded;
        BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(mBluetoothDeviceAddress);
        bonded = device.getBondState() == BluetoothDevice.BOND_BONDED;
        return bonded;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBound = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mBound = false;
        close();
        return super.onUnbind(intent);
    }

    /**
     * Initializes a reference to the local BlueTooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        System.out.println("BLEService----------------->initialize");
        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        return mBluetoothAdapter != null;

    }

    /**
     * After using a given BLE device, the app must call this method to ensure
     * resources are released properly.
     */
    void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    @Override
    public void onCreate() {
        // Initializing the service
        if (!initialize()) {
            System.out.println("Service not initialized");
        }
    }

    /**
     * Local binder class
     */
    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

}
---
title: DJI Bridge
description: Plugin to bridge native android DJI SDK and Cordova
---
<!--
# license: Licensed to the Apache Software Foundation (ASF) under one
#         or more contributor license agreements.  See the NOTICE file
#         distributed with this work for additional information
#         regarding copyright ownership.  The ASF licenses this file
#         to you under the Apache License, Version 2.0 (the
#         "License"); you may not use this file except in compliance
#         with the License.  You may obtain a copy of the License at
#
#           http://www.apache.org/licenses/LICENSE-2.0
#
#         Unless required by applicable law or agreed to in writing,
#         software distributed under the License is distributed on an
#         "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#         KIND, either express or implied.  See the License for the
#         specific language governing permissions and limitations
#         under the License.
-->

# dji-bridge

This plugin acts as a bridge between a cordova app and the android DJI SDK. It handles the connection to the SDK, keeps an updated telemetry list, and provides a test suite to aid in development.

## Installation

Clone the repository from github

From a directory inside the cordova project run:

```
$ cordova plugin add {path_to_repository}
$ cordova prepare
```

Make sure the Android platform is already added to your cordova project
Copy build-extras.gradle from the dji-bridge/hooks directory to the platforms/android directory of your cordova project

```
$ cp {path_to_repository}/hooks/build-extas.gradle {path_to_cordova_project}/platforms/android/
```


Add the required items to Manifest.xml in the platforms/android directory. See DJI documentation for reference:
http://developer.dbeta.me/mobile-sdk/documentation/application-development-workflow/workflow-integrate.html#android-studio-project-integration

```
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-feature android:name="android.hardware.camera" />
<uses-feature android:name="android.hardware.camera.autofocus" />
<uses-feature android:name="android.hardware.usb.host" android:required="false" />
<uses-feature android:name="android.hardware.usb.accessory" android:required="true" />
...
    <application android:hardwareAccelerated="true" android:icon="@mipmap/icon" android:label="@string/app_name" android:supportsRtl="true">
...
        <uses-library android:name="com.android.future.usb.accessory" />
        <meta-data android:name="com.dji.sdk.API_KEY" android:value="123456789ZBCDEFG_ENTER_YOURS" />
        <activity android:launchMode="singleTop" android:name="dji.sdk.sdkmanager.DJIAoaControllerActivity" android:theme="@android:style/Theme.Translucent">
            <intent-filter>
                <action android:name="android.hardware.usb.action.USB_ACCESSORY_ATTACHED" />
            </intent-filter>
            <meta-data android:name="android.hardware.usb.action.USB_ACCESSORY_ATTACHED" android:resource="@xml/accessory_filter" />
        </activity>
        <service android:name="dji.sdk.sdkmanager.DJIGlobalService" />
...
```


## Supported Cordova Platforms

* Android 4.0.0 or above

## Operation

DJI Bridge automatically connects to the DJI SDK service. Once the USB connection to the remote controller is connected, it automatically negotiates and begins capturing telemetry. Telemetry can be accessed from inside the cordova web page by polling the plugin.

```
//Check if drone is attached
plugin.attachToDevice(callback)

//Retrieve telemetry
plugin.getAttitude(callback)
plugin.getLocation(callback)
plugin.getStatus(callback)

//Turn sythentic data on or off
plugin.setTestMode(mode, cb) 

```

## Todo

Several things are still needed:

Video Streaming
Push based notifications
Namespace deconfliction
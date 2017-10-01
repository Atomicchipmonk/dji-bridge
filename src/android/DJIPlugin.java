package io.cordova.hellocordova;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import java.util.Date;


import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import dji.sdk.products.Aircraft;
import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.battery.Battery;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.products.HandHeld;
import dji.sdk.sdkmanager.DJISDKManager;



/**
* This class echoes a string called from JavaScript.
*/
public class DJIPlugin extends CordovaPlugin {

    private static final String TAG = DJIPlugin.class.getName();

    Context appContext;

    private DJIProduct mDJIProduct;

    private boolean productConnected;


    private double lat = 0;
    private double lon = 0;
    private float alt = 0;

    private double yaw = 0;
    private double pitch = 0;
    private double roll = 0;
    private double gimbalYaw = 0;
    private double gimbalPitch = 0;
    private double gimbalRoll = 0;

    private int batteryPercent = 0;
    private boolean isFlying = false;
    private int gpsSatellites = 0;


    public void initialize(CordovaInterface cordova, CordovaWebView webView){
        super.initialize(cordova,webView);
        Log.d(TAG, "Intializing DJIPlugin");



        appContext = this.cordova.getActivity().getApplicationContext();


        //What actually registers with DJI service

        mDJIProduct = new DJIProduct(this);

        DJISDKManager.getInstance().registerApp(appContext, mDJIProduct.mDJISDKManagerCallback);


        //one for SDK Manager
        String [] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.READ_PHONE_STATE,
        };

        cordova.requestPermissions(this, 0, permissions);

    }



    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("echo")) {
            String message = args.getString(0);
            this.echo(message, callbackContext);
            return true;
        } else if(action.equals("getDate")) {
            final PluginResult result = new PluginResult(PluginResult.Status.OK, (new Date()).toString());
           callbackContext.sendPluginResult(result);
           return true;
        } else if(action.equals("attachToDevice")){

            Log.d(TAG, "Checking Device");
            if(productConnected == true){
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, ("Product Connected")));
            } else {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, ("Product Disconnected")));
            }



        } else if(action.equals("setTestMode")){

            Log.d(TAG, "Checking Device");
            if(productConnected == true){
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, ("Product Connected")));
            } else {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, ("Product Disconnected")));
            }

  
        } else if(action.equals("getAttitude")){

            JSONObject attJson = new JSONObject();
            attJson.put("yaw", String.valueOf(yaw));
            attJson.put("pitch", String.valueOf(pitch));
            attJson.put("roll", String.valueOf(roll));
            attJson.put("gimbalYaw", String.valueOf(gimbalYaw));
            attJson.put("gimbalPitch", String.valueOf(gimbalPitch));
            attJson.put("gimbalRoll", String.valueOf(gimbalRoll));
            PluginResult att = new PluginResult(PluginResult.Status.OK, attJson.toString());
            callbackContext.sendPluginResult(att);

        } else if(action.equals("getLocation")){

            JSONObject locJson = new JSONObject();
            locJson.put("alt", String.valueOf(alt));
            locJson.put("lat", String.valueOf(lat));
            locJson.put("lon", String.valueOf(lon));
            PluginResult loc = new PluginResult(PluginResult.Status.OK, locJson.toString());
            callbackContext.sendPluginResult(loc);

        } else if(action.equals("getStatus")){

            JSONObject statJson = new JSONObject();
            statJson.put("batteryPercent", String.valueOf(batteryPercent));
            statJson.put("gpsSatellites", String.valueOf(gpsSatellites));
            statJson.put("isFlying", String.valueOf(isFlying));
            PluginResult stat = new PluginResult(PluginResult.Status.OK, statJson.toString());
            callbackContext.sendPluginResult(stat);
            
        }else if(action.equals("jurg")){
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, ("schwing")));

        } else {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, ("other")));
        }
        return false;
    }


    private void echo(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            Log.d(TAG, message);
            callbackContext.success(message + "win");
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }



    /* setters and getters for DJI state */

    public void updateConnection(boolean productConnected){
        this.productConnected = productConnected;
    }

    public void updateBatteryStatus(int batteryPercent){
        this.batteryPercent = batteryPercent;
    }

    public void updateAttitudeStatus(double yaw, double pitch, double roll){
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;  
    }

    public void updateLocationStatus(double lat, double lon, float alt){
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
    }

    public void updateUAVStatus(boolean isFlying, int gpsSatellites){
        this.isFlying = isFlying;
        this.gpsSatellites = gpsSatellites;
    }

    public void updateGimbalStatus(double gimalYaw, double gimbalPitch, double gimbalRoll){
        this.gimbalYaw = gimbalYaw;
        this.gimbalPitch = gimbalPitch;
        this.gimbalRoll = gimbalRoll;
    }




}





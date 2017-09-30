package com.dji.plugin;

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
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;



/**
* This class echoes a string called from JavaScript.
*/
public class DJIPlugin extends CordovaPlugin {

    private static final String TAG = DJIPlugin.class.getName();

    DJIProduct productInstance;

    public void initialize(CordovaInterface cordova, CordovaWebView webView){
        super.initialize(cordova,webView);
        Log.d(TAG, "Intializing DJIPlugin");


        IntentFilter filter = new IntentFilter();
        filter.addAction(DJIProduct.FLAG_CONNECTION_CHANGE);

        //productInstance = new DJIProduct();
  //      registerReceiver(mReceiver, filter);
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
        } else {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, ("schwing")));
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


/*
    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
*/


    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            refreshSDKRelativeUI();
        }
    };

    private void refreshSDKRelativeUI() {
        BaseProduct mProduct = DJIProduct.getProductInstance();

        if (null != mProduct && mProduct.isConnected()) {
            Log.v(TAG, "refreshSDK: True");
//            mBtnOpen.setEnabled(true);

            String str = mProduct instanceof Aircraft ? "DJIAircraft" : "DJIHandHeld";
//            mTextConnectionStatus.setText("Status: " + str + " connected");

            if (null != mProduct.getModel()) {
//                mTextProduct.setText("" + mProduct.getModel().getDisplayName());
            } else {
//                mTextProduct.setText(R.string.product_information);
            }

        } else {
            Log.v(TAG, "refreshSDK: False");
//            mBtnOpen.setEnabled(false);

//            mTextProduct.setText(R.string.product_information);
//            mTextConnectionStatus.setText(R.string.connection_loose);
        }
    }
/*
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_open: {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }
*/


}





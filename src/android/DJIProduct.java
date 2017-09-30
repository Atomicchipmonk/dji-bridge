package io.cordova.hellocordova;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.products.Aircraft;
import dji.sdk.products.HandHeld;
import dji.sdk.sdkmanager.DJISDKManager;

public class DJIProduct {

    public Handler mHandler;
    private static BaseProduct mProduct;


    /**
     * This function is used to get the instance of DJIBaseProduct.
     * If no product is connected, it returns null.
     */
    public static synchronized BaseProduct getProductInstance() {
        if (null == mProduct) {
            mProduct = DJISDKManager.getInstance().getProduct();
        }
        return mProduct;
    }

    public static boolean isAircraftConnected() {
        return getProductInstance() != null && getProductInstance() instanceof Aircraft;
    }

    public static boolean isHandHeldConnected() {
        return getProductInstance() != null && getProductInstance() instanceof HandHeld;
    }

    public static synchronized Camera getCameraInstance() {

        if (getProductInstance() == null) return null;

        Camera camera = null;

        if (getProductInstance() instanceof Aircraft){
            camera = ((Aircraft) getProductInstance()).getCamera();

        } else if (getProductInstance() instanceof HandHeld) {
            camera = ((HandHeld) getProductInstance()).getCamera();
        }

        return camera;
    }


    public static synchronized FlightController getFlightControllerInstance() {

        if (getProductInstance() == null) return null;

        FlightController fc = null;

        if (getProductInstance() instanceof Aircraft){
            fc = ((Aircraft) getProductInstance()).getFlightController();

        }

        return fc;
    }

    public static synchronized Gimbal getGimbalInstance() {

        if (getProductInstance() == null) return null;

        Gimbal g = null;

        if (getProductInstance() instanceof Aircraft){
            g = ((Aircraft) getProductInstance()).getGimbal();

        }

        return g;
    }


}
package io.cordova.hellocordova;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import android.support.annotation.NonNull;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.products.Aircraft;
import dji.sdk.products.HandHeld;
import dji.sdk.battery.Battery;
import dji.sdk.sdkmanager.DJISDKManager;

import dji.common.battery.AggregationState;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.gimbal.GimbalState;



public class DJIProduct {

    private static final String TAG = DJIProduct.class.getName();

    public Handler mHandler;
    private static BaseProduct mProduct;
    private DJIPlugin mPluginCallback;


    FlightController mFlightController = null;
    Gimbal mGimbalController = null;
    Battery mBatteryController = null;


    public DJIProduct(DJIPlugin pluginCallback){
        mPluginCallback = pluginCallback;
    }





    public DJISDKManager.SDKManagerCallback mDJISDKManagerCallback = new DJISDKManager.SDKManagerCallback() {

        //Listens to the SDK registration result
        @Override
        public void onRegister(DJIError error) {

            if(error == DJISDKError.REGISTRATION_SUCCESS) {
                Log.d(TAG, "Register Success");

                DJISDKManager.getInstance().startConnectionToProduct();

            } else {
                Log.d(TAG, "Register sdk fails, check network is available");
            }
            Log.e("TAG", error.toString());
        }

        //Listens to the connected product changing, including two parts, component changing or product connection changing.
        @Override
        public void onProductChange(BaseProduct oldProduct, BaseProduct newProduct) {

            mProduct = newProduct;
            if(mProduct != null) {

                

                mProduct.setBaseProductListener(mDJIBaseProductListener);

                 //instantiate each controller and being receiving telemetry

            }

            Log.d(TAG, "Product Change");
        }
    };

    private BaseProduct.BaseProductListener mDJIBaseProductListener = new BaseProduct.BaseProductListener() {

        @Override
        public void onComponentChange(BaseProduct.ComponentKey key, BaseComponent oldComponent, BaseComponent newComponent) {

            if(newComponent != null) {
                newComponent.setComponentListener(mDJIComponentListener);
                if(key == BaseProduct.ComponentKey.GIMBAL){
                        mGimbalController = DJIProduct.getGimbalInstance();
                    if (mGimbalController != null) {
                        mGimbalController.setStateCallback(new GimbalState.Callback() {
                            @Override
                            public void onUpdate(@NonNull GimbalState gimbalState) {
                                mPluginCallback.updateGimbalStatus(
                                    gimbalState.getAttitudeInDegrees().getYaw(), 
                                    gimbalState.getAttitudeInDegrees().getPitch(), 
                                    gimbalState.getAttitudeInDegrees().getRoll());
                            }
                        });
                    }
                } else if (key == BaseProduct.ComponentKey.FLIGHT_CONTROLLER){

                    mFlightController = DJIProduct.getFlightControllerInstance();
                    if (mFlightController != null) {
                        mFlightController.setStateCallback(new FlightControllerState.Callback() {
                            @Override
                            public void onUpdate(@NonNull FlightControllerState flightControllerState) {
                                mPluginCallback.updateLocationStatus(
                                    flightControllerState.getAircraftLocation().getLatitude(), 
                                    flightControllerState.getAircraftLocation().getLongitude(), 
                                    flightControllerState.getAircraftLocation().getAltitude());

                                mPluginCallback.updateAttitudeStatus(
                                    flightControllerState.getAttitude().yaw, 
                                    flightControllerState.getAttitude().pitch, 
                                    flightControllerState.getAttitude().roll);

                                mPluginCallback.updateUAVStatus(
                                    flightControllerState.isFlying(), 
                                    flightControllerState.getSatelliteCount());
                            }
                        });
                    }
                } else if (key == BaseProduct.ComponentKey.BATTERY) {

                    mBatteryController = DJIProduct.getBatteryInstance();
                    if (mBatteryController != null) {
                        mBatteryController.setAggregationStateCallback(new AggregationState.Callback() {
                            @Override
                            public void onUpdate(@NonNull AggregationState batteryState) {
                                //MAVIC specific, only one battery
                                mPluginCallback.updateBatteryStatus(
                                    batteryState.getBatteryOverviews()[0].getChargeRemainingInPercent());
                            }
                        });
                    }
                }

                //Probably want to parse the key of the componentKey and then set up each instance as needed here. Much cleaner than at the beginning.
            }
            Log.d(TAG, "Component Change");
        }

        @Override
        public void onConnectivityChange(boolean isConnected) {
            mPluginCallback.updateConnection(isConnected);
            Log.d(TAG, "BaseProductListener: Connection Change: " + String.valueOf(isConnected));
        }

    };

    private BaseComponent.ComponentListener mDJIComponentListener = new BaseComponent.ComponentListener() {

        @Override
        public void onConnectivityChange(boolean isConnected) {
            Log.d(TAG, "ComponentListener: Connection Change: " + String.valueOf(isConnected));
        }

    };







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

    public static synchronized Battery getBatteryInstance() {

        if (getProductInstance() == null) return null;

        Battery b = null;

        if (getProductInstance() instanceof Aircraft){
            b = ((Aircraft) getProductInstance()).getBattery();

        }

        return b;
    }










}
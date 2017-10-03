package io.cordova.hellocordova;


import java.util.TimerTask;
import java.util.Date;
import java.util.Timer;


public class DJISimulator {

    private DJIPlugin mPluginCallback;



    /*
    Slew Fields - set different as needed
    */


    private static final double timeframeSeconds = 10;
    private static final double hz = 10;
    private static final double increment = 1 / (hz * timeframeSeconds);

    private static final double maxYaw = 90.0;
    private static final double minYaw = 0.0;
    private static final double yawIncrement = (maxYaw - minYaw) * increment;
    
    private static final double maxGimbalPitch = 0.0;
    private static final double minGimbalPitch = -45.0;
    private static final double gimbalPitchIncrement = (maxGimbalPitch - minGimbalPitch) * increment;
    
    private static final double maxLatitude = 39.170951;
    private static final double minLatitude = 39.167491;
	private static final double latIncrement = (maxLatitude - minLatitude) * increment;
    
	private static final double maxLongitude = -107.084118;
    private static final double minLongitude = -107.082383;
	private static final double lonIncrement = (maxLongitude - minLongitude) * increment;
    
	private static final float maxAltitude = 200;
    private static final float minAltitude = 400;
	private static final float altIncrement = (float) (maxAltitude - minAltitude) * (float) increment;
    


    private int counter = 0; 
    private boolean increasing = true;
    
    private double yaw = 0;
    private double gimbalPitch = 0;
    private double lat = 0;
    private double lon = 0;
    private float alt = 0;

    Timer time;


    public DJISimulator(DJIPlugin pluginCallback){
        mPluginCallback = pluginCallback;
		time = new Timer(); // Instantiate Timer Object
    }


    public void startSimulation(){
		ScheduledTask st = new ScheduledTask(); // Instantiate SheduledTask class
		time.schedule(st, 0, (long) (1000/hz));
    }

    public void stopSimulation(){
    	time.cancel();
    	time.purge();
    }

	/**
	 *
	 * @author Dhinakaran P.
	 */
	// Create a class extends with TimerTask
	public class ScheduledTask extends TimerTask {

		// Add your task here
		public void run() {
			yaw = minYaw + (counter * yawIncrement);
			gimbalPitch = minGimbalPitch + (counter * gimbalPitchIncrement);
			lat = minLatitude + (counter * latIncrement);
			lon = minLongitude + (counter * lonIncrement);
			alt = minAltitude + (counter * altIncrement);

			mPluginCallback.updateLocationStatus(
                lat, 
                lon, 
                alt);

            mPluginCallback.updateAttitudeStatus(
                yaw, 
                0.0, 
                0.0);

            mPluginCallback.updateUAVStatus(
                true, 
                16);
            mPluginCallback.updateGimbalStatus(
                yaw, 
                gimbalPitch, 
                0.0);

            mPluginCallback.updateBatteryStatus(
                89);

			if(counter > (hz * timeframeSeconds)){
				increasing = false;
			} else if( counter < 0) {
				increasing = true;
			}

			if(increasing){
				counter++;
			} else {
				counter--;
			}


		}
	}

}

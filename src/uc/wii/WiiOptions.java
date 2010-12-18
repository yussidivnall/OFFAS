package uc.wii;

import android.hardware.SensorManager;

public class WiiOptions {
	public static boolean time_stamp=true;
	
	public static boolean output_json=true;
	public static boolean output_python=false;
	public static boolean output_rotation=true;
	public static boolean output_orientation=true;
	public static boolean output_inclination=true;
	
	public static boolean output_raw_json=true;
	public static int orientation_timer=500; 
	
	public static int DefaultSensorDelay=SensorManager.SENSOR_DELAY_NORMAL;
}

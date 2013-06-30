package uc.wii;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


public class WiiSensors implements SensorEventListener{
	WiiConnection mWiiConnection=null;
	SensorManager mSensorManager;
	WiiService mWiiService;
	
	public WiiSensors(WiiConnection wc,SensorManager sm,WiiService ws) {
		mWiiConnection=wc;
		mSensorManager = sm;
		mWiiService=ws;
		initSensors();
		//this.start();
	}
	public void initSensors(){
		try{
			//mWiiService.alert("Init Sensors");
			//TODO Make this configurable, probably will slow it down too much to have useless sensor data
			List <Sensor>sensorList=mSensorManager.getSensorList(Sensor.TYPE_ALL);
			//mWiiService.alert("Init Sensors - got Sensor list");
			
			mWiiConnection.writeConnectionHeader(sensorList);
			//mWiiService.alert("Init Sensors - Wrote Connection Header");
			for (Sensor s:sensorList){
				//TODO And configurable SENSOR_DELAY for each sensor type
				mSensorManager.registerListener(this,s,WiiOptions.DefaultSensorDelay);
			}
			//mWiiService.alert("Init Sensors - done");
		}catch(Exception ioe){
			Log.d("InitSensors",ioe.getMessage());
			mWiiService.alert("initSensors()"+ioe.getMessage());
		}
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
		
	public void onSensorChanged(SensorEvent event) {
		try{
			//Log.d("Sensor Changed","Sensor Changed");
			mWiiConnection.writeSensorEvent(event);			
		}catch(IOException ioe){
			Log.w("onSensorChanged()",ioe.getMessage());
			mWiiService.alert(ioe.getMessage());
			mWiiService.stop();
		} catch (JSONException jse) {
			mWiiService.alert(jse.getMessage());
			Log.w("onSensorChanged() - JSONException",jse.getMessage());
			mWiiService.stop();
		}
	}
}

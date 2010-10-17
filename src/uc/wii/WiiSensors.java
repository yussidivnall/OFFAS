package uc.wii;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


public class WiiSensors extends Thread implements SensorEventListener{
	WiiConnection mWiiConnection=null;
	SensorManager mSensorManager;
	WiiService mWiiService;
	
	public WiiSensors(WiiConnection wc,SensorManager sm,WiiService ws) {
		mWiiConnection=wc;
		mSensorManager = sm;
		mWiiService=ws;
		initSensors();
		this.start();
	}
	public void initSensors(){
		try{
			//TODO Make this configurable, probably will slow it down too much to have useless sensor data
			List <Sensor>sensorList=mSensorManager.getSensorList(Sensor.TYPE_ALL);
			mWiiConnection.writeConnectionHeader(sensorList);
			for (Sensor s:sensorList){
				//TODO And configurable SENSOR_DELAY for each sensor type
				mSensorManager.registerListener(this,s,SensorManager.SENSOR_DELAY_NORMAL);
			}
		}catch(IOException ioe){
			Log.d("InitSensors",ioe.getMessage());
			mWiiService.alert(ioe.getMessage());
		}
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
		
	public void onSensorChanged(SensorEvent event) {
		try{		
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

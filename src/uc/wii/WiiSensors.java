package uc.wii;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
//@NOTE - I'm not sure what's he best way about all this, documentation says
//that for Orientation Sensor onSensorChange(...) is depreciated, and this needs to be done 
//through SensorManager.getInclination(), getOrientation() and getRotationMatrix()
//But for now it works and it saves me implementing all of this through 
//two interfaces(One for Orientation, and one through onSensorChanged() for all other sensors)
//So in conclusion...
//@ TODO add (proper) interface for Orientation, I guess on a timer base
//http://developer.android.com/reference/android/hardware/Sensor.html#TYPE_ORIENTATION

public class WiiSensors extends Thread implements SensorEventListener{
	WiiConnection mWiiConnection=null;
	SensorManager mSensorManager;
	WiiService mWiiService;
	
	Timer SettingsTimer;
	Timer OrientationTimer;
	
	public WiiSensors(WiiConnection wc,SensorManager sm,WiiService ws) {
		mWiiConnection=wc;
		mSensorManager = sm;
		mWiiService=ws;
		initSensors();
		this.start();
		OrientationTimer=new Timer();
		setOrientationUpdate();
	}
	public void setOrientationUpdate(){
		//OrientationTimer.cancel();
		//OrientationTimer.purge();
		if(WiiOptions.orientation_timer>0){
			OrientationTimer.schedule(new UpdateOrientation(),0,WiiOptions.orientation_timer);
		}		
	}
	
	public void initSensors(){
		try{
			List <Sensor>sensorList=mSensorManager.getSensorList(Sensor.TYPE_ALL);
			mWiiConnection.writeConnectionHeader(sensorList);
			//for (Sensor s:sensorList){
			//	mSensorManager.registerListener(this,s,SensorManager.SENSOR_DELAY_NORMAL);
			//}
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
	private class UpdateOptions extends TimerTask{
		@Override
		public void run() {	
		}
	}
	private class UpdateOrientation extends TimerTask{
		public void run() {
			try {
				mWiiConnection.writeOrientation(mSensorManager);
			} catch (IOException ioe) {
				Log.d("UpdateOrientation",ioe.getMessage());
				mWiiService.alert(ioe.getMessage());
			}
		}
	}
}

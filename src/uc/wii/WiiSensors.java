package uc.wii;

import java.io.IOException;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
//@NOTE - I'm not sure what's he best way about all this, documentation says
//that for Orientation Sensor onSensorChange(...) is deprecated, and this needs to be done 
//through SensorManager.getInclination(), getOrientation() and getRotationMatrix()
//But for now it works and it saves me implementing all of this through 
//two interfaces(One for Orientation, and one through onSensorChanged() for all other sensors)
//So in conclusion...
//TODO add (proper) interface for Orientation, I guess on a timer base
//http://developer.android.com/reference/android/hardware/Sensor.html#TYPE_ORIENTATION

public class WiiSensors implements SensorEventListener{
	WiiConnection mWiiConnection=null;
	SensorManager mSensorManager;
	
	public WiiSensors(WiiConnection wc,SensorManager sm) {
		mWiiConnection=wc;
		mSensorManager = sm;
		initSensors();
	}
	public void initSensors(){
		List <Sensor>sensorList=mSensorManager.getSensorList(Sensor.TYPE_ALL);
		for (Sensor s:sensorList){
			mSensorManager.registerListener(this,s,SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	public void writeGenericValues(SensorEvent event) throws IOException{
		mWiiConnection.write("Values='");
		String values="";
		for (double v:event.values){
			values=values+v+",";
		}
		values=values.substring(0, values.length()-1); // Remove trailing ,
		mWiiConnection.write(values+"' ");
	}
	
	public void writeAccelerometerData(SensorEvent event) throws IOException{
		mWiiConnection.write("Type='TYPE_ACCELEROMETER' ");
		mWiiConnection.write("X='"+event.values[0]+"' ");
		mWiiConnection.write("Y='"+event.values[1]+"' ");
		mWiiConnection.write("Z='"+event.values[2]+"' ");
	}
	public void writeGyroscopeData(SensorEvent event) throws IOException{
		mWiiConnection.write("Type='TYPE_GYROSCOPE' ");
		writeGenericValues(event);
	}
	public void writeLightData(SensorEvent event) throws IOException{
		mWiiConnection.write("Type='TYPE_LIGHT' ");
		writeGenericValues(event);
	}
	public void writeMagneticFieldData(SensorEvent event) throws IOException{
		mWiiConnection.write("Type='TYPE_MAGNETIC_FIELD' ");
		writeGenericValues(event);
	}
	public void writeOrientationData(SensorEvent event) throws IOException{//@NOTE see above!
		mWiiConnection.write("Type='TYPE_ORIENTATION' ");
		mWiiConnection.write("Azimtuh='"+event.values[0]+"' ");
		mWiiConnection.write("Pitch='"+event.values[1]+"' ");
		mWiiConnection.write("Roll='"+event.values[2]+"' ");	
	}
	public void writePressureData(SensorEvent event) throws IOException {
		mWiiConnection.write("Type='TYPE_PRESSURE' ");
		writeGenericValues(event);
	}
	public void writeProximityData(SensorEvent event)throws IOException{
		mWiiConnection.write("Type='TYPE_PROXIMITY' ");
		writeGenericValues(event);
	}
	public void writeTemperatureData(SensorEvent event) throws IOException{
		mWiiConnection.write("Type='TYPE_TEMPERATURE' ");
		writeGenericValues(event);
	}
	public void outputSensorEvent(SensorEvent event) throws IOException{
		mWiiConnection.write("<Sensor ");
		switch(event.sensor.getType()){
			case Sensor.TYPE_ACCELEROMETER:	writeAccelerometerData(event);break;
			case Sensor.TYPE_GYROSCOPE:writeGyroscopeData(event);break;
			case Sensor.TYPE_LIGHT:writeLightData(event);break;
			case Sensor.TYPE_MAGNETIC_FIELD:writeMagneticFieldData(event);break;
			case Sensor.TYPE_ORIENTATION:writeOrientationData(event);break; //Move this to it's own handler see @NOTE
			case Sensor.TYPE_PRESSURE:writePressureData(event);break;
			case Sensor.TYPE_PROXIMITY:writeProximityData(event);break;
			case Sensor.TYPE_TEMPERATURE:writeTemperatureData(event);break;
			default:
				mWiiConnection.write("Type='TYPE_UNKNOWN' ");
				writeGenericValues(event);
				break;
		}
		mWiiConnection.write("/>\n");
	}
	
	
	public void onSensorChanged(SensorEvent event) {
		try{
			outputSensorEvent(event);	
		}catch(IOException ioe){
			Log.w("onSensorChanger()",ioe.getMessage());
			mWiiConnection.close();
			// TODO inform WiiService to alert user of disconnection
		}
	}
}

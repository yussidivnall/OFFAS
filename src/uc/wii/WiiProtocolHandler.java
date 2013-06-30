package uc.wii;
import java.util.List;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class WiiProtocolHandler {

	public float[] mGravity = new float[3];
	public float[] mGeomagnetic = new float[3];
	public float[] mRotationMatrix = new float[9];
	public float[] mInclinationMatrix= new float[9];
	public float mInclination;
	public float[] mOrientation = new float[3]; //[0]Azimuth,[1]Pitch,[2]Roll
	//Returns a multidimentional JSON array
	//Params values[],rows,columns
	//Return JSONArray()
	//
	public JSONArray getJSONMatrix(float values[],int rows, int columns)throws JSONException{
		JSONArray ret=new JSONArray();
		if(values.length!=rows*columns){return null;}
		for(int r=0;r<rows;r++){
			JSONArray row=new JSONArray();
			for (int c=0;c<columns;c++){
				double value=values[r+c];
				row.put(value);
			}
			ret.put(row);
		}
		return ret;
	}
	public WiiProtocolHandler(){}
	
	public String serializeSensorEventToPython(SensorEvent se){return null;}
	
	public String serializeSensorEventToJSON(SensorEvent se) throws JSONException{
		JSONObject jo = new JSONObject();
		JSONArray values=new JSONArray();
		
		if(WiiOptions.time_stamp)jo.put("time", new java.util.Date().getTime());
		jo.put("sensor_name",se.sensor.getName());
		jo.put("sensor_type",se.sensor.getType());
		for (double v:se.values){
			values.put(v);
		}
		jo.put("values",values);
		
		//jo.accumulate("values",se.values);//TODO put these values
		if(WiiOptions.output_rotation){
			JSONArray rotation=getJSONMatrix(mRotationMatrix,3,3);
			jo.put("rotation_matrix", rotation);
		}
		if(WiiOptions.output_orientation){
			JSONArray orientation=new JSONArray();
			for (float v : mOrientation){
				orientation.put(v);
			}
			jo.put("orientation_matrix",orientation);
		}
		if(WiiOptions.output_inclination){
			JSONArray inclination = getJSONMatrix((float[])mInclinationMatrix,3,3);
			jo.put("inclination_matrix", inclination);
		}
		String ret = jo.toString();
		jo=null;
		return ret;
	}
	
	
	public String sensorEvent(SensorEvent se) throws JSONException{
		if(WiiOptions.output_rotation||WiiOptions.output_inclination||WiiOptions.output_orientation){
			if(se.sensor.getType()==Sensor.TYPE_ACCELEROMETER)mGravity=se.values;		
			if(se.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD)mGeomagnetic=se.values;
			if(mGravity!=null && mGeomagnetic!=null){
				if(!SensorManager.getRotationMatrix(mRotationMatrix,mInclinationMatrix,mGravity,mGeomagnetic)){
					//COULD NOW COMPUTE ROTATION MATRIX (IN FREE FALL?) NOW WHAT?
					Log.d("NO ROTATION MATRIX","NO ROTATION_MATRIX");
				}else{
					//for (float i:mRotationMatrix){
					//	Log.i("RotationMatrix",""+i);
					//}
					
					//Fails - Throws a null pointer exception why?
					if(WiiOptions.output_inclination)mInclination=SensorManager.getInclination(mInclinationMatrix);
					if(WiiOptions.output_orientation)SensorManager.getOrientation(mRotationMatrix,mOrientation);
					
				}
			}
		}
		return serializeSensorEventToJSON(se);
	}
	
	
	
	
	public String connectionHeader(List<Sensor> list){
		return "";
	}
	

}

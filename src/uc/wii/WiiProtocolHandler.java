package uc.wii;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import org.json.JSONException;
import org.json.JSONObject;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class WiiProtocolHandler {
	public final String XML_HEADER="<?xml version=\"1.0\"?>\r\n";
	public final String REQUEST_TAG="Request";
	public final String SENSOR_EVENT_TAG="SensorEvent";
	public final String JSON_TAG="JSON";
	public final String CONNECTION_HEADER_TAG="Header";
	public final String AVAILABLE_SENSORS_TAG="AvailableSensors";
	public final String SENSOR_TAG="Sensor";
	public final String SENSOR_NAME_TAG="SensorName";
	public final String SENSOR_TYPE_TAG="SensorType";
	public final String ROTATION_TAG="Rotation";
	public final String INCLINATION_TAG="Inclination";
	public final String ORIENTATION_TAG="Orientation";
		public final String AZIMUTH_TAG="Azimuth";
		public final String PITCH_TAG="Pitch";
		public final String ROLL_TAG="Roll";
	
	public float[] mGravity = new float[3];
	public float[] mGeomagnetic = new float[3];
	public float[] mRotationMatrix = new float[9];
	public float[] mInclinationMatrix= new float[9];
	public float mInclination;
	public float[] mOrientation = new float[3]; //[0]Azimuth,[1]Pitch,[2]Roll
	
	public WiiProtocolHandler(){}
	
	public String serializeSensorEventToPython(SensorEvent se){return null;}
	
	public String serializeSensorEventToJSON(SensorEvent se) throws JSONException{
		JSONObject jo = new JSONObject();
		jo.put("sensor_name",se.sensor.getName());
		jo.put("sensor_type",se.sensor.getType());
		jo.put("values",se.values); //TODO fix this, at the moment only output reference to array
		//TODO put these values
		if(WiiOptions.output_rotation){}
		if(WiiOptions.output_orientation){}
		if(WiiOptions.output_inclination){}
		
		return jo.toString();
	}
	public String serializeSensorEventToXML(SensorEvent se){return null;}
	
	public String tagStart(String tag){
		return "<"+tag;
	}
	public void extract(String s){
		if(s.matches("<"+REQUEST_TAG)){}
	}
	public String tag(String name){
		return "<"+name+">";
	}
	public String endTag(String name){
		return "<\\"+name+">";
	}
	String TagValue(String tag,String value){
		return tag(tag)+value+endTag(tag);
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
		
		String ret=XML_HEADER;
		ret+=tag(SENSOR_EVENT_TAG)+"\n";
		if(WiiOptions.output_json)ret+=TagValue(JSON_TAG,serializeSensorEventToJSON(se))+"\n";
		//TODO
		//if(WiiOptions.output_python)ret+=TagValue(JSON_TAG,serializeSensorEventToPython(se))+"\n";
		//if(WiiOptions.output_xml)ret+=TagValue(JSON_TAG,serializeSensorEventToXML(se))+"\n";
		ret+=endTag(SENSOR_EVENT_TAG);
		return ret+"\n";
	}
	
	public void parseRequest(XmlPullParser xpp) throws XmlPullParserException, IOException{
		int eventType=xpp.getEventType();
		while(eventType != XmlPullParser.END_TAG && xpp.getName()!="REQUEST_TAG"){
			eventType=xpp.next();
		}
	}
	
	public void pullParser(String s) throws IOException{
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(s));
			int eventType=xpp.getEventType();
			while(eventType!=XmlPullParser.END_DOCUMENT){
				switch(eventType){
					case XmlPullParser.START_TAG:
						if(xpp.getName()==REQUEST_TAG)parseRequest(xpp);
					break;
					case XmlPullParser.END_TAG:break;
					default:break;
				}
				eventType=xpp.next();
			}
			
		} catch (XmlPullParserException xppe) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	
	public void parseInput(InputStream is) throws IOException{
		InputStreamReader isr=new InputStreamReader(is);
		//int i=is.read();
		
		char b[]= new char[is.available()];
		isr.read(b,1,is.available()-1);
		String s=new String(b);
		Log.d("Input",s);
		//extract(s);
	}
	
	public String connectionHeader(List<Sensor> list){
		String ret=XML_HEADER;
		ret+=tag(CONNECTION_HEADER_TAG)+"\n";
		ret+=tag(AVAILABLE_SENSORS_TAG)+"\n";
		for(Sensor s:list){
			ret+=tag(SENSOR_TAG)+"\n";
			ret+=TagValue(SENSOR_NAME_TAG,s.getName())+"\n";
			ret+=TagValue(SENSOR_TYPE_TAG,""+s.getType())+"\n";
			ret+=endTag(SENSOR_TAG)+"\n";
		}
		ret+=endTag(AVAILABLE_SENSORS_TAG)+"\n";
		ret+=endTag(CONNECTION_HEADER_TAG)+"\n";
		return ret;
	}
	


	public String rotation(float[] acceleration, float[] gravity) {
		float R[] = new float[9]; //Rotation Matrix
		float I[] = new float[9]; //Inclination Matrix
		String ret="";
		if(SensorManager.getRotationMatrix(R,I,gravity,acceleration)){
			ret+=XML_HEADER;
			ret+=tag(ROTATION_TAG);
			ret+=endTag(ROTATION_TAG);
		}
		return ret;
	}
}

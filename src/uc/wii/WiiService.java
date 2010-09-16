package uc.wii;

import java.io.IOException;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WiiService extends Service {
	WiiConnection mWiiConnection;
	WiiSensors mWiiSensors;
	SensorManager mSensorManager;
	
	@Override
	public void onCreate(){
		super.onCreate();
		mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
	}
	@Override
	public void onDestroy(){
		try{
			if(mWiiConnection!=null)mWiiConnection.close();
			mSensorManager.unregisterListener(mWiiSensors);
		}catch(Exception ioe){
			alert(ioe.getMessage());
			Log.w("onDestroy()",ioe.getMessage());
		}
		super.onDestroy();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		Toast.makeText(this,"onBind()...",Toast.LENGTH_LONG).show();
		return null;
	}
	@Override
	public int onStartCommand(Intent intent,int flags,int startID){
		try{
			Bundle bundle=intent.getExtras();
			String address = (String)bundle.get("address");
			int port = new Integer((String)bundle.get("port"));
			if(address!="" && port>1){
				if(mWiiConnection!=null){
						mWiiConnection.close();
						alert("Closed old connectionm");
				}
				alert("Connecting to "+address+":"+port);
				mWiiConnection = new WiiConnection(address,port,false);
				mWiiConnection.write("<?xml version='1.0' encoding='UTF-8'?>\n");
				Toast.makeText(this,"Connected",Toast.LENGTH_SHORT).show();
				mWiiSensors=new WiiSensors(mWiiConnection,mSensorManager);
			}
		}catch(IOException ioe){
			alert(ioe.getMessage());
			Log.w("onStartCommand()","error:"+ioe.getMessage());
		}
		return Service.START_STICKY;
	}
	public void alert(String msg){
		Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
	}
}

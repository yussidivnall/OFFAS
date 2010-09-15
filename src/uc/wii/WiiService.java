package uc.wii;

import java.io.IOException;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
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
		super.onDestroy();
		try{
			mWiiConnection.close();
			mSensorManager.unregisterListener(mWiiSensors);
		}catch(IOException ioe){
			Toast.makeText(this,ioe.getLocalizedMessage(),Toast.LENGTH_LONG);
		}
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
						Toast.makeText(this,"Closed old connection",Toast.LENGTH_LONG).show();
				}
				Toast.makeText(this,"Connecting"+address+":"+port,Toast.LENGTH_LONG).show();
				mWiiConnection = new WiiConnection(address,port,false);
				mWiiConnection.write("<?xml version='1.0' encoding='UTF-8'?>\n");
				Toast.makeText(this,"Connected",Toast.LENGTH_SHORT).show();
				mWiiSensors=new WiiSensors(mWiiConnection,mSensorManager);
			}
		}catch(IOException ioe){
			Toast.makeText(this,ioe.getLocalizedMessage(),Toast.LENGTH_LONG);
			//Toast.makeText(this,"Could not connect",Toast.LENGTH_LONG);
		}
		return Service.START_STICKY;
	}
}

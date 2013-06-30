package uc.wii;

import java.io.IOException;
import java.util.ArrayList;


import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
/*
 * Starting point when running as a service
 */
public class WiiService extends Service {
	private NotificationManager mNotificationManager;
	ArrayList<Messenger> mClients = new ArrayList<Messenger>();

    class IncomingHandler extends Handler {
    	@Override
        public void handleMessage(Message msg) {
    		switch (msg.what){
    			case STOP_SERVICE:
    				stop();
    				mClients.remove(msg.replyTo);
    				break;
    			case REGISTER_CLIENT: 
    				mClients.add(msg.replyTo);
    				break;
    			case UNREGISTER_CLIENT:
    				mClients.remove(msg.replyTo);
    				break;
    		}
    	}	
    }
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    public static final int STOP_SERVICE=1;
    public static final int CONNECTING_SOCKET=2;
    public static final int DISCONNECTING_SOCKET=3;
    public static final int CONNECTED=4;
    public static final int DISCONNECTED=5;
    public static final int REGISTER_CLIENT=6;
    public static final int UNREGISTER_CLIENT=7;
    
    public void sendMessage(int what){
    	try{
    		alert("Sending message:"+what);
    		for (Messenger messenger:mClients){
    			alert("Client:");
    			Message msg = Message.obtain(null,what);
    			msg.replyTo=mMessenger;
    			messenger.send(msg);
    		}
    	}catch(RemoteException re){
    		Log.e("Message dispatch error", re.getLocalizedMessage());
    	}
    }
    
    
    
	WiiConnection mWiiConnection;
	WiiSensors mWiiSensors;
	SensorManager mSensorManager;
	@Override
	public void onCreate(){
		super.onCreate();
		mNotificationManager=(NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
		mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		try{
			stop();
		}catch(Exception ioe){
			alert("WiiService.onDestroy()"+ioe.getMessage());
			Log.w("onDestroy()",ioe.getMessage());
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Toast.makeText(this,"onBind()...",Toast.LENGTH_LONG).show();
		//start(intent);
		return mMessenger.getBinder();
	}
	@Override
	public int onStartCommand(Intent intent,int flags,int startID){
		start(intent);
		return Service.START_STICKY;
	}
	public void start(Intent intent){
		
		try{
			sendMessage(CONNECTING_SOCKET);
			Bundle bundle=intent.getExtras();
			String address = (String)bundle.get("address");
			int port = new Integer((String)bundle.get("port"));
			if(address!="" && port>1){
				//if(mWiiConnection!=null){
				//		mWiiConnection.close();
				//		alert("Closed old connection");
				//}
				alert("Connecting to "+address+":"+port);
				mWiiConnection = new WiiConnection(address,port,false);
				alert("Connected");
				mWiiSensors=new WiiSensors(mWiiConnection,mSensorManager,this);
			}
		}catch(Exception ioe){
			alert("WiiService.start()"+ioe.getMessage());
			Log.w("onStartCommand()","error:"+ioe.getMessage());
		}
				
	}
	
	
	public void alert(String msg){
		Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
	}
	public void stop(){
		super.stopSelf();
		if(mWiiConnection!=null){
			mWiiConnection.close();
			
		}
		mSensorManager.unregisterListener(mWiiSensors);
		mNotificationManager.cancel(1);
		this.stopSelf();
	}

}

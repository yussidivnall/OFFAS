package uc.wii;

import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import java.net.Socket;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;
//@ TODO implement SSL
public class WiiConnection extends Thread{
	private boolean SSL;
	private Socket sock;
	private boolean done=false;
	public WiiProtocolHandler mWiiProtocolHandler;
	
	public WiiConnection(String address,int port,boolean ssl) throws IOException{
			SSL=ssl;
			mWiiProtocolHandler=new WiiProtocolHandler();
			if(!SSL){
				Log.d("WiiConnection","Starting a nonSSL socket thread");
				sock=new Socket(address,port);
				sock.setKeepAlive(true);
				sock.setSoTimeout(30);
				this.start();
			}
	}
	public void run(){
		try{
			Log.d("run()","Started run()");
			if(!SSL){
				Log.d("run()","Non SSL run cluase");
				while(!done){
					if(sock.isClosed()||!sock.isConnected()|| sock.isInputShutdown()||!sock.isBound()||sock.isOutputShutdown())done=true;
					
					if(new InputStreamReader(sock.getInputStream()).ready()){
						mWiiProtocolHandler.parseInput(sock.getInputStream());
					}
					//if(sock.getInputStream().available()>=1){
					//	Log.d("Something to read","Hey");
					//	mWiiProtocolHandler.parseInput(sock.getInputStream());
					//}
				}
				Log.d("run()","Quiting");
				mWiiProtocolHandler=null;
				sock.close();
			}
		}catch(Exception ioe){
			Log.e("run()","Run Error:"+ioe.getMessage());
		}
	}
	
	public void write(String data) throws IOException{
			if(!SSL)sock.getOutputStream().write(data.getBytes());
	}
	public void writeSensorEvent(SensorEvent se) throws JSONException, IOException{
		write(mWiiProtocolHandler.sensorEvent(se));
	}
	public void writeConnectionHeader(List<Sensor> list) throws IOException{
		write(mWiiProtocolHandler.connectionHeader(list));
	}
	
	public void close(){
		done=true;
		Log.d("close()","Closing socket");
	}
	public void writeOrientation(SensorManager sm) throws IOException {
		write(mWiiProtocolHandler.orientation(sm));
	}
}

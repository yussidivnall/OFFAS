package uc.wii;

import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;
//@ TODO implement SSL
public class WiiConnection {
	//public String connection_state="uninitilized";
	
	
	private String mAddress;
	private int mPort;
	private boolean SSL;
	private Socket sock;
	public boolean done=false;
	public WiiProtocolHandler mWiiProtocolHandler;
	
	public WiiConnection(String address,int port,boolean ssl) throws IOException{
			SSL=ssl;
			mAddress=address;
			mPort=port;	
			mWiiProtocolHandler=new WiiProtocolHandler();
			
			initSock();
			//this.start();
			
	}
	public void initSock() throws IOException{
			if(!SSL){
				//connection_state="CONNECTING";
				Log.d("WiiConnection","Starting a nonSSL socket thread");
				sock=new Socket(mAddress,mPort);
				sock.setKeepAlive(true);
				sock.setSoTimeout(30);	
			}

		
	}
	
	
	public void run(){
		Log.d("run()","Started run()");
		//initSock();
	}
	
	public void write(String data) throws IOException,NullPointerException{
			if(!SSL){
				//if(sock.isClosed())return;
				sock.getOutputStream().write(data.getBytes());
			}
	}
	public void writeSensorEvent(SensorEvent se) throws JSONException, IOException{
		//Log.d("writeSensorEvent", "writing");
		write(mWiiProtocolHandler.sensorEvent(se));
	}
	public void writeConnectionHeader(List<Sensor> list) throws IOException{
		
		write(mWiiProtocolHandler.connectionHeader(list));
	}
	
	public void close(){
		done=true;
		try {
			sock.close();
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			Log.d("WiiConnection.close", ioe.getLocalizedMessage());
		} catch (Exception e){
			Log.d("WiiConnection.close - Exception", e.getLocalizedMessage());
		}
		Log.d("close()","Closing socket");
	}
	/*
	public void writeRotation(float[] acceleration, float[] gravity) throws IOException {
		write(mWiiProtocolHandler.rotation(acceleration,gravity));
	}
	*/
}

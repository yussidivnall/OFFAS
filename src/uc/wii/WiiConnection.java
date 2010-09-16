package uc.wii;

import java.io.IOException;
import java.net.Socket;

import android.util.Log;
//@ TODO implement SSL
public class WiiConnection extends Thread{
	private boolean SSL;
	private Socket sock;
	private boolean done=false;
	
	public WiiConnection(String address,int port,boolean ssl) throws IOException{
			SSL=ssl;
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
				}
				Log.d("run()","Quiting");
				sock.close();
			}
		}catch(Exception ioe){
			Log.e("run()","Run Error:"+ioe.getMessage());
		}
	}
	
	public void write(String data) throws IOException{
			if(!SSL)sock.getOutputStream().write(data.getBytes());
	}
	
	public void close(){
		done=true;
		Log.d("close()","Closing socket");
	}
}

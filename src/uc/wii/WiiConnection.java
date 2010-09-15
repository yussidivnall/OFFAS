package uc.wii;

import java.io.IOException;
import java.net.Socket;
//Bugs - This sometimes crash, and does not Toast when connection is not established
public class WiiConnection{
	public boolean SSL;
	private Socket sock;
	
	public WiiConnection(String address,int port,boolean ssl) throws IOException{
			SSL=ssl;
			if(!SSL)sock=new Socket(address,port);
	}
	public void write(String data) throws IOException{
		try {
			
			if(!SSL)sock.getOutputStream().write(data.getBytes());
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void close() throws IOException{
			if(sock != null) sock.close();
			//if(!SSL)sock.close();
	}
}

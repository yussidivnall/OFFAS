package uc.wii;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;

public class WiiActivity extends Activity  {
	Messenger mService; //Outgoing to service
	final Messenger mMessenger = new Messenger(new IncomingHandler()); //incoming from service 
	
	boolean bound;
	class IncomingHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			Toast.makeText(null, "Got Message", Toast.LENGTH_LONG);
			switch(msg.what){
				case WiiService.CONNECTING_SOCKET:
					((TextView)findViewById(R.id.Status)).setText("Connecting");
					break;
				case WiiService.DISCONNECTING_SOCKET: break;
				case WiiService.CONNECTED: break;
				case WiiService.DISCONNECTED: break;
			}
		}
	}
	private ServiceConnection mConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = new Messenger(service);
			try{
				Message msg = Message.obtain(null, WiiService.REGISTER_CLIENT);
				msg.replyTo = mMessenger;
				mService.send(msg);
			}catch(RemoteException re){
				
			}
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService=null;
			
		}
		
	};
	
	
	SharedPreferences preferences;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        preferences=PreferenceManager.getDefaultSharedPreferences(this);

        Button conn_button=(Button)findViewById(R.id.ConnectButton);
        Button disconn_button=(Button)findViewById(R.id.DisconnectButton);

        
        conn_button.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
				if(WiiOptions.run_as_service)startService();
				else ; 
			}
        });
        disconn_button.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
				if(WiiOptions.run_as_service)stopService();
				else ;
			}
        });
    }
    /*
    @Override
    public void onSaveInstanceState(Bundle bundle){
    	super.onSaveInstanceState(bundle);
    }
    @Override
    public void onRestoreInstanceState(Bundle bundle){
    	super.onRestoreInstanceState(bundle);
    }
    
    @Override
    public void onPause(){
    	//super.onPause();
    }
    @Override
    public void onResume(){
    	//super.onResume();
    }
    
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	MenuInflater inflater = this.getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
    	if(item.getItemId()==R.id.settings){
    		Intent intent= new Intent(WiiActivity.this,SettingsActivity.class);
    		this.startActivity(intent);
    	}
    	return true;
    }
    
    
    public void startService(){
    	String add=preferences.getString("address", "127.0.0.1");
    	String port=preferences.getString("port", "234");
    	
    	Intent intent=new Intent(this,WiiService.class);
    	
    	intent.putExtra("address", add);
    	intent.putExtra("port", port);

    	bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
    	bound=true;
    	startService(intent);
    }
    public void stopService(){
    	//if(bound){
    		if(mService != null){
                try {
                    Message msg = Message.obtain(null,WiiService.STOP_SERVICE);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                    
                } catch (RemoteException e) {
                	Log.e("WiiActivity.stopService", e.getLocalizedMessage());
                }
                unbindService(mConnection);
                stopService(new Intent(this,WiiService.class));
    		}
    		bound= false;
    	//}
    }
}
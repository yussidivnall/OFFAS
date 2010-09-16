package uc.wii;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.Toast;

public class WiiActivity extends Activity {
	WiiService mWiiService;

	
	//ServiceConnection mServiceConnection;
	Intent wiiServiceIntent;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button conn_button=(Button)findViewById(R.id.ConnectButton);
        Button disconn_button=(Button)findViewById(R.id.DisconnectButton);    
        conn_button.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {startService();}
        });
        disconn_button.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {stopService();}
        });
    }
    public void startService(){
    	EditText AddressET= (EditText)findViewById(R.id.AddressEditText);
    	EditText PortET = (EditText)findViewById(R.id.PortEditText);
    	String add=AddressET.getText().toString();
    	String port=PortET.getText().toString();
    	Intent intent=new Intent(this,WiiService.class);
    	intent.putExtra("address",add);
    	intent.putExtra("port",port);
    	startService(intent);
    }
    public void stopService(){
    	stopService(new Intent(this,WiiService.class));
    }
}
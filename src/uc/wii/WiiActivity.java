package uc.wii;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        
        SharedPreferences preferences=getPreferences(MODE_PRIVATE);
        if(preferences!=null){
        	String addr=preferences.getString("address",null);
        	String port=preferences.getString("port",null);
        	if(addr!=null)((EditText)findViewById(R.id.AddressEditText)).setText(addr);
        	if(port!=null)((EditText)findViewById(R.id.PortEditText)).setText(port);
        }
        
        
        //Restore state bundle(if any)
        if(savedInstanceState!=null){
        	String addr=savedInstanceState.getString("address");
        	String port=savedInstanceState.getString("port");
        	if(addr!=null)((EditText)findViewById(R.id.AddressEditText)).setText(addr);
        	if(port!=null)((EditText)findViewById(R.id.PortEditText)).setText(port);
        }
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
    @Override
    public void onSaveInstanceState(Bundle bundle){
    	bundle.putString("address", ((EditText)findViewById(R.id.AddressEditText)).getText().toString());
    	bundle.putString("port", ((EditText)findViewById(R.id.PortEditText)).getText().toString());
    	super.onSaveInstanceState(bundle);
    }
    @Override
    public void onRestoreInstanceState(Bundle bundle){
    	super.onRestoreInstanceState(bundle);
    	((EditText)findViewById(R.id.AddressEditText)).setText(bundle.getString("address"));
    	((EditText)findViewById(R.id.PortEditText)).setText(bundle.getString("port"));
    }
    @Override
    public void onPause(){
    	super.onPause();
    	SharedPreferences preferences = getPreferences(MODE_PRIVATE);
    	SharedPreferences.Editor editor = preferences.edit();
    	editor.putString("address", ((EditText)findViewById(R.id.AddressEditText)).getText().toString());
    	editor.putString("port", ((EditText)findViewById(R.id.PortEditText)).getText().toString());
    	
    	editor.commit();
    }
    @Override
    public void onResume(){
    	super.onResume();
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	MenuInflater inflater = this.getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	return true;
    }
    public void startService(){
    	String add=((EditText)findViewById(R.id.AddressEditText)).getText().toString();
    	String port=((EditText)findViewById(R.id.PortEditText)).getText().toString();
    	Intent i=new Intent(this,WiiService.class);
    	i.putExtra("address", add);
    	i.putExtra("port", port);
    	startService(i);
    	
    }
    public void stopService(){
    	stopService(new Intent(this,WiiService.class));
    }
}
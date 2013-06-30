package uc.wii;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedBundle){
		super.onCreate(savedBundle);
		this.addPreferencesFromResource(R.xml.settings);
	}
}

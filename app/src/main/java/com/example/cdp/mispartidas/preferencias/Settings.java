package com.example.cdp.mispartidas.preferencias;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.cdp.mispartidas.R;

/**
 * Created by CDP on 11/02/2016.
 */
public class Settings extends PreferenceActivity {
  
  @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}

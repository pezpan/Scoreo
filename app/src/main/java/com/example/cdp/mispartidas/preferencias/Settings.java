package com.example.cdp.mispartidas.preferencias;

/**
 * Created by CDP on 11/02/2016.
 */
public class Settings extends PreferenceActivity{
  
  @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}

package com.example.cdp.mispartidas.actividades;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.cdp.mispartidas.R;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class ConfiguracionActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    // Valores para las preferencias
    public static int incremento = 1;
    public static int decremento = 1;
    public static int contador_inicial = 0;
    public static int caras = 6;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferencias);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("pref_contador_inicial")) {
            ConfiguracionActivity.contador_inicial = Integer.parseInt(sharedPreferences.getString(s, ""));
        }else if (s.equals("pref_incremento")) {
            ConfiguracionActivity.incremento = Integer.parseInt(sharedPreferences.getString(s, ""));
        }else if (s.equals("pref_decremento")) {
            ConfiguracionActivity.decremento = Integer.parseInt(sharedPreferences.getString(s, ""));
        }else if (s.equals("pref_caras_dado")) {
            ConfiguracionActivity.caras = Integer.parseInt(sharedPreferences.getString(s, ""));
        }
    }
}
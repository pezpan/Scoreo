package com.example.cdp.mispartidas.actividades;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.cdp.mispartidas.R;

public class Duelo extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duelo);
        
        Log.i("MILOG", "Obtenemos el backup");
        backup = Backup.getMiBackup(getApplicationContext());

        // Obtenemos el numero de jugadores
        Bundle bundle = getIntent().getExtras();
        identificador = bundle.getString("idpartida");

        Log.i("MILOG", "El identificador de la partida es " + identificador);

        // Habilitamos la fecha volver a la activity principal
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Buscamos la partida
        indice = backup.getPartida(identificador);
        if (indice >= 0) {
            
        } else {
            Toast.makeText(this, "No se ha encontrado la partida " + identificador, Toast.LENGTH_SHORT).show();
        }
        
        
    }
    
    
    
    
    
}

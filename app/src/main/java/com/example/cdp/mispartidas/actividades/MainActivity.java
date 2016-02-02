package com.example.cdp.mispartidas.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.cdp.mispartidas.auxiliares.Utilidades;
import com.example.cdp.mispartidas.dialogos.NumeroJugadoresDialogFragment;
import com.example.cdp.mispartidas.R;
import com.example.cdp.mispartidas.almacenamiento.operaciones.Backup;

public class MainActivity extends ActionBarActivity implements NumeroJugadoresDialogFragment.NumberDialogListener {
    
    private MainListener listeneropciones;
    private Backup backup;
    private String identificadorultima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Indicamos el archivo de backup
        Log.i("MILOG", "Obtenemos el backup");
        backup = Backup.getMiBackup(getApplicationContext());
        // Obtenemos el backup para tenerlo disponible desde ahora
        Log.i("MILOG", "Leemos el archivo y obtenemos la variable backup");
        if((backup.getBackup() == null) || (backup.getBackup().size() == 0)) {
            backup.obtenerBackup();
        }

        // Obtenemos los botones
        Button botonaceptar = (Button) findViewById(R.id.botonnueva);
        Button botonhistorial = (Button) findViewById(R.id.botonhistorial);
        Button botonduelo = (Button) findViewById(R.id.botonduelo);
        Button botoncontinuar = (Button) findViewById(R.id.botoncontinuar);
        // Mostramos el nombre de la ultima partida
        /*
        if(identificadorultima != null){
            Partida partida = backup.getBackup().get(backup.getPartida(identificadorultima));
            botoncontinuar.setText(botoncontinuar.getText().toString() + "\n" + partida.getNombre());
        }
        */
        // Definimos nuestro listener
        listeneropciones = new MainListener();
        botonaceptar.setOnClickListener(listeneropciones);
        botonhistorial.setOnClickListener(listeneropciones);
        botonduelo.setOnClickListener(listeneropciones);
        botoncontinuar.setOnClickListener(listeneropciones);
        // Incluimos el efecto de hacer click
        Utilidades.buttonEffect(botonaceptar);
        Utilidades.buttonEffect(botonhistorial);
        Utilidades.buttonEffect(botonduelo);
        Utilidades.buttonEffect(botoncontinuar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    @Override
    // Sobreescribimos el método de la interfaz para obtener el numero de jugadores seleccionados
    public void onNumberSelected(int number) {
        llamarSetupJugadores(number);
    }
    
    public void llamarSetupJugadores(int numero){
        // Llamamos al intent para la configuracion inicial de la partida
        Intent intentjugadores = new Intent(this, SetupJugadores.class);
        // Pasamos como datos el numero de jugadores seleccionados
        Bundle b = new Bundle();
        b.putInt("numjugadores", numero);
        //Lo anadimos al intent
        intentjugadores.putExtras(b);
        // Lanzamos la actividad
        startActivity(intentjugadores);
    }
    
    public class MainListener implements View.OnClickListener {
    
        @Override
        public void onClick(View v) {
            //Comprobamos que vista ha lanzado el evento y lo gestionamos
            switch (v.getId()) {
                case R.id.botonnueva:
                    NumeroJugadoresDialogFragment fragmento = new NumeroJugadoresDialogFragment();
                    Bundle bundles = new Bundle();
                    bundles.putString("titulo", getString(R.string.mensaje_jugadores));
                    bundles.putInt("maximo", 30);
                    bundles.putInt("minimo", 1);
                    fragmento.setArguments(bundles);
                    fragmento.show(getFragmentManager(),"Dialogo_jugadores");
                    break;
                case R.id.botonhistorial:
                    // Llamamos al intent de nuestras partidas guardadas
                    Intent intenthistorial = new Intent(getApplicationContext(), Historial.class);
                    startActivity(intenthistorial);
                    break;
                case R.id.botonduelo:
                    // Creamos una partida de dos jugadores
                    llamarSetupJugadores(2);
                    break;
                case R.id.botoncontinuar:
                    // Obtenemos la ultima partida modificada
                    identificadorultima = backup.getUltimaActualizada();
                    if(identificadorultima != null){
                        // Lanzamos la pantalla de nueva partida, pasando el identificador de la partida creada
                        Intent intentpartida = new Intent(getApplicationContext(), Tanteo.class);
                        // Pasamos como datos el numero de jugadores seleccionados
                        Bundle b = new Bundle();
                        Log.i("MILOG", "Guardamos los parametros desde la pantalla inicial para llamar al intent de la partida");
                        b.putString("idpartida", identificadorultima);
                        //Lo anadimos al intent
                        intentpartida.putExtras(b);
                        // Lanzamos la actividad
                        Log.i("MILOG", "Lanzamos la pantalla de tanteo desde la pantalla inicial");
                        startActivity(intentpartida);
                    }
                    break;
            }
        }
        
    }

}

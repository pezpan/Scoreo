package com.example.cdp.mispartidas.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.cdp.mispartidas.dialogos.NumeroJugadoresDialogFragment;
import com.example.cdp.mispartidas.R;
import com.example.cdp.mispartidas.almacenamiento.operaciones.Backup;

public class MainActivity extends ActionBarActivity implements NumeroJugadoresDialogFragment.NumberDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Indicamos el archivo de backup
        Log.i("MILOG", "Obtenemos el backup");
        Backup backup = Backup.getMiBackup(getApplicationContext());
        // Obtenemos el backup para tenerlo disponible desde ahora
        Log.i("MILOG", "Leemos el archivo y obtenemos la variable backup");
        if((backup.getBackup() == null) || (backup.getBackup().size() == 0)) {
            backup.obtenerBackup();
        }
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

        switch(id){
            // Lanzamos una nueva partida
            case R.id.nuevapartida:
                NumeroJugadoresDialogFragment fragmento = new NumeroJugadoresDialogFragment();
                Bundle bundles = new Bundle();
                bundles.putString("titulo", getString(R.string.mensaje_jugadores));
                bundles.putInt("maximo", 30);
                bundles.putInt("minimo", 1);
                fragmento.setArguments(bundles);
                fragmento.show(getFragmentManager(),"Dialogo_jugadores");
                break;
            case R.id.partidasguardadas:
                // Llamamos al intent de nuestras partidas guardadas
                Intent intenthistorial = new Intent(this, Historial.class);
                startActivity(intenthistorial);
                break;
            default:
                return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    @Override
    // Sobreescribimos el método de la interfaz para obtener el numero de jugadores seleccionados
    public void onNumberSelected(int number) {
        // Llamamos al intent para la configuracion inicial de la partida
        Intent intentjugadores = new Intent(this, SetupJugadores.class);
        // Pasamos como datos el numero de jugadores seleccionados
        Bundle b = new Bundle();
        b.putInt("numjugadores", number);
        //Lo anadimos al intent
        intentjugadores.putExtras(b);
        // Lanzamos la actividad
        startActivity(intentjugadores);
    }

}

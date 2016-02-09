package com.example.cdp.mispartidas.actividades;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cdp.mispartidas.R;
import com.example.cdp.mispartidas.almacenamiento.objetos.Partida;
import com.example.cdp.mispartidas.almacenamiento.operaciones.Backup;
import com.example.cdp.mispartidas.auxiliares.Dado;
import com.example.cdp.mispartidas.auxiliares.Utilidades;
import com.example.cdp.mispartidas.dialogos.NumeroTanteoDialogFragment;

public class Duelo extends ActionBarActivity implements NumeroTanteoDialogFragment.NumberTanteoDialogListener{

    private Backup backup;
    private String identificador;
    private int indice;
    private Partida partida;
    private static Context context;

    private ImageButton botonsumarA;
    private ImageButton botonsumarB;
    private ImageButton botonrestarA;
    private ImageButton botonrestarB;
    private TextView puntosA;
    private TextView puntosB;
    private TextView nombreA;
    private TextView nombreB;
    private TextView tiradaA;
    private TextView tiradaB;
    private ImageButton dadoA;
    private ImageButton dadoB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duelo);

        this.context = getApplicationContext();

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
            partida = backup.getBackup().get(indice);
            setTitle(partida.getNombre().toString());

            CustomListener listenerA = new CustomListener(0);
            CustomListener listenerB = new CustomListener(1);

            // Guardamos las vistas
            botonsumarA = (ImageButton) findViewById(R.id.sumardueloA);
            botonsumarA.setOnClickListener(listenerA);
            botonsumarB = (ImageButton) findViewById(R.id.sumardueloB);
            botonsumarB.setOnClickListener(listenerB);
            botonrestarA = (ImageButton) findViewById(R.id.restardueloA);
            botonrestarA.setOnClickListener(listenerA);
            botonrestarB = (ImageButton) findViewById(R.id.restardueloB);
            botonrestarB.setOnClickListener(listenerB);
            dadoA = (ImageButton) findViewById(R.id.dadodueloA);
            dadoA.setOnClickListener(listenerA);
            dadoB = (ImageButton) findViewById(R.id.sumardueloB);
            dadoB.setOnClickListener(listenerB);

            puntosA = (TextView)findViewById(R.id.tantosdueloA);
            puntosA.setOnClickListener(listenerA);
            puntosB = (TextView)findViewById(R.id.tantosdueloB);
            puntosB.setOnClickListener(listenerB);
            nombreA = (TextView)findViewById(R.id.nombrejugadorA);
            nombreA.setOnClickListener(listenerA);
            nombreB = (TextView)findViewById(R.id.nombrejugadorB);
            nombreB.setOnClickListener(listenerB);
            tiradaA = (TextView)findViewById(R.id.resultadodadoA);
            tiradaA.setOnClickListener(listenerA);
            tiradaA.setText(String.valueOf(0));
            tiradaB = (TextView)findViewById(R.id.resultadodadoB);
            tiradaB.setOnClickListener(listenerB);
            tiradaB.setText(String.valueOf(0));

            // Rellenamos las vistas
            nombreA.setText(partida.getJugadores().get(0).getNombre());
            puntosA.setText(String.valueOf(partida.getJugadores().get(0).getPuntuacion()));
            nombreB.setText(partida.getJugadores().get(1).getNombre());
            puntosB.setText(String.valueOf(partida.getJugadores().get(1).getPuntuacion()));

        } else {
            Toast.makeText(this, "No se ha encontrado la partida " + identificador, Toast.LENGTH_SHORT).show();
        }


    }

    // Sobreescribimos el metodo del dialogo para elegir el numero
    @Override
    public void onNumberSelected(int number, int position) {

        Log.i("MILOG", "Actualizamos los puntos con el dialog");

        // Actualizamos los jugadores
        int tantos = partida.getJugadores().get(position).getPuntuacion();
        partida.getJugadores().get(position).setPuntuacion(tantos + number);

        if(position == 0)
            puntosA.setText(String.valueOf(tantos));
        else
            puntosB.setText(String.valueOf(tantos));

        // Actualizamos el backup
        actualizar(indice);
    }


    public class CustomListener implements View.OnClickListener {
        private int position;

        protected CustomListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            //Comprobamos que vista ha lanzado el evento y lo gestionamos
            switch (v.getId()) {
                case R.id.tantosdueloA:
                case R.id.tantosdueloB:
                    try {
                        // Decrementamos el tanteo
                        Log.i("MILOG", "Modificamos el tanteo");
                        // Lanzamos el dialog
                        NumeroTanteoDialogFragment fragmento = new NumeroTanteoDialogFragment();
                        Bundle bundles = new Bundle();
                        bundles.putString("titulo", getString(R.string.sumar_puntos));
                        bundles.putInt("posicion", position);
                        fragmento.setArguments(bundles);
                        Log.i("MILOG", "Mostramos el dialog para elegir el numero que queremos modificar");
                        FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                        fragmento.show(fragmentManager, "Dialogo_tanteo");
                    } catch (Exception ex) {
                        Toast.makeText(Duelo.context, "Se produjo un error al modificar el tanteo", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.sumardueloA:
                    Log.i("MILOG", "Sumamos uno");
                    int tantosa = partida.getJugadores().get(position).getPuntuacion();
                    partida.getJugadores().get(position).setPuntuacion(tantosa + 1);
                    puntosA.setText(String.valueOf(tantosa));
                    //puntosA.invalidate();
                    // Actualizamos el backup
                    actualizar(indice);
                    break;
                case R.id.sumardueloB:
                    try {
                        Log.i("MILOG", "Sumamos uno");
                        int tantosb = partida.getJugadores().get(position).getPuntuacion();
                        partida.getJugadores().get(position).setPuntuacion(tantosb + 1);
                        puntosB.setText(String.valueOf(tantosb));
                        //puntosA.invalidate();
                        // Actualizamos el backup
                        actualizar(indice);
                    } catch (Exception ex) {
                        Toast.makeText(Duelo.context, "Se produjo un error al incrementar el tanteo", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.restardueloA:
                    // Decrementamos el tanteo
                    Log.i("MILOG", "Restamos uno");
                    int tantosaa = partida.getJugadores().get(position).getPuntuacion();
                    partida.getJugadores().get(position).setPuntuacion(tantosaa - 1);
                    puntosA.setText(String.valueOf(tantosaa));
                    // Actualizamos el backup
                    actualizar(indice);

                case R.id.restardueloB:
                    try {
                        // Decrementamos el tanteo
                        Log.i("MILOG", "Restamos uno");
                        int tantosbb = partida.getJugadores().get(position).getPuntuacion();
                        partida.getJugadores().get(position).setPuntuacion(tantosbb - 1);
                        puntosB.setText(String.valueOf(tantosbb));
                        // Actualizamos el backup
                        actualizar(indice);
                    } catch (Exception ex) {
                        Toast.makeText(Duelo.context, "Se produjo un error al decrementar el tanteo", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.dadodueloA:
                    tiradaA.setText(String.valueOf(Dado.tirar()));
                    break;
                case R.id.dadodueloB:
                    tiradaB.setText(String.valueOf(Dado.tirar()));
                    break;
            }
        }
    }

    // Metodo para actualizar el backup cada vez que modificamos algo en la pantalla
    public void actualizar(int indice){
        // Actualizamos el backup
        partida.setFechaactualizacion(Utilidades.getFechaActual());
        // Actualizamos el backup
        backup.getBackup().set(indice, partida);
        // Almacenamos
        Log.i("MILOG", "Guardamos el backup");
        backup.guardarBackup();
        //Log.i("MILOG", "Actualizamos la vista");
        //notifyDataSetChanged();
    }

}

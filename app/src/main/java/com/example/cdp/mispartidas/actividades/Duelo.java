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

    private ImageButton botonsumar;
    private ImageButton botonrestar;
    private TextView puntos;
    private TextView nombre;
    private TextView tirada;
    private ImageButton dado;
    
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
            
            // Obtenemos los dos layouts de los jugadores
            View jugadores[] = new View[2];
            jugadores[0] = (View)findViewById( R.id.jugador0 );
            jugadores[1] = (View)findViewById( R.id.jugador1 );

            // Definimos los listeners y los incluimos en las vistas
            // Actualizamos los valores a mostrar
            for(int i = 0; i < jugadores.size(); i++){
                ViewHolder holder = new ViewHolder();
                
                CustomListener listener = new CustomListener(i);
                holder.listener = listener;
                
                holder.nombrejugador = (TextView)jugadores[i].findViewById(R.id.nombrejugador);
                holder.nombrejugador.setText(partida.getJugadores().get(i).getNombre());
                holder.puntosjugador = (TextView)jugadores[i].findViewById(R.id.tantosduelo);
                holder.puntosjugador.setText(String.valueOf(partida.getJugadores().get(i).getPuntuacion()));
                holder.puntosjugador.setOnClickListener(holder.listener);
                holder.puntosdado = (TextView)jugadores[i].findViewById(R.id.resultadodado);
                holder.puntosdado.setText(String.valueOf(0));
                holder.botonmas = (ImageButton) jugadores[i].findViewById(R.id.sumarduelo);
                holder.botonmas.setOnClickListener(holder.listener);
                holder.botonmenos = (ImageButton) jugadores[i].findViewById(R.id.restarduelo);
                holder.botonmenos.setOnClickListener(holder.listener);
                holder.botondado = (ImageButton) jugadores[i].findViewById(R.id.dadoduelo);
                holder.botondado.setOnClickListener(holder.listener);
                
                // Guardamos en el layout apropiado
                jugadores[i].setTag(holder);
            }

        } else {
            Toast.makeText(this, "No se ha encontrado la partida " + identificador, Toast.LENGTH_SHORT).show();
        }
    }

    // Sobreescribimos el metodo del dialogo para elegir el numero
    @Override
    public void onNumberSelected(int numero, int position) {

        Log.i("MILOG", "Actualizamos los puntos con el dialog");

        // Actualizamos los jugadores
        int tantos = partida.getJugadores().get(position).getPuntuacion();
        partida.getJugadores().get(position).setPuntuacion(tantos + numero);

        // Actualizamos la vista
        ViewHolder holder = (ViewHolder) jugadores[position].getTag();
        holder.puntosjugador.setText(String.valueOf(tantos));

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
                case R.id.tantosduelo:
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

                case R.id.sumarduelo:
                    Log.i("MILOG", "Sumamos uno");
                    int suma = partida.getJugadores().get(position).getPuntuacion();
                    partida.getJugadores().get(position).setPuntuacion(suma + 1);
                    ViewHolder holder = (ViewHolder) jugadores[position].getTag();
                    holder.puntosjugador.setText(String.valueOf(suma + 1));
                    // Actualizamos el backup
                    actualizar(indice);
                    break;

                case R.id.restarduelo:
                    // Decrementamos el tanteo
                    Log.i("MILOG", "Restamos uno");
                    int resta = partida.getJugadores().get(position).getPuntuacion();
                    partida.getJugadores().get(position).setPuntuacion(resta - 1);
                    ViewHolder holder = (ViewHolder) jugadores[position].getTag();
                    holder.puntosjugador.setText(String.valueOf(resta - 1));
                    // Actualizamos el backup
                    actualizar(indice);
                    
                case R.id.dadoduelo:
                    ViewHolder holder = (ViewHolder) jugadores[position].getTag();
                    holder.puntosdado.setText(String.valueOf(Dado.tirar()));
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
    
    static class ViewHolder {
        TextView nombrejugador;
        TextView puntosjugador;
        TextView puntosdado;
        ImageButton botonmas;
        ImageButton botonmenos;
        ImageButton botondado;
        AdaptadorTanteo.CustomListener listener;
    }

}

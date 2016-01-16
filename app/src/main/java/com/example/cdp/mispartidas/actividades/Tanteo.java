package com.example.cdp.mispartidas.actividades;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cdp.mispartidas.dialogos.NumeroTanteoDialogFragment;
import com.example.cdp.mispartidas.R;
import com.example.cdp.mispartidas.almacenamiento.objetos.Jugador;
import com.example.cdp.mispartidas.almacenamiento.objetos.Partida;
import com.example.cdp.mispartidas.almacenamiento.operaciones.Backup;

import java.util.List;

public class Tanteo extends ActionBarActivity implements NumeroTanteoDialogFragment.NumberTanteoDialogListener {

    private String identificador;
    private Partida partida;
    private Backup backup;
    private int indice;
    private static Context context;
    AdaptadorTanteo adaptador;
    ListView listviewjugadores;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanteo);

        // Parametros
        listviewjugadores = (ListView) findViewById(R.id.jugadorestanteo);

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
            // Establecemos el adaptador
            Log.i("MILOG", "Establecemos el adaptador");
            adaptador = new AdaptadorTanteo(this, getTaskId(), partida.getJugadores());
            listviewjugadores.setAdapter(adaptador);
        } else {
            Toast.makeText(this, "No se ha encontrado la partida " + identificador, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tanteo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        int numjugadores;

        switch(id){
            // Anadimos un nuevo jugador a la partida
            case R.id.addjugador:
                numjugadores = listviewjugadores.getAdapter().getCount();
                Jugador player = new Jugador();
                // Ponemos un nombre por defecto
                player.setNombre("Jugador" + String.valueOf(numjugadores + 1));
                player.setNumerojugador(numjugadores + 1);
                // Anadimos la puntuacion
                player.setPuntuacion(0);
                // Anadimos el jugador a la lista
                partida.addJugador(player);
                // Actualizamos el backup
                backup.getBackup().set(indice, partida);
                // Almacenamos
                Log.i("MILOG", "Guardamos el backup");
                backup.guardarBackup();
                // Si todo ha ido bien, acutalizamos la lista de jugadores
                ((AdaptadorTanteo) listviewjugadores.getAdapter()).notifyDataSetChanged();
                break;

            case R.id.partidasguardadas:
                // Llamamos al intent de nuestras partidas guardadas
                Intent intenthistorial = new Intent(this, Historial.class);
                startActivity(intenthistorial);
                break;

            case R.id.reiniciarpartida:
                // Ponemos todos los marcadores a 0
                // Recorremos nuestra partida
                numjugadores = partida.getJugadores().size();
                // recorremos y reiniciamos
                for(int i = 0; i < numjugadores; i++){
                    partida.getJugadores().get(i).setPuntuacion(0);
                }
                // Actualizamos el backup
                backup.getBackup().set(indice, partida);
                // Almacenamos
                Log.i("MILOG", "Guardamos el backup");
                backup.guardarBackup();
                // Si todo ha ido bien, acutalizamos la lista de jugadores
                ((AdaptadorTanteo) listviewjugadores.getAdapter()).notifyDataSetChanged();
                break;

            case R.id.action_settings:
                break;
            // Fecha de volver atras
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            
            default:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Sobreescribimos el metodo del dialogo para elegir el numero
    @Override
    public void onNumberSelected(int number, int position) {

        Log.i("MILOG", "Actualizamos los puntos con el dialog");

        // Actualizamos los jugadores
        int tantos = adaptador.jugadores.get(position).getPuntuacion();
        adaptador.jugadores.get(position).setPuntuacion(tantos + number);

        // Actualizamos el backup
        backup.getBackup().set(indice, partida);
        // Almacenamos
        Log.i("MILOG", "Guardamos el backup");
        backup.guardarBackup();
        Log.i("MILOG", "Actualizamos la vista");
        ((AdaptadorTanteo) listviewjugadores.getAdapter()).notifyDataSetChanged();
    }

    // Adaptador para el layout del listview
    public class AdaptadorTanteo extends ArrayAdapter<Jugador> {

        Activity context;
        List<Jugador> jugadores;
        ViewHolder holder;

        AdaptadorTanteo(Activity context, int textViewResourceId, List<Jugador> listajugadores) {
            super(context, textViewResourceId, listajugadores);
            this.context = context;
            this.jugadores = listajugadores;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            View item = convertView;

            // Optimizamos el rendimiento de nuestra lista
            // Si la vista no existe, la creamos
            if (item == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                item = inflater.inflate(R.layout.tanteo_jugador, null);

                holder = new ViewHolder();

                // Declaramos el holder pasandole nuestras vistas
                holder.nombrejugador = (TextView) item.findViewById(R.id.nombrejugador);
                holder.puntos = (TextView) item.findViewById(R.id.puntos);
                holder.botonmas = (ImageButton) item.findViewById(R.id.sumar);
                holder.botonmenos = (ImageButton) item.findViewById(R.id.restar);

                // Establecemos el tag
                item.setTag(holder);
            }
            // Si la vista existe, la reusamos
            else {
                holder = (ViewHolder) item.getTag();
            }

            // Guardamos la posicion en el holder para usarlo en los listener
            holder.botonmas.setTag(position);
            holder.botonmenos.setTag(position);
            holder.puntos.setTag(position);

            // Definimos los listener para las vistas
            holder.listener = new CustomListener(position);
            holder.botonmenos.setOnClickListener(holder.listener);
            holder.botonmas.setOnClickListener(holder.listener);
            holder.puntos.setOnClickListener(holder.listener);

            // Establecemos el nombre por defecto
            holder.nombrejugador.setText(jugadores.get(position).getNombre());
            holder.puntos.setText(String.valueOf(jugadores.get(position).getPuntuacion()));

            return (item);
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
                    case R.id.nombrejugador:

                        break;

                    case R.id.puntos:
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
                            Toast.makeText(Tanteo.context, "Se produjo un error al modificar el tanteo", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.sumar:
                        try {
                            Log.i("MILOG", "Sumamos uno");
                            int tantos = jugadores.get(position).getPuntuacion();
                            jugadores.get(position).setPuntuacion(tantos + 1);
                            // Actualizamos el backup
                            backup.getBackup().set(indice, partida);
                            // Almacenamos
                            Log.i("MILOG", "Guardamos el backup");
                            backup.guardarBackup();
                            Log.i("MILOG", "Actualizamos la vista");
                            ((AdaptadorTanteo) listviewjugadores.getAdapter()).notifyDataSetChanged();
                        } catch (Exception ex) {
                            Toast.makeText(Tanteo.context, "Se produjo un error al incrementar el tanteo", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.restar:
                        try {
                            // Decrementamos el tanteo
                            Log.i("MILOG", "Restamos uno");
                            int tantos = jugadores.get(position).getPuntuacion();
                            jugadores.get(position).setPuntuacion(tantos - 1);
                            // Actualizamos el backup
                            backup.getBackup().set(indice, partida);
                            // Almacenamos
                            Log.i("MILOG", "Guardamos el backup");
                            backup.guardarBackup();
                            Log.i("MILOG", "Actualizamos la vista");
                            ((AdaptadorTanteo) listviewjugadores.getAdapter()).notifyDataSetChanged();
                        } catch (Exception ex) {
                            Toast.makeText(Tanteo.context, "Se produjo un error al decrementar el tanteo", Toast.LENGTH_SHORT).show();
                        }
                        break;

                }
            }
        }
    }

    static class ViewHolder{
        TextView nombrejugador;
        TextView puntos;
        ImageButton botonmas;
        ImageButton botonmenos;
        AdaptadorTanteo.CustomListener listener;
    }


}



    


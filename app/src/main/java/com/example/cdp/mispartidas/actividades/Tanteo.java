package com.example.cdp.mispartidas.actividades;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cdp.mispartidas.auxiliares.Utilidades;
import com.example.cdp.mispartidas.dialogos.NombreDialogFragment;
import com.example.cdp.mispartidas.dialogos.NumeroTanteoDialogFragment;
import com.example.cdp.mispartidas.R;
import com.example.cdp.mispartidas.almacenamiento.objetos.Jugador;
import com.example.cdp.mispartidas.almacenamiento.objetos.Partida;
import com.example.cdp.mispartidas.almacenamiento.operaciones.Backup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Tanteo extends ActionBarActivity implements NumeroTanteoDialogFragment.NumberTanteoDialogListener, NombreDialogFragment.NuevoNombreListener {

    private String identificador;
    private Partida partida;
    private Backup backup;
    private int indice;
    private static Context context;
    private AdaptadorTanteo adaptador;
    private ListView listviewjugadores;

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
            setTitle(partida.getNombre().toString());
        } else {
            Toast.makeText(this, "No se ha encontrado la partida " + identificador, Toast.LENGTH_SHORT).show();
        }

        // Definimos el contextual action bar
        Log.i("MILOG", "Definimos el contextual action bar");
        listviewjugadores.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listviewjugadores.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            private int nr = 0;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // TODO Auto-generated method stub
                Log.i("MILOG", "onitemcheckedstatechanged()");
                if (checked) {
                    nr++;
                    adaptador.setNewSelection(position);
                } else {
                    nr--;
                    adaptador.removeSelection(position);
                }
                mode.setTitle(nr + " selected");

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                Log.i("MILOG", "onactionitemclicked()");
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    // Borrar jugador
                    case R.id.menu_borrar:
                        nr = 0;
                        // Ordenamos la lista en orden inverso para un borrado seguro
                        Collections.sort(adaptador.getCurrentCheckedPosition(), Collections.reverseOrder());
                        for (Integer indice : adaptador.getCurrentCheckedPosition()) {
                            partida.getJugadores().remove(indice);
                        }

                        break;
                    // Cambiar el color del jugador
                    case R.id.menu_color:

                        break;
                    // Cambiamos el nombre del jugador
                    case R.id.menu_nombre:
                        nr = 0;
                        try {
                            // Decrementamos el tanteo
                            Log.i("MILOG", "Cambiamos el nombre de la partida");
                            // Lanzamos el dialog
                            NombreDialogFragment fragmento = new NombreDialogFragment();
                            Bundle bundles = new Bundle();
                            // Pasamos el indice como parametro
                            bundles.putInt("posicion", adaptador.getCurrentCheckedPosition().get(0));
                            fragmento.setArguments(bundles);
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmento.show(fragmentManager, "Dialogo_jugador");
                        
                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(), "Se produjo un error al cambiar el nombre", Toast.LENGTH_SHORT).show();
                        }

                        break;
                        // Reiniciamos el jugador
                    case R.id.menu_reiniciar:
                        nr = 0;
                        // Reiniciamos la puntuacion
                        for (Integer indice : adaptador.getCurrentCheckedPosition())
                        {   
                            partida.getJugadores().get(indice).setPuntuacion(0);
                        }

                        break;
                }
                // Actualizamos la partida
                actualizar(indice);
                // Actualizamos la vista
                adaptador.clearSelection();
                mode.finish();

                return false;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                Log.i("MILOG", "oncreateactionmode");
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.cab_tanteo, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                Log.i("MILOG", "ondestroyactionmode");
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
                nr = 0;
                adaptador.clearSelection();
                mode.finish();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                Log.i("MILOG", "onprepareactionmode");
                return false;
            }
        });

        //listviewjugadores.setLongClickable(true);

        listviewjugadores.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                listviewjugadores.setItemChecked(position, !adaptador.getCurrentCheckedPosition().contains(position));
                return true;
            }
        });

        Log.i("MILOG", "Fin de oncreate tanteo");
    }
    
    // Sobreescribimos el metodo del dialogo para cambiar el numero
    @Override
    public void onNombreSelected(String nombre, int position) {

        Log.i("MILOG", "Actualizamos el nombre del jugador");
        // Actualizamos el jugador
        partida.getJugadores().get(position).setNombre(nombre);
        // Actualizamos la lista
        adaptador.notifyDataSetChanged();
        // Actualizamos el backup
        Log.i("MILOG", "Guardamos el backup");
        backup.guardarBackup();
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
                actualizar(indice);
                break;

            case R.id.partidasguardadas:
                // Llamamos al intent de nuestras partidas guardadas
                Intent intenthistorial = new Intent(this, Historial.class);
                startActivity(intenthistorial);
                break;

            case R.id.reiniciarpartida:
                // Reiniciamos la partida
                partida.reiniciarPartida();
                // Actualizamos el backup
                actualizar(indice);
                break;

            case R.id.action_settings:
                break;
            case R.id.home:
                // Fecha de volver atras
                NavUtils.navigateUpFromSameTask(this);
                break;
                
            case R.id.mododuelo:
                // Fecha de volver atras
                // Lanzamos la pantalla de nueva partida, pasando el identificador de la partida creada
                Intent intentduelo = new Intent(getApplicationContext(), Duelo.class);
                // Pasamos como datos el numero de jugadores seleccionados
                Bundle b = new Bundle();
                Log.i("MILOG", "Guardamos los parametros desde el tanteo para llamar al intent de duelo");
                b.putString("idpartida", partida.getIdentificador());
                //Lo anadimos al intent
                intentduelo.putExtras(b);
                // Lanzamos la actividad
                Log.i("MILOG", "Lanzamos la pantalla de duelo desde tanteo");
                startActivity(intentduelo);
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
        actualizar(indice);
    }

    // Adaptador para el layout del listview
    public class AdaptadorTanteo extends ArrayAdapter<Jugador> {

        Activity context;
        List<Jugador> jugadores;
        ViewHolder holder;
        
        private List<Integer> mSelection = new ArrayList<Integer>();

        AdaptadorTanteo(Activity context, int textViewResourceId, List<Jugador> listajugadores) {
            super(context, textViewResourceId, listajugadores);
            this.context = context;
            this.jugadores = listajugadores;
        }
        
        public void setNewSelection(int position) {
    		mSelection.add(position);
    		notifyDataSetChanged();
    	}
  
        public List<Integer> getCurrentCheckedPosition() {
    		return mSelection;
    	}
  
        public void removeSelection(int position) {
    		mSelection.remove(Integer.valueOf(position));
    		notifyDataSetChanged();
    	}
  
        public void clearSelection() {
    		mSelection = new ArrayList<Integer>();
    		notifyDataSetChanged();
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
            
            // Definimos lo que necesitamos para el cab
            item.setBackgroundColor(getResources().getColor(android.R.color.background_light)); //default color
              
            if (mSelection.contains(position)) {
                item.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));// this is a selected position so make it red
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
            /*
            if (position % 2 == 1) {
                item.setBackgroundColor(getResources().getColor(R.color.background1));
            } else {
                item.setBackgroundColor(getResources().getColor(R.color.background2));
            }
            */
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
                            Toast.makeText(Tanteo.context, "Nombre", Toast.LENGTH_SHORT).show();
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
                            actualizar(indice);
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
                            actualizar(indice);
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
    
    // Metodo para actualizar el backup cada vez que modificamos algo en la pantalla
    public void actualizar(int indice){
        // Actualizamos el backup
        partida.setFechaactualizacion(Utilidades.getFechaActual());
        // Actualizamos el backup
        backup.getBackup().set(indice, partida);
        // Almacenamos
        Log.i("MILOG", "Guardamos el backup");
        backup.guardarBackup();
        Log.i("MILOG", "Actualizamos la vista");
        ((AdaptadorTanteo) listviewjugadores.getAdapter()).notifyDataSetChanged();
    }
}



    


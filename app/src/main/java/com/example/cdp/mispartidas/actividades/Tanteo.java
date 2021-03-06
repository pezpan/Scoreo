package com.example.cdp.mispartidas.actividades;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cdp.mispartidas.auxiliares.Utilidades;
import com.example.cdp.mispartidas.dialogos.NombreDialogFragment;
import com.example.cdp.mispartidas.dialogos.NumeroTanteoDialogFragment;
import com.example.cdp.mispartidas.dialogos.ConfirmacionDialogFragment;
import com.example.cdp.mispartidas.R;
import com.example.cdp.mispartidas.almacenamiento.objetos.Jugador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tanteo extends BaseTanteoActivity implements NumeroTanteoDialogFragment.NumberTanteoDialogListener, NombreDialogFragment.NuevoNombreListener, ConfirmacionDialogFragment.ConfirmarListener {

    private AdaptadorTanteo adaptador;
    private ListView listviewjugadores;
    ActionMode actionMode = null;
    private boolean hayDuelo = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_tanteo, menu);
        // Comprobamos si tenemos que desactivar la opcion de menu de pantalla de duelo
        MenuItem item = menu.findItem(R.id.mododuelo);
        if(hayDuelo){
            item.setVisible(true);
        }else{
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ((AdaptadorTanteo) listviewjugadores.getAdapter()).notifyDataSetChanged();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_tanteo;
    }

    // Sobreescribimos el metodo del dialogo para cambiar el numero
    @Override
    public void onNombreSelected(String nombre, int position) {

        Log.i("MILOG", "Actualizamos el nombre del jugador");
        // Actualizamos el jugador
        partida.getJugadores().get(position).setNombre(nombre);
        // Actualizamos el backup
        Log.i("MILOG", "Guardamos el backup");
        // Actualizamos la vista
        actionMode.finish();
        // Actualizamos la partida
        actualizar(indice);
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
    
    // Sobreescribimos el metodo del dialogo de confirmacion
    @Override
    public void onAceptarSelected(int opcion, int position) {
        switch(opcion){
            case ConfirmacionDialogFragment.BORRAR_JUGADOR:
                Log.i("MILOG", "Confirmamos la opcion");
                // Ordenamos la lista en orden inverso para un borrado seguro
                List<Integer> selected = adaptador.getCurrentCheckedPosition();
                Collections.sort(selected, Collections.reverseOrder());
                for (Integer indice : selected) {
                    partida.deleteJugador(indice);
                }
                // Actualizamos la vista
                actionMode.finish();
                break;
        }

        // Comprobamos si podemos mostrar el modo duelo
        if((listviewjugadores.getAdapter().getCount()) == 2){
            hayDuelo = false; // setting state
        }

        // Actualizamos el menu
        invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
        
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
            //item.setBackgroundColor(getResources().getColor(android.R.color.background_light)); //default color
            item.setBackgroundColor(partida.getJugadores().get(position).getColor());
              
            if (mSelection.contains(position)) {
                item.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));// this is a selected position so make it red
            }

            // Guardamos la posicion en el holder para usarlo en los listener
            holder.botonmas.setTag(position);
            holder.botonmenos.setTag(position);
            holder.puntos.setTag(position);

            // Definimos los listener para las vistas
            holder.listener = new CustomListener(position);
            holder.longlistener = new LongListener(position);
            holder.botonmenos.setOnClickListener(holder.listener);
            holder.botonmenos.setOnLongClickListener(holder.longlistener);
            holder.botonmas.setOnClickListener(holder.listener);
            holder.botonmas.setOnLongClickListener(holder.longlistener);

            // Establecemos el nombre por defecto
            holder.nombrejugador.setText(jugadores.get(position).getNombre());
            holder.puntos.setText(String.valueOf(jugadores.get(position).getPuntuacion()));

            // Ponemos el fondo de los botones con un tono mas oscuro
            // boton mas
            holder.botonmas.setBackgroundColor(Utilidades.getDarker(partida.getJugadores().get(position).getColor()));

            // boton menos
            holder.botonmenos.setBackgroundColor(Utilidades.getDarker(partida.getJugadores().get(position).getColor()));

            // Comprobamos si tenemos que poner el texto negro por el fondo blanco
            if(partida.getJugadores().get(position).getColor() == Color.WHITE){
                holder.puntos.setTextColor(getResources().getColor(R.color.textonegro));
                holder.nombrejugador.setTextColor(getResources().getColor(R.color.textonegro));
            }else{
                holder.puntos.setTextColor(getResources().getColor(R.color.texto));
                holder.nombrejugador.setTextColor(getResources().getColor(R.color.texto));
            }

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
                    case R.id.sumar:
                        Log.i("MILOG", "Sumamos uno");
                        int suma = jugadores.get(position).getPuntuacion();
                        jugadores.get(position).setPuntuacion(suma + ConfiguracionActivity.incremento);
                        // Actualizamos el backup
                        actualizar(indice);
                        break;

                    case R.id.restar:
                        // Decrementamos el tanteo
                        Log.i("MILOG", "Restamos uno");
                        int resta = jugadores.get(position).getPuntuacion();
                        jugadores.get(position).setPuntuacion(resta - ConfiguracionActivity.decremento);
                        // Actualizamos el backup
                        actualizar(indice);
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
        LongListener longlistener;
    }
    
    // Metodos abstractos implementados

    @Override
    protected void mostrarBotonAtras(){
        // Habilitamos la fecha volver a la activity principal
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void establecerLayout() {
        // Establecemos el layout
        setContentView(getLayoutResourceId());
    }

    @Override
    protected void notificaCambiosInterfaz() {
        ((AdaptadorTanteo) listviewjugadores.getAdapter()).notifyDataSetChanged();
    }
    
    @Override
    protected void optionAddJugador(){
        // Anadimos un nuevo jugador a la partida
        int numjugadores;
        Log.i("MILOG", "Anadimos un jugador a la partida");
        numjugadores = listviewjugadores.getAdapter().getCount();
        Jugador player = new Jugador();
        // Ponemos un nombre por defecto
        player.setNombre("Jugador" + String.valueOf(numjugadores + 1));
        player.setNumerojugador(numjugadores + 1);
        // Anadimos la puntuacion
        player.setPuntuacion(ConfiguracionActivity.contador_inicial);
        // Incluimos el color por defecto de los botones
        player.setColor(getResources().getColor(R.color.botonbase));
        // Anadimos el jugador a la lista
        partida.addJugador(player);

        // Comprobamos si podemos mostrar el modo duelo
        if((numjugadores + 1) == 2){
            hayDuelo = true; // setting state
        }else{
            hayDuelo = false; // setting state
        }
        // Actualizamos el menu
        invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
    }
    
    @Override
    protected void optionReiniciarPartida(){
        // Reiniciamos la partida
        partida.reiniciarPartida();
        actualizar(indice);
    }

    @Override
    protected void onColorSeleccionado(int color, List<Integer> positions){
        // Alamcenamos el color para la siguiente activity
        for(Integer position : positions) {
            partida.getJugadores().get(position).setColor(color);
        }
        // Actualizamos la vista
        actionMode.finish();
        // Actualizamos el adaptador para ver el nuevo color
        ((AdaptadorTanteo) listviewjugadores.getAdapter()).notifyDataSetChanged();
    }
    
    @Override
    protected void gestionarOnCreate() {
        // Parametros
        listviewjugadores = (ListView) findViewById(R.id.jugadorestanteo);
        // Establecemos el adaptador
        Log.i("MILOG", "Establecemos el adaptador");
        adaptador = new AdaptadorTanteo(this, getTaskId(), partida.getJugadores());
        listviewjugadores.setAdapter(adaptador);
        setTitle(partida.getNombre().toString());
        
        // Comprobamos si tenemos que mostrar la opcion de duelo
        if(partida.getJugadores().size() == 2){
            hayDuelo = true;
        }

        // Obtenemos las preferencias
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        ConfiguracionActivity.caras = Integer.parseInt(pref.getString("pref_caras_dado", "6"));
        ConfiguracionActivity.contador_inicial = Integer.parseInt(pref.getString("pref_contador_inicial", "0"));
        ConfiguracionActivity.incremento = Integer.parseInt(pref.getString("pref_incremento", "1"));
        ConfiguracionActivity.decremento = Integer.parseInt(pref.getString("pref_decremento", "1"));
        
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
                mode.invalidate();
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                Log.i("MILOG", "onactionitemclicked()");
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    // Borrar jugador
                    case R.id.menu_borrar:
                        Log.i("MILOG", "Borramos el jugador");
                        // Lanzamos el dialog
                        ConfirmacionDialogFragment fragmentoconfirmacion = new ConfirmacionDialogFragment();
                        Bundle bundlesborrar = new Bundle();
                        bundlesborrar.putInt("posicion", 0);
                        bundlesborrar.putInt("opcion", ConfirmacionDialogFragment.BORRAR_JUGADOR);
                        fragmentoconfirmacion.setArguments(bundlesborrar);
                        FragmentManager fragmentManagerborrar = getFragmentManager();
                        fragmentoconfirmacion.show(fragmentManagerborrar, "Dialogo_confirmacion");
                        break;
                    // Cambiar el color del jugador
                    case R.id.menu_color:
                        Log.i("MILOG", "Cambiamos el color del jugador");
                        // Mostramos el dialogo para cambiar el color
                        ColorListener colorlistener = new ColorListener(adaptador.getCurrentCheckedPosition());
                        colorlistener.muestraColores();
                        break;
                    // Cambiamos el nombre del jugador
                    case R.id.menu_nombre:
                        // Decrementamos el tanteo
                        Log.i("MILOG", "Cambiamos el nombre del jugador");
                        // Lanzamos el dialog
                        NombreDialogFragment fragmentonombre = new NombreDialogFragment();
                        Bundle bundles = new Bundle();
                        // Pasamos el indice como parametro
                        bundles.putInt("posicion", adaptador.getCurrentCheckedPosition().get(0));
                        fragmentonombre.setArguments(bundles);
                        FragmentManager fragmentManagernombre = getFragmentManager();
                        // Hacemos que aparezca el teclado sin necesidad de seleccionar el edittext
                        //fragmentonombre.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        fragmentonombre.show(fragmentManagernombre, "Dialogo_jugador");
                        break;
                    // Reiniciamos el jugador
                    case R.id.menu_reiniciar:
                        Log.i("MILOG", "Reiniciamos la partida");
                        // Reiniciamos la puntuacion
                        for (Integer indice : adaptador.getCurrentCheckedPosition()) {
                            partida.getJugadores().get(indice).setPuntuacion(ConfiguracionActivity.contador_inicial);
                        }
                        // Actualizamos la vista
                        mode.finish();

                        break;
                }
                return false;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                Log.i("MILOG", "oncreateactionmode");
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.cab_tanteo, menu);
                // Guardamos mode
                actionMode = mode;
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                Log.i("MILOG", "ondestroyactionmode");
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
                nr = 0;
                // Actualizamos la partida
                actualizar(indice);
                adaptador.clearSelection();
                actionMode = null;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                Log.i("MILOG", "onprepareactionmode");
                if (nr == 2) {
                    // Invalidamos las opciones de cambiar el nombre y cambiar el color
                    MenuItem item = menu.findItem(R.id.menu_nombre);
                    item.setVisible(false);
                } else {
                    MenuItem item = menu.findItem(R.id.menu_nombre);
                    item.setVisible(true);
                }
                return true;
            }
        });

        Log.i("MILOG", "Fin de oncreate tanteo");
    }
}



    


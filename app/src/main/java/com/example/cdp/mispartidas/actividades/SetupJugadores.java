package com.example.cdp.mispartidas.actividades;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.cdp.mispartidas.auxiliares.JugadorSetup;
import com.example.cdp.mispartidas.auxiliares.Utilidades;
import com.example.cdp.mispartidas.R;
import com.example.cdp.mispartidas.almacenamiento.objetos.Jugador;
import com.example.cdp.mispartidas.almacenamiento.objetos.Partida;
import com.example.cdp.mispartidas.almacenamiento.operaciones.Backup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SetupJugadores extends ActionBarActivity {

    private ListView listviewjugadores;
    private int jugadores = 0;
    private JugadorSetup players[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_jugadores);

        // Variables
        listviewjugadores = (ListView) findViewById(R.id.listajugadores);
        Button aceptar = (Button) findViewById(R.id.okjugadores);

        // Obtenemos el numero de jugadores
        Bundle bundle = getIntent().getExtras();
        jugadores = bundle.getInt("numjugadores");
        players = new JugadorSetup[jugadores];
        for(int i = 0; i < jugadores; i++){
            players[i] = new JugadorSetup();
            players[i].setNombre("Jugador" + Integer.toString(i + 1));
        }
        // Habilitamos la fecha volver a la activity principal
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Establecemos el adaptador
        final AdaptadorSetup adaptador = new AdaptadorSetup(this);
        listviewjugadores.setAdapter(adaptador);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamarTanteo();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setup_jugadores, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        
        switch(id){
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
    
    public void llamarTanteo(){
        // Creamos una nueva partida y la guardamos en memoria
        Partida partida = new Partida();
        List<Jugador> listajugadores = new ArrayList<Jugador>();
        for(int i = 0; i < jugadores; i++){

            Jugador player = new Jugador();
            // Guardamos el nombre
            player.setNombre(players[i].getNombre());
            // Anadimos el numero de jugador
            player.setNumerojugador(i);
            // Anadimos la puntuacion
            player.setPuntuacion(0);
            // Anadimos el jugador a la lista
            listajugadores.add(player);
        }

        // Si el titulo de la partida viene vacio, le ponemos un nombre
        EditText titulo = (EditText) findViewById(R.id.tituloPartida);
        if((titulo.getText() != null) && (titulo.getText().length() != 0)){
            partida.setNombre(titulo.getText().toString());
        }else{
            partida.setNombre("Partida " + Utilidades.getFechaSinHora());
        }
        // Obtenemos la fecha actual
        partida.setFechainicio(Utilidades.getFechaActual());
        partida.setFechaactualizacion(Utilidades.getFechaActual());
        String identificador = String.valueOf(new Date().getTime());
        partida.setIdentificador(identificador);
        // Guardamos los jugadores
        partida.setJugadores(listajugadores);
        Log.i("MILOG", "Identificador de la partida es " + identificador);
        
        // Añadimos la partida
        Log.i("MILOG", "Obtenemos el objeto backup");
        Backup backup = Backup.getMiBackup(getApplicationContext());
        Log.i("MILOG", "Anadimos la nueva partida");
        backup.addPartida(partida);
        // Guardamos
        Log.i("MILOG", "Guardamos el backup");
        backup.guardarBackup();
        Log.i("MILOG", "Hemos guardado el backup");
        
        // Lanzamos la pantalla de nueva partida, pasando el identificador de la partida creada
        Intent intentpartida = new Intent(getApplicationContext(), Tanteo.class);
        // Pasamos como datos el numero de jugadores seleccionados
        Bundle b = new Bundle();
        Log.i("MILOG", "Guardamos los parametros para llamar al intent de la partida");
        b.putString("idpartida", identificador);
        //Lo anadimos al intent
        intentpartida.putExtras(b);
        // Lanzamos la actividad
        Log.i("MILOG", "Lanzamos la pantalla de tanteo");
        startActivity(intentpartida);
    }

    // Adaptador para el layout del listview
    public class AdaptadorSetup extends ArrayAdapter{

        Activity context;
        AdaptadorSetup(Activity context){
            super(context, R.layout.setup_jugador, players);
            this.context = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View item = convertView;
            ViewHolder holder;
         
        	// Optimizamos el rendimiento de nuestra lista
        	// Si la vista no existe, la creamos
            if(item == null)
            {
                LayoutInflater inflater = context.getLayoutInflater();
                item = inflater.inflate(R.layout.setup_jugador, null);
         
                holder = new ViewHolder();
                holder.nombre = (EditText) item.findViewById(R.id.nombre);        
         
        		// Establecemos el tag
                item.setTag(holder);
            }
            // Si la vista existe, la reusamos
            else
            {
                holder = (ViewHolder)item.getTag();
            }

            // Establecemos el nombre por defecto
            //holder.nombre.setHint(players[position].getNombre());
            holder.nombre.setText(players[position].getNombre());

            // Tenemos que guardar los nombres de los jugadores para que cada vez que una vista pierda el foco
            // no se modifiquen los valores introducidos por el reciclaje de vistas de la lista
            holder.nombre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus){
                        EditText et = (EditText) v.findViewById(R.id.nombre);
                        players[position].setNombre(et.getText().toString());
                    }
                }
            });
            return(item);
        }		
    }
    
    static class ViewHolder {
        EditText nombre;
    }
}

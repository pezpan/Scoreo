package com.example.cdp.mispartidas.actividades;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.ImageView;
import android.widget.ListView;

import com.example.cdp.mispartidas.Utils;
import com.example.cdp.mispartidas.auxiliares.JugadorSetup;
import com.example.cdp.mispartidas.auxiliares.Utilidades;
import com.example.cdp.mispartidas.R;
import com.example.cdp.mispartidas.almacenamiento.objetos.Jugador;
import com.example.cdp.mispartidas.almacenamiento.objetos.Partida;
import com.example.cdp.mispartidas.almacenamiento.operaciones.Backup;
import com.example.cdp.mispartidas.colorpicker.ColorPickerDialog;
import com.example.cdp.mispartidas.colorpicker.ColorPickerSwatch.OnColorSelectedListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SetupJugadores extends ActionBarActivity {

    public static final int NORMAL = 0;
    public static final int DUELO = 1;
    
    private ListView listviewjugadores;
    private int jugadores = 0;
    private int tipopartida = NORMAL;
    private List<JugadorSetup> players;
    int mSelectedColorCal0;
    ColorPickerDialog colorcalendar;
    
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
        tipopartida = bundle.getInt("duelo");
        //players = new JugadorSetup[jugadores];
        players = new ArrayList<JugadorSetup>();
        for(int i = 0; i < jugadores; i++){
            JugadorSetup aux = new JugadorSetup();
            aux.setHint("Jugador" + Integer.toString(i + 1));
            aux.setNombre("");
            aux.setColor(getResources().getColor(R.color.botonbase));
            players.add(aux);
        }
        // Habilitamos la fecha volver a la activity principal
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Establecemos el adaptador
        final AdaptadorSetup adaptador = new AdaptadorSetup(this, getTaskId(), players);
        //adaptador = new AdaptadorTanteo(this, getTaskId(), partida.getJugadores());
        listviewjugadores.setAdapter(adaptador);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamarTanteo();
                // Terminamos esta actividad porque no queremos volver si damos atras en la pantalla de tanteo
                finish();
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
            // Comprobamos si se ha cambiado el valor de tento hint
            if(players.get(i).getNombre().compareTo("") == 0) {
                player.setNombre(players.get(i).getHint());
            }else{
                player.setNombre(players.get(i).getNombre());
            }
            // Anadimos el numero de jugador
            player.setNumerojugador(i);
            // Anadimos la puntuacion
            player.setPuntuacion(ConfiguracionActivity.contador_inicial);
            // Anadimos el color
            player.setColor(players.get(i).getcolor());
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

        // Comprobamos si es un tipo de partida normal o una partida con opcion de duelo
        // Pasamos como datos el numero de jugadores seleccionados
        Intent intentpartida;
        Bundle b = new Bundle();
        Log.i("MILOG", "Guardamos los parametros para llamar al intent de la partida");
        b.putString("idpartida", identificador);
        
        if(tipopartida == NORMAL){
            // Lanzamos la pantalla de nueva partida, pasando el identificador de la partida creada
            intentpartida = new Intent(getApplicationContext(), Tanteo.class);    
        }else{
            intentpartida = new Intent(getApplicationContext(), Duelo.class);    
        }
        
        //Lo anadimos al intent
        intentpartida.putExtras(b);
        // Lanzamos la actividad
        Log.i("MILOG", "Lanzamos la pantalla de tanteo/duelo");
        startActivity(intentpartida);
    }

    // Adaptador para el layout del listview
    public class AdaptadorSetup extends ArrayAdapter<JugadorSetup>{

        Activity context;
        List<JugadorSetup> jugadores;
        ViewHolder holder;
        
        AdaptadorSetup(Activity context, int textViewResourceId, List<JugadorSetup> listanombre){
            super(context, textViewResourceId, listanombre);
            this.context = context;
            this.jugadores = listanombre;
        }

        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View item = convertView;
         
        	// Optimizamos el rendimiento de nuestra lista
        	// Si la vista no existe, la creamos
            if(item == null)
            {
                LayoutInflater inflater = context.getLayoutInflater();
                item = inflater.inflate(R.layout.setup_jugador, null);
         
                holder = new ViewHolder();
                holder.nombre = (EditText) item.findViewById(R.id.nombre);
                holder.colores = (ImageView) item.findViewById(R.id.imgcolor);
                
        		// Establecemos el tag
                item.setTag(holder);
            }
            // Si la vista existe, la reusamos
            else
            {
                holder = (ViewHolder)item.getTag();
            }

            holder.colores.setTag(position);
            holder.listener = new ColorListener(position);
            holder.colores.setOnClickListener(holder.listener);
            
            // Tenemos que guardar los nombres de los jugadores para que cada vez que una vista pierda el foco
            // no se modifiquen los valores introducidos por el reciclaje de vistas de la lista
            holder.nombre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        EditText et = (EditText) v.findViewById(R.id.nombre);
                        jugadores.get(position).setNombre(et.getText().toString());
                    }
                }
            });
            
            // Aqui comprobamos si tenemos que poner el texto o el hint
            if(jugadores.get(position).getNombre().toString().compareTo("") == 0){
                holder.nombre.setText("");
                holder.nombre.setHint(jugadores.get(position).getHint());
            }else{
                holder.nombre.setText(jugadores.get(position).getNombre());
            }

            // Modificamos el color en esta activity
            GradientDrawable bgShape = (GradientDrawable)holder.colores.getBackground();
            bgShape.mutate();
            bgShape.setColor(players.get(position).getcolor());

            return(item);
        }

        public class ColorListener implements View.OnClickListener {
            private int position;

            protected ColorListener(int position) {
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                //Comprobamos que vista ha lanzado el evento y lo gestionamos
                try {
                    int[] mColor = Utils.ColorUtils.colorChoice(getApplicationContext());
                    colorcalendar = ColorPickerDialog.newInstance(
                            R.string.color_picker_default_title,
                            mColor,
                            mSelectedColorCal0,
                            4,
                            Utils.isTablet(getApplicationContext()) ? ColorPickerDialog.SIZE_LARGE : ColorPickerDialog.SIZE_SMALL);

                    //Implement listener to get selected color value
                    colorcalendar.setOnColorSelectedListener(new OnColorSelectedListener() {

                        @Override
                        public void onColorSelected(int color) {
                            // Alamcenamos el color para la siguiente activity
                            players.get(position).setColor(color);
                            // Actualizamos el adaptador para ver el nuevo color
                            ((AdaptadorSetup) listviewjugadores.getAdapter()).notifyDataSetChanged();
                        }
                    });
                    colorcalendar.show(getFragmentManager(), "cal");

                }catch(Exception e){
                    Log.i("MILOG", "Exception en colorpicker: " + e.getMessage());
                }
            }
        }
    }
    
    static class ViewHolder {
        EditText nombre;
        ImageView colores;
        AdaptadorSetup.ColorListener listener;
    }
}

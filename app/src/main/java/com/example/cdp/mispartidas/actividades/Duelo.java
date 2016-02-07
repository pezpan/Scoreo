package com.example.cdp.mispartidas.actividades;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cdp.mispartidas.R;
import com.example.cdp.mispartidas.almacenamiento.objetos.Jugador;
import com.example.cdp.mispartidas.almacenamiento.objetos.Partida;
import com.example.cdp.mispartidas.almacenamiento.operaciones.Backup;
import com.example.cdp.mispartidas.auxiliares.Dado;
import com.example.cdp.mispartidas.auxiliares.Utilidades;
import com.example.cdp.mispartidas.dialogos.NumeroTanteoDialogFragment;

import java.util.List;

public class Duelo extends ActionBarActivity implements NumeroTanteoDialogFragment.NumberTanteoDialogListener{

    private Backup backup;
    private String identificador;
    private int indice;
    private Partida partida;
    private static Context context;
    private AdaptadorDuelo adaptador;
    private ListView listviewjugadores;

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

        listviewjugadores = (ListView) findViewById(R.id.jugadoresduelo);
        
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
            adaptador = new AdaptadorDuelo(this, getTaskId(), partida.getJugadores());
            listviewjugadores.setAdapter(adaptador);
            setTitle(partida.getNombre().toString());
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

        // Actualizamos el backup
        actualizar(indice);
    }


    // Adaptador para el layout del listview
    public class AdaptadorDuelo extends ArrayAdapter<Jugador> {

        Activity context;
        List<Jugador> jugadores;
        ViewHolder holder;

        AdaptadorDuelo(Activity context, int textViewResourceId, List<Jugador> listajugadores) {
            super(context, textViewResourceId, listajugadores);
            this.context = context;
            this.jugadores = listajugadores;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            View item = convertView;

            // Optimizamos el rendimiento de nuestra lista
            // Si la vista no existe, la creamos
            if(item == null)
            {
                LayoutInflater inflater = context.getLayoutInflater();
                item = inflater.inflate(R.layout.tanteo_duelo, null);

                holder = new ViewHolder();

                holder.nombrejugador = (TextView) item.findViewById(R.id.nombrejugadorA);
                holder.puntos = (TextView) item.findViewById(R.id.tantosdueloA);
                holder.botonmas = (ImageButton) item.findViewById(R.id.sumardueloA);
                holder.botonmenos = (ImageButton) item.findViewById(R.id.restardueloA);
                holder.botondado = (ImageButton) item.findViewById(R.id.dadodueloA);

                // Establecemos el tag
                item.setTag(holder);
            }
            // Si la vista existe, la reusamos
            else
            {
                holder = (ViewHolder)item.getTag();
            }

            // Definimos los listener para las vistas
            holder.listener = new CustomListener(position);
            holder.botonmenos.setOnClickListener(holder.listener);
            holder.botonmas.setOnClickListener(holder.listener);
            holder.puntos.setOnClickListener(holder.listener);
            holder.botondado.setOnClickListener(holder.listener);

            // Establecemos el nombre por defecto
            holder.nombrejugador.setText(jugadores.get(position).getNombre());
            holder.puntos.setText(String.valueOf(jugadores.get(position).getPuntuacion()));

            // Definimos el color de fondo del boton
            GradientDrawable bgShapemas = (GradientDrawable)holder.botonmas.getBackground();
            bgShapemas.mutate();
            bgShapemas.setColor(partida.getJugadores().get(position).getColor());

            GradientDrawable bgShapemenos = (GradientDrawable)holder.botonmenos.getBackground();
            bgShapemenos.mutate();
            bgShapemenos.setColor(partida.getJugadores().get(position).getColor());


            return(item);
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
                        try {
                            Log.i("MILOG", "Sumamos uno");
                            int tantos = partida.getJugadores().get(position).getPuntuacion();
                            partida.getJugadores().get(position).setPuntuacion(tantos + 1);
                            // Actualizamos el backup
                            actualizar(indice);
                        } catch (Exception ex) {
                            Toast.makeText(Duelo.context, "Se produjo un error al incrementar el tanteo", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.restardueloA:
                        try {
                            // Decrementamos el tanteo
                            Log.i("MILOG", "Restamos uno");
                            int tantos = partida.getJugadores().get(position).getPuntuacion();
                            partida.getJugadores().get(position).setPuntuacion(tantos - 1);
                            // Actualizamos el backup
                            actualizar(indice);
                        } catch (Exception ex) {
                            Toast.makeText(Duelo.context, "Se produjo un error al decrementar el tanteo", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.dadodueloA:
                        tiradaA.setText(String.valueOf(Dado.tirar()));
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
        ImageButton botondado;
        AdaptadorDuelo.CustomListener listener;
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

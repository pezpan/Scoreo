package com.example.cdp.mispartidas.actividades;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class Duelo extends BaseTanteoActivity implements NumeroTanteoDialogFragment.NumberTanteoDialogListener{

    View jugadores[] = new View[2];
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_duelo, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_duelo;
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
        holder.puntosjugador.setText(String.valueOf(tantos + numero));

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
            ViewHolder holder;
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
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmento.show(fragmentManager, "Dialogo_tanteo");
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "Se produjo un error al modificar el tanteo", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.sumarduelo:
                    Log.i("MILOG", "Sumamos uno");
                    int suma = partida.getJugadores().get(position).getPuntuacion();
                    partida.getJugadores().get(position).setPuntuacion(suma + 1);
                    holder = (ViewHolder) jugadores[position].getTag();
                    holder.puntosjugador.setText(String.valueOf(suma + 1));
                    // Actualizamos el backup
                    actualizar(indice);
                    break;

                case R.id.restarduelo:
                    // Decrementamos el tanteo
                    Log.i("MILOG", "Restamos uno");
                    int resta = partida.getJugadores().get(position).getPuntuacion();
                    partida.getJugadores().get(position).setPuntuacion(resta - 1);
                    holder = (ViewHolder) jugadores[position].getTag();
                    holder.puntosjugador.setText(String.valueOf(resta - 1));
                    // Actualizamos el backup
                    actualizar(indice);
                    break;
                    
                case R.id.dadoduelo:
                    holder = (ViewHolder) jugadores[position].getTag();
                    holder.puntosdado.setText(String.valueOf(Dado.tirar()));
                    break;
            }
        }
    }
    
    static class ViewHolder {
        TextView nombrejugador;
        TextView puntosjugador;
        TextView puntosdado;
        ImageButton botonmas;
        ImageButton botonmenos;
        ImageButton botondado;
        CustomListener listener;
    }
    
    
    // Metodos abstractos implementados
    
    @Override
    protected void optionAddJugador(){
        // En los duelos no anadimos nuevos jugadores
    }
    
    @Override
    protected void optionReiniciarPartida(){
        // Reiniciamos la partida
        partida.reiniciarPartida();
        for(int i=0; i < jugadores.length; i++){
            ViewHolder holder = (ViewHolder) jugadores[i].getTag();
            holder.puntosjugador.setText(String.valueOf(0));
        }
    }
    
    @Override
    protected void notificaCambiosInterfaz() {
        // No tenemos adaptador
    }
    
     @Override
    protected void gestionarOnCreate() {
         // Obtenemos los dos layouts de los jugadores
         jugadores[0] = (View) findViewById(R.id.jugador0);
         jugadores[1] = (View) findViewById(R.id.jugador1);

         // Rotamos el layout del primer jugador
         jugadores[0].setRotation(180.0f);

         // Definimos los listeners y los incluimos en las vistas
         // Actualizamos los valores a mostrar
         for (int i = 0; i < jugadores.length; i++) {
             ViewHolder holder = new ViewHolder();

             CustomListener listener = new CustomListener(i);
             holder.listener = listener;

             holder.nombrejugador = (TextView) jugadores[i].findViewById(R.id.nombrejugador);
             holder.nombrejugador.setText(partida.getJugadores().get(i).getNombre());
             holder.puntosjugador = (TextView) jugadores[i].findViewById(R.id.tantosduelo);
             holder.puntosjugador.setText(String.valueOf(partida.getJugadores().get(i).getPuntuacion()));
             holder.puntosjugador.setOnClickListener(holder.listener);
             holder.puntosdado = (TextView) jugadores[i].findViewById(R.id.resultadodado);
             holder.puntosdado.setText(String.valueOf(0));
             holder.botonmas = (ImageButton) jugadores[i].findViewById(R.id.sumarduelo);
             holder.botonmas.setOnClickListener(holder.listener);
             holder.botonmenos = (ImageButton) jugadores[i].findViewById(R.id.restarduelo);
             holder.botonmenos.setOnClickListener(holder.listener);
             holder.botondado = (ImageButton) jugadores[i].findViewById(R.id.dadoduelo);
             holder.botondado.setOnClickListener(holder.listener);

             // Definimos los colores de los botones
             GradientDrawable bgShapemas = (GradientDrawable) holder.botonmas.getBackground();
             bgShapemas.mutate();
             bgShapemas.setColor(partida.getJugadores().get(i).getColor());
             // boton menos
             GradientDrawable bgShapemenos = (GradientDrawable) holder.botonmenos.getBackground();
             bgShapemenos.mutate();
             bgShapemenos.setColor(partida.getJugadores().get(i).getColor());

             // Guardamos en el layout apropiado
             jugadores[i].setTag(holder);
         }
     }
}

package com.example.cdp.mispartidas.actividades;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.cdp.mispartidas.R;
import com.example.cdp.mispartidas.auxiliares.Utilidades;
import com.example.cdp.mispartidas.dialogos.NumeroTanteoDialogFragment;

import java.util.List;

public class Duelo extends BaseTanteoActivity implements NumeroTanteoDialogFragment.NumberTanteoDialogListener{

    View jugadores[] = new View[2];
    boolean orientacion_vertical = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_duelo_vertical;
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

    public class DueloListener implements View.OnClickListener {
        private int position;

        protected DueloListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            ViewHolder holder;
            //Comprobamos que vista ha lanzado el evento y lo gestionamos
            switch (v.getId()) {

                case R.id.sumarduelo:
                    Log.i("MILOG", "Sumamos uno");
                    int suma = partida.getJugadores().get(position).getPuntuacion();
                    partida.getJugadores().get(position).setPuntuacion(suma + ConfiguracionActivity.incremento);
                    holder = (ViewHolder) jugadores[position].getTag();
                    holder.puntosjugador.setText(String.valueOf(suma + ConfiguracionActivity.incremento));
                    // Actualizamos el backup
                    actualizar(indice);
                    break;

                case R.id.restarduelo:
                    // Decrementamos el tanteo
                    Log.i("MILOG", "Restamos uno");
                    int resta = partida.getJugadores().get(position).getPuntuacion();
                    partida.getJugadores().get(position).setPuntuacion(resta - ConfiguracionActivity.decremento);
                    holder = (ViewHolder) jugadores[position].getTag();
                    holder.puntosjugador.setText(String.valueOf(resta - ConfiguracionActivity.decremento));
                    // Actualizamos el backup
                    actualizar(indice);
                    break;
            }
        }
    }
    
    static class ViewHolder {
        TextView nombrejugador;
        TextView puntosjugador;
        ImageButton botonmas;
        ImageButton botonmenos;
        DueloListener listener;
        LongListener longListener;
    }
    
    // Metodos abstractos implementados
    @Override
    protected void optionAddJugador(){
        // En los duelos no anadimos nuevos jugadores
    }

    @Override
    protected void onColorSeleccionado(int color, List<Integer> positions){

    }
    
    @Override
    protected void optionReiniciarPartida(){
        // Reiniciamos la partida
        partida.reiniciarPartida();
        for(int i=0; i < jugadores.length; i++){
            ViewHolder holder = (ViewHolder) jugadores[i].getTag();
            holder.puntosjugador.setText(String.valueOf(ConfiguracionActivity.contador_inicial));
        }
    }
    
    @Override
    protected void notificaCambiosInterfaz() {
        // No tenemos adaptador
    }

    @Override
    protected void mostrarBotonAtras(){

    }

    @Override
    protected void establecerLayout() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        // Comprobamos la orientacion
        switch(rotation){
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                // Orientacion vertical
                // Establecemos el layout
                setContentView(R.layout.activity_duelo_vertical);
                orientacion_vertical = true;
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                // Horientacion vertical
                setContentView(R.layout.activity_duelo_horizontal);
                orientacion_vertical = false;
                break;
        }
    }

    @Override
    protected void gestionarOnCreate() {
         // Obtenemos los dos layouts de los jugadores
         jugadores[0] = (View) findViewById(R.id.jugador0);
         jugadores[1] = (View) findViewById(R.id.jugador1);

         // Definimos los botones de la fila central
         ImageButton botondado = (ImageButton) findViewById(R.id.dadoduelo);
         ImageButton botonjugadorinicial = (ImageButton) findViewById(R.id.jugador_duelo);
         ImageButton botonreiniciar = (ImageButton) findViewById(R.id.resetarduelo);
         ImageButton botonconfiguracion = (ImageButton) findViewById(R.id.settings_duelo);

         // Establecemos el listener
         OpcionesListener opcionesListener = new OpcionesListener();
         botondado.setOnClickListener(opcionesListener);
         botonjugadorinicial.setOnClickListener(opcionesListener);
         botonreiniciar.setOnClickListener(opcionesListener);
         botonconfiguracion.setOnClickListener(opcionesListener);

         // Rotamos el layout del primer jugador
        if(orientacion_vertical)
            jugadores[0].setRotation(180.0f);

         // Definimos los listeners y los incluimos en las vistas
         // Actualizamos los valores a mostrar
         for (int i = 0; i < jugadores.length; i++) {
             ViewHolder holder = new ViewHolder();

             DueloListener listener = new DueloListener(i);
             LongListener longlistener = new LongListener(i);
             holder.listener = listener;
             holder.longListener = longlistener;

             holder.nombrejugador = (TextView) jugadores[i].findViewById(R.id.nombrejugador);
             holder.nombrejugador.setText(partida.getJugadores().get(i).getNombre());
             holder.puntosjugador = (TextView) jugadores[i].findViewById(R.id.tantosduelo);
             holder.puntosjugador.setText(String.valueOf(partida.getJugadores().get(i).getPuntuacion()));
             holder.botonmas = (ImageButton) jugadores[i].findViewById(R.id.sumarduelo);
             holder.botonmas.setOnClickListener(holder.listener);
             holder.botonmas.setOnLongClickListener(holder.longListener);
             holder.botonmenos = (ImageButton) jugadores[i].findViewById(R.id.restarduelo);
             holder.botonmenos.setOnClickListener(holder.listener);
             holder.botonmenos.setOnLongClickListener(holder.longListener);

             // Definimos los colores de los botones
             jugadores[i].setBackgroundColor(partida.getJugadores().get(i).getColor());
             // Cambiamos tambien el color de los botones
             //holder.botonmas.setBackgroundColor(partida.getJugadores().get(i).getColor());
             //holder.botonmenos.setBackgroundColor(partida.getJugadores().get(i).getColor());

             holder.botonmas.setBackgroundColor(Utilidades.getDarker(partida.getJugadores().get(i).getColor()));
             holder.botonmenos.setBackgroundColor(Utilidades.getDarker(partida.getJugadores().get(i).getColor()));

             // Comprobamos si tenemos que poner el texto negro por el fondo blanco
             if(partida.getJugadores().get(i).getColor() == Color.WHITE){
                 holder.nombrejugador.setTextColor(getResources().getColor(R.color.textonegro));
                 holder.puntosjugador.setTextColor(getResources().getColor(R.color.textonegro));
             }

             // Guardamos en el layout apropiado
             jugadores[i].setTag(holder);

             // Registramos la lista para el menu contextual
             registerForContextMenu(jugadores[0]);
             //registerForContextMenu(jugadores[1]);
         }
     }

    public class OpcionesListener implements View.OnClickListener {

       @Override
        public void onClick(View v) {
            //Comprobamos que vista ha lanzado el evento y lo gestionamos
            switch (v.getId()) {
                case R.id.dadoduelo:
                    // Tiramos el dado
                    tirarDado();
                    break;

                case R.id.resetarduelo:
                    Log.i("MILOG", "Reseteamos los tanteos");
                    // Reiniciamos la partida
                    optionReiniciarPartida();
                    // Actualizamos el backup
                    actualizar(indice);
                    break;

                case R.id.jugador_duelo:
                    // Elegimos el contador inicial
                    elegirInicial();
                    break;
                case R.id.settings_duelo:

                    break;
            }
        }
    }
}

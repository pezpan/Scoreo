package com.example.cdp.mispartidas.actividades;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.cdp.mispartidas.Utils;
import com.example.cdp.mispartidas.auxiliares.Dado;
import com.example.cdp.mispartidas.auxiliares.Utilidades;
import com.example.cdp.mispartidas.colorpicker.ColorPickerDialog;
import com.example.cdp.mispartidas.colorpicker.ColorPickerSwatch;
import com.example.cdp.mispartidas.R;
import com.example.cdp.mispartidas.almacenamiento.objetos.Partida;
import com.example.cdp.mispartidas.almacenamiento.operaciones.Backup;
import com.example.cdp.mispartidas.dialogos.MensajeDialogFragment;
import com.example.cdp.mispartidas.dialogos.NumeroTanteoDialogFragment;

import java.util.List;

public abstract class BaseTanteoActivity extends ActionBarActivity {

  public String identificador;
  public Partida partida;
  public Backup backup;
  public int indice;
  public Context context;
  ColorPickerDialog colorcalendar;
  int mSelectedColorCal0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      try {
          super.onCreate(savedInstanceState);
      }catch (Exception e){
          Log.i("MILOG", "Obtenemos el backup");
      }

      // Establecemos el layout
      establecerLayout();

      // Guardamos el contexto
      this.context = getApplicationContext();
      
      Log.i("MILOG", "Obtenemos el backup");
      backup = Backup.getMiBackup(this.context);

      // Obtenemos el numero de jugadores
      Bundle bundle = getIntent().getExtras();
      identificador = bundle.getString("idpartida");

      Log.i("MILOG", "El identificador de la partida es " + identificador);

      mostrarBotonAtras();

      // Buscamos la partida
      indice = backup.getPartida(identificador);
      if (indice >= 0) {
            partida = backup.getBackup().get(indice);
            setTitle(partida.getNombre().toString());
            // Gestionamos el inicio de forma distinta para cada pantalla
            gestionarOnCreate();
            
      } else {
          Toast.makeText(this, "No se ha encontrado la partida " + identificador, Toast.LENGTH_SHORT).show();
      }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      return true;
  }
  
   @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();
      switch (id) {
          case R.id.addjugador:
              // Anadimos un nuevo jugador a la partida
              optionAddJugador();
              actualizar(indice);
              break;

          case R.id.partidasguardadas:
              Log.i("MILOG", "Vamos al historial");
              // Llamamos al intent de nuestras partidas guardadas
              Intent intenthistorial = new Intent(this, Historial.class);
              startActivity(intenthistorial);
              break;

          case R.id.reiniciarpartida:
              Log.i("MILOG", "Reiniciamos la partida");
              // Reiniciamos la partida
              optionReiniciarPartida();
              // Actualizamos el backup
              actualizar(indice);
              break;

          case R.id.action_settings:
              Log.i("MILOG", "Settings");
              startActivity(new Intent(this,
                      ConfiguracionActivity.class));
              break;
          case R.id.home:
              Log.i("MILOG", "Volvemos a la pagina principal");
              // Fecha de volver atras
              NavUtils.navigateUpFromSameTask(this);
              break;

          case R.id.mododuelo:
              Log.i("MILOG", "Vamos a la pantalla de duelo");
              // Fecha de volver atras
              // Lanzamos la pantalla de nueva partida, pasando el identificador de la partida creada
              Intent intentmodo = new Intent(getApplicationContext(), this.getClass().equals(Tanteo.class) ? Duelo.class : Tanteo.class);
              // Pasamos como datos el numero de jugadores seleccionados
              Bundle b = new Bundle();
              Log.i("MILOG", "Guardamos los parametros para cambiar de activity");
              b.putString("idpartida", partida.getIdentificador());
              //Lo anadimos al intent
              intentmodo.putExtras(b);
              // Lanzamos la actividad
              Log.i("MILOG", "Lanzamos la pantalla");
              startActivity(intentmodo);
              break;

          case R.id.ordenarpartida:
              Log.i("MILOG", "Ordenamos la partida");
              partida.ordenarJugadores(false);
              // Actualizamos el backup
              actualizar(indice);
              break;

          case R.id.tirardado:
              tirarDado();
              break;

          case R.id.jugadorinicial:
              elegirInicial();
              break;

          default:
              break;
      }

      return super.onOptionsItemSelected(item);
  }

    public void tirarDado(){
        // Lanzamos el dialog
        MensajeDialogFragment fragmentodado = new MensajeDialogFragment();
        Bundle bundlesdado = new Bundle();
        bundlesdado.putString("titulo", getString(R.string.resultado_tirada));
        bundlesdado.putString("mensaje", String.valueOf(Dado.tirar()));
        bundlesdado.putInt("tamTexto", 40);
        fragmentodado.setArguments(bundlesdado);
        Log.i("MILOG", "Mostramos el dialog para tirar el dado");
        FragmentManager fragmentManagerdado = this.getFragmentManager();
        fragmentodado.show(fragmentManagerdado, "Dialogo_dado");
    }

    public void elegirInicial(){
        // Lanzamos el dialog
        MensajeDialogFragment fragmentoinicial = new MensajeDialogFragment();
        Bundle bundlesjugador = new Bundle();
        bundlesjugador.putString("titulo", getString(R.string.jugador_inicial));
        bundlesjugador.putString("mensaje", partida.getJugadorAleatorio());
        bundlesjugador.putInt("tamTexto", 20);
        fragmentoinicial.setArguments(bundlesjugador);
        Log.i("MILOG", "Mostramos el dialog para elegir el jugador inicial");
        FragmentManager fragmentManagerinicial = this.getFragmentManager();
        fragmentoinicial.show(fragmentManagerinicial, "Dialogo_jugadorinicial");
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
      // Actualizamos la interfaz
      notificaCambiosInterfaz();
  }

    public class LongListener implements View.OnLongClickListener{

        private int position;
        private int operacion;

        protected LongListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.sumar:
                case R.id.sumarduelo:
                    operacion = NumeroTanteoDialogFragment.sumar;
                    break;
                case R.id.restar:
                case R.id.restarduelo:
                    operacion = NumeroTanteoDialogFragment.restar;
                    break;
            }
            // Decrementamos el tanteo
            Log.i("MILOG", "Modificamos el tanteo");
            // Lanzamos el dialog
            NumeroTanteoDialogFragment fragmento = new NumeroTanteoDialogFragment();
            Bundle bundles = new Bundle();
            bundles.putString("titulo", getString(R.string.sumar_puntos));
            bundles.putInt("posicion", position);
            bundles.putInt("operacion", operacion);
            fragmento.setArguments(bundles);
            Log.i("MILOG", "Mostramos el dialog para elegir el numero que queremos modificar");
            FragmentManager fragmentManager = getFragmentManager();
            fragmento.show(fragmentManager, "Dialogo_tanteo");
            return true;
        }
    }

    public class ColorListener {
        private List<Integer> positions;

        protected ColorListener(List<Integer> positions) {
            this.positions = positions;
        }

        public void muestraColores(){
            int[] mColor = Utils.ColorUtils.colorChoice(getApplicationContext());
            try {
                colorcalendar = ColorPickerDialog.newInstance(R.string.color_picker_default_title, mColor, mSelectedColorCal0,
                        4, Utils.isTablet(getApplicationContext()) ? ColorPickerDialog.SIZE_LARGE : ColorPickerDialog.SIZE_SMALL);
                //Implement listener to get selected color value
                colorcalendar.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
                        onColorSeleccionado(color, positions);
                    }
                });
                colorcalendar.show(getFragmentManager(), "cal");

            }catch(Exception e){
                Log.i("MILOG", "Exception en colorpicker: " + e.getMessage());
            }
        }
    }

    // Metodos abstractos
  protected abstract int getLayoutResourceId();
  protected abstract void gestionarOnCreate();
  protected abstract void notificaCambiosInterfaz();
  protected abstract void optionAddJugador();
  protected abstract void optionReiniciarPartida();
  protected abstract void mostrarBotonAtras();
  protected abstract void establecerLayout();
  protected abstract void onColorSeleccionado(int color, List<Integer> seleccionados);
}

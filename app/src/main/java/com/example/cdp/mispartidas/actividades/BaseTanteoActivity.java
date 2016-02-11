package com.example.cdp.mispartidas.actividades;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cdp.mispartidas.Utils;
import com.example.cdp.mispartidas.auxiliares.Dado;
import com.example.cdp.mispartidas.auxiliares.Utilidades;
import com.example.cdp.mispartidas.dialogos.ConfirmacionDialogFragment;
import com.example.cdp.mispartidas.R;
import com.example.cdp.mispartidas.almacenamiento.objetos.Jugador;
import com.example.cdp.mispartidas.almacenamiento.objetos.Partida;
import com.example.cdp.mispartidas.almacenamiento.operaciones.Backup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class BaseTanteoActivity extends ActionBarActivity {

  public String identificador;
  public Partida partida;
  public Backup backup;
  public int indice;
  public Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // Establecemos el layout
      setContentView(getLayoutResourceId());
      // Guardamos el contexto
      this.context = getApplicationContext();
      
      Log.i("MILOG", "Obtenemos el backup");
      backup = Backup.getMiBackup(this.context);

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
      
      switch(id){
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
              // Actualizamos el backup
              actualizar(indice);
              break;

          case R.id.action_settings:
              Log.i("MILOG", "Settings");
              break;
          case R.id.home:
              Log.i("MILOG", "Volvemos a la pagina principal");
              // Fecha de volver atras
              NavUtils.navigateUpFromSameTask(this);
              break;
              
          case R.id.mododuelo:
          case R.id.modolista:
              Log.i("MILOG", "Vamos a la pantalla de duelo");
              // Fecha de volver atras
              // Lanzamos la pantalla de nueva partida, pasando el identificador de la partida creada
              Intent intentmodo = new Intent(getApplicationContext(),  this.getClass());
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
              Log.i("MILOG", "Tiramos el dado");
              AlertDialog.Builder builderdado = new AlertDialog.Builder(this);
              TextView myMsgdado = new TextView(this);
              myMsgdado.setText(String.valueOf(Dado.tirar()));
              myMsgdado.setGravity(Gravity.CENTER_HORIZONTAL);
              myMsgdado.setPaddingRelative(10,10,10,10);
              myMsgdado.setTextSize(20.0);
              builderdado.setView(myMsgdado);
              builderdado.setPositiveButton("OK", null);
              builderdado.show();
              break;
              
          case R.id.jugadorinicial:
              Log.i("MILOG", "Elegimos el jugador inicial");
              AlertDialog.Builder builderinicial = new AlertDialog.Builder(this);
              TextView myMsginicial = new TextView(this);
              myMsginicial.setText(partida.getJugadorAleatorio());
              myMsginicial.setGravity(Gravity.CENTER_HORIZONTAL);
              myMsginicial.setPaddingRelative(10,10,10,10);
              myMsginicial.setTextSize(20.0);
              builderinicial.setView(myMsginicial);
              builderinicial.setPositiveButton("OK", null);
              builderinicial.show();
              break;
          
          default:
              break;
      }
      return super.onOptionsItemSelected(item);
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
  
  // Metodos abstractos
  protected abstract int getLayoutResourceId();
  protected abstract int getCreateOptionsMenu();
  protected abstract void gestionarOnCreate();
  protected abstract void notificaCambiosInterfaz();
  protected abstract void optionAddJugador();
  protected abstract void optionReiniciarPartida();

}

package com.example.cdp.mispartidas.actividades;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cdp.mispartidas.dialogos.NombreDialogFragment;
import com.example.cdp.mispartidas.dialogos.ConfirmacionDialogFragment;
import com.example.cdp.mispartidas.R;
import com.example.cdp.mispartidas.almacenamiento.objetos.Partida;
import com.example.cdp.mispartidas.almacenamiento.operaciones.Backup;

import java.util.List;

public class Historial extends ActionBarActivity implements NombreDialogFragment.NuevoNombreListener, ConfirmacionDialogFragment.ConfirmarListener  {

    private ListView listapartidas;
    private AdaptadorHistorial adaptador;
    private Backup backup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        // Variables
        listapartidas = (ListView) findViewById(R.id.listapartidas);
        // Guardamos el contexto
        //this.context = getApplicationContext();
        Log.i("MILOG", "Obtenemos el backup");
        try {
            backup = Backup.getMiBackup(getApplicationContext());
            //mispartidas = backup.getBackup();
            adaptador = new AdaptadorHistorial(this, getTaskId(), backup.getBackup());
            listapartidas.setAdapter(adaptador);
        }catch(Exception ex){
            Log.i("MILOG", "Error al obtener el historial de partidas");
        }

        // Habilitamos la fecha volver a la activity principal
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Registramos la lista para el menu contextual
        registerForContextMenu(listapartidas);

        // Definimos el listener para la lista
        listapartidas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Lanzamos la pantalla de nueva partida, pasando el identificador de la partida creada
                Intent intentpartida = new Intent(getApplicationContext(), Tanteo.class);
                // Pasamos como datos el numero de jugadores seleccionados
                Bundle b = new Bundle();
                Log.i("MILOG", "Guardamos los parametros desde el historial para llamar al intent de la partida");
                b.putString("idpartida", backup.getBackup().get(position).getIdentificador());
                //Lo anadimos al intent
                intentpartida.putExtras(b);
                // Lanzamos la actividad
                Log.i("MILOG", "Lanzamos la pantalla de tanteo desde historial");
                startActivity(intentpartida);
            }
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_historial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){

            case R.id.borrarhistorial:
                // Borramos todo el historial si el usuario lo confirma
                Log.i("MILOG", "Cambiamos el nombre de la partida");
                // Lanzamos el dialog
                ConfirmacionDialogFragment fragmento = new ConfirmacionDialogFragment();
                Bundle bundles = new Bundle();
                bundles.putInt("posicion", 0);
                bundles.putInt("opcion", ConfirmacionDialogFragment.BORRAR_HISTORIAL);
                fragmento.setArguments(bundles);
                FragmentManager fragmentManager = this.getFragmentManager();
                fragmento.show(fragmentManager, "Dialogo_confirmacion");
                break;

            // Fecha de volver atras
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;

            case R.id.action_settings:
                startActivity(new Intent(this, ConfiguracionActivity.class));
                break;
            default:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Menu contextual para nuestra lista de partidas
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listapartidas) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(backup.getBackup().get(info.position).getNombre());
            String[] menuItems = getResources().getStringArray(R.array.menupartidas);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.menupartidas);
        //String menuItemName = menuItems[menuItemIndex];
        //String listItemName = ((Partida) mispartidas.get(info.position)).getNombre();
        
        switch(menuItemIndex){
            // Borrar partida
            case 0:
                // Lanzamos el dialog
                ConfirmacionDialogFragment fragmentoborrar = new ConfirmacionDialogFragment();
                Bundle bundlesborrar = new Bundle();
                bundlesborrar.putInt("posicion", info.position);
                bundlesborrar.putInt("opcion", ConfirmacionDialogFragment.BORRAR_PARTIDA);
                fragmentoborrar.setArguments(bundlesborrar);
                FragmentManager fragmentManagerborrar = this.getFragmentManager();
                fragmentoborrar.show(fragmentManagerborrar, "Dialogo_confirmacion");
                break;
            // Cambiar nombre de la partida
            case 1:
                try {
                    // Decrementamos el tanteo
                    Log.i("MILOG", "Cambiamos el nombre de la partida");
                    // Lanzamos el dialog
                    NombreDialogFragment fragmentonombre = new NombreDialogFragment();
                    Bundle bundles = new Bundle();
                    bundles.putInt("posicion", info.position);
                    fragmentonombre.setArguments(bundles);
                    FragmentManager fragmentManagernombre = this.getFragmentManager();
                    // Hacemos que aparezca el teclado sin necesidad de seleccionar el edittext
                    //fragmentManagernombre.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    fragmentonombre.show(fragmentManagernombre, "Dialogo_nombre");
                } catch (Exception ex) {
                    Toast.makeText(this.getApplicationContext(), "Se produjo un error al cambiar el nombre", Toast.LENGTH_SHORT).show();
                }
                break;
            // Duplicamos la partida
            case 2:
                Partida copiapartida = new Partida(backup.getBackup().get(info.position));
                // Anadimos la partida
                backup.addPartida(copiapartida);
                // Actualizamos la lista
                adaptador.notifyDataSetChanged();
                break;
            // Reiniciamos la partida
            case 3:
                backup.getBackup().get(info.position).reiniciarPartida();
                // Actualizamos la lista
                adaptador.notifyDataSetChanged();
                break;
        }
        
        // Almacenamos
        Log.i("MILOG", "Guardamos el backup");
        backup.guardarBackup();

        return true;
    }
    
    // Sobreescribimos el metodo del dialogo de confirmacion
    @Override
    public void onAceptarSelected(int opcion, int position) {
        switch(opcion){
            case ConfirmacionDialogFragment.BORRAR_PARTIDA:
                // Eliminamos de la lista la partida seleccionada
                backup.deletePartida(backup.getBackup().get(position));
                break;
            case ConfirmacionDialogFragment.BORRAR_HISTORIAL:
                // Borramos todas las partidas que tenemos guardadas
                backup.deleteAll();
                break;
        }
        // Actualizamos el backup
        Log.i("MILOG", "Guardamos el backup");
        backup.guardarBackup();
        // Actualizamos la lista
        ((AdaptadorHistorial) listapartidas.getAdapter()).notifyDataSetChanged();
    }
    
    // Sobreescribimos el metodo del dialogo para cambiar el numero
    @Override
    public void onNombreSelected(String nombre, int position) {

        Log.i("MILOG", "Actualizamos el nombre de la partida");

        // Actualizamos la partida
        backup.getBackup().get(position).setNombre(nombre);

        // Actualizamos la lista
        adaptador.notifyDataSetChanged();

        // Actualizamos el backup
        Log.i("MILOG", "Guardamos el backup");
        backup.guardarBackup();
    }

    // Adaptador para el layout del listview
    public class AdaptadorHistorial extends ArrayAdapter<Partida> {

        Activity context;
        List<Partida> partidas;

        AdaptadorHistorial(Activity context, int textViewResourceId, List<Partida> listapartidas){
            super(context, textViewResourceId, listapartidas);
            this.context = context;
            this.partidas = listapartidas;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            View item = convertView;
            ViewHolder holder;

            // Optimizamos el rendimiento de nuestra lista
            // Si la vista no existe, la creamos
            if(item == null)
            {
                LayoutInflater inflater = context.getLayoutInflater();
                item = inflater.inflate(R.layout.partida_historial, null);

                holder = new ViewHolder();
                holder.nombrepartida = (TextView) item.findViewById(R.id.nombrepartida);
                holder.numerojugadores = (TextView) item.findViewById(R.id.cuantosjugadores);
                holder.actualizacion = (TextView) item.findViewById(R.id.actualizada);

                // Establecemos el tag
                item.setTag(holder);
            }
            // Si la vista existe, la reusamos
            else
            {
                holder = (ViewHolder)item.getTag();
            }
            // Guardamos el nombre de la partida
            // Si no se puso, se pone la fecha

            String nombre = partidas.get(position).getNombre();
            if(nombre == null){
                holder.nombrepartida.setText("Partida creada el " + partidas.get(position).getFechainicio());
            }else{
                holder.nombrepartida.setText(partidas.get(position).getNombre());
            }
            // Guardamos el numero de jugadores
            holder.numerojugadores.setText(partidas.get(position).getJugadores().size() + " jugadores");
            // Guardamos la fecha de actualizacion
            holder.actualizacion.setText("Actualizada " + String.valueOf(partidas.get(position).getFechaactualizacion()));

            return(item);
        }
    }

    static class ViewHolder {
        TextView nombrepartida;
        TextView numerojugadores;
        TextView actualizacion;
    }
}

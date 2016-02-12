package com.example.cdp.mispartidas.almacenamiento.operaciones;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.cdp.mispartidas.almacenamiento.objetos.Partida;
import com.example.cdp.mispartidas.almacenamiento.sax.SAXXMLParser;
import com.example.cdp.mispartidas.almacenamiento.sax.SAXXMLSerializer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * Created by CDP on 14/11/2015.
 */
public class Backup {

    private List<Partida> partidas;
    private static Backup mibackup;
    private Context fileContext;
    
    // Voy a usar el patrón Singleton para tener solo una instancia de mi Backup
    private Backup(Context contexto){
        // Guardamos el contexto
        fileContext = contexto;
        // Creamos nuestras partidas
        this.partidas = new ArrayList<Partida>();
    }
    
    // Definimos el metodo publico que nos permitira acceder a nuestro backup
    public static Backup getMiBackup(Context contexto){
        Log.i("MILOG", "Obtenemos la variable backup");
        // Si el objeto no existe, lo creamos
        if(mibackup == null){
            mibackup = new Backup(contexto);
        }
        return mibackup;
    }

    public void addPartida(Partida partida){
        if(mibackup.partidas == null){
            mibackup.partidas = new ArrayList<Partida>();
        }
        mibackup.partidas.add(partida);
    }

    public void deletePartida(Partida partida){
        mibackup.partidas.remove(partida);
    }

    public void deleteAll(){
        mibackup.partidas.clear();
    }

    public int getPartida(String identificador){
        List<Partida> lstpartidas = mibackup.getBackup();
        for (int indice = 0; indice < lstpartidas.size(); indice++){
            Partida partida = lstpartidas.get(indice);
            String id = partida.getIdentificador();
            if(identificador.equals(id)){
                Log.i("MILOG", "Partida encontrada");
                return indice;
            }
        }
        return -1;
    }
    
    public void setBackup(List<Partida> partidas){
        this.partidas = partidas;
    }
    
    public List<Partida> getBackup(){
        return mibackup.partidas;
    }
    
    public void obtenerBackup(){
        Log.i("MILOG", "Lanzamos la tarea asincrona de obtener el backup");

        try {
            List<Partida> partidas_guardadas = leerBackup();
            // Si viene vacia porque no tenemos ninguna, creamos una lista vacia para evitar excepciones
            if(partidas_guardadas == null){
                partidas_guardadas = new ArrayList<Partida>();
            }
            mibackup.setBackup(partidas_guardadas);
        } catch (Exception e) {
            Log.i("MILOG", "Excepcion al obtener el backup" + e.getMessage());
        }
        Log.i("MILOG", "Salimos de la tarea asincrona de obtener el backup");
    }

    private List<Partida> leerBackup() {
        try {
            return (SAXXMLParser.parse(fileContext));
        } catch (Exception e) {
            Log.d("MILOG", "Error al lanzar la tarea asincrona de leer el xml");
        }
        return null;
    }
    
    public void guardarBackup(){
        Log.i("MILOG", "Lanzamos la tarea asincrona de guardar el backup");
        // Guardamos las partidas por orden cronologico, las mas recientes primero
        ordenarPartidas(mibackup.partidas, false);
        new SaveBackupAsyncTask().execute(mibackup);
        Log.i("MILOG", "Salimos de la tarea asincrona de guardar el backup");
    }
    
    public String getUltimaActualizada(){
        String identificador = null;
        if((this.partidas != null) && (this.partidas.size() != 0)){
            List<Partida> auxpartidas = new ArrayList<Partida>();
            auxpartidas = this.partidas;
            ordenarPartidas(auxpartidas, true);
            // Obtenemos la primera partida de la lista ordenada
            identificador = auxpartidas.get(auxpartidas.size() - 1).getIdentificador();
        }
        return identificador;
    }
    
    public void ordenarPartidas(List<Partida> listapartidas, final boolean ascendente){
        Collections.sort(listapartidas, new Comparator<Partida>() {
            public int compare(Partida p1, Partida p2) {
                Date fechao1 = null;
                Date fechao2 = null;
                try {
                    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                    fechao1 = formato.parse(p1.getFechaactualizacion());
                    fechao2 = formato.parse(p2.getFechaactualizacion());
                }catch (Exception ex){
                    Log.i("MILOG", "Error al parsear las fechas");
                }
                if(ascendente)
                    return fechao1.compareTo(fechao2);
                else
                    return fechao2.compareTo(fechao1);
            }
        });
    }

    // Vamos a escribir en el almacenamiento interno usando una tarea asincrona
    private class SaveBackupAsyncTask extends AsyncTask<Backup, Void, Void>{

        @Override
        protected Void doInBackground(Backup... params) {
            // Guardamos nuestras partidas
            SAXXMLSerializer.store(params[0].partidas, fileContext);
            return null;
        }
    }
    
    // Tarea asincrona para leer el xml
    private class GetBackupAsyncTask extends AsyncTask<Backup, Void, List<Partida>> {
        
        @Override
        protected List<Partida> doInBackground(Backup... params) {
            // Obtenemos el xml y lo parseamos
            try {
                return (SAXXMLParser.parse(fileContext));
            } catch (Exception e) {
                Log.d("MILOG", "Error al lanzar la tarea asincrona de leer el xml");
            }
            return null;
        }
    }

}

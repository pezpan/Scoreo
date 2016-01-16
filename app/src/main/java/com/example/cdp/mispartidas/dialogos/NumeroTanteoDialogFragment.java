package com.example.cdp.mispartidas.dialogos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.NumberPicker;

import com.example.cdp.mispartidas.R;

/**
 * Created by CDP on 17/10/2015.
 */
// Clase para el numero de jugadores
public class NumeroTanteoDialogFragment extends DialogFragment {

    private String titulo;
    private int posicion;
    private final int MAX = 20;
    private final int MIN = 1;

    public NumeroTanteoDialogFragment(){

    }
    
    public void getParameters(){
        Bundle bundle = getArguments();
        this.titulo = bundle.getString("titulo");
        this.posicion = bundle.getInt("posicion");
    }
    
    // Container Activity must implement this interface
    public interface NumberTanteoDialogListener {
        public void onNumberSelected(int number, int position);
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Activity actividad = getActivity();
        final NumberPicker myNumberPicker = new NumberPicker(actividad);

        // Obtenemos los parametros
        getParameters();
        final int posicion = this.posicion;
        myNumberPicker.setMaxValue(MAX);
        myNumberPicker.setMinValue(MIN);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(actividad);
        builder.setTitle(this.titulo)
                .setView(myNumberPicker)
                .setNegativeButton(R.string.restar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        int valor = myNumberPicker.getValue();
                        // Cambiamos el signo
                        valor *= -1;
                        // Declaramos un objeto de la interfaz que hemos definido para devolver el valor
                        NumberTanteoDialogListener activity = (NumberTanteoDialogListener) getActivity();
                        activity.onNumberSelected(valor, posicion);
                    }
                })
                .setPositiveButton(R.string.sumar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Debemos guardar el numero de jugadores seleccionados
                        try {
                            int valor = myNumberPicker.getValue();
                            // Declaramos un objeto de la interfaz que hemos definido para devolver el valor
                            NumberTanteoDialogListener activity = (NumberTanteoDialogListener) getActivity();
                            activity.onNumberSelected(valor, posicion);
                        }catch(Exception e){
                            Log.i("MILOG", e.getMessage());
                        }
                    }
                });


        // Create the AlertDialog object and return it
        return builder.create();
    }
}

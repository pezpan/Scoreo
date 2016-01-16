package com.example.cdp.mispartidas.dialogos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.NumberPicker;

import com.example.cdp.mispartidas.R;

/**
 * Created by CDP on 17/10/2015.
 */
// Clase para el numero de jugadores
public class NumeroJugadoresDialogFragment extends DialogFragment {
    
    private String titulo;
    private int max;
    private int min;

    public NumeroJugadoresDialogFragment(){

    }
    
    public void getParameters(){
        Bundle bundle = getArguments();
        this.titulo = bundle.getString("titulo");
        this.max = bundle.getInt("maximo");
        this.min = bundle.getInt("minimo");
    }
    
    // Container Activity must implement this interface
    public interface NumberDialogListener {
        public void onNumberSelected(int number);
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Activity actividad = getActivity();
        final NumberPicker myNumberPicker = new NumberPicker(actividad);
        // Obtenemos los parametros
        getParameters();
        myNumberPicker.setMaxValue(max);
        myNumberPicker.setMinValue(min);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(actividad);
        builder.setTitle(this.titulo)
                .setView(myNumberPicker)
                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Debemos guardar el numero de jugadores seleccionados
                        int valor = myNumberPicker.getValue();
                        // Declaramos un objeto de la interfaz que hemos definido para devolver el valor
                        NumberDialogListener activity = (NumberDialogListener) getActivity();
                        activity.onNumberSelected(valor);
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

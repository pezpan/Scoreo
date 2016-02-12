package com.example.cdp.mispartidas.dialogos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import com.example.cdp.mispartidas.R;

/**
 * Created by CDP on 13/01/2016.
 */
public class MensajeDialogFragment extends DialogFragment {
  
  private String titulo;
  private String mensaje;
  
  public MensajeDialogFragment(){
    
  }
  
  public void getParameters(){
        Bundle bundle = getArguments();
        this.titulo = bundle.getString("titulo");
        this.mensaje = bundle.getString("mensaje");
    }
  
  @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Activity actividad = getActivity();
        final TextView texto = new TextView(actividad);

        // Obtenemos los parametros
        getParameters();
        // Modificamos el textview
        texto.setText(mensaje);
        texto.setGravity(Gravity.CENTER_HORIZONTAL);
        texto.setPadding(20,20,20,20);
        texto.setTextSize(20);
        texto.setTextColor(#212121);
        
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(actividad);
        builder.setTitle(titulo)
                .setView(myName)
                .setView(texto)
                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        this.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

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
public class NombreDialogFragment extends DialogFragment {
  
  private int posicion;
  
  public NombreDialogFragment(){
    
  }
  
  public void getParameters(){
        Bundle bundle = getArguments();
        this.posicion = bundle.getInt("posicion");
    }
  
  // Container Activity must implement this interface
  public interface NuevoNombreListener {
      public void onNombreSelected(String nombre, int position);
  }
  
  @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Activity actividad = getActivity();
        final EditText myName = new EditText(actividad);
        
        // Obtenemos los parametros
        getParameters();
        final int posicion = this.posicion;
        
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(actividad);
        builder.setTitle(R.string.dialogonombre)
                .setView(myName)
                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Debemos guardar el numero de jugadores seleccionados
                        String valor = String.valueOf(myName.getText());
                        // Declaramos un objeto de la interfaz que hemos definido para devolver el valor
                        NuevoNombreListener activity = (NuevoNombreListener) getActivity();
                        activity.onNombreSelected(valor, posicion); 
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

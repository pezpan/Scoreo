package com.example.cdp.mispartidas.dialogos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import com.example.cdp.mispartidas.R;


public class ConfirmacionDialogFragment extends DialogFragment {

    // Definimos constantes para las distintas opciones
    public static final int BORRAR_PARTIDA = 0;
    public static final int BORRAR_HISTORIAL = 1;
    public static final int BORRAR_JUGADOR = 2;

    private int posicion;
    private int opcion;

    public void getParameters(){
        Bundle bundle = getArguments();
        this.posicion = bundle.getInt("posicion");
    }

    // Container Activity must implement this interface
    public interface ConfirmarListener {
        public void onAceptarSelected(int opcion, int position);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //final Activity actividad = getActivity();
        // Obtenemos los parametros
        getParameters();
        final int posicion = this.posicion;
        final int opcion = this.opcion;

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirmacion)
                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ConfirmarListener activity = (ConfirmarListener) getActivity();
                        activity.onAceptarSelected(opcion, posicion);
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

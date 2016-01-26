package com.example.cdp.mispartidas.auxiliares;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by CDP on 24/11/2015.
 */
public class Utilidades {

    public static String getFechaActual(){

        Date fecha = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        formato.setTimeZone(TimeZone.getTimeZone("UTC"));
        return(formato.format(fecha));
    }
    
    public static String getFechaSinHora(){

        Date fecha = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        formato.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
        return(formato.format(fecha));
    }

    public static String colorIntToHex(int intcolor){
        String hexColor = String.format("#%06X", (0xFFFFFF & intcolor));
        return hexColor;
    }
    
    public static void buttonEffect(View button){
        button.setOnTouchListener(new OnTouchListener() {
    
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0f47521,PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
}
}

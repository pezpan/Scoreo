package com.example.cdp.mispartidas.auxiliares;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by CDP on 24/11/2015.
 */
public class Utilidades {

    public static String getFechaActual(){

        Date fecha = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        formato.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
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
        button.setOnTouchListener(new View.OnTouchListener() {
    
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ColorDrawable colordraw = (ColorDrawable)v.getBackground();
                        int color = colordraw.getColor();
                        float[] hsv = new float[3];
                        Color.colorToHSV(color, hsv);
                        hsv[2] *= 0.8f; // value component
                        color = Color.HSVToColor(hsv);
                        v.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
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

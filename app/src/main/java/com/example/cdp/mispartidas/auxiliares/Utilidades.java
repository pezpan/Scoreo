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
        formato.setTimeZone(TimeZone.getTimeZone("Europe/Copenhagen"));
        return(formato.format(fecha));
    }


}

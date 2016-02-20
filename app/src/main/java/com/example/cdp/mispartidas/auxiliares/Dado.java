package com.example.cdp.mispartidas.auxiliares;

import com.example.cdp.mispartidas.actividades.ConfiguracionActivity;

/**
 * Created by CDP on 06/02/2016.
 */
public class Dado {

    public static int tirar() {
        return (int)(ConfiguracionActivity.caras * Math.random()) + 1;
    }
}

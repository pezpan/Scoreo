package com.example.cdp.mispartidas.auxiliares;

/**
 * Created by CDP on 06/02/2016.
 */
public class Dado {

    private static double base = 6.0;

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public static int tirar() {
        return (int)(base * Math.random()) + 1;
    }
}

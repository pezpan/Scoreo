package com.example.cdp.mispartidas.auxiliares;

/**
 * Created by CDP on 16/11/2015.
 */
public class JugadorSetup {

    private String nombre;
    private String hint;
    private int color;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getHint(){
        return hint;
    }
    
    public void setHint(String nuevo){
        this.hint = nuevo;
    }
    
    public int getcolor(){
        return color;
    }
    
    public void setColor(int nuevocolor){
        this.color = nuevocolor;;
    }
}

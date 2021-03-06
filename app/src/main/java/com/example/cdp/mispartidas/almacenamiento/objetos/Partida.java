package com.example.cdp.mispartidas.almacenamiento.objetos;

import com.example.cdp.mispartidas.actividades.ConfiguracionActivity;
import com.example.cdp.mispartidas.auxiliares.Utilidades;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Partida{
		
	String nombre;	
	String fechainicio;		
	String fechaactualizacion;
	String identificador;
	List<Jugador> jugadores;

	public Partida() {
	}

	public Partida(Partida viejapartida){
		this.nombre = viejapartida.getNombre();
		this.fechainicio = Utilidades.getFechaActual();
		this.fechaactualizacion = Utilidades.getFechaActual();
		this.identificador = String.valueOf(new Date().getTime());
		this.jugadores = new ArrayList<Jugador>();
		for(Jugador j : viejapartida.getJugadores()){
			Jugador tempjugador = new Jugador();
			tempjugador.setNombre(j.getNombre());
			tempjugador.setPuntuacion(j.getPuntuacion());
			tempjugador.setColor(j.getColor());
			tempjugador.setNumerojugador(j.getNumerojugador());
			this.jugadores.add(tempjugador);
		}
	}

	public void reiniciarPartida(){
		// Ponemos todos los marcadores a 0
		// Recorremos nuestra partida
		int numjugadores = this.getJugadores().size();
		// recorremos y reiniciamos
		for(int i = 0; i < numjugadores; i++){
			this.getJugadores().get(i).setPuntuacion(ConfiguracionActivity.contador_inicial);
		}
	}
		
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getFechainicio() {
		return fechainicio;
	}

	public void setFechainicio(String fechainicio) {
		this.fechainicio = fechainicio;
	}

	public String getFechaactualizacion() {
		return fechaactualizacion;
	}

	public void setFechaactualizacion(String fechaactualizacion) {
		this.fechaactualizacion = fechaactualizacion;
	}
	
	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String id) {
		this.identificador = id;
	}

	public List<Jugador> getJugadores() {
		return jugadores;
	}

	public void setJugadores(List<Jugador> jugadores2) {
		this.jugadores = jugadores2;
	}
	
	public void addJugador(Jugador jugador){
		jugadores.add(jugador);
	}

	public void deleteJugador(int indice){
		this.jugadores.remove(indice);
	}
	
	public void ordenarJugadores(final boolean ascendente){
        Collections.sort(this.jugadores, new Comparator<Jugador>() {
			public int compare(Jugador j1, Jugador j2) {
				Integer puntuacion1 = 0;
				Integer puntuacion2 = 0;
				puntuacion1 = j1.getPuntuacion();
				puntuacion2 = j2.getPuntuacion();
				if (ascendente)
					return puntuacion1.compareTo(puntuacion2);
				else
					return puntuacion2.compareTo(puntuacion1);
			}
		});
    	}
    	
    	public String getJugadorAleatorio(){
    		Random randomizer = new Random();
			return(jugadores.get(randomizer.nextInt(jugadores.size())).getNombre());
    	}


}

package com.example.cdp.mispartidas.almacenamiento.objetos;

import com.example.cdp.mispartidas.auxiliares.Utilidades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
			this.getJugadores().get(i).setPuntuacion(0);
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

}

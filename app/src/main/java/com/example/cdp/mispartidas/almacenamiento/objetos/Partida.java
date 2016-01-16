package com.example.cdp.mispartidas.almacenamiento.objetos;

import java.util.List;


public class Partida {
		
	String nombre;	
	String fechainicio;		
	String fechaactualizacion;
	String identificador;
	List<Jugador> jugadores;
		
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

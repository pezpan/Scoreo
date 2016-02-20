package com.example.cdp.mispartidas.almacenamiento.sax;

import com.example.cdp.mispartidas.almacenamiento.objetos.Jugador;
import com.example.cdp.mispartidas.almacenamiento.objetos.Partida;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXXMLHandler extends DefaultHandler{

	private List<Partida> partidas;
	private List<Jugador> jugadores;
    private String tempVal;
    private Partida tempPart;
    private Jugador tempJug;
 
    public SAXXMLHandler() {
        partidas = new ArrayList<Partida>();
        jugadores = new ArrayList<Jugador>();
    }
 
    public List<Partida> getPartidas() {
        return partidas;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    // Event Handlers
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        tempVal = "";
        if (qName.equalsIgnoreCase("Partida")) {
            // create a new instance of partida
        	tempPart = new Partida();
            // Creamos una nueva lista de jugadores
            jugadores = new ArrayList<Jugador>();
        } else if (qName.equalsIgnoreCase("Jugador")) {
            // create a new instance of partida
        	tempJug = new Jugador();
        }
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {

    	// Anadimos la partida a la lista de partidas
        if (qName.equalsIgnoreCase("Partida")) {
            // anadimos la partida a la lista de partidas
            partidas.add(tempPart);
        }
        // Anadimos el jugador a la lista de jugadores
        else if (qName.equalsIgnoreCase("Jugador")) {
            // anadimos el jugador a la lista de jugadores
            jugadores.add(tempJug);
        }
        // Guardamos todas las partidas
        else if (qName.equalsIgnoreCase("Partidas")) {
            // anadimos el jugador a la lista de jugadores
        }
        // Partida
        // Titulo de la partida
        else if (qName.equalsIgnoreCase("titulo")) {
        	tempPart.setNombre(tempVal);            
        } 
        // Fecha de inicio de la partida
        else if (qName.equalsIgnoreCase("fechainicio")) {        	        	
        	tempPart.setFechainicio(tempVal);
        } 
        // Fecha de ultima actualizacion de la partida
        else if (qName.equalsIgnoreCase("fechaactualizacion")) {
        	tempPart.setFechaactualizacion(tempVal);        	        	
        } 
        // Identificador de la partida
        else if (qName.equalsIgnoreCase("identificador")) {
        	tempPart.setIdentificador(tempVal);        	        	
        } 
        // Anadimos los jugadores a la partida
        else if (qName.equalsIgnoreCase("Jugadores")) {
            // anadimos los jugadores a la partida
            tempPart.setJugadores(jugadores);        
        } 
        // Jugador
        // Nombre del jugador
        else if (qName.equalsIgnoreCase("nombre")) {
        	tempJug.setNombre(tempVal);        	        	
        }
        // Puntuacion del jugador
        else if (qName.equalsIgnoreCase("puntuacion")) {
        	tempJug.setPuntuacion(Integer.parseInt(tempVal));        	        	
        }
        // Color del jugador
        else if (qName.equalsIgnoreCase("color")) {
        	tempJug.setColor(Integer.parseInt(tempVal));        	        	
        }
        // Color del jugador
        else if (qName.equalsIgnoreCase("numerojugador")) {
        	tempJug.setNumerojugador(Integer.parseInt(tempVal));        	        	
        }
    }

}

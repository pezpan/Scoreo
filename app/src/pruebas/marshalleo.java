package pruebas;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import xml.JAXBXMLHandler;

import backup.Backup;
import backup.Jugador;
import backup.Jugadores;
import backup.Partida;
import backup.Partidas;

public class marshalleo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub								
		
		List<Jugador> listajugadores = new ArrayList<Jugador>();
		
		
		for(int i = 0; i < 4; i++){
			Jugador jugador = new Jugador();
			jugador.setColor(12345);
			jugador.setNombre("Juanito");
			jugador.setNumerojugador(i + 1);
			jugador.setPuntuacion(i + 5);
			listajugadores.add(jugador);
		}
		
		Jugadores jugadores = new Jugadores();
		
		jugadores.setLista_jugadores(listajugadores);
		
		Partida partida = new Partida();
		
		partida.setFechaactualizacion("24-07-1987");
		partida.setFechainicio("04-05-1999");
		partida.setNombre("A vs B");
		partida.setJugadores(jugadores);
				
		
		List<Partida> listapartidas = new ArrayList<Partida>();
		listapartidas.add(partida);
		listapartidas.add(partida);
		
		Partidas partidas = new Partidas();
		
		Backup guardado = new Backup();
		partidas.setLista_partidas(listapartidas);
		
		guardado.setPartidas(partidas);
						
		
		//Marshalling: Writing Java objects to XMl file
        try {
            JAXBXMLHandler.marshal(guardado, new File("partidas.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
         
        //Unmarshalling: Converting XML content to Java objects
        try {
            guardado = JAXBXMLHandler.unmarshal(new File("partidas.xml"));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        System.out.println(guardado);
	}

}

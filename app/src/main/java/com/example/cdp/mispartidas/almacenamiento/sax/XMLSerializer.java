package com.example.cdp.mispartidas.almacenamiento.sax;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.example.cdp.mispartidas.almacenamiento.objetos.Jugador;
import com.example.cdp.mispartidas.almacenamiento.objetos.Partida;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.util.List;


public class XMLSerializer {

		
	public static void store(List<Partida> backup, Context contexto){
		
		final String xmlFile = "userData.xml";		

		try {
			
		FileOutputStream fos = contexto.openFileOutput(xmlFile,Context.MODE_PRIVATE);
			
			
		    //FileOutputStream fos = new  FileOutputStream(rutacompleta);
		    //FileOutputStream fileos= getApplicationContext().openFileOutput(xmlFile, Context.MODE_PRIVATE);
		    XmlSerializer xmlSerializer = Xml.newSerializer();
		    xmlSerializer.setOutput(fos, "UTF-8");
    		    xmlSerializer.startDocument(null, Boolean.valueOf(true));
    		    xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
		    
		    //StringWriter writer = new StringWriter();
		    //xmlSerializer.setOutput(writer);		    		    		    
		    //xmlSerializer.startDocument("UTF-8", true);
		    
		    // Obtenemos las partidas
		    List<Partida> listapartidas = backup;
		    
		    Log.i("MILOG", "Empezamos la serializacion del backup");
		    		    
		    // Tag de inicio de las partidas
		    xmlSerializer.startTag(null, "Partidas");
		    
		    	if((listapartidas != null) && (listapartidas.size() > 0)){
		    		// Obtenemos todas las partidas
		    		for(int indicepartidas = 0; indicepartidas < listapartidas.size(); indicepartidas++){
		    			
		    			Partida partida = listapartidas.get(indicepartidas);
		    			
		    			// Partida
		    			xmlSerializer.startTag(null, "Partida");
		    			
			    			// Titulo
			    			xmlSerializer.startTag(null, "titulo");
			    		    xmlSerializer.text(partida.getNombre());
			    		    xmlSerializer.endTag(null, "titulo");
			    		    
			    		    // Fecha de inicio
			    			xmlSerializer.startTag(null, "fechainicio");
			    		    xmlSerializer.text(partida.getFechainicio());
			    		    xmlSerializer.endTag(null, "fechainicio");
			    		    
			    		    // Fecha de actualizacion
			    			xmlSerializer.startTag(null, "fechaactualizacion");
			    		    xmlSerializer.text(partida.getFechaactualizacion());
			    		    xmlSerializer.endTag(null, "fechaactualizacion");
			    		    
			    		    // Identificador
			    			xmlSerializer.startTag(null, "identificador");
			    		    xmlSerializer.text(partida.getIdentificador());
			    		    xmlSerializer.endTag(null, "identificador");
			    		    
			    		    // Jugadores
			    			xmlSerializer.startTag(null, "Jugadores");
			    		    	// Obtenemos la lista de jugadores
			    				List<Jugador> listajugadores = partida.getJugadores();
			    				for(int indicejugadores = 0; indicejugadores < listajugadores.size(); indicejugadores++){
			    					
			    					Jugador jugador = listajugadores.get(indicejugadores);
			    					
			    					// Jugador
					    			xmlSerializer.startTag(null, "Jugador");
					    		    	
						    			// Nombre
						    			xmlSerializer.startTag(null, "nombre");
						    		    xmlSerializer.text(jugador.getNombre());
						    		    xmlSerializer.endTag(null, "nombre");
						    		    
						    		    // Puntuacion
						    			xmlSerializer.startTag(null, "puntuacion");
						    		    xmlSerializer.text(String.valueOf(jugador.getPuntuacion()));
						    		    xmlSerializer.endTag(null, "puntuacion");
						    		    
						    		    // Color
						    			xmlSerializer.startTag(null, "color");
						    		    xmlSerializer.text(String.valueOf(jugador.getColor()));
						    		    xmlSerializer.endTag(null, "color");
						    		    
						    		    // Numero
						    			xmlSerializer.startTag(null, "numerojugador");
						    		    xmlSerializer.text(String.valueOf(jugador.getNumerojugador()));
						    		    xmlSerializer.endTag(null, "numerojugador");
					    			
					    		    xmlSerializer.endTag(null, "Jugador");
			    				}
			    				
			    			// Tag final jugadores
			    		    xmlSerializer.endTag(null, "Jugadores");
			    		// Tag final Partida
		    		    xmlSerializer.endTag(null, "Partida");	
		    		}
		    	}
		    
		    // Tag final Partidas
		    xmlSerializer.endTag(null, "Partidas");
		    
		    // Fin del documento
		    xmlSerializer.endDocument();
		    xmlSerializer.flush();
		    fos.close();
		    
		    Log.i("MILOG", "Hemos terminado la serializacion del backup");
		    //String dataWrite = writer.toString();
		//    fileos.write(dataWrite.getBytes());
		 //   fileos.close();
		}
		catch (Exception e) {
		    // TODO Auto-generated catch block
		    Log.d("MILOG", "Error al guardar el xml: " + e.getMessage());
		}
	}

}

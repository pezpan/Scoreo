package com.example.cdp.mispartidas.almacenamiento.sax;

import android.content.Context;
import android.util.Log;
import com.example.cdp.mispartidas.almacenamiento.objetos.Partida;
import java.io.InputStream;
import java.util.List;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class SAXXMLParser {

	public static List<Partida> parse(Context contexto) {

        List<Partida> backup = null;
		
        try {
            
            // Anadimo para almacenamiento interno
            InputStream is = contexto.openFileInput("userData.xml");
            
            // create a XMLReader from SAXParser
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
            // create a SAXXMLHandler
            SAXXMLHandler saxHandler = new SAXXMLHandler();
            // store handler in XMLReader
            xmlReader.setContentHandler(saxHandler);
            // the process starts
            xmlReader.parse(new InputSource(is));
            
            // Obtenemos el backup de datos
            backup = saxHandler.getPartidas();

        } catch (Exception ex) {
            Log.d("MILOG", "SAXXMLParser: parse() failed");
        }
 
        // return Employee list
        return backup;
    }
}

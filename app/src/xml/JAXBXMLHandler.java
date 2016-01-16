package xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import backup.Backup;
 
public class JAXBXMLHandler {
 
    // Export
    public static void marshal(Backup backup, File selectedFile)
            throws IOException, JAXBException {
    	
        JAXBContext context;
        BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter(selectedFile));
        context = JAXBContext.newInstance(Backup.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(backup, writer);
        writer.close();
    }
 
    // Import
    public static Backup unmarshal(File importFile) throws JAXBException {
        
    	Backup backup = new Backup();
 
        JAXBContext context = JAXBContext.newInstance(Backup.class);
        Unmarshaller um = context.createUnmarshaller();
        backup = (Backup) um.unmarshal(importFile);
 
        return backup;
    }
}

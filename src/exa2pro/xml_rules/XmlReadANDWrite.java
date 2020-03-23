/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro.xml_rules;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Nikos
 */
public class XmlReadANDWrite {
    
    /**
     * Reads the Rules from the xml File
     * @param version the version for Fortran f90 or f77
     * @return all the rules
     */
    public static IcodelintRrules readRules(String version) throws JAXBException{
        File file = new File("sonar-icode-cnes-plugin-1.3.0/src/main/resources/rules/icode-"+version+"-rules.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(IcodelintRrules.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (IcodelintRrules) jaxbUnmarshaller.unmarshal(file);
    }
    
    /**
     * Writes all the Rules again in the file
     * @param que all the Rules
     * @param file the file to save
     */
    public static void writeRules(IcodelintRrules que, File file) throws JAXBException{
        JAXBContext jaxbContext = JAXBContext.newInstance(IcodelintRrules.class);
            
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(que, file);
        jaxbMarshaller.marshal(que, System.out);
    }
    
}

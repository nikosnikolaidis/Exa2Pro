/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsers;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Nikos
 */
public abstract class CodeFile implements Serializable{
    public File file;
    public int fanOut;
    public HashMap<String, Integer> methodsLOC;
    public HashMap<String, Integer> methodsCC;
    public double cohesion;                     //TODO maybe change to hashmap for cohesion for each method
    public ArrayList<String> opportunities;
    
    public CodeFile(File file){
        this.file=file;
        methodsLOC= new HashMap<>();
        methodsCC= new HashMap<>();
    }
    
    public abstract void parse();
    public abstract void calculateCohesion();
    public abstract void calculateOpportunities(boolean fast);
    
}

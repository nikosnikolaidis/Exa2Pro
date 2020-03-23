/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import parsers.CodeFile;
import parsers.cFile;
import parsers.fortranFile;

/**
 *
 * @author Nikos
 */
public class Project implements Serializable {
    private ProjectCredentials credentials;
    private String projectVersion;
    private ArrayList<CodeFile> projectFiles=new ArrayList<>();
    private Report projectReport;
    private ArrayList<CodeFile> fortranDeletedFiles=new ArrayList<>();
    
    public Project(ProjectCredentials c, String version){
        credentials=c;
        credentials.addProject(this);
        projectVersion=version;
        
        getFilesForAnalysis(credentials.getProjectDirectory());
    }
    
    /**
     * Creates an analysis and executes it
     */
    public void projectVersionAnalysis(){
        Analysis a= new Analysis(this);
        a.runCustomCreatedMetrics();
        a.createPropertiesFile();
        a.executeAnalysis();
        
        saveToFile();
    }
    
    /**
     * Finds all the files in the directory that will be analyzed
     * @param directoryName the directory to search for files
     */
    public void getFilesForAnalysis(String directoryName){
        File directory = new File(directoryName);
        // Get all files from a directory.
        File[] fList = directory.listFiles();
        if(fList != null){
            for (File file : fList) {
                if (file.isFile() && file.getName().contains(".") && file.getName().charAt(0)!='.') {
                    String[] str=file.getName().split("\\.");
                    // For all the filles of this dirrecory get the extension
                    if(str[str.length-1].equalsIgnoreCase("F90") )
                        projectFiles.add(new fortranFile(file,true));
                    else if(str[str.length-1].equalsIgnoreCase("f") || str[str.length-1].equalsIgnoreCase("f77")
                            || str[str.length-1].equalsIgnoreCase("for") || str[str.length-1].equalsIgnoreCase("fpp")
                            || str[str.length-1].equalsIgnoreCase("ftn"))
                        projectFiles.add(new fortranFile(file,false));
                    else if(str[str.length-1].equalsIgnoreCase("c") || str[str.length-1].equalsIgnoreCase("h") ||
                            str[str.length-1].equalsIgnoreCase("cpp") || str[str.length-1].equalsIgnoreCase("hpp") ||
                            str[str.length-1].equalsIgnoreCase("cc") || str[str.length-1].equalsIgnoreCase("cp") ||
                            str[str.length-1].equalsIgnoreCase("cxx") || str[str.length-1].equalsIgnoreCase("c++") ||
                            str[str.length-1].equalsIgnoreCase("hh") || str[str.length-1].equalsIgnoreCase("h++") ||
                            str[str.length-1].equalsIgnoreCase("hp") || str[str.length-1].equalsIgnoreCase("hxx") ||
                            str[str.length-1].equalsIgnoreCase("cu") || str[str.length-1].equalsIgnoreCase("hcu") )
                        projectFiles.add(new cFile(file));
                } else if (file.isDirectory()) {
                    getFilesForAnalysis(file.getAbsolutePath());
                }
            }
        }
    }
    
    /**
     * Copies all Fortran files to home directory of project
     * in order for icode to analyze them
     */
    public void copyFortranFilesToSiglePlace() throws IOException {
        for(CodeFile cf: projectFiles){
            String[] str=cf.file.getName().split("\\.");
            if(str[str.length-1].equalsIgnoreCase("f") || str[str.length-1].equalsIgnoreCase("f77")
                            || str[str.length-1].equalsIgnoreCase("for") || str[str.length-1].equalsIgnoreCase("fpp")
                            || str[str.length-1].equalsIgnoreCase("ftn") || str[str.length-1].equalsIgnoreCase("F90")){
                fortranDeletedFiles.add(cf);
                Files.copy(cf.file.toPath(), Paths.get(credentials.getProjectDirectory()+"\\temp_fortran_" + cf.file.getName()));
                cf.file.delete();
            }
        }
    }
    
    //Restore official Fortran files
    public void restoreTempFortranFiles() throws IOException {
        File[] files = new File(credentials.getProjectDirectory()).listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                String fName=f.getName();
                if (fName.startsWith("temp_fortran_")) {
                    fName= fName.replace("temp_fortran_", "");
                    for(CodeFile cf: fortranDeletedFiles){
                        if(cf.file.getName().equals(fName)){
                            Files.copy(f.toPath(), Paths.get(cf.file.getParent()+"\\"+fName) );
                            f.delete();
                        }
                    }
                }
            }
        }
        fortranDeletedFiles.clear();
    }
    
    // Returns true or flase if this projects contains Fortran code
    public boolean containsFortran() {
        for(CodeFile cf: projectFiles){
            String[] str=cf.file.getName().split("\\.");
            if(str[str.length-1].equalsIgnoreCase("f") || str[str.length-1].equalsIgnoreCase("f77")
                            || str[str.length-1].equalsIgnoreCase("for") || str[str.length-1].equalsIgnoreCase("fpp")
                            || str[str.length-1].equalsIgnoreCase("ftn") || str[str.length-1].equalsIgnoreCase("F90")){
                return true;
            }
        }
        return false;
    }
    
    // Saves to File the List of projects
    public void saveToFile(){
        try {
            FileOutputStream f = new FileOutputStream(new File("myProjects.txt"));
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(Exa2Pro.projecCredentialstList);
            o.close();
            f.close();
        } catch (IOException ex) {
            Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Getters
    public String getProjectVersion() {
        return projectVersion;
    }
    public ArrayList<CodeFile> getprojectFiles() {
        return projectFiles;
    }
    public Report getprojectReport() {
        return projectReport;
    }
    public ProjectCredentials getCredentials(){
        return credentials;
    }

    public void setProjectReport(Report projectReport) {
        this.projectReport = projectReport;
    }
    
}

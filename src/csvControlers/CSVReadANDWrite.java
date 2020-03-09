/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csvControlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import parsers.CodeFile;

/**
 *
 * @author Nikos
 */
public class CSVReadANDWrite {

    private String projectName;
    private ArrayList<CodeFile> files;
    public ArrayList<Artifacts> artifacts= new ArrayList<>();
    
    List<String[]> dataLines;

    public CSVReadANDWrite(ArrayList<CodeFile> files,String projectName) {
        this.projectName = projectName;
        this.files=files;
        read();
        write();
    }

    private void read() {
        BufferedReader csvReader;
        try {
            csvReader = new BufferedReader(new FileReader(projectName+".csv"));
            String row;
            int line=1;
            while ((row = csvReader.readLine()) != null) {
                if(line>1){
                    String[] data = row.split(",");
                    Artifacts art= new Artifacts(data);
                    artifacts.add(art);
                }
                line++;
            }
                csvReader.close();
        } catch (IOException ex) {
            Logger.getLogger(CSVReadANDWrite.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    
    
    private void write(){
        dataLines = new ArrayList<>();
        dataLines.add(new String[]{"Artifacts","Functions","Complexity","Ncloc","Comments Density (%)",
                "Code Smells","Technical Debt (min)","Fan-Out","LCOM2"});
        for(Artifacts ar: artifacts){
            boolean found=false;
            for(CodeFile cf: files){
                if(cf.file.getAbsolutePath().endsWith(ar.getPath().split(":")[1].replace("/", "\\"))){
                    dataLines.add(new String[]{ar.getPath(),""+ar.getFunctions(),""+ar.getComplexity(),
                        ""+ar.getNcloc(),""+ar.getCommentsDensity(),""+ar.getCodeSmells(),
                        ""+ar.getTechnicalDebt(),""+cf.fanOut,""+cf.cohesion});
                    found=true;
                    break;
                }
            }
            if(!found){
                dataLines.add(new String[]{ar.getPath(),""+ar.getFunctions(),""+ar.getComplexity(),
                        ""+ar.getNcloc(),""+ar.getCommentsDensity(),""+ar.getCodeSmells(),
                        ""+ar.getTechnicalDebt() });
            }
        }
        
        givenDataArray_whenConvertToCSV_thenOutputCreated(projectName);
    }

    public void givenDataArray_whenConvertToCSV_thenOutputCreated(String projectName) {
        File csvOutputFile = new File(projectName+"_output.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVoutput.class.getName()).log(Level.SEVERE, null, ex);
        }
        //assertTrue(csvOutputFile.exists());
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data).map(this::escapeSpecialCharacters).collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}

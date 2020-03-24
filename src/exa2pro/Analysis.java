/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import parsers.CodeFile;

/**
 *
 * @author Nikos
 */
public class Analysis {

    private Project project;

    public Analysis(Project project) {
        this.project = project;
    }
    
    //Execute the Analysis in this project
    public void executeAnalysis() {
        if(project.containsFortran()){
            try {
                project.copyFortranFilesToSiglePlace();
            } catch (IOException ex) {
                Logger.getLogger(Analysis.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //For Windows
        if ( Exa2Pro.isWindows() )
            runAnalysisWindows();
        //For Linux
        else
            runAnalysisLinux();
        
        try {
            project.restoreTempFortranFiles();
        } catch (IOException ex) {
            Logger.getLogger(Analysis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    // Creates the sonar-project.properties file
    // required to run sonar-scanner
    public void createPropertiesFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(project.getCredentials().getProjectDirectory()
                    + "sonar-project.properties"));
            writer.write("sonar.projectKey=" + project.getCredentials().getProjectKey() + System.lineSeparator());
            writer.append("sonar.projectName=" + project.getCredentials().getProjectName() + System.lineSeparator());
            writer.append("sonar.projectVersion=" + project.getProjectVersion() + System.lineSeparator());

            writer.append("sonar.sources=." + System.lineSeparator());

            if(project.containsFortran()){
                writer.append("sonar.icode.launch=true" +System.lineSeparator());
                writer.append("sonar.icode.path="+ Exa2Pro.iCodePath.replace("\\", "\\\\") +System.lineSeparator());
            }
            //writer.append("sonar.sourceEncoding=UTF-8");
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Runs our metrics
    public void runCustomCreatedMetrics() {
        for (CodeFile file : project.getprojectFiles()) {
            file.parse();
        }
        
        //print
        for (CodeFile file : project.getprojectFiles()) {
            System.out.println("File: "+file.file.getPath()+" ---> "+"fan-out:"+file.fanOut
                    + "  cohesion:"+file.cohesion);
        }
        
        //export csv file 
        File tempFile= new File(project.getCredentials().getProjectName()+".csv");
        if(tempFile.exists())
            new csvControlers.CSVReadANDWrite(project.getprojectFiles(),project.getCredentials().getProjectName());
        else
            new csvControlers.CSVoutput(project.getprojectFiles(),project.getCredentials().getProjectName());
        
    }

    // Windows
    // Runs in CMD sonar-scanner
    private void runAnalysisWindows() {
        try {
            //TODO
            // Add waiting animation

            System.out.println(System.getProperty("user.dir"));
            Process proc = Runtime.getRuntime().exec("cmd /c \"cd " + project.getCredentials().getProjectDirectory() + " && "
                    + System.getProperty("user.dir") + "\\sonar-scanner-4.2-windows\\bin\\sonar-scanner.bat\"");
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            //waits till sonar scanner finishes
            String line;
            while ((line = reader.readLine()) != null) {    
                System.out.println(line);
                if (line.contains("INFO: Final Memory")) {
                    break;
                }
            }
            //create a report and wait till sonarqube finishes analysing
            Report report = new Report(project);
            project.setProjectReport(report);
            while (!report.isFinishedAnalyzing()) {
                Thread.sleep(1000);
            }
            Thread.sleep(500);
            
            // Can Read Reports now
            double dept = Double.parseDouble(report.getMetricFromSonarQube("sqale_index"));
            report.setTotalDebt(formatTehnicalDebt(dept));
            report.setTotalDebt_Index(dept);
            report.setTotalCodeSmells(Integer.parseInt(report.getMetricFromSonarQube("code_smells")));
            report.setTotalLinesOfCode(Integer.parseInt(report.getMetricFromSonarQube("ncloc")));
            report.setTotalComplexity(Integer.parseInt(report.getMetricFromSonarQube("complexity")));
            report.setLinesOfCodeForAllLanguages(report.getMetricFromSonarQube("ncloc_language_distribution").replace(";", "\n"));
            
            report.getIssuesFromSonarQube();
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(Analysis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Formats the Technical Debt from number to string with days, hours or minutes
     * @param dept the total debt in minutes
     */
    private String formatTehnicalDebt(double dept) {
        String str = "";
        DecimalFormat df = new DecimalFormat("#.#");
        if (dept > 59) {
            dept = dept / 60;
        } else {
            str = "min";
        }
        if (dept > 7) {
            dept = dept / 8;
            str = "d";
        } else {
            str = "h";
        }
        return Double.parseDouble(df.format(dept)) + str;
    }

    // Linux
    // runs in terminal sonar-scanner
    private void runAnalysisLinux() {
        ProcessBuilder pbuilder = new ProcessBuilder("bash", "-c",
                "sonar-scanner-4.2-linux/bin/sonar-scanner");
        File err = new File("err.txt");
        try {
            pbuilder.redirectError(err);
            Process p = pbuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            System.out.println("Sonar Scanner in project folder");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

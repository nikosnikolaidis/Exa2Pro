/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Nikos
 */
public class Report  implements Serializable{
    private Project project;
    private ArrayList<Issue> issuesList= new ArrayList<>();
    private Date date;
    private String totalDebt;
    private double totalDebt_Index;
    private int totalCodeSmells;
    private int totalLinesOfCode;
    private String linesOfCodeForAllLanguages;
    private int totalComplexity;
    
    public Report(Project project){
        this.project=project;
    }
    
    public boolean containsIssue(Issue i){
        for(Issue issue: issuesList){
            if(issue.getIssueName().equals(i.getIssueName()) && issue.getIssueDirectory().equals(i.getIssueDirectory())
                    && issue.getIssueStartLine().equals(i.getIssueStartLine())){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Return the issues from Sonar Qube
     */
    public void getIssuesFromSonarQube(){
        int page= (totalCodeSmells-1)/500 + 1;
        
        //if there are more than limit of API 10,000 split 
        if(page>10000){
            page= (getIssuesNumbers("&resolved=false&severities=INFO")-1)/500 + 1;
            if(page>0){
                for(int i=1; i<=page; i++){
                    getIssuesFromPage(1,"&resolved=false&severities=INFO");
                }
            }
            
            page= (getIssuesNumbers("&resolved=false&severities=MINOR,MAJOR,CRITICAL,BLOCKER")-1)/500 + 1;
            if(page>0 && page<10000){
                for(int i=1; i<=page; i++){
                    getIssuesFromPage(i,"&resolved=false&severities=MINOR,MAJOR,CRITICAL,BLOCKER");
                }
            }
            // if again more than 10,000, then split again
            else if(page>1000){
                page= (getIssuesNumbers("&resolved=false&severities=MINOR,MAJOR")-1)/500 + 1;
                for(int i=1; i<=page; i++){
                    getIssuesFromPage(1,"&resolved=false&severities=MINOR,MAJOR");
                }
                
                page= (getIssuesNumbers("&resolved=false&severities=CRITICAL,BLOCKER")-1)/500 + 1;
                for(int i=1; i<=page; i++){
                    getIssuesFromPage(i,"&resolved=false&severities=CRITICAL,BLOCKER");
                }
            }
        }
        else{   //if rules less than 10,000 then all together
            for(int i=1; i<=page; i++){
                getIssuesFromPage(i,"&resolved=false");
            }
        }
        
        //To get the fixed issues
        //if(Integer.parseInt(project.getProjectVersion())>1){
        //    getIssuesFromPage(i,"resolutions=FIXED");
        //}
    }
    
    /**
     * Get number of issus for a specific api call
     * @param extra the severities we want each time
     */
    private int getIssuesNumbers(String extra){
        try {
            URL url = new URL(Exa2Pro.sonarURL+"/api/issues/search?pageSize=500&componentKeys="
                    +project.getCredentials().getProjectName()+"&types=CODE_SMELL"+extra+"&p=1");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if(responsecode != 200)
                throw new RuntimeException("HttpResponseCode: "+responsecode);
            else{
                Scanner sc = new Scanner(url.openStream());
                String inline="";
                while(sc.hasNext()){
                    inline+=sc.nextLine();
                }
                String number =inline.split(",",2)[0].replace("{\"total\":", "");
                sc.close();
                return Integer.parseInt(number);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    /**
     * Get Issues From Sonar API
     * @param page the results
     * @param extra extra filtering
     */
    private void getIssuesFromPage(int page, String extra){
        try {
            date= new Date();
            URL url = new URL(Exa2Pro.sonarURL+"/api/issues/search?pageSize=500&componentKeys="
                    +project.getCredentials().getProjectName()+"&types=CODE_SMELL"+extra+"&p="+page);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if(responsecode != 200)
                throw new RuntimeException("HttpResponseCode: "+responsecode);
            else{
                Scanner sc = new Scanner(url.openStream());
                String inline="";
                while(sc.hasNext()){
                    inline+=sc.nextLine();
                }
                sc.close();
                
                JSONParser parse = new JSONParser();
                JSONObject jobj = (JSONObject)parse.parse(inline);
                JSONArray jsonarr_1 = (JSONArray) jobj.get("issues");
                for(int i=0;i<jsonarr_1.size();i++){
                    JSONObject jsonobj_1 = (JSONObject)jsonarr_1.get(i);
                    JSONObject jsonobj_2=(JSONObject)jsonobj_1.get("textRange");
                    Issue issue=new Issue(jsonobj_1.get("rule").toString(), jsonobj_1.get("message").toString()
                            , jsonobj_1.get("severity").toString(), jsonobj_1.get("debt").toString()
                            , jsonobj_1.get("type").toString(), jsonobj_1.get("component").toString()
                            , jsonobj_2.get("startLine").toString(), jsonobj_2.get("endLine").toString());
                    issuesList.add(issue);
                }
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Return the metric from Sonar Qube
     * @param metric the metric we want
     */
    public String getMetricFromSonarQube(String metric){
        try {
            URL url = new URL(Exa2Pro.sonarURL+"/api/measures/component?component="
                    +project.getCredentials().getProjectName()+"&metricKeys="+metric);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if(responsecode != 200)
                throw new RuntimeException("HttpResponseCode: "+responsecode);
            else{
                Scanner sc = new Scanner(url.openStream());
                String inline="";
                while(sc.hasNext()){
                    inline+=sc.nextLine();
                }
                sc.close();
                
                JSONParser parse = new JSONParser();
                JSONObject jobj = (JSONObject)parse.parse(inline);
                JSONObject jobj1= (JSONObject) jobj.get("component");
                JSONArray jsonarr_1 = (JSONArray) jobj1.get("measures");
                
                JSONObject jsonobj_1 = (JSONObject)jsonarr_1.get(0);
                return jsonobj_1.get("value").toString();
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    /*
    * Returns if the project is finished being analyzed
    */
    public boolean isFinishedAnalyzing(){
        boolean finished=false;
        try {
            URL url = new URL(Exa2Pro.sonarURL+"/api/ce/component?component="+project.getCredentials().getProjectName());
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if(responsecode != 200)
                throw new RuntimeException("HttpResponseCode: "+responsecode);
            else{
                Scanner sc = new Scanner(url.openStream());
                while(sc.hasNext()){
                    String line=sc.nextLine();
                    if(line.trim().contains("\"analysisId\":") &&
                            line.trim().contains("\"queue\":[],")){
                        finished=true;
                    }
                }
                sc.close();
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
        return finished;
    }

    
    // Getters
    public Project getProject() {
        return project;
    }
    public ArrayList<Issue> getIssuesList() {
        return issuesList;
    }
    public Date getDate() {
        return date;
    }
    public String getTotalDebt() {
        return totalDebt;
    }
    public double getTotalDebt_Index() {
        return totalDebt_Index;
    }
    public int getTotalCodeSmells() {
        return totalCodeSmells;
    }
    public int getTotalLinesOfCode() {
        return totalLinesOfCode;
    }
    public int getTotalComplexity() { 
        return totalComplexity;
    }
    public String getLinesOfCodeForAllLanguages() {
        return linesOfCodeForAllLanguages;
    }

    // Setters
    public void setTotalDebt(String totalDebt) {
        this.totalDebt = totalDebt;
    }
    public void setTotalDebt_Index(double totalDebt) {
        this.totalDebt_Index = totalDebt;
    }
    public void setTotalCodeSmells(int totalCodeSmells) {
        this.totalCodeSmells = totalCodeSmells;
    }
    public void setTotalLinesOfCode(int linesOfCode) {
        this.totalLinesOfCode = linesOfCode;
    }
    public void setLinesOfCodeForAllLanguages(String linesOfCodeForAllLanguages) {
        this.linesOfCodeForAllLanguages = linesOfCodeForAllLanguages;
    }
    public void setTotalComplexity(int totalComplexity) {
        this.totalComplexity = totalComplexity;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsers;

import java.io.File;
import java.util.ArrayList;
import semi.Analyser;
import semi.JavaClass;
import semi.Method;
import semi.MethodOppExtractor;
import semi.MethodOppExtractorSettings;
import semi.clustering.Opportunity;

/**
 *
 * @author Nikos
 */
public class ParsedFilesController {
    //private ArrayList<JavaClass> classResults = new ArrayList<>();
    private String selected_metric = "SIZE";// "LCOM1", "LCOM2", "LCOM4", "COH", "CC" //***???
    public MethodOppExtractorSettings extractor_settings =new MethodOppExtractorSettings();
    private int number_of_refs = 0;
        
    
    public double doAnalysis(File file) {
	//boolean ret = true;
        Analyser analyser = new Analyser();
        analyser.setFile(file);
        JavaClass myclass=analyser.performAnalysis();
        
        //Calculate sum of all methods lcom2
        double sum=0;
        for(int i=0; i< myclass.getMethods().size(); i++){
            sum += myclass.getMethods().get(i).getMetricIndexFromName("lcom2");
        }
        
//        classResults.add(myclass);
//        JavaClass clazz = classResults.get(classResults.size()-1);
//        for (int index = 0; index < clazz.getMethods().size(); index++) {
//            boolean needsRefactoring = clazz.getMethods().get(index).needsRefactoring(selected_metric);	
//            
//            if(needsRefactoring) {
//            	String className = file.getName().replaceFirst("./", "");
//            	String methodName = clazz.getMethods().get(index).getName();
//            	
//            	MethodOppExtractor extractor = new MethodOppExtractor(file, clazz.getMethods().get(index).getName(), extractor_settings, classResults.get(classResults.size()-1));
//
//            	Method method = clazz.getMethods().getMethodByName(methodName);
//            	ArrayList<Opportunity> opportunities = method.getOpportunityList().getOptimals();
//            	
//            	int count=1;
//            	for(Opportunity opp : opportunities) {
//            		if(count>1) {
//                            break;
//                        }
//            		number_of_refs++;
//                        System.out.println("File Name:"+className+ "   " + method.getMetricIndexFromName("lcom2"));
//            		//ret = ret && dbCon.insertMethodToDatabase(projectName, className, methodName, opp.getStartLineCluster(), opp.getEndLineCluster(), opp.getOpportunityBenefitMetricByName("lcom2"), method.getMetricIndexFromName("lcom2"), method.getMetricIndexFromName("size"), classPath);
//            		count++;
//            	}
//            }
//        }
	File fileDel = new File("./" + file.getName() + "_parsed.txt");
        fileDel.delete();
	//return ret;
        
        //return average
        int num=myclass.getMethods().size();
        if(num==0)
            num=1;
        return sum/num;
    }
    
}


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exa2pro.Exa2Pro;
import exa2pro.Project;
import exa2pro.ProjectCredentials;
import exa2pro.Report;

class SaveRestoreProjectsTest {
	
	ProjectCredentials pc;

	@BeforeEach
	void setUp() throws Exception {
		//create list and add PC and save
		Exa2Pro.projecCredentialstList = new ArrayList<>();
		pc=new ProjectCredentials("test", "test", "src/test/java/f90");
		Project p=new Project(pc, "1");
		Exa2Pro.projecCredentialstList.add(pc);
		p.saveToFile();

		//delete list
		Exa2Pro.projecCredentialstList = new ArrayList<>();
		
		//restore list
		Exa2Pro.getProjetsFromFile();
 	}

	@Test
	void test_NumberOfPC() {
		assertEquals(1, Exa2Pro.projecCredentialstList.size());
	}

	@Test
	void test_NumberOfVersions() {
		assertEquals(1, Exa2Pro.projecCredentialstList.get(0).getProjects().size());
	}
	
	@Test
	void test_Name() {
		assertEquals("test", Exa2Pro.projecCredentialstList.get(0).getProjectName());
	}
	
	@Test
	void test_Directory() {
            if (Exa2Pro.isWindows())
		assertEquals("src/test/java/f90\\", Exa2Pro.projecCredentialstList.get(0).getProjectDirectory());
            else
		assertEquals("src/test/java/f90", Exa2Pro.projecCredentialstList.get(0).getProjectDirectory());
	}

	@Test
	void test_project_analysis() {
		 ProjectCredentials pc2 = new ProjectCredentials("test", "test", "src/test/java/f90");
		 Project p1=new Project(pc2, "1");
		 p1.setProjectReport(new Report(p1));
		 System.out.println("num of projects: "+pc2.getProjects().size());
		 p1.projectVersionAnalysis();
		 
		 //add new file
		 try {
			 File myObj = new File(System.getProperty("user.dir") +File.separator+
					 new File("src/test/java/f90/new.f90") );
			 if (myObj.createNewFile()) {
				 System.out.println("File created: " + myObj.getName());
				 
				 //new analysis
				 Project p2=new Project(pc2, "2");
				 p2.projectVersionAnalysis();
				 System.out.println("num of projects: "+pc2.getProjects().size());
				 assertEquals(2, pc2.getProjects().size());
				 
				 //delete new file
				 myObj.delete();
			 } else {
				 System.out.println("File already exists.");
			 }
		 } catch (IOException e) {
			 System.out.println("An error occurred.");
		 }	
	}

}

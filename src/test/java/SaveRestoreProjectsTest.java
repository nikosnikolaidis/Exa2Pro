
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exa2pro.Exa2Pro;
import exa2pro.Project;
import exa2pro.ProjectCredentials;

class SaveRestoreProjectsTest {
	
	ProjectCredentials pc;

	@BeforeEach
	void setUp() throws Exception {
		//create list and add PC and save
		Exa2Pro.projecCredentialstList = new ArrayList<>();
		pc=new ProjectCredentials("test", "test", "src/tests/f90");
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
		assertEquals("src/tests/f90//", Exa2Pro.projecCredentialstList.get(0).getProjectDirectory());
	}
}

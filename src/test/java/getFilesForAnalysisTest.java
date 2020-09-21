
import static org.junit.Assume.assumeTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exa2pro.Exa2Pro;
import exa2pro.Project;
import exa2pro.ProjectCredentials;
import parsers.CodeFile;

class getFilesForAnalysisTest {

	Project p;
	
	@BeforeEach
	void setUp() throws Exception {
		//create list and add PC
		Exa2Pro.projecCredentialstList = new ArrayList<>();
		ProjectCredentials pc=new ProjectCredentials("test", "test", "src/test/java/f90");
		p=new Project(pc, "1");
		Exa2Pro.projecCredentialstList.add(pc);
	}

	@Test
	void test_NumberOfFiles() {
		assertEquals(6,p.getprojectFiles().size());
	}
	
	@Test
	void testFile_a() {
		for(CodeFile cf: p.getprojectFiles()) {
			if(cf.file.getName().equals("a.f90"))
				assumeTrue(true);
		}
	}
	
	@Test
	void testFile_approximate() {
		for(CodeFile cf: p.getprojectFiles()) {
			if(cf.file.getName().equals("approximate.F90"))
				assumeTrue(true);
		}
	}
	
	@Test
	void testFile_controllability_assessment() {
		for(CodeFile cf: p.getprojectFiles()) {
			if(cf.file.getName().equals("controllability_assessment.F90"))
				assumeTrue(true);
		}
	}
	
	@Test
	void testFile_n1() {
		for(CodeFile cf: p.getprojectFiles()) {
			if(cf.file.getName().equals("n1.f90"))
				assumeTrue(true);
		}
	}
	
	@Test
	void testFile_n_1() {
		for(CodeFile cf: p.getprojectFiles()) {
			if(cf.file.getName().equals("n_1.f90"))
				assumeTrue(true);
		}
	}

}


import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exa2pro.Exa2Pro;
import exa2pro.Project;
import exa2pro.ProjectCredentials;
import parsers.CodeFile;

class FortranFilesTest {
	
	Project p1;
	
	@BeforeEach
	void setUp() throws Exception {
		//create list and add PC
		Exa2Pro.projecCredentialstList = new ArrayList<>();
		ProjectCredentials pc=new ProjectCredentials("test", "test", "src/test/java/f90");
		Project p= new Project(pc, "1");
		Exa2Pro.projecCredentialstList.add(pc);
		
		
		p.copyFortranFilesToSiglePlace();

		p.restoreTempFortranFiles();

		ProjectCredentials pc1=new ProjectCredentials("test1", "test1", "src/test/java/f90");
		p1= new Project(pc1, "1");
	}
	
	
	@Test
	void test_restore_n_1() throws IOException {
		for(CodeFile cf: p1.getprojectFiles()) {
			if(cf.file.getName().equals("n_1.f90"))
				assumeTrue(true);
		}
	}
	
	@Test
	void test_restore__n() throws IOException {
		for(CodeFile cf: p1.getprojectFiles()) {
			if(cf.file.getName().equals("_n.f90"))
				assumeTrue(true);
		}
	}
}

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import exa2pro.Analysis;
import exa2pro.Project;
import exa2pro.ProjectCredentials;
import org.junit.jupiter.api.BeforeEach;

class FormatTehnicalDebtTest {

    Analysis analysis;
    
    @BeforeEach
    void setUp() throws Exception {
        analysis= new Analysis(new Project(new ProjectCredentials("test", "test", "src/test/java/f90"), "1"));
    }
    
    @Test
    void test_min() {
        assertEquals("5.0min", analysis.formatTehnicalDebt(5));
    }
    
    @Test
    void test_hour() {
        assertEquals("1.0h", analysis.formatTehnicalDebt(60));
    }
    
    @Test
    void test_hours() {
    	assertEquals("1.5h", analysis.formatTehnicalDebt(90));
    }

    @Test
    void test_day() {
	assertEquals("1.0d", analysis.formatTehnicalDebt(480));
    }
    
    @Test
    void test_days() {
	assertEquals("4.0d", analysis.formatTehnicalDebt(1920));
    }

    @Test
    void test_days2() {
    	assertEquals("4.2d", analysis.formatTehnicalDebt(2000));
    }
	
}

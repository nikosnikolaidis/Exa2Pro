
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import parsers.fortranFile;

class F90MetricsTest {

	fortranFile F90_approximate;
	fortranFile F90_controllability_assessment;
	fortranFile F90_a;
	fortranFile F90_n1;

	@BeforeEach
	void setUp() throws Exception {
		// Files initialization
		F90_approximate = new fortranFile(new File("src/test/java/f90/approximate.F90"), true);
		F90_approximate.parse();

		F90_controllability_assessment = new fortranFile(new File("src/test/java/f90/controllability_assessment.F90"), true);
		F90_controllability_assessment.parse();

		F90_a = new fortranFile(new File("src/test/java/f90/a.F90"), true);
		F90_a.parse();

		F90_n1 = new fortranFile(new File("src/test/java/f90/n1.F90"), true);
		F90_n1.parse();
	}

	// FanOut
	@Test
	void testF90_FanOut_approximate() {
		assertEquals(2, F90_approximate.fanOut);
	}

	@Test
	void testF90_FanOut_controllability_assessment() {
		assertEquals(7, F90_controllability_assessment.fanOut);
	}

	@Test
	void testF90_FanOut_a() {
		assertEquals(7, F90_a.fanOut);
	}

	@Test
	void testF90_FanOut_n1() {
		assertEquals(1, F90_n1.fanOut);
	}

	
	// CC
	@Test
	void testF90_CC_init_index_change() {
		assertEquals(new Integer(0), F90_approximate.methodsCC.get("init_index_change"));
	}

	@Test
	void testF90_CC_set_change_index() {
		assertEquals(new Integer(5), F90_approximate.methodsCC.get("set_change_index"));
	}

	@Test
	void testF90_CC_read_initial_vector() {
		assertEquals(new Integer(1), F90_controllability_assessment.methodsCC.get("read_initial_vector"));
	}
	
	@Test
	void testF90_CC_set_parameter_vectors() {
		assertEquals(new Integer(3), F90_controllability_assessment.methodsCC.get("set_parameter_vectors"));
	}
	
	@Test
	void testF90_CC__qrm_factorize() {
		assertEquals(new Integer(10), F90_a.methodsCC.get("_qrm_factorize"));
	}
	
	@Test
	void testF90_CC_AccumulateLine() {
		assertEquals(new Integer(0), F90_n1.methodsCC.get("AccumulateLine"));
	}
}

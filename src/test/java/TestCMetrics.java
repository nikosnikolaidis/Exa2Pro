
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import parsers.cFile;

class TestCMetrics {

	cFile C_1;
	cFile C_2;

	@BeforeEach
	void setUp() throws Exception {
		//Files initialization
		C_1 = new cFile(new File("src/test/java/c/1.c"));
		C_1.parse();

		C_2 = new cFile(new File("src/test/java/c/2.c"));
		C_2.parse();
	}

	//FanOut
	@Test
	void testC_FanOut_1() {
		assertEquals(7, C_1.fanOut);
	}

	@Test
	void testC_FanOut_2() {
		assertEquals(1, C_2.fanOut);
	}
	
	//CC
	@Test
	void testC_CC_pastix_fortran() {
		assertEquals(new Integer(1), C_1.methodsCC.get("void pastix_fortran "));
	}

	@Test
	void testC_CC_pastix_fortran_checkmatrix() {
		assertEquals(new Integer(6), C_1.methodsCC.get("void pastix_fortran_checkmatrix "));
	}
	
	@Test
	void testC_CC_pastix_fortran_getlocalnodelst() {
		assertEquals(new Integer(0), C_1.methodsCC.get("void pastix_fortran_getlocalnodelst "));
	}
	
	@Test
	void testC_CC_c_check_orthogonality() {
		assertEquals(new Integer(5), C_2.methodsCC.get("int c_check_orthogonality"));
	}
	
	@Test
	void testC_CC_c_check_QRfactorization() {
		assertEquals(new Integer(8), C_2.methodsCC.get("int c_check_QRfactorization"));
	}
	
	@Test
	void testC_CC_c_check_gemm() {
		assertEquals(new Integer(0), C_2.methodsCC.get("float c_check_gemm"));
	}
	
}

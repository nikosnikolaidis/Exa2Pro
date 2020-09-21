
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import parsers.cFile;

class CppMetricsTest {

	cFile Cpp_1;
	cFile Cpp_enthalpy3;
	cFile Cpp_cg;
	cFile Cpp_serializer;

	@BeforeEach
	void setUp() throws Exception {
		//Files initialization
		Cpp_1 = new cFile(new File("src/test/java/cpp/1.cpp"));
		Cpp_1.parse();

		Cpp_enthalpy3 = new cFile(new File("src/test/java/cpp/enthalpy3.cpp"));
		Cpp_enthalpy3.parse();

		Cpp_cg = new cFile(new File("src/test/java/cpp/cg.cpp"));
		Cpp_cg.parse();
		
		Cpp_serializer = new cFile(new File("src/test/java/cpp/serializer.hpp"));
		Cpp_serializer.parse();
	}

	//FanOut
	@Test
	void testCpp_FanOut_1() {
		assertEquals(2, Cpp_1.fanOut);
	}

	@Test
	void testCpp_FanOut_enthalpy3() {
		assertEquals(3, Cpp_enthalpy3.fanOut);
	}

	@Test
	void testCpp_FanOut_cg() {
		assertEquals(1, Cpp_cg.fanOut);
	}
	
	//CC
	@Test
	void testCpp_CC_cpp_enthalpy3_() {
		assertEquals(new Integer(6), Cpp_1.methodsCC.get("void cpp_enthalpy3_"));
	}

	@Test
	void testCpp_CC_cpp_enthalpy3_liquid() {
		assertEquals(new Integer(1), Cpp_1.methodsCC.get("static double cpp_enthalpy3_liquid"));
	}
	
	@Test
	void testCpp_CC_cpp_enthalpy3_liquid_grad() {
		assertEquals(new Integer(2), Cpp_1.methodsCC.get("static void cpp_enthalpy3_liquid_grad"));
	}
	
	@Test
	void testCpp_CC_dump() {
		assertEquals(new Integer(23), Cpp_serializer.methodsCC.get("void dump"));
	}
	
	@Test
	void testCpp_CC_dump_integer() {
		assertEquals(new Integer(3), Cpp_serializer.methodsCC.get("void dump_integer"));
	}
	
	@Test
	void testCpp_CC_mw_cg_solve() {
		assertEquals(new Integer(13), Cpp_cg.methodsCC.get("int mw_cg_solve"));
	}
	
	@Test
	void testCpp_CC_runtime_error() {
		assertEquals(new Integer(0), Cpp_cg.methodsCC.get("int runtime_error"));
	}
	
	@Test
	void testCpp_CC_enthalpy_grad_vap() {
		assertEquals(new Integer(2), Cpp_enthalpy3.methodsCC.get("void enthalpy_grad_vap"));
	}
	
}

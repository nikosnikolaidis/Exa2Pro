
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import parsers.fortranFile;

class F77MetricsTest {

	fortranFile F77_annealing;
	fortranFile F77_frpmainmn;
	fortranFile F77_main_anneal_par;
	fortranFile F77_sa_moves;

	@BeforeEach
	void setUp() throws Exception {
		// Files initialization
		F77_annealing = new fortranFile(new File("src/test/java/f77/annealing.f"), false);
		F77_annealing.parse();

		F77_frpmainmn = new fortranFile(new File("src/test/java/f77/frpmainmn.f"), false);
		F77_frpmainmn.parse();

		F77_main_anneal_par = new fortranFile(new File("src/test/java/f77/main_anneal_par.f"), false);
		F77_main_anneal_par.parse();

		F77_sa_moves = new fortranFile(new File("src/test/java/f77/sa_moves.f"), false);
		F77_sa_moves.parse();

	}

	
	//// FanOut
	@Test
	void testF77_FanOut_annealing() {
		assertEquals(0, F77_annealing.fanOut);
	}

	@Test
	void testF77_FanOut_frpmainmn() {
		assertEquals(0, F77_frpmainmn.fanOut);
	}

	@Test
	void testF77_FanOut_main_anneal_par() {
		assertEquals(0, F77_main_anneal_par.fanOut);
	}

	@Test
	void testF77_FanOut_sa_moves() {
		assertEquals(0, F77_sa_moves.fanOut);
	}

	
	//// CC
	@Test
	void testF77_CC_annealing() {
		assertEquals(new Integer(8), F77_annealing.methodsCC.get("annealing"));
	}

	@Test
	void testF77_CC_median_calc() {
		assertEquals(new Integer(0), F77_annealing.methodsCC.get("median_calc"));
	}
	
	@Test
	void testF77_CC_GSPAR() {
		assertEquals(new Integer(2), F77_frpmainmn.methodsCC.get("GSPAR"));
	}
	
	@Test
	void testF77_CC_exit_flag_not_zero() {
		assertEquals(new Integer(7), F77_main_anneal_par.methodsCC.get("exit_flag_not_zero"));
	}
	
	@Test
	void testF77_CC_cool_SA() {
		assertEquals(new Integer(4), F77_main_anneal_par.methodsCC.get("cool_SA"));
	}
	
	@Test
	void testF77_CC_exceeded_limits() {
		assertEquals(new Integer(10), F77_sa_moves.methodsCC.get("exceeded_limits"));
	}
	
	@Test
	void testF77_CC_get_diff_Temp() {
		assertEquals(new Integer(2), F77_sa_moves.methodsCC.get("get_diff_Temp"));
	}
	
	@Test
	void testF77_CC_init_var_sim() {
		assertEquals(new Integer(4), F77_sa_moves.methodsCC.get("init_var_sim"));
	}
	
	@Test
	void testF77_CC_RANDOM_GEN() {
		assertEquals(new Integer(0), F77_sa_moves.methodsCC.get("RANDOM_GEN"));
	}
	
}

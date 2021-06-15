import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import exa2pro.Issue;

class IssuesCompareTest {

	@Test
	void test1() {
		Issue i1= new Issue("java:1", "Test1", "CRITICAL", "6min", "CODE_SMELL", "test:src/test/test.java", "0", "1");
		Issue i2= new Issue("java:1", "Test1", "CRITICAL", "10min", "CODE_SMELL", "test:src/test/test.java", "0", "1");
		assertEquals(-1, i2.compareTo(i1));
	}

	@Test
	void test2() {
		Issue i1= new Issue("java:1", "Test1", "CRITICAL", "10min", "CODE_SMELL", "test:src/test/test.java", "0", "1");
		Issue i2= new Issue("java:1", "Test1", "CRITICAL", "6min", "CODE_SMELL", "test:src/test/test.java", "0", "1");
		assertEquals(1, i2.compareTo(i1));
	}

	@Test
	void test3() {
		Issue i1= new Issue("java:1", "Test1", "CRITICAL", "10min", "CODE_SMELL", "test:src/test/test.java", "0", "1");
		Issue i2= new Issue("java:1", "Test1", "CRITICAL", "10min", "CODE_SMELL", "test:src/test/test.java", "0", "1");
		assertEquals(0, i2.compareTo(i1));
	}
	
	@Test
	void test4() {
		Issue i1= new Issue("java:1", "Test1", "MAJOR", "6min", "CODE_SMELL", "test:src/test/test.java", "0", "1");
		Issue i2= new Issue("java:1", "Test1", "CRITICAL", "10min", "CODE_SMELL", "test:src/test/test.java", "0", "1");
		assertEquals(-1, i2.compareTo(i1));
	}
	
	@Test
	void test5() {
		Issue i1= new Issue("java:1", "Test1", "INFO", "6min", "CODE_SMELL", "test:src/test/test.java", "0", "1");
		Issue i2= new Issue("java:1", "Test1", "CRITICAL", "10min", "CODE_SMELL", "test:src/test/test.java", "0", "1");
		assertEquals(-1, i2.compareTo(i1));
	}
}

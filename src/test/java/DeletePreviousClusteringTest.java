import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exa2pro.Exa2Pro;

class DeletePreviousClusteringTest {

	@BeforeEach
	void setUp() throws Exception {
		Exa2Pro.ClusteringPath= System.getProperty("user.dir") +File.separator+ new File("src/test/resources/clustering");
	}
	
	@Test
	void testOneFile() {
		try {
			File myObj = new File(Exa2Pro.ClusteringPath +File.separator+ "filename.txt");
			if (myObj.createNewFile()) {
		    	System.out.println("File created: " + myObj.getName());
		    	
		    	Exa2Pro.deletePreviousClasteringCSV();
		    	assertEquals(0, (new File(Exa2Pro.ClusteringPath)).listFiles().length);
		    } else {
		    	System.out.println("File already exists.");
		    }
		} catch (IOException e) {
			System.out.println("An error occurred.");
		}
	}

	@Test
	void testCSVFile() {
		try {
			File myObj = new File(Exa2Pro.ClusteringPath +File.separator+ "filename.csv");
			if (myObj.createNewFile()) {
		    	System.out.println("File created: " + myObj.getName());
		    	
		    	Exa2Pro.deletePreviousClasteringCSV();
		    	assertEquals(0, (new File(Exa2Pro.ClusteringPath)).listFiles().length);
		    } else {
		    	System.out.println("File already exists.");
		    }
		} catch (IOException e) {
			System.out.println("An error occurred.");
		}
	}

	@Test
	void testCSVFiles() {
		try {
			int i=0;
			File myObj = new File(Exa2Pro.ClusteringPath +File.separator+ "filename.csv");
			if (myObj.createNewFile()) {
		    	System.out.println("File created: " + myObj.getName());
		    	i++;
		    } else {
		    	System.out.println("File already exists.");
		    }
			File myObj2 = new File(Exa2Pro.ClusteringPath +File.separator+ "filename2.csv");
			if (myObj2.createNewFile()) {
		    	System.out.println("File created: " + myObj2.getName());
		    	i++;
		    } else {
		    	System.out.println("File already exists.");
		    }
			
			if(i==2) {
		    	Exa2Pro.deletePreviousClasteringCSV();
		    	assertEquals(0, (new File(Exa2Pro.ClusteringPath)).listFiles().length);
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
		}
	}
	
	@Test
	void testPythonFile() {
		try {
			File myObj = new File(Exa2Pro.ClusteringPath +File.separator+ "filename.py");
			if (myObj.createNewFile()) {
		    	System.out.println("File created: " + myObj.getName());
		    	
		    	Exa2Pro.deletePreviousClasteringCSV();
		    	assertEquals(1, (new File(Exa2Pro.ClusteringPath)).listFiles().length);

		    	myObj.delete();
		    } else {
		    	System.out.println("File already exists.");
		    }
		} catch (IOException e) {
			System.out.println("An error occurred.");
		}
	}

	@Test
	void testCSVandPythonFiles() {
		try {
			int i=0;
			File myObj = new File(Exa2Pro.ClusteringPath +File.separator+ "filename.csv");
			if (myObj.createNewFile()) {
		    	System.out.println("File created: " + myObj.getName());
		    	i++;
		    } else {
		    	System.out.println("File already exists.");
		    }
			File myObj2 = new File(Exa2Pro.ClusteringPath +File.separator+ "filename2.py");
			if (myObj2.createNewFile()) {
		    	System.out.println("File created: " + myObj2.getName());
		    	i++;
		    } else {
		    	System.out.println("File already exists.");
		    }
			
			if(i==2) {
		    	Exa2Pro.deletePreviousClasteringCSV();
		    	assertEquals(1, (new File(Exa2Pro.ClusteringPath)).listFiles().length);
		    	
		    	myObj2.delete();
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
		}
	}
	
	@Test
	void testDirectoryFiles() {
		File myObj = new File(Exa2Pro.ClusteringPath +File.separator+ "filename");
		if (myObj.mkdir()) {
			System.out.println("Directory created: " + myObj.getName());

			Exa2Pro.deletePreviousClasteringCSV();

	    	assertEquals(1, (new File(Exa2Pro.ClusteringPath)).listFiles().length);
	    	
	    	myObj.delete();
		} else {
			System.out.println("Dir not created.");
		}
	}
}

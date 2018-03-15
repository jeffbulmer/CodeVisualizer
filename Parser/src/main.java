import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class main {
	
	final static int numQuestions = 10;
	final static int numErrors = 6;

	public static void main(String[] args) throws IOException {
		System.out.println(System.getProperty("user.dir"));
		File fileOut = new File("errorStats.txt");
		PrintWriter errorstats = new PrintWriter(fileOut);
		String epu = "Errors per User:\n";
		String epq = "Errors per Question:\n";
		int[][] errors = new int[numQuestions][numErrors];
		int indexFolders = 1;
		int countFolders = 0;
		while(countFolders < Files.list(Paths.get("Submissions")).count()){
			String dirString = "Submissions\\"+indexFolders;
			File dir = new File(dirString);
			if(dir.exists() && dir.isDirectory()){
				int indexFiles = 1;
				int countFiles = 0;
				epu += "\n"+indexFolders+"\n";
				int[] userErrors = new int[numErrors];
				while(countFiles < Files.list(Paths.get(dirString)).count()){
					String fileString = dirString+"\\answer"+indexFiles+".txt";
					File file = new File(fileString);
					System.out.println(fileString);
					if(file.exists() && file.isFile()){
//						Don't uncomment the next few lines unless you are working with new data!
//						InputStream is = new FileInputStream(file);
//						cleanQuotes(is, file);
//						is.close();
						TextProcessor tp = new TextProcessor(fileString, true);
						userErrors[0] += tp.checkScanner().size();
						userErrors[1] += tp.checkBracketCount().size();
						userErrors[2] += tp.checkBracketMatch().size();
						userErrors[3] += tp.checkBadSemiColon().size();
						userErrors[4] += tp.checkAssignment().size();
						userErrors[5] += tp.checkTabbing(4).size();
						System.out.println("");
						countFiles++;
						indexFiles++;
						
					} else {
						indexFiles++;
					}
				}
				for(int i = 0; i < userErrors.length; i++){
					errors[indexFiles-2][i] += userErrors[i];
					switch(i){
					case 0:
						epu += "Unclosed Scanners: ";
						break;
					case 1:
						epu += "Bracket Miscounts: ";
						break;
					case 2: 
						epu += "Bracket Mismatches: ";
						break;
					case 3: 
						epu += "Semi-Colons right after a loop or conditional: ";
						break;
					case 4: 
						epu += "Switched Comparison and Assignment Operators: ";
						break;
					case 5:
						epu += "Misaligned tabbing/spacing: ";
						break;	
					}
					epu += ""+userErrors[i]+"\n";
						
				}
				
				indexFolders++;
				countFolders++;
			} else {
				indexFolders++;
			}
		}
		for(int i =0; i < errors.length; i++){
			epq += "\nQuestion "+ (i+1) + ": ";
			for(int j = 0; j < errors[i].length; j++){
				switch(j){
				case 0:
					epq += "Unclosed Scanners: ";
					break;
				case 1:
					epq += "Bracket Miscounts: ";
					break;
				case 2: 
					epq += "Bracket Mismatches: ";
					break;
				case 3: 
					epq += "Semi-Colons right after a loop or conditional: ";
					break;
				case 4: 
					epq += "Switched Comparison and Assignment Operators: ";
					break;
				case 5:
					epq += "Misaligned tabbing/spacing: ";
					break;	
				}
				epq += errors[i][j] + "\n";
			}
		}
		errorstats.println(epu);
		errorstats.println(epq);
		errorstats.close();
//		for(int i =0; i < Files.list(Paths.get("Submissions")).count(); i++){
//			String dirString = "Submissions\\"+(171+10*i);
//			
//			for(int j = 1; j <= Files.list(Paths.get(dirString)).count(); j++){
//				String fileString = dirString+"\\answer"+j+".txt";
//				System.out.println("\n\nFile "+fileString+"\n");
//				TextProcessor tp = new TextProcessor(fileString);
//				tp.checkScanner();
//				tp.checkBracketCount();
//				tp.checkBracketMatch();
//				tp.checkBadSemiColon();
//				tp.checkAssignment();
//				tp.checkTabbing(4);
//			}
//		}
//		
//		TextProcessor tp = new TextProcessor("sampleSub2.txt");
//		tp.checkScanner();
//		tp.checkBracketCount();
//		tp.checkBracketMatch();
//		tp.checkBadSemiColon();
//		tp.checkAssignment();
//		tp.checkTabbing(4);
//		

	}
	
	/*
	 * cleanQuotes goes through a file and changes all instances of double quotes ("") to single quotes ("). This should be turned on or off as needed. 
	 * The reason this method is here is because a strange consequence of saving the data we saved was that all the quotes got duplicated, which kind of 
	 * throws off one or two of the methods in the TextProcessor. To alleviate this, we can clean the input.  
	 * Within this method, there's also a section that saves the output to the file which is read in. The end result is that we overwrite the original
	 * file with a new one which has had extra quotes scrubbed from it. Because of this, this method should only be run ONCE per set of data. 
	 * If you are using this code with to analyze the data that came with it (in the Submissions folder), this data has already been scrubbed, so don't
	 * run this method!
	 */
	public static String cleanQuotes(InputStream fileIS, File file){
		StringBuilder sb = new StringBuilder(512);
		String returnString = "";
	    try {
	        Reader r = new InputStreamReader(fileIS, "UTF-8");
	        int c = 0;
	        while ((c = r.read()) != -1) {
	            sb.append((char) c);
	        }
	        returnString = sb.toString().replaceAll("\"\"", "\"");
	        PrintWriter pw = new PrintWriter(file);
	        pw.println(returnString);
	        pw.close();
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	    return returnString;
		
	}

}
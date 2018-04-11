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
	final static int numErrors = 8;

	public static void main(String[] args) throws IOException {
		boolean test = false;
		if(test){
			TextProcessor tp = new TextProcessor("testDocument.txt", true);
			tp.checkScanner();
			tp.checkBracketCount();
			tp.checkBracketMatch();
			tp.checkBadSemiColon();
			tp.checkAssignment(); 
			tp.checkTabbing(4); 
			tp.getCodeAmount();
		} else {
			
		System.out.println(System.getProperty("user.dir"));
		String[] folders2Check = { "5 Questions", "10 Questions" };
		PrintWriter[] errorstats = { new PrintWriter(new File("errorStats " + folders2Check[0] + ".txt")),
				new PrintWriter(new File("errorStats " + folders2Check[1] + ".txt")) };
		int indexFolders = 1;
		for (int x = 0; x < folders2Check.length; x++) {
			int countFolders = 0;
			String epu = "Errors per User:\n";
			String epq = "Errors per Question:\n";
			int[][] errors = new int[numQuestions][numErrors-1];
			System.out.println(folders2Check[x]);
			String initDirString = "Submissions\\" + folders2Check[x];
			while (countFolders < Files.list(Paths.get(initDirString)).count()) {
				String dirString = initDirString + "\\" + indexFolders;
				File dir = new File(dirString);
				if (dir.exists() && dir.isDirectory()) {
					int indexFiles = 1;
					int countFiles = 0;
					epu += "\n" + indexFolders + "\n";
					int[] userErrors = new int[numErrors];
					while (countFiles < Files.list(Paths.get(dirString)).count()) {
						String fileString = dirString + "\\answer" + indexFiles + ".txt";
						File file = new File(fileString);
						System.out.println(fileString);
						if (file.exists() && file.isFile()) {
							// Don't uncomment the next few lines unless you are
							// working with new data!
							// InputStream is = new FileInputStream(file);
							// cleanQuotes(is, file);
							// is.close();
							TextProcessor tp = new TextProcessor(fileString, true);
							int[] thisRun = { tp.checkScanner().size(), tp.checkBracketCount().size(),
									tp.checkBracketMatch().size(), tp.checkBadSemiColon().size(),
									tp.checkAssignment().size(), tp.checkTabbing(4).size(), tp.getCodeAmount() };
							userErrors[0] += thisRun[0];
							userErrors[1] += thisRun[1];
							userErrors[2] += thisRun[2];
							userErrors[3] += thisRun[3];
							userErrors[4] += thisRun[4];
							userErrors[5] += thisRun[5];
							userErrors[6] += thisRun[6]; // total cumulative
															// number of lines
															// written
							userErrors[7] = tp.associateConfidence(indexFolders, "Submissions\\confidence.csv", "Submissions\\submissions.csv");
							for (int i = 0; i < userErrors.length; i++) {
								if(i == 7){
									continue;
								}
								errors[indexFiles - 1][i] += thisRun[i];
							}
							System.out.println("");
							countFiles++;
							indexFiles++;

						} else {
							indexFiles++;
						}
					}
					double cumulativeErrors = 0.0;
					for (int i = 0; i < userErrors.length; i++) {
						switch (i) {
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
						case 6:
							epu += "Lines written: " + userErrors[i] + "\n";
							epu += "Average number of errors per line: ";
							break;
						case 7:
							epu += "Associated Confidence Value: " + userErrors[i] + "\n";
							break;
						}
						if (i == 6) {
							if (cumulativeErrors != 0)
								epu += "" + cumulativeErrors / userErrors[i] + "\n";
							else
								epu += "0\n";
						} else if (i == 7) {
							continue;
						} else {
							epu += "" + userErrors[i] + "\n";
							cumulativeErrors += userErrors[i];
						}

					}

					indexFolders++;
					countFolders++;
				} else {
					indexFolders++;
				}
			}
			for (int i = 0; i < errors.length; i++) {
				double cumulativeErrors = 0.0;
				epq += "\nQuestion " + (i + 1) + ": ";
				for (int j = 0; j < errors[i].length; j++) {
					switch (j) {
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
					case 6:
						epq += "Lines Written: " + errors[i][j] + "\n";
						epq += "Average number of errors per line: ";
						break;
					}
					if (j == 6) {
						if (cumulativeErrors != 0)
							epq += cumulativeErrors / errors[i][j] + "\n";
						else
							epq += "0\n";
					} else {
						epq += errors[i][j] + "\n";
						cumulativeErrors += errors[i][j];
					}
				}
			}
			errorstats[x].println(epu);
			errorstats[x].println(epq);
			errorstats[x].close();
		}
		}
		// for(int i =0; i < Files.list(Paths.get("Submissions")).count(); i++){
		// String dirString = "Submissions\\"+(171+10*i);
		//
		// for(int j = 1; j <= Files.list(Paths.get(dirString)).count(); j++){
		// String fileString = dirString+"\\answer"+j+".txt";
		// System.out.println("\n\nFile "+fileString+"\n");
		// TextProcessor tp = new TextProcessor(fileString);
		// tp.checkScanner();
		// tp.checkBracketCount();
		// tp.checkBracketMatch();
		// tp.checkBadSemiColon();
		// tp.checkAssignment();
		// tp.checkTabbing(4);
		// }
		// }
		//
		// TextProcessor tp = new TextProcessor("sampleSub2.txt");
		// tp.checkScanner();
		// tp.checkBracketCount();
		// tp.checkBracketMatch();
		// tp.checkBadSemiColon();
		// tp.checkAssignment();
		// tp.checkTabbing(4);
		//

	}

	/*
	 * cleanQuotes goes through a file and changes all instances of double
	 * quotes ("") to single quotes ("). This should be turned on or off as
	 * needed. The reason this method is here is because a strange consequence
	 * of saving the data we saved was that all the quotes got duplicated, which
	 * kind of throws off one or two of the methods in the TextProcessor. To
	 * alleviate this, we can clean the input. Within this method, there's also
	 * a section that saves the output to the file which is read in. The end
	 * result is that we overwrite the original file with a new one which has
	 * had extra quotes scrubbed from it. Because of this, this method should
	 * only be run ONCE per set of data. If you are using this code with to
	 * analyze the data that came with it (in the Submissions folder), this data
	 * has already been scrubbed, so don't run this method!
	 */
	public static String cleanQuotes(InputStream fileIS, File file) {
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
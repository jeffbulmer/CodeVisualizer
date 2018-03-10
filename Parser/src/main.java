import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class main {

	public static void main(String[] args) throws IOException {
		System.out.println(System.getProperty("user.dir"));
//		for(int i =0; i < Files.list(Paths.get("Submissions")).count(); i++){
//			String dirString = "Submissions\\"+(171+10*i);
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
		
		TextProcessor tp = new TextProcessor("sampleSub2.txt");
		tp.checkScanner();
		tp.checkBracketCount();
		tp.checkBracketMatch();
		tp.checkBadSemiColon();
		tp.checkAssignment();
		tp.checkTabbing(4);
		

	}

}
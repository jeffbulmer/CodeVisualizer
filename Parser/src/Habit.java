
public class Habit {

/*
 * attributes:
 * lineNumber: the line number on which the error occurs
 * errorMessage: the error message for the error
 * positionStart: the index of the first character at which the error occurs
 * positionEnd: the index after the last character at which the error occurs. 
 * NOTE: if you use substring(positionStart, positionEnd), you should get the exact occurrence of the error	
 */
	private int lineNumber;
	private String errorMessage;
	private int positionStart;
	private int positionEnd;
	
	public Habit(int lineNumber, String errorMessage) {
		this.lineNumber = lineNumber;
		this.errorMessage = errorMessage;
	}
	
	public Habit(int lineNumber, String errorMessage, int positionStart, int positionEnd){
		this.lineNumber = lineNumber;
		this.errorMessage = errorMessage;
		this.positionStart = positionStart;
		this.positionEnd = positionEnd;
	}
	
	public int getLineNumber(){
		return lineNumber;
	}
	
	public String getErrorMessage(){
		return errorMessage;
	}
	
	public int[] getPositions(){
		int[] positions = new int[2];
		positions[0] = positionStart;
		positions[1] = positionEnd;
		return positions;
	}
}

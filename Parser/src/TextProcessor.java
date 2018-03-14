import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TextProcessor {

	private File file;

	public TextProcessor() {
		// TODO Auto-generated constructor stub
	}

	public TextProcessor(String filename) {
		loadFile(filename);
	}

	public boolean loadFile(String name) {
		boolean rval = true;
		file = new File(name);
		try {
			Scanner scan = new Scanner(file);
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.print("File " + name + " not found.");
			rval = false;
		}
		return rval;
	}

	/*
	 * The following methods process an input text and check for various bad
	 * habits or errors The checks performed are as follows:
	 * 
	 * checkScanner(): This method checks the code for open scanners. It does
	 * this by parsing through the code and identifying all opened scanners by
	 * name. It then checks for instances where .close is called from those same
	 * scanners. If a scanner is declared, but no .close() is found to be called
	 * from that scanner, then the method will end by displaying a message
	 * noting that the scanner was opened, but not closed. Additionally, the
	 * line on which the scanner is first opened will be displayed.
	 * 
	 * checkBracketCount(): This method checks for open brackets, quotes and
	 * apostrophes. The methodology is similar to checkScanner. If the method
	 * finds one of the possible brackets it's searching for, it adds the
	 * lineIndex to a list, and removes the line index of the last instance if
	 * it finds a closed bracket. In the case of quotes and apostrophes, it
	 * keeps the line number as an integer, resetting it if it ever finds a
	 * second instance (which would indicate the closing of quotes or
	 * apostrophes) Note that this method ONLY checks the count, so it will not
	 * find cases where {} is used where [] should be used, or where "" is used
	 * instead of '', etc. This requires an additional method.
	 * 
	 * checkBracketMatch(): This method finds instances where '(','{','[' are
	 * mismatched. It does this by keeping a list of opened brackets in LIFO
	 * order, and then checking the last element anytime a bracket is closed. If
	 * a mismatch is found, the error is noted, and the last bracket removed, to
	 * avoid showing one bracket mismatching with multiple others at once.
	 * 
	 * checkBadSemiColon(): This method finds occurrences of semi-colons
	 * immediately following a conditional (if) statement, or a loop
	 * declaration. It does this by finding if statements and loop declarations,
	 * and then checking if the next character is a semi-colon. checking the
	 * next character is sufficient if all whitespaces are removed from the line
	 * first.
	 * 
	 * checkAssignment(): This method checks for cases where a comparison
	 * operator ("==") was used instead of an assignment operator ("="), and
	 * vice-versa. In order to do this, it checks all variables in scope to see
	 * if they are mistakenly being compared to a value instead of assigned a
	 * value. Booleans are handled specially, since "==" CAN occur in the
	 * assignment of a boolean. It then checks conditionals, and loop
	 * declarations, to ensure that variables are not being assigned, but are
	 * actually being compared. Here, for-loops are handled separately, since
	 * for loops may contain both a comparison, and an assignment, or multiple
	 * comparisons, if a boolean is used, which is legal (it just doesn't
	 * typically occur).
	 * 
	 * checkTabbing(): Takes integer parameter numSpaces This method checks that
	 * every line begins with the appropriate number of tabs/spaces. It does
	 * this by finding open brackets, and incrementing a level variable every
	 * time brackets are opened, and decrementing the same variable any time
	 * brackets are closed. For the sake of this method, we assume the brackets
	 * are correct (i.e. not mismatched). That's a different error, and
	 * shouldn't technically affect indentation conventions. Additionally, an
	 * extra tab/set of spaces is added if a loop is started, but no bracket is
	 * opened. This is the case of a single-line loop (works with conditionals,
	 * too). The numSpaces parameter exists because some people use spaces
	 * instead of tabs for indentation, but not everyone uses the same number of
	 * spaces. So, we can declare a number of spaces to look for here at each
	 * indentation level.
	 */
	public int checkScanner() {
		int open = 0;
		List<String> scanners = new ArrayList<String>();
		List<Integer> scannersPos = new ArrayList<Integer>();
		int errorCount = 0;

		try {
			Scanner scan = new Scanner(file);
			int lineIndex = 0;
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				Pattern pattern = Pattern.compile("(?![Scanner ]).*(?==)");
				if (line.contains("new Scanner")) {
					Matcher matcher = pattern.matcher(line);
					if (matcher.find()) {
						scanners.add(matcher.group(0).replace("Scanner", "").trim()); // sometimes,
																						// the
																						// capture
																						// group
																						// contains
																						// the
																						// full
																						// declaration,
																						// which
																						// we
																						// don't
																						// need,
																						// so
																						// we
																						// chop
																						// it
																						// away
																						// here.
						scannersPos.add(lineIndex);
						open++;
					}
				}
				int[] removals = new int[scanners.size()];
				int j = 0;
				for (int i = 0; i < scanners.size(); i++) {
					pattern = Pattern.compile(scanners.get(0) + ".close()");
					Matcher matcher = pattern.matcher(line);
					if (matcher.find()) {
						removals[j] = i;
						j++;
					}
				}
				for (int k = j - 1; k >= 0; k--) {
					scanners.remove(removals[k]);
					scannersPos.remove(removals[k]);
					open--;
				}
				lineIndex++;
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.print("File not found.");
		}
		for (int i = 0; i < scanners.size(); i++) {
			System.out.println("Scanner " + scanners.get(i) + " on line " + scannersPos.get(i) + " not closed");
			errorCount++;
		}
		return errorCount;
	}

	public int checkBracketCount() {
		ArrayList<Integer> parentheses = new ArrayList<Integer>();
		ArrayList<Integer> square = new ArrayList<Integer>();
		ArrayList<Integer> curly = new ArrayList<Integer>();
		int quote = 0;
		int apostrophe = 0;
		int errorCount = 0;

		try {
			Scanner scan = new Scanner(file);
			int lineIndex = 1;
			boolean quoted = false;
			boolean commented = false;
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				if (line.replaceAll("(\\s|\n)", "").startsWith("/*"))
					commented = true;
				if (line.replaceAll("(\\s|\n)", "").startsWith("*/") || line.replaceAll("(\\s|\n)", "").endsWith("*/"))
					commented = false;
				if (commented || line.replaceAll("(\\s|\n)", "").startsWith("//")) {
					lineIndex++;
					continue;
				}
				for (int i = 0; i < line.length(); i++) {
					switch (line.charAt(i)) {
					case '(':
						if (quoted) {
							break;
						}
						if (i != 0 && i != line.length() - 1 && line.charAt(i - 1) == '\''
								&& line.charAt(i + 1) == '\'') {
							break; // if the bracket is enclosed by apostrophes,
									// it's a character
						}
						parentheses.add(lineIndex);
						break;
					case '[':
						if (quoted) {
							break;
						}
						if (i != 0 && i != line.length() - 1 && line.charAt(i - 1) == '\''
								&& line.charAt(i + 1) == '\'') {
							break; // if the bracket is enclosed by apostrophes,
									// it's a character
						}
						square.add(lineIndex);
						break;
					case '{':
						if (quoted) {
							break;
						}
						if (i != 0 && i != line.length() - 1 && line.charAt(i - 1) == '\''
								&& line.charAt(i + 1) == '\'') {
							break; // if the bracket is enclosed by apostrophes,
									// it's a character
						}
						curly.add(lineIndex);
						break;
					case ')':
						if (quoted) {
							break;
						}
						if (i != 0 && i != line.length() - 1 && line.charAt(i - 1) == '\''
								&& line.charAt(i + 1) == '\'') {
							break; // if the bracket is enclosed by apostrophes,
									// it's a character
						}
						if (parentheses.size() > 0)
							parentheses.remove(parentheses.size() - 1);
						break;
					case ']':
						if (quoted) {
							break;
						}
						if (i != 0 && i != line.length() - 1 && line.charAt(i - 1) == '\''
								&& line.charAt(i + 1) == '\'') {
							break; // if the bracket is enclosed by apostrophes,
									// it's a character
						}
						if (square.size() > 0)
							square.remove(square.size() - 1);
						break;
					case '}':
						if (quoted) {
							break;
						}
						if (i != 0 && i != line.length() - 1 && line.charAt(i - 1) == '\''
								&& line.charAt(i + 1) == '\'') {
							break; // if the bracket is enclosed by apostrophes,
									// it's a character
						}
						if (curly.size() > 0)
							curly.remove(curly.size() - 1);
						break;
					case '"':
						if (i != 0 && line.charAt(i - 1) == '\\')
							break;
						else if (i != 0 && i != line.length() && line.charAt(i - 1) == '\''
								&& line.charAt(i + 1) == '\'')
							break;
						if (quote == 0) {
							quote = lineIndex;
						} else {
							quote = 0;
						}
						quoted = !quoted;
						break;
					case '\'':
						if (i != 0 && line.charAt(i - 1) == '\\')
							break;
						else if (apostrophe == 0)
							apostrophe = lineIndex;
						else
							apostrophe = 0;
						break;

					}
				}
				lineIndex++;
			}
			for (int i = 0; i < parentheses.size(); i++) {
				System.out.println("Unclosed parentheses on line " + parentheses.get(i));
				errorCount++;
			}
			for (int i = 0; i < square.size(); i++) {
				System.out.println("Unclosed square brackets on line " + square.get(i));
				errorCount++;
			}
			for (int i = 0; i < curly.size(); i++) {
				System.out.println("Unclosed curly brace on line " + curly.get(i));
				errorCount++;
			}

			if (quote != 0) {
				System.out.println("Unclosed quotation marks on line " + quote);
				errorCount++;
			}

			if (apostrophe != 0) {
				System.out.println("Unclosed apostrophes on line " + apostrophe);
				errorCount++;
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.print("File not found.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return errorCount;
	}

	public int checkBracketMatch() {
		ArrayList<Character> lastBracket = new ArrayList<Character>();
		ArrayList<Integer> lastBracketIndex = new ArrayList<Integer>();
		ArrayList<String> errors = new ArrayList<String>();
		try {
			Scanner scan = new Scanner(file);
			int lineIndex = 1;
			boolean quoted = false;
			boolean commented = false;
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				if (line.replaceAll("(\\s|\n)", "").startsWith("/*"))
					commented = true;
				if (line.replaceAll("(\\s|\n)", "").startsWith("*/") || line.replaceAll("(\\s|\n)", "").endsWith("*/"))
					commented = false;
				if (commented || line.replaceAll("(\\s|\n)", "").startsWith("//")) {
					lineIndex++;
					continue;
				}
				for (int i = 0; i < line.length(); i++) {
					switch (line.charAt(i)) {
					case '\"':
						if (i > 0 && i < line.length() && line.charAt(i - 1) == '\'' && line.charAt(i + 1) != '\'') {
							break;
						}
						quoted = !quoted;
						break;
					case '(':
						if (quoted) {
							break;
						}
						if (i != 0 && i != line.length() - 1 && line.charAt(i - 1) == '\''
								&& line.charAt(i + 1) == '\'') {
							break; // if the bracket is enclosed by apostrophes,
									// it's a character
						}
						lastBracket.add('(');
						lastBracketIndex.add(lineIndex);
						break;
					case '[':
						if (quoted) {
							break;
						}
						if (i != 0 && i != line.length() - 1 && line.charAt(i - 1) == '\''
								&& line.charAt(i + 1) == '\'') {
							break; // if the bracket is enclosed by apostrophes,
									// it's a character
						}
						lastBracket.add('[');
						lastBracketIndex.add(lineIndex);
						break;
					case '{':
						if (quoted) {
							break;
						}
						if (i != 0 && i != line.length() - 1 && line.charAt(i - 1) == '\''
								&& line.charAt(i + 1) == '\'') {
							break; // if the bracket is enclosed by apostrophes,
									// it's a character
						}
						lastBracket.add('{');
						lastBracketIndex.add(lineIndex);
						break;
					case ')':
						if (quoted) {
							break;
						}
						if (i != 0 && i != line.length() - 1 && line.charAt(i - 1) == '\''
								&& line.charAt(i + 1) == '\'') {
							break; // if the bracket is enclosed by apostrophes,
									// it's a character
						}
						if (lastBracket.size() > 0 && lastBracket.get(lastBracket.size() - 1) != '(') {
							/*
							 * If the found closing bracket does not match the
							 * bracket type of the last opening bracket, we have
							 * a mismatch. In this case, note the mismatch, and
							 * remove the last bracket from the list of
							 * brackets. Note that, in the case of a bracket
							 * miscount, this may result in all subsequent
							 * brackets being mismatched.
							 */
							errors.add("Bracket Mismatch: " + lastBracket.get(lastBracket.size() - 1)
									+ " matching with ')' on line " + lastBracketIndex.get(lastBracketIndex.size() - 1)
									+ " and line " + lineIndex);
						}
						if (lastBracket.size() > 0) {
							lastBracket.remove(lastBracket.size() - 1);
							lastBracketIndex.remove(lastBracketIndex.size() - 1);
						}
						break;
					case ']':
						if (quoted) {
							break;
						}
						if (i != 0 && i != line.length() - 1 && line.charAt(i - 1) == '\''
								&& line.charAt(i + 1) == '\'') {
							break; // if the bracket is enclosed by apostrophes,
									// it's a character
						}
						if (lastBracket.size() > 0 && lastBracket.get(lastBracket.size() - 1) != '[') {
							errors.add("Bracket Mismatch: " + lastBracket.get(lastBracket.size() - 1)
									+ " matching with ']' on line " + lastBracketIndex.get(lastBracketIndex.size() - 1)
									+ " and line " + lineIndex);
						}
						if (lastBracket.size() > 0) {
							lastBracket.remove(lastBracket.size() - 1);
							lastBracketIndex.remove(lastBracketIndex.size() - 1);
						}
						break;
					case '}':
						if (quoted) {
							break;
						}
						if (i != 0 && i != line.length() - 1 && line.charAt(i - 1) == '\''
								&& line.charAt(i + 1) == '\'') {
							break; // if the bracket is enclosed by apostrophes,
									// it's a character
						}
						if (lastBracket.size() > 0 && lastBracket.get(lastBracket.size() - 1) != '{') {
							errors.add("Bracket Mismatch: " + lastBracket.get(lastBracket.size() - 1)
									+ "matching with '}' on line " + lastBracketIndex.get(lastBracketIndex.size() - 1)
									+ " and line " + lineIndex);
						}
						if (lastBracket.size() > 0) {
							lastBracket.remove(lastBracket.size() - 1);
							lastBracketIndex.remove(lastBracketIndex.size() - 1);
						}
						break;
					}
				}
				lineIndex++;
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.print("File not found.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < errors.size(); i++) {
			System.out.println(errors.get(i));
		}
		return errors.size();
	}

	public int checkBadSemiColon() {

		ArrayList<String> errors = new ArrayList<String>();
		try {
			Scanner scan = new Scanner(file);
			int lineIndex = 0;
			while (scan.hasNextLine()) {
				String line = scan.nextLine().replaceAll("(\\s|\n)", ""); // removes
																			// all
																			// whitespace
																			// characters
																			// and
																			// newlines
																			// within
																			// a
																			// line
																			// (there
																			// shouldn't
																			// be
																			// any
																			// newlines
																			// anyway)
				Pattern patternIf = Pattern.compile("if\\(.*\\)"); // finds if
																	// statements.
				Matcher matcherIf = patternIf.matcher(line);
				if (matcherIf.find()) {
					for (int i = 0; i <= matcherIf.groupCount(); i++) {
						int nextidx = line.indexOf(matcherIf.group(i)) + matcherIf.group(i).length(); // finds
																										// the
																										// index
																										// of
																										// the
																										// first
																										// character
																										// after
																										// the
																										// matching
																										// group.
						if (line.length() > nextidx && line.charAt(nextidx) == ';') {
							errors.add("Semi-colon after conditional statement on line " + lineIndex);

						}
					}
				}
				Pattern patternLoop = Pattern.compile("(for\\(.*\\)|while\\(.*\\))"); // finds
																						// while
																						// and
																						// for
																						// loops
				Matcher matcherLoop = patternLoop.matcher(line);
				if (matcherLoop.find()) {
					for (int i = 0; i <= matcherLoop.groupCount(); i++) {
						if (i != 0 && matcherLoop.group(i).equals(matcherLoop.group(i - 1))) {
							lineIndex++;
							continue; // skips any groups that were mistakenly
										// matched more than once.
						}
						int nextidx = line.indexOf(matcherLoop.group(i)) + matcherLoop.group(i).length();
						String looptype = "";
						if (line.charAt(line.indexOf(matcherLoop.group(i))) == 'f')
							looptype = "for";
						else if (line.charAt(line.indexOf(matcherLoop.group(i))) == 'w')
							looptype = "while";
						if (nextidx < line.length() && line.charAt(nextidx) == ';') {
							errors.add("Semi-colon after " + looptype + " loop declaration on line " + lineIndex);
						}
					}
				}
				lineIndex++;
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.print("File not found.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < errors.size(); i++) {
			System.out.println(errors.get(i));
		}
		return errors.size();

	}

	public int checkAssignment() {
		int errorCount = 0;
		ArrayList<String> errors = new ArrayList<String>();
		ArrayList<String> vars = new ArrayList<String>();
		ArrayList<String> types = new ArrayList<String>();
		ArrayList<Integer> levels = new ArrayList<Integer>();
		int currLevel = 0;

		try {
			Scanner scan = new Scanner(file);
			int lineIndex = 1;
			boolean commented = false;
			boolean quoted = false;
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				if (line.replaceAll("(\\s|\n)", "").startsWith("/*"))
					commented = true;
				if (line.replaceAll("(\\s|\n)", "").startsWith("*/") || line.replaceAll("(\\s|\n)", "").endsWith("*/"))
					commented = false;
				if (commented || line.replaceAll("(\\s|\n)", "").startsWith("//")) {
					lineIndex++;
					continue;
				}
				for (int i = 0; i < line.length(); i++) {
					if (line.charAt(i) == '\"' || line.charAt(i) == '\'') {
						quoted = !quoted;
					}
					if (line.charAt(i) == '{' && !quoted) {
						currLevel++;
					} else if (line.charAt(i) == '}' && !quoted) {
						currLevel--;
						int[] removals = new int[vars.size()];
						for (int j = 0; j < vars.size(); j++) {
							if (levels.get(j) > currLevel) {
								removals[j] = 1;
							}
						}
						for (int k = removals.length - 1; k >= 0; k--) {
							if (removals[k] == 1) {
								vars.remove(k);
								types.remove(k);
								levels.remove(k);
							}
						}
					}
				}
				if (line.contains("=")) {
					Pattern pattern = Pattern.compile("(?<=\\().*(?=\\))"); // pattern
																			// that
																			// grabs
																			// everything
																			// within
																			// parentheses.
					Pattern singleSign = Pattern.compile("(?<!\\=|\\!|\\<|\\>|\\\'|\\\")=(?!=|\\\'|\\\")"); // pattern
																											// that
																											// grabs
																											// every
																											// instance
																											// of
																											// an
																											// assignment
																											// operator
																											// (single
																											// =)
					Pattern doubleSign = Pattern.compile("==");
					Pattern variableType = Pattern
							.compile("((Scanner|int|String|char|double|Scanner|float|long|boolean)(\\[\\])*(?!At|Of))"); // gets
					// the
					// type
					// of
					// a
					// variable
					Pattern variableName = Pattern.compile(".*?(?==)");
					String trimmedLine = line.trim();
					boolean loop = false;
					Matcher typeMatch = variableType.matcher(trimmedLine);
					if (typeMatch.find()) {
						if (trimmedLine.contains("for")) {
							loop = true;
						}
						trimmedLine = line.replaceAll("(for\\(|while\\()", "");
						if (typeMatch.start(0) == 0) {
							types.add(typeMatch.group(0));
							trimmedLine = trimmedLine.replace(typeMatch.group(0), "").trim();
							Matcher varMatch = variableName.matcher(trimmedLine);
							if (varMatch.find()) {
								vars.add(varMatch.group().trim());
								if (loop) {
									levels.add(currLevel + 1);
									loop = false;
								} else {
									levels.add(currLevel);
								}
							}
						}
					}
					for (int i = 0; i < vars.size(); i++) {
						Pattern varOnly = Pattern
								.compile("(?<![^ ])" + vars.get(i).replaceAll("[\\[\\]]", "") + "(?![^ ])");
						Matcher matchVar = varOnly.matcher(line);
						if (matchVar.find()) {
							if (line.contains("while") || line.contains("if") || line.contains("for")) { // loops
																											// and
																											// conditionals
																											// handled
																											// separately.
								continue;
							}
							line = line.substring(matchVar.end(0)).trim();
							if (line.startsWith("==")) {
								errors.add(
										"Comparison on line " + lineIndex + ". This should be variable assignment (=)");
							} else if (types.get(i).equals("boolean") && singleSign.matcher(line).groupCount() > 1) {
								errors.add("Assignment (=) after the initial assignment of the boolean variable "
										+ vars.get(i) + " on line " + lineIndex
										+ ". This should be a comparison (==).");
							}

						}
					}
					if (line.replaceAll("(\\s|\n)", "").contains("if(")
							|| line.replaceAll("(\\s|\n)", "").contains("while(")) { // checks
																						// for
																						// a
																						// conditional
																						// or
																						// a
																						// while
																						// loop.
						Matcher matcher = pattern.matcher(line);
						if (matcher.find()) {
							String condition = matcher.group().replaceAll("(\\s|\n)", "");
							String[] conditionParts = condition.split("(\\|\\|)|(\\&\\&)"); // splits
																							// the
																							// condition
																							// at
																							// instances
																							// of
																							// &&
																							// or
																							// ||
							Pattern firstComparator = pattern.compile(".*?(?==)");
							Pattern secondComparator = pattern.compile("([^=]*)$");
							for (int i = 0; i < conditionParts.length; i++) {
								boolean isString = false;
								Matcher fCMatcher = firstComparator.matcher(conditionParts[i]);
								Matcher sCMatcher = secondComparator.matcher(conditionParts[i]);
								if (fCMatcher.find() && sCMatcher.find()) {
									String fC = fCMatcher.group();
									String sC = sCMatcher.group();
									if (fC.contains("\"") || sC.contains("\"")) { // if
																					// either
																					// match
																					// contains
																					// quotes,
																					// we
																					// have
																					// a
																					// String.
										isString = true;
									} else {
										innerloop: for (int k = 0; k < vars.size(); k++) {
											if (fC.trim().equals(vars.get(k))) {
												if (types.get(k).equals("String") || types.get(k).equals("Scanner")) {
													isString = true;
													break innerloop;
												}
											}
											if (sC.trim().equals(vars.get(k))) {
												if (types.get(k).equals("String") || types.get(k).equals("Scanner")) {
													isString = true;
													break innerloop;
												}
											}
										}

									}
								}

								if (singleSign.matcher(conditionParts[i]).find()) {
									if (isString) {
										errors.add("Variable assignment (=) on line " + lineIndex
												+ ". This is a conditional statement on Strings, so this should be a comparison using .equals.");
										isString = false;
									}
									errors.add("Variable assignment (=) on line " + lineIndex
											+ ". This is a conditional statement, so this should be a comparison (==).");
								} else if (doubleSign.matcher(conditionParts[i]).find() && isString) {
									errors.add("Comparison (==) on line " + lineIndex
											+ ", but this is a conditional statement on Strings, so this should be a comparison using .equals.");
									isString = false;
								}
							}
						}
					}
					if (line.replaceAll("(\\s|\n)", "").contains("for(")) { // handles
																			// for
																			// loops
																			// specially.
						Matcher matcher = pattern.matcher(line);
						if (matcher.find()) {
							String[] conditionParts = matcher.group().split(";");
							if (conditionParts.length < 3) {
								String[] checkComma = matcher.group().split(",");
								String[] checkColon = matcher.group().split(":");

								if (checkComma.length == 3) {
									errors.add("Wrong Separator used for for loop on line " + lineIndex
											+ ". You've used commas (,), but should be using semi-colons (;)");
								} else if (checkColon.length == 3) {
									errors.add("Wrong Separator used for for loop on line " + lineIndex
											+ ". You've used colons (:), but should be using semi-colons (;)");
								} else {
									errors.add("For loops must have 3 parts. Your for loop on line " + lineIndex
											+ " has only " + Math.max(checkColon.length,
													Math.max(conditionParts.length, checkComma.length)));
								}
							} else {
								if (conditionParts[0].contains("=")) { // check
																		// the
																		// first
																		// part
																		// of
																		// the
																		// for
																		// loop
									if (conditionParts[0].trim().startsWith("boolean")) {
										String pared = conditionParts[0].replaceAll("boolean", "");
										Matcher getVar = Pattern.compile(".*(?=^([^=]+))").matcher(pared);
										if (getVar.find()) {
											vars.add(getVar.group().trim());
											types.add("boolean");
											levels.add(currLevel);
										}
										pared = conditionParts[0].replace(".*(?=^([^=]+))", "");
										if (pared.trim().startsWith("==")) {
											errors.add("Comparison (==) on line " + lineIndex
													+ " within the first section of the for statement. This should be a variable assignment (=)");
										} else {
											pared = pared.replaceFirst("=", "");
											if (singleSign.matcher(pared).find()) {
												errors.add(
														"Variable assignment (=) within the definition of initial boolean statement. This should be a comparison (==).");
											}
										}
									} else if (conditionParts[0].contains("==")) {
										errors.add("Comparison (==) on line " + lineIndex
												+ " within the first section of the for statement. This should be a variable assignment (=)");
									}
								}
								if (conditionParts[1].contains("=")) {
									Pattern firstComparator = pattern.compile(".*?(?==)");
									Pattern secondComparator = pattern.compile("([^=]*)$");
									boolean isString = false;
									Matcher fCMatcher = firstComparator.matcher(conditionParts[1]);
									Matcher sCMatcher = secondComparator.matcher(conditionParts[1]);
									if (fCMatcher.find() && sCMatcher.find()) {
										String fC = fCMatcher.group();
										String sC = sCMatcher.group();
										if (fC.contains("\"") || sC.contains("\"")) { // if
																						// either
																						// match
																						// contains
																						// quotes,
																						// we
																						// have
																						// a
																						// String.
											isString = true;
										} else {
											innerloop: for (int k = 0; k < vars.size(); k++) {
												if (fC.trim().equals(vars.get(k))) {
													if (types.get(k).equals("String")
															|| types.get(k).equals("Scanner")) {
														isString = true;
														break innerloop;
													}
												}
												if (sC.trim().equals(vars.get(k))) {
													if (types.get(k).equals("String")
															|| types.get(k).equals("Scanner")) {
														isString = true;
														break innerloop;
													}
												}
											}
										}
									}
									if (types.size() > 0 && types.get(types.size() - 1).equals("boolean")) {
										String pared = conditionParts[1].replace(".*(?=^([^=]+))", "");
										if (!(pared.trim().startsWith("==") || pared.trim().startsWith("!="))) {
											errors.add("Assignment (=) on line " + lineIndex
													+ " in the second section of the for statement. This should be a comparison (==)");
										} else {
											pared = pared.substring(2);
											if (singleSign.matcher(pared).find()) {
												errors.add(
														"Variable assignment (=) within the definition of compared boolean statement. This should be a comparison (==).");
											}
										}
									} else if (singleSign.matcher(conditionParts[1]).find() && !isString) {
										errors.add("Assignment (=) on line " + lineIndex
												+ " in the second section of the for statement. This should be a comparison (==)");
									} else if (singleSign.matcher(conditionParts[1]).find()) {
										errors.add("Assignment (=) on line " + lineIndex
												+ " in the second section of the for statement, and the comparators are Strings, so this should be .equals() .");
									} else if (doubleSign.matcher(conditionParts[1]).find() && isString) {
										errors.add("Comparison (==) on line " + lineIndex
												+ " in the second section of the for statement, but the comparators are Strings, so this should be .equals() .");
									}
								}
							}
						}
					}
				}
				lineIndex++;
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.print("File not found.");
		}
		for (int i = 0; i < errors.size(); i++) {
			System.out.println(errors.get(i));
			errorCount++;
		}
		return errorCount;
	}

	public int checkTabbing(int numSpaces) { // #dablife #maverickmerch
												// #itslitfam
		ArrayList<String> errors = new ArrayList<String>();
		int errorCount = 0;
		int level = 0;
		String opens = "{[(";
		String closeds = "}])";
		boolean loop = false;
		try {
			Scanner scan = new Scanner(file);
			int lineIndex = 1;
			boolean quoted = false;
			boolean commented = false;
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				String tabs = "";
				String spaces = "";
				if (line.replaceAll("(\\s|\n)", "").startsWith("/*"))
					commented = true;
				if (line.replaceAll("(\\s|\n)", "").startsWith("*/") || line.replaceAll("(\\s|\n)", "").endsWith("*/"))
					commented = false;
				if (commented || line.replaceAll("(\\s|\n)", "").startsWith("//") || line.trim().equals("")) {
					lineIndex++;
					continue;
				}
				if (line.trim().startsWith("}") || line.trim().startsWith(")") || line.trim().startsWith("]"))
					level--;
				for (int i = 0; i < level; i++) {
					tabs += "\t";
					for (int j = 0; j < numSpaces; j++) {
						spaces += " ";
					}
				}
				if (loop) { // handles the extra tab after a conditional or loop
							// declaration.
					tabs += "\t";
					for (int j = 0; j < numSpaces; j++) {
						spaces += " ";
					}
					loop = !loop;
				}

				if ((line.trim().startsWith("while") || line.trim().startsWith("if") || line.trim().startsWith("for"))
						&& !line.trim().endsWith("{") && !line.trim().endsWith(";"))
					loop = true;
				if (!line.startsWith(tabs) && !line.startsWith(spaces) && !commented) {

					if (level != 1) {
						errors.add("Incorrect indentation on line " + lineIndex + ". Should have " + level
								+ " tabs (or " + numSpaces * level + " spaces)");
					} else if (numSpaces == 1) {
						errors.add("Incorrect indentation on line " + lineIndex + ". Should have " + level + " tab (or "
								+ numSpaces + " space)");
					} else {
						errors.add("Incorrect indentation on line " + lineIndex + ". Should have " + level + " tab (or "
								+ numSpaces + " spaces)");
					}
				}
				for (int i = 0; i < line.length(); i++) {
					if (line.charAt(i) == '\"' || line.charAt(i) == '\'') {
						quoted = !quoted;
					}
					if (opens.contains("" + line.charAt(i)) && !quoted) {
						level++;
					} else if (closeds.contains("" + line.charAt(i)) && !quoted) {
						level--;
					}
				}
				lineIndex++;
			}
		} catch (IOException e) {

		}
		for (int i = 0; i < errors.size(); i++) {
			System.out.println(errors.get(i));
			errorCount++;
		}
		return errorCount;
	}

}

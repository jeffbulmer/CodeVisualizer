
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.*;
import javafx.animation.Animation.Status;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * AnimateFile.java: A program to animate keystroke data.
 * <p>
 * AnimateFile is a program to animate keystroke data, collected from Computer
 * Science students at the University of British Columbia Okanagan.
 * <p>
 * Attributes:<br>
 * 
 * <p>
 * 
 * @author Angie Pinchbeck
 *         <p>
 * @version 1.0v<br>
 *          Date Created: 10/02/2018<br>
 *          Last Modified: 15/03/2018<br>
 *          <p>
 * 
 * 
 */

public class AnimateFile extends Application {

	Stage pStage;
	BorderPane bp;
	ScrollPane textArea;
	Text animationText;
	int[] codeToAnimate;
	String currentCode;
	Timeline timeline;
	Slider slider;
	Status status;
	
	static String prevPrint;
	static String prevPrint2;
	static int printCounter;

	@Override
	public void start(Stage pStage) {
		// Instantiate vars and build the scene
		this.pStage = pStage;
		bp = new BorderPane();
		Scene scene = new Scene(bp, 500, 300);
		animationText = new Text();
		textArea = new ScrollPane();
		textArea.getStyleClass().add("noborder-scroll-pane");
		textArea.setContent(animationText);
		textArea.setFitToWidth(true);
		textArea.setFitToHeight(true);
		bp.setCenter(textArea);
		bp.setTop(makeMenuBar());
		timeline = new Timeline();
		pStage.setScene(scene);
		pStage.setTitle("Show me the words");
		// pStage.setMaximized(true);
		pStage.show();

	}

	/**
	 * makeMenuBar() is a method to create the menu bar at the top of the GUI.
	 * <p>
	 * Currently only has a file menu with the options to open and exit.
	 * 
	 * @return VBox The menu bar.
	 */
	public VBox makeMenuBar() {
		VBox vb = new VBox();
		MenuBar menuBar = new MenuBar();

		Menu menuFile = new Menu("File");

		MenuItem menuFileExit = new MenuItem("Exit");
		menuFileExit.setOnAction(e -> {
			Platform.exit();
		});
		menuFileExit.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN));

		MenuItem menuFileOpen = new MenuItem("Open");
		menuFileOpen.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();

			// Set extension filter
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
			fileChooser.getExtensionFilters().add(extFilter);

			// Show open file dialog
			File file = fileChooser.showOpenDialog(pStage);

			// Clear whatever is already happening

			// What to do when a file is opened
			if (file != null) {
				timeline = new Timeline();
				status = timeline.getStatus();
				codeToAnimate = readFile(file);
				animationText.setText("");
				bp.setBottom(makeMediaBar());
			}
		});
		menuFileOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));

		menuFile.getItems().addAll(menuFileOpen, menuFileExit);
		menuBar.getMenus().addAll(menuFile);

		vb.getChildren().add(menuBar);

		return vb;
	}

	/**
	 * makeMediaBar() is a method that creates the play button and scroll bar.
	 * 
	 * @return The media bar as an HBox.
	 */
	public HBox makeMediaBar() {
		HBox mediaBar = new HBox();
		mediaBar.setAlignment(Pos.CENTER);
		mediaBar.setPadding(new Insets(5, 10, 5, 10));

		/*
		 * Create the slider bar at the bottom with a Listener.
		 */
		slider = new Slider(0, codeToAnimate.length, 0);
		slider.setPrefWidth((.75) * pStage.getWidth());
		currentCode = "";
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				int[] codeToAnimateNow = Arrays.copyOfRange(codeToAnimate, 0, new_val.intValue());
				// translateKeyCodes returns a String representation of the code at any given
				// time.
				currentCode = translateKeyCodes(codeToAnimateNow);
				animationText.setText(currentCode);
			}
		});

		/*
		 * Create the Play button. Note that the Listener for the slider bar appears
		 * again on the inside of the KeyFrame animation so that a user may slide to any
		 * point in the animation while it's currently playing.
		 */
		final Button playButton = new Button(">");
		playButton.setOnAction(e -> {
			if (timeline.getStatus() == Status.RUNNING) {
				playButton.setText("||");
				timeline.pause();
			} else {
				playButton.setText("||");
				final IntegerProperty i = new SimpleIntegerProperty(0);
				i.set(currentCode.length());
				KeyFrame keyFrame = new KeyFrame(Duration.seconds(.3), event -> {
					if (i.get() > codeToAnimate.length) {
						// If we're at the end of the code, stop the animation.
						timeline.stop();
					} else {
						int[] codeToAnimateNow = Arrays.copyOfRange(codeToAnimate, 0, i.intValue() + 10);
						// translateKeyCodes returns a String representation of the code at any given
						// time.
						currentCode = translateKeyCodes(codeToAnimateNow);
						animationText.setText(currentCode);
						slider.setValue(i.intValue());
						i.set(i.get() + 1);
						slider.valueProperty().addListener(new ChangeListener<Number>() {
							public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
								int[] codeToAnimateNow = Arrays.copyOfRange(codeToAnimate, 0, new_val.intValue());
								currentCode = translateKeyCodes(codeToAnimateNow);
								animationText.setText(currentCode);
								i.set(new_val.intValue());
							}
						});
					}
				});
				timeline.getKeyFrames().add(keyFrame);
				timeline.setCycleCount(Animation.INDEFINITE);
				timeline.play();
			}
		});

		mediaBar.getChildren().addAll(playButton, slider);
		return mediaBar;
	}

	/**
	 * translateKeycodes() is a method to take in an array of Javascript int values
	 * and translate it into the String that is to be animated.
	 * <p>
	 * The method builds an ArrayList of StringBuilder objects, as this was the
	 * easiest data structure to navigate when it came to considering arrow keys.
	 * Each object is one line of input code, separated by the Enter keycode, 13.
	 * 
	 * @param keycodes
	 *            int[]: The array of ints to be animated
	 * @return The String representation of the Integer keycodes
	 */
	public static String translateKeyCodes(int[] keycodes) {

		ArrayList<StringBuilder> res = new ArrayList<StringBuilder>();
		StringBuilder currentString = new StringBuilder();
		res.add(currentString);
		int currentLine = 0;
		int cursor = 0; // tracks the index within the currentLine
		boolean isCapsOn = false;

		for (int i = 0; i < keycodes.length; i++) {
			if (keycodes[i] == 20) { // Caps Lock
				isCapsOn = (isCapsOn) ? false : true;
			} else if (keycodes[i] == 8) { // Backspace
				if (cursor == 0 && currentLine == 0) {
					continue;
				} else if (cursor == 0) {
					StringBuilder toAppend = currentString;
					currentLine--;
					currentString = res.get(currentLine);
					cursor = res.get(currentLine).length();
					currentString.append(toAppend);
					res.remove(currentLine + 1);
				} else {
					currentString.deleteCharAt(--cursor);
				}
			} else if (keycodes[i] == 37) { // Left Arrow
				if (cursor == 0 && currentLine == 0) {
					continue;
				} else if (cursor == 0) {
					currentLine--;
					currentString = res.get(currentLine);
					cursor = res.get(currentLine).length();
				} else {
					cursor--;
				}
			} else if (keycodes[i] == 39) { // Right Arrow
				sopl("YES, " + keycodes[i]);
				if (cursor == res.get(currentLine).length() && currentLine == res.size()) {
					continue;
				} else if (++cursor < res.get(currentLine).length() - 1) {
					continue;
				} else {
					currentLine++;
					currentString = res.get(currentLine);
					cursor = 0;
				}
			} else if (keycodes[i] == 38) { // Up Arrow
				if (currentLine == 0) {
					cursor = 0;
				} else {
					currentLine--;
					currentString = res.get(currentLine);
					if (cursor > res.get(currentLine).length()) {
						cursor = res.get(currentLine).length();
					}
				}
			} else if (keycodes[i] == 40) { // Down Arrow
				if (currentLine == res.size() - 1) {
					cursor = res.get(currentLine).length();
				} else {
					currentLine++;
					currentString = res.get(currentLine);
					if (cursor > res.get(currentLine).length()) {
						cursor = res.get(currentLine).length();
					}
				}
			} else if (keycodes[i] == 13) { // Enter
				String toAppend = currentString.substring(cursor);
				res.set(currentLine++, currentString.delete(cursor, currentString.length()));
				cursor = 0;
				currentString = new StringBuilder();
				currentString.append(toAppend);
				res.add(currentLine, currentString);
				continue;
			} else if (keycodes[i] == 32) { // Space
				currentString.insert(cursor++, " ");
			} else if (keycodes.length > 1 && i > 0 && keycodes[i - 1] == 16) {
				if (keycodes[i] == 49) { // Regular keys with Shift held down start here
					currentString.insert(cursor++, "!");
				} else if (keycodes[i] == 50) {
					currentString.insert(cursor++, "@");
				} else if (keycodes[i] == 51) {
					currentString.insert(cursor++, "#");
				} else if (keycodes[i] == 52) {
					currentString.insert(cursor++, "$");
				} else if (keycodes[i] == 53) {
					currentString.insert(cursor++, "%");
				} else if (keycodes[i] == 54) {
					currentString.insert(cursor++, "^");
				} else if (keycodes[i] == 55) {
					currentString.insert(cursor++, "&");
				} else if (keycodes[i] == 56) {
					currentString.insert(cursor++, "*");
				} else if (keycodes[i] == 57) {
					currentString.insert(cursor++, "(");
				} else if (keycodes[i] == 48) {
					currentString.insert(cursor++, ")");
				} else if (keycodes[i] == 65) {
					currentString.insert(cursor++, "A");
				} else if (keycodes[i] == 66) {
					currentString.insert(cursor++, "B");
				} else if (keycodes[i] == 67) {
					currentString.insert(cursor++, "C");
				} else if (keycodes[i] == 68) {
					currentString.insert(cursor++, "D");
				} else if (keycodes[i] == 69) {
					currentString.insert(cursor++, "E");
				} else if (keycodes[i] == 70) {
					currentString.insert(cursor++, "F");
				} else if (keycodes[i] == 71) {
					currentString.insert(cursor++, "G");
				} else if (keycodes[i] == 72) {
					currentString.insert(cursor++, "H");
				} else if (keycodes[i] == 73) {
					currentString.insert(cursor++, "I");
				} else if (keycodes[i] == 74) {
					currentString.insert(cursor++, "J");
				} else if (keycodes[i] == 75) {
					currentString.insert(cursor++, "K");
				} else if (keycodes[i] == 76) {
					currentString.insert(cursor++, "L");
				} else if (keycodes[i] == 77) {
					currentString.insert(cursor++, "M");
				} else if (keycodes[i] == 78) {
					currentString.insert(cursor++, "N");
				} else if (keycodes[i] == 79) {
					currentString.insert(cursor++, "O");
				} else if (keycodes[i] == 80) {
					currentString.insert(cursor++, "P");
				} else if (keycodes[i] == 81) {
					currentString.insert(cursor++, "Q");
				} else if (keycodes[i] == 82) {
					currentString.insert(cursor++, "R");
				} else if (keycodes[i] == 83) {
					currentString.insert(cursor++, "S");
				} else if (keycodes[i] == 84) {
					currentString.insert(cursor++, "T");
				} else if (keycodes[i] == 85) {
					currentString.insert(cursor++, "U");
				} else if (keycodes[i] == 86) {
					currentString.insert(cursor++, "V");
				} else if (keycodes[i] == 87) {
					currentString.insert(cursor++, "W");
				} else if (keycodes[i] == 88) {
					currentString.insert(cursor++, "X");
				} else if (keycodes[i] == 89) {
					currentString.insert(cursor++, "Y");
				} else if (keycodes[i] == 90) {
					currentString.insert(cursor++, "Z");
				} else if (keycodes[i] == 219) {
					currentString.insert(cursor++, "{");
				} else if (keycodes[i] == 221) {
					currentString.insert(cursor++, "}");
				} else if (keycodes[i] == 189) {
					currentString.insert(cursor++, "_");
				} else if (keycodes[i] == 187) {
					currentString.insert(cursor++, "+");
				} else if (keycodes[i] == 186) {
					currentString.insert(cursor++, ":");
				} else if (keycodes[i] == 222) {
					currentString.insert(cursor++, "\"");
				} else if (keycodes[i] == 220) {
					currentString.insert(cursor++, "|");
				} else if (keycodes[i] == 188) {
					currentString.insert(cursor++, "<");
				} else if (keycodes[i] == 190) {
					currentString.insert(cursor++, ">");
				} else if (keycodes[i] == 191) {
					currentString.insert(cursor++, "?");
				} else if (keycodes[i] == 9) {
					currentString.insert(cursor++, "\t");
				}
			} else if (keycodes[i] == 48) { // Regular keys without Shift start here
				currentString.insert(cursor++, "0");
			} else if (keycodes[i] == 48) {
				currentString.insert(cursor++, "1");
			} else if (keycodes[i] == 49) {
				currentString.insert(cursor++, "2");
			} else if (keycodes[i] == 50) {
				currentString.insert(cursor++, "3");
			} else if (keycodes[i] == 51) {
				currentString.insert(cursor++, "4");
			} else if (keycodes[i] == 52) {
				currentString.insert(cursor++, "5");
			} else if (keycodes[i] == 53) {
				currentString.insert(cursor++, "6");
			} else if (keycodes[i] == 54) {
				currentString.insert(cursor++, "7");
			} else if (keycodes[i] == 55) {
				currentString.insert(cursor++, "8");
			} else if (keycodes[i] == 56) {
				currentString.insert(cursor++, "9");
			} else if (keycodes[i] == 65) {
				char toInsert = isCapsOn ? 'A' : 'a';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 66) {
				char toInsert = isCapsOn ? 'B' : 'b';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 67) {
				char toInsert = isCapsOn ? 'C' : 'c';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 68) {
				char toInsert = isCapsOn ? 'D' : 'd';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 69) {
				char toInsert = isCapsOn ? 'E' : 'e';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 70) {
				sopl("entered 70");
				char toInsert = isCapsOn ? 'F' : 'f';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 71) {
				char toInsert = isCapsOn ? 'G' : 'g';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 72) {
				char toInsert = isCapsOn ? 'H' : 'h';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 73) {
				char toInsert = isCapsOn ? 'I' : 'i';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 74) {
				char toInsert = isCapsOn ? 'J' : 'j';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 75) {
				char toInsert = isCapsOn ? 'K' : 'k';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 76) {
				char toInsert = isCapsOn ? 'L' : 'l';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 77) {
				char toInsert = isCapsOn ? 'M' : 'm';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 78) {
				char toInsert = isCapsOn ? 'N' : 'n';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 79) {
				char toInsert = isCapsOn ? 'O' : 'o';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 80) {
				char toInsert = isCapsOn ? 'P' : 'p';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 81) {
				char toInsert = isCapsOn ? 'Q' : 'q';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 82) {
				char toInsert = isCapsOn ? 'R' : 'r';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 83) {
				char toInsert = isCapsOn ? 'S' : 's';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 84) {
				char toInsert = isCapsOn ? 'T' : 't';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 85) {
				char toInsert = isCapsOn ? 'U' : 'u';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 86) {
				char toInsert = isCapsOn ? 'V' : 'v';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 87) {
				char toInsert = isCapsOn ? 'W' : 'w';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 88) {
				char toInsert = isCapsOn ? 'X' : 'x';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 89) {
				char toInsert = isCapsOn ? 'Y' : 'y';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 90) {
				char toInsert = isCapsOn ? 'Z' : 'z';
				currentString.insert(cursor++, toInsert);
			} else if (keycodes[i] == 219) {
				currentString.insert(cursor++, "[");
			} else if (keycodes[i] == 221) {
				currentString.insert(cursor++, "]");
			} else if (keycodes[i] == 9) {
				currentString.insert(cursor++, "\t");
			} else if (keycodes[i] == 189) {
				currentString.insert(cursor++, "-");
			} else if (keycodes[i] == 187) {
				currentString.insert(cursor++, "=");
			} else if (keycodes[i] == 186) {
				currentString.insert(cursor++, ";");
			} else if (keycodes[i] == 222) {
				currentString.insert(cursor++, "\'");
			} else if (keycodes[i] == 220) {
				currentString.insert(cursor++, "\\");
			} else if (keycodes[i] == 188) {
				currentString.insert(cursor++, ",");
			} else if (keycodes[i] == 190) {
				currentString.insert(cursor++, ".");
			} else if (keycodes[i] == 191) {
				currentString.insert(cursor++, "/");
			} else if (keycodes[i] == 9) {
				currentString.insert(cursor++, "\t");
			}
			res.set(currentLine, currentString);
			String currPrint2 = i + ": k" + keycodes[i] + ", cursor: " + cursor + ", res size: " + res.size()
					+ ", current line: " + currentLine + ", current String: " + currentString;
			//if (i > 55)
				sopl(currPrint2);

		}

		String result = "";
		for (int i = 0; i < res.size(); i++) {
			result += res.get(i) + "\n";
		}

		sopl(result);
		return result;

	}

	/**
	 * readFile is a method to read in a file from disk and return it in the form of
	 * a String
	 * <p>
	 * Note the portions of the code for this method came from the blog Java-Buddy
	 * <p>
	 * 
	 * @web http://java-buddy.blogspot.com/
	 * @param file
	 *            The file to be read
	 * @return String
	 */
	private int[] readFile(File file) {
		StringBuilder stringBuffer = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String text;
			while ((text = bufferedReader.readLine()) != null) {
				stringBuffer.append(text);
			}

		} catch (FileNotFoundException ex) {
			Logger.getLogger(AnimateFile.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(AnimateFile.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException ex) {
				Logger.getLogger(AnimateFile.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		String[] keycodeStrings = stringBuffer.toString().split(",");
		int[] keycodes = new int[keycodeStrings.length];
		for (int i = 0; i < keycodeStrings.length; i++) {
			try {
				keycodes[i] = Integer.valueOf(keycodeStrings[i]);
			} catch (NumberFormatException e) {
				System.err.println(e + ": The problem came from " + keycodeStrings[i]);
				break;
			}

		}
		return keycodes;
	}

	/**
	 * Main method should not be accessed unless something else fails.
	 * <p>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}

	/**
	 * The following methods are various forms of shortening System.out.print()
	 * methods for ease of decoding.
	 * <p>
	 * 
	 * @author Angie Pinchbeck
	 * 
	 * @param s
	 *            The generic to be printed (could be String, int, etc)
	 * @param grid
	 *            The generic array to be printed; these methods handle 1D and 2D
	 *            arrays.
	 */

	public static void sop(int[] grid) {
		for (int i = 0; i < grid.length; i++) {
			System.out.print(grid[i] + " ");
		}
		System.out.println();
	}

	public static <E> void sopa(E[][] grid) {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				System.out.print(grid[i][j] + " ");
			}
			System.out.println();
		}
	}

	public static void sopl() {
		System.out.println();
	}

	public static <E> void sopl(E s) {
		System.out.println(s);
	}

	public static <E> void sop(E s) {
		System.out.print(s);
	}

}
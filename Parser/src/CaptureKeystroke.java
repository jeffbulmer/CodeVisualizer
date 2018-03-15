import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @web http://java-buddy.blogspot.com/
 */
public class CaptureKeystroke extends Application {

	Stage pStage;
	ArrayList<Integer> keystrokes;

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) {
		this.pStage = primaryStage;

		BorderPane bp = new BorderPane();
		TextArea textArea = new TextArea();
		keystrokes = new ArrayList<Integer>();

		textArea.setOnKeyPressed(e -> {
			KeyCode key = e.getCode();
			if (key == KeyCode.A)
				keystrokes.add(65);
			else if (key == KeyCode.B)
				keystrokes.add(66);
			else if (key == KeyCode.C)
				keystrokes.add(67);
			else if (key == KeyCode.D)
				keystrokes.add(68);
			else if (key == KeyCode.E)
				keystrokes.add(69);
			else if (key == KeyCode.F)
				keystrokes.add(70);
			else if (key == KeyCode.G)
				keystrokes.add(71);
			else if (key == KeyCode.H)
				keystrokes.add(72);
			else if (key == KeyCode.I)
				keystrokes.add(73);
			else if (key == KeyCode.J)
				keystrokes.add(74);
			else if (key == KeyCode.K)
				keystrokes.add(75);
			else if (key == KeyCode.L)
				keystrokes.add(76);
			else if (key == KeyCode.M)
				keystrokes.add(77);
			else if (key == KeyCode.N)
				keystrokes.add(78);
			else if (key == KeyCode.O)
				keystrokes.add(79);
			else if (key == KeyCode.P)
				keystrokes.add(80);
			else if (key == KeyCode.Q)
				keystrokes.add(81);
			else if (key == KeyCode.R)
				keystrokes.add(82);
			else if (key == KeyCode.S)
				keystrokes.add(83);
			else if (key == KeyCode.T)
				keystrokes.add(84);
			else if (key == KeyCode.U)
				keystrokes.add(85);
			else if (key == KeyCode.V)
				keystrokes.add(86);
			else if (key == KeyCode.W)
				keystrokes.add(87);
			else if (key == KeyCode.X)
				keystrokes.add(88);
			else if (key == KeyCode.Y)
				keystrokes.add(89);
			else if (key == KeyCode.Z)
				keystrokes.add(90);
			else if (key == KeyCode.ESCAPE)
				keystrokes.add(27);
			else if (key == KeyCode.F1)
				keystrokes.add(112);
			else if (key == KeyCode.F2)
				keystrokes.add(113);
			else if (key == KeyCode.F3)
				keystrokes.add(114);
			else if (key == KeyCode.F4)
				keystrokes.add(115);
			else if (key == KeyCode.F5)
				keystrokes.add(116);
			else if (key == KeyCode.F6)
				keystrokes.add(117);
			else if (key == KeyCode.F7)
				keystrokes.add(118);
			else if (key == KeyCode.F8)
				keystrokes.add(119);
			else if (key == KeyCode.F9)
				keystrokes.add(120);
			else if (key == KeyCode.F10)
				keystrokes.add(121);
			else if (key == KeyCode.F11)
				keystrokes.add(122);
			else if (key == KeyCode.F12)
				keystrokes.add(123);
			else if (key == KeyCode.INSERT)
				keystrokes.add(45);
			else if (key == KeyCode.DELETE)
				keystrokes.add(46);
			else if (key == KeyCode.BACK_QUOTE)
				keystrokes.add(192);
			else if (key == KeyCode.DIGIT1)
				keystrokes.add(49);
			else if (key == KeyCode.DIGIT2)
				keystrokes.add(50);
			else if (key == KeyCode.DIGIT3)
				keystrokes.add(51);
			else if (key == KeyCode.DIGIT4)
				keystrokes.add(52);
			else if (key == KeyCode.DIGIT5)
				keystrokes.add(53);
			else if (key == KeyCode.DIGIT6)
				keystrokes.add(54);
			else if (key == KeyCode.DIGIT7)
				keystrokes.add(55);
			else if (key == KeyCode.DIGIT8)
				keystrokes.add(56);
			else if (key == KeyCode.DIGIT9)
				keystrokes.add(57);
			else if (key == KeyCode.DIGIT0)
				keystrokes.add(48);
			else if (key == KeyCode.MINUS)
				keystrokes.add(189);
			else if (key == KeyCode.EQUALS)
				keystrokes.add(187);
			else if (key == KeyCode.BACK_SPACE)
				keystrokes.add(8);
			else if (key == KeyCode.TAB)
				keystrokes.add(9);
			else if (key == KeyCode.OPEN_BRACKET)
				keystrokes.add(219);
			else if (key == KeyCode.CLOSE_BRACKET)
				keystrokes.add(221);
			else if (key == KeyCode.ENTER)
				keystrokes.add(13);
			else if (key == KeyCode.CAPS)
				keystrokes.add(20);
			else if (key == KeyCode.SEMICOLON)
				keystrokes.add(186);
			else if (key == KeyCode.QUOTE)
				keystrokes.add(222);
			else if (key == KeyCode.BACK_SLASH)
				keystrokes.add(220);
			else if (key == KeyCode.SHIFT)
				keystrokes.add(16);
			else if (key == KeyCode.COMMA)
				keystrokes.add(188);
			else if (key == KeyCode.PERIOD)
				keystrokes.add(190);
			else if (key == KeyCode.SLASH)
				keystrokes.add(191);
			else if (key == KeyCode.CONTROL)
				keystrokes.add(17);
			else if (key == KeyCode.WINDOWS)
				keystrokes.add(91);
			else if (key == KeyCode.ALT)
				keystrokes.add(18);
			else if (key == KeyCode.SPACE)
				keystrokes.add(32);
			else if (key == KeyCode.LEFT)
				keystrokes.add(37);
			else if (key == KeyCode.UP)
				keystrokes.add(38);
			else if (key == KeyCode.RIGHT)
				keystrokes.add(39);
			else if (key == KeyCode.DOWN)
				keystrokes.add(40);
		});

		bp.setCenter(textArea);
		bp.setTop(makeMenuBar());
		Scene scene = new Scene(bp);
		pStage.setTitle("Capture Keystrokes");
		//pStage.setMaximized(true);
		pStage.setScene(scene);
		pStage.show();

	}

	private MenuBar makeMenuBar() {
		// Create main app menu
		MenuBar menuBar = new MenuBar();

		// File menu and subitems
		Menu menuFile = new Menu("File");
		MenuItem menuFileSave = new MenuItem("Save");
		menuFileSave.setOnAction(e -> {
			String stringToWrite = "";
			for (int i = 0; i < keystrokes.size(); i++) {
				int j = keystrokes.get(i);
				//int j = KeyEvent.getExtendedKeyCodeForChar(keystrokes.get(i));
				if (i == keystrokes.size() - 1) {
					stringToWrite += j;
				} else {
					stringToWrite += j + ",";
				}
			}
			Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
			try {
				File file = new File(timeStamp + ".txt");
				FileWriter out = new FileWriter(file);
				out.write(stringToWrite);
				out.flush();
				out.close();
			} catch (IOException ex) {
				System.err.println(ex + ": Something went wrong");
			}
		});
		menuFileSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

		MenuItem menuFileExit = new MenuItem("Exit");
		menuFileExit.setOnAction(e -> {
			Platform.exit();
		});
		menuFileExit.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN));

		menuFile.getItems().addAll(menuFileSave, new SeparatorMenuItem(), menuFileExit);
		menuBar.getMenus().addAll(menuFile);

		return menuBar;
	}

	private void saveFile(String content, File file) {
		try {
			FileWriter fileWriter = null;

			fileWriter = new FileWriter(file);
			fileWriter.write(content);
			fileWriter.close();
		} catch (IOException ex) {
			Logger.getLogger(CreateKeystrokeFile.class.getName()).log(Level.SEVERE, null, ex);
		}

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

	public static <E> void sop(E[] grid) {
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

	public static <E> void sopl(E s) {
		System.out.println(s);
	}

	public static <E> void sop(E s) {
		System.out.print(s);
	}

}

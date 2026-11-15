package view;

import java.awt.*;

import javax.swing.*;

public class AppTheme {
	public static String APP_NAME     = "Grade Tracker";
	public static String APP_TAG_LINE = "By Angelic and Amelia";

	public static int BORDER_RADIUS = 8;

	public static Color ACCENT_COLOUR_WHITE  = new Color(0xF3F3F3);
	public static Color ACCENT_COLOUR_BRIGHT = new Color(0xC5E898);
	public static Color ACCENT_COLOUR_MIDDLE = new Color(0x29ADB2);
	public static Color ACCENT_COLOUR_DARK   = new Color(0x0766AD);

	public static int H1_SIZE = 46;
	public static int H2_SIZE = 22;
	public static int P_SIZE = 16;
	public static int NOTE_SIZE = 12;
	public static int TABLE_ENTRY_SIZE = 14;
	

	public static String LAUNCHER_BACKGROUND_IMAGE_PATH =
		"assets/Grade Tracker Launcher Background.png";

	public static String APP_BACKGROUND_IMAGE_PATH =
		"assets/Grade Tracker App Background.png";


	public static String INFO_DIALOG_BACKGROUND_IMAGE_PATH =
		"assets/Grade Tracker Info Dialog Background.png";


	public static Font ArtfulFont(int size) {
		return new Font("Courier new", Font.BOLD, size);
	}
	public static Font NormalFont(int size) {
		return new Font("Consolas", Font.BOLD, size);
	}
	
	public static JLabel ArtfulText(String text, int size, Color colour) {
		JLabel label = new JLabel(text);
		label.setForeground(colour);
		if (size == P_SIZE) label.setFont(NormalFont(size));
		else label.setFont(ArtfulFont(size));

		return label;
	}

	static AAButton ArtfulButton(String text, int fontSize, int preferredWidth, int preferredHeight, Runnable onClick) {
		AAButton button = new AAButton(text);
		button.setFont(AppTheme.NormalFont(fontSize));
		if (preferredHeight != 0 && preferredWidth != 0)
			button.setPreferredSize(new Dimension(preferredWidth, 40));
		
		if (onClick != null) button.addActionListener(e -> onClick.run());

		return button;
	}
	static void ShowError(String message) {
		JOptionPane.showMessageDialog(null, message, "Input error", JOptionPane.WARNING_MESSAGE);
	}


}

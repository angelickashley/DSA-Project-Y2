package view;

import java.awt.*;

import javax.swing.*;

public class AppTheme {
	public static String APP_NAME     = "Spider Lily";
	public static String APP_TAG_LINE = "Student Grade Tracker";

	public static int BORDER_RADIUS = 8;

	public static Color ACCENT_COLOUR_WHITE  = new Color(0xF1E9E9);
	public static Color ACCENT_COLOUR_PINK   = new Color(0xE491C9);
	public static Color ACCENT_COLOUR_PURPLE = new Color(0x982598);
	public static Color ACCENT_COLOUR_DARK   = new Color(0x15173D);

	public static int H1_SIZE = 50;
	public static int H2_SIZE = 25;
	public static int P_SIZE = 18;
	public static int NOTE_SIZE = 14;

	public static String LAUNCHER_BACKGROUND_IMAGE_PATH =
		"assets/Spider Lily Launcher Background.png";

	public static String APP_BACKGROUND_IMAGE_PATH =
		"assets/Spider Lily App Background.png";


	public static Font ArtfulFont(int size) {
		return new Font("courier new", Font.BOLD, size);
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
		
		button.addActionListener(e -> onClick.run());

		return button;
	}
	static void ShowError(String message) {
		JOptionPane.showMessageDialog(null, message, "Input error", JOptionPane.WARNING_MESSAGE);
	}


}

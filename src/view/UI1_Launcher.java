package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.awt.*;

public class UI1_Launcher {

	private static final int LAUNCHER_WIDTH  = 400;
	private static final int LAUNCHER_HEIGHT = 300;

	public static void StartLauncher() {
		JFrame launcherFrame = new JFrame(AppTheme.APP_NAME + " - Launcher");
		launcherFrame.setSize(LAUNCHER_WIDTH, LAUNCHER_HEIGHT);
		launcherFrame.setLocationRelativeTo(null);
		launcherFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		launcherFrame.setResizable(false);
		
		JPanelWithBackground bg = new JPanelWithBackground(AppTheme.LAUNCHER_BACKGROUND_IMAGE_PATH);

		JLabel titleLabel   = AppTheme.ArtfulText(
			AppTheme.APP_NAME, AppTheme.H1_SIZE, Color.WHITE
		);
		JLabel taglineLabel = AppTheme.ArtfulText(
			AppTheme.APP_TAG_LINE, AppTheme.H2_SIZE, Color.WHITE
		);

		AAButton newListButton = AppTheme.ArtfulButton(
			"New List", AppTheme.P_SIZE, LAUNCHER_WIDTH/2, 40,
			() -> {
				launcherFrame.dispose();
				controller.AppSystem.StartWindow_NewStudentList();
			}
		);
		AAButton continueListButton = AppTheme.ArtfulButton(
			"Continue List", AppTheme.P_SIZE, LAUNCHER_WIDTH/2, 40,
			() -> {
				launcherFrame.dispose();
				controller.AppSystem.StartWindow_ContinueStudentList();
			}
		);

		
		bg.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;  // one component per row, centered
		c.insets = new Insets(6, 0, 6, 0);           // vertical gap between rows

		bg.add(titleLabel, c);
		bg.add(taglineLabel, c);
		bg.add(newListButton, c);
		bg.add(continueListButton, c);

		launcherFrame.setContentPane(bg);

		launcherFrame.setVisible(true);
	}
}
class JPanelWithBackground extends JPanel {
	private Image backgroundImage;

	public JPanelWithBackground(String fileName) {
		setBackground(AppTheme.ACCENT_COLOUR_DARK); // fallback colour
		try {
			backgroundImage = ImageIO.read(new File(fileName));
		} catch (IOException | IllegalArgumentException e) {
			backgroundImage = null;
			System.err.println("Could not load background image: " + fileName);
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Draw the background image.
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			
		}
	}
}
package view;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class UI3_ListWindow {
	private static final int WINDOW_WIDTH  = 800;
	private static final int WINDOW_HEIGHT = 600;

	private static boolean WINDOW_IS_OPEN = false;
	
	public static boolean IsWindowOpen() { return WINDOW_IS_OPEN;}

	public static void StartListWindow() {
		WINDOW_IS_OPEN = true;
		JFrame win = new JFrame(AppTheme.APP_NAME + " - List View");
		win.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		win.setLocationRelativeTo(null);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// win.addWindowListener(new java.awt.event.WindowAdapter() {
		// 	@Override
		// 	public void windowClosing(java.awt.event.WindowEvent e) {
		// 		int choice = JOptionPane.showConfirmDialog(
		// 				win,
		// 				"Are you sure you want to quit?",
		// 				"Confirm Exit",
		// 				JOptionPane.YES_NO_OPTION,
		// 				JOptionPane.QUESTION_MESSAGE);

		// 		if (choice == JOptionPane.YES_OPTION) {
		// 			win.dispose();       // or System.exit(0) to end the whole program
		// 		}
		// 	}
		// });
		JPanelWithBackground body = new JPanelWithBackground(AppTheme.APP_BACKGROUND_IMAGE_PATH);
		// bg.setPreferredSize(new Dimension(100,100));
		body.add(AppTheme.ArtfulButton("Add New Student", AppTheme.P_SIZE, WINDOW_WIDTH/4, 40, () -> {
			controller.AppSystem.StartWindow_NewStudentList();
		}));

		win.add(body);
		win.setVisible(true);
	}
}

package view;

import javax.imageio.ImageIO;
import javax.swing.*;

import controller.AppSystem;

import java.io.*;
import java.awt.*;

public class UI2_AddStudentWindow {
	private static final int WINDOW_WIDTH  = 560;
	private static final int WINDOW_HEIGHT = 480;
	
	private static boolean WINDOW_IS_OPEN = false;
	public static boolean IsWindowOpen() { return WINDOW_IS_OPEN;}

	private static JFrame win;

	public static void StartAddStudentWindow() {
		WINDOW_IS_OPEN = true;
		win = new JFrame(AppTheme.APP_NAME + " - Add Student");
		
		win.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		win.setMinimumSize(new Dimension(400, 350));
		win.setLocationRelativeTo(null);
		if (!UI3_ListWindow.IsWindowOpen()) win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		else win.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		win.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override public void windowClosing(java.awt.event.WindowEvent e) {
				CloseAddStudentWindow();
			}
		});
		
		JPanelWithBackground body = new JPanelWithBackground(AppTheme.APP_BACKGROUND_IMAGE_PATH);

		// body.setLayout(new GridBagLayout());
		
		// JPanelWithBackground bg = new JPanelWithBackground(AppTheme.APP_BACKGROUND_IMAGE_PATH);
		body.setLayout(new BorderLayout());
		JPanel wrapper = new JPanel(new FlowLayout());
		wrapper.setOpaque(false);
		wrapper.add(buildForm());
		body.add(wrapper, BorderLayout.NORTH);
		win.setContentPane(body);
		win.setVisible(true);
	}

	public static void CloseAddStudentWindow() {
		WINDOW_IS_OPEN = false;
		win.dispose();
	}

	private static JPanel buildForm() {
		JPanel form = new JPanel(new GridBagLayout());
		form.setOpaque(false);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(4, 4, 4, 4);
		c.anchor = GridBagConstraints.WEST;

		// Title: spans both columns
		c.gridx = 0; c.gridy = 0; c.gridwidth = 2;

		JLabel title = AppTheme.ArtfulText("Add Student", AppTheme.H1_SIZE, AppTheme.ACCENT_COLOUR_WHITE);
		form.add(title, c);

		// Vertical space
		c.gridy = 1;
		form.add(Box.createVerticalStrut(15), c);

		// Label + field rows
		AATextField studentName = new AATextField(15);
		AATextField studentNum = new AATextField(15, true, false);
		AATextField studentGrade = new AATextField(15, true, true);
		addRow(form, 2, "Student Name",   studentName);
		addRow(form, 3, "Student Number", studentNum);
		addRow(form, 4, "Average Grade",  studentGrade);

		JLabel addedStudentNote = AppTheme.ArtfulText("", AppTheme.NOTE_SIZE, AppTheme.ACCENT_COLOUR_WHITE);


		// Vertical space
		c.gridx = 0; c.gridy = 5; c.gridwidth = 2; c.weightx = 0; c.fill = GridBagConstraints.NONE;
		form.add(Box.createVerticalStrut(15), c);

		// Buttons side by side, with a gap between them
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		buttons.setOpaque(false);

		AAButton addStudentButton = AppTheme.ArtfulButton(
			"Add Student", AppTheme.P_SIZE, 0, 0,
			() -> {
				String name  = studentName.getText();
				int    num   = studentNum.getInt(-1);
				double grade = studentGrade.getDouble(-1);
				
				if (name.isEmpty()) {
					AppTheme.ShowError("Please enter a student name.");
					studentName.requestFocusInWindow();
					return;
				}
				if (num < 0) {
					AppTheme.ShowError("Please enter a student number (whole number).");
					studentName.requestFocusInWindow();
					return;
				}
				if (grade < 0) {
					AppTheme.ShowError("Please enter an average grade (0% to 100%).");
					studentName.requestFocusInWindow();
					return;
				}

				controller.AppSystem.AddNewStudent(name, num, grade);
				addedStudentNote.setText(String.format(
					"Added: %s (No. %d, grade %.2f%%)", name, num, grade
				));

				// text disappears after 3 seconds after 3 seconds
				Timer t = new Timer(3000, e -> {
					addedStudentNote.setText("");
				});
				t.setRepeats(false); t.start();

				studentName.setText("");
				studentNum.setText("");
				studentGrade.setText("");
			}
		);
		AAButton ViewStudentListButton = AppTheme.ArtfulButton(
			"View Students", AppTheme.P_SIZE, 0, 0,
			() -> {
				CloseAddStudentWindow();
				controller.AppSystem.StartWindow_ContinueStudentList();
			}
		);

		buttons.add(addStudentButton);
		buttons.add(ViewStudentListButton);

		c.gridy = 6;
		form.add(buttons, c);
		
		c.gridy = 7;
		form.add(addedStudentNote, c);

		return form;
	}

	private static void addRow(JPanel form, int row, String labelText, JComponent field) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = row;
		c.insets = new Insets(4, 4, 4, 4);
		c.anchor = GridBagConstraints.WEST;

		c.gridx = 0; // label column: sized by the longest label
		JLabel label = AppTheme.ArtfulText(labelText, AppTheme.P_SIZE, AppTheme.ACCENT_COLOUR_WHITE);
		form.add(label, c);

		c.gridx = 1; // field column
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL; // fields stretch to the same width
		form.add(field, c);
	}
}

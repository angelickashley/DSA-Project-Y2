package controller;

import javax.swing.JFrame;
import javax.swing.UIManager;
import java.awt.*;

import model.*;
import view.UI1_Launcher;

class EntryPoint {
	public static void main() {
		Database.Students = new LeftJustifiedList<Student>(30, 5);
		try {
			// Use the look of the operating system (Windows, macOS, ...)
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
			// If it fails, Swing simply keeps its default look. Nothing to fix.
		}

		view.UI1_Launcher.StartLauncher();
	}
}
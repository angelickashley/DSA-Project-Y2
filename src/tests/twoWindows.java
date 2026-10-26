package tests;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class twoWindows {

	public static void main(String[] args)  throws Exception {
		JFrame a = new JFrame("frame A");
		a.add(new JLabel("hello"));
		a.setSize(null);
		JFrame b = new JFrame("frame B");
		b.add(new JLabel("world"));

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
					a.setVisible(true);
					b.setVisible(true);
			}
		});
	}
}

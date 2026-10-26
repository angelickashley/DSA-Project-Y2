package tests;

import java.awt.GraphicsEnvironment;

public class idk {
	public static void main(String[] args) {
		String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (String n : names) {
			if (n.toLowerCase().contains("cascadia"))
				System.out.println(n);
			else
				System.out.println("whyyyyy: " + n);
		}
	}
}

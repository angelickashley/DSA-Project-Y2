package view;

import java.awt.*;

final class AAStyle {

	private AAStyle() {}

	/** Paints the rounded translucent box used by every AA component. */
	static void paintBox(Graphics g, Component c, boolean hover, boolean focused) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Color pink = AppTheme.ACCENT_COLOUR_PINK;
		Color highlightFill = new Color(pink.getRed(), pink.getGreen(), pink.getBlue(), 40);

		g2.setColor((hover || focused) ? highlightFill : new Color(0, 0, 0, 120));
		g2.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1,
				AppTheme.BORDER_RADIUS, AppTheme.BORDER_RADIUS);

		g2.setColor((hover) ? pink : AppTheme.ACCENT_COLOUR_WHITE);
		g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1,
				AppTheme.BORDER_RADIUS, AppTheme.BORDER_RADIUS);

		g2.dispose();
	}
}
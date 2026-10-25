package view;

import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import java.awt.*;

public class AAButton extends JButton {

	private boolean hover   = false;
	private boolean focused = false;

	AAButton(String text) {
		super(text);
		// stop "look & feel" from messing stuff up
		setContentAreaFilled(false);  // stop the L&F painting its own background
		setBorderPainted(false);      // stop the L&F painting its own border

		setFocusPainted(false);
		setOpaque(false);             // let the background image show through
		setForeground(AppTheme.ACCENT_COLOUR_WHITE);
		
		setFont(new Font("Courier New", Font.PLAIN, 16));
		// setPreferredSize(new Dimension(preferredWidth, preferredHeight));
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		addMouseListener(new java.awt.event.MouseAdapter() {
			@Override public void mouseEntered(java.awt.event.MouseEvent e) { hover = true;  repaint(); }
			@Override public void mouseExited (java.awt.event.MouseEvent e) { hover = false; repaint(); }
		});
		addFocusListener(new java.awt.event.FocusAdapter() {
			@Override public void focusGained(java.awt.event.FocusEvent e) { focused = true;  repaint(); }
			@Override public void focusLost  (java.awt.event.FocusEvent e) { focused = false; repaint(); }
		});

		InputMap im = getInputMap(JComponent.WHEN_FOCUSED);
		im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0, false), "pressed");
		im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0, true),  "released");
	}

	
	@Override
	protected void paintComponent(Graphics g) {
		AAStyle.paintBox(g, this, hover, focused);
		super.paintComponent(g); // draws the text
	}
	// protected void paintComponent(Graphics g) {
	// 	Graphics2D g2 = (Graphics2D) g.create();
	// 	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	// 	// translucent fill so the image still shows through; brighter on hover
	// 	Color bgCol = new Color(
	// 		AppTheme.ACCENT_COLOUR_PINK.getRed(),
	// 		AppTheme.ACCENT_COLOUR_PINK.getGreen(),
	// 		AppTheme.ACCENT_COLOUR_PINK.getBlue(),
	// 		40
	// 	);
	// 	g2.setColor((!hover && !focused) ? new Color(0, 0, 0, 120) : bgCol);
	// 	// g2.setColor((!focused) ? g2.getColor() : )

	// 	g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, AppTheme.BORDER_RADIUS, AppTheme.BORDER_RADIUS);

	// 	g2.setColor((!hover) ? AppTheme.ACCENT_COLOUR_WHITE : AppTheme.ACCENT_COLOUR_PINK);
	// 	// g2.setColor((!focused) ? g2.getColor() : )

	// 	g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, AppTheme.BORDER_RADIUS, AppTheme.BORDER_RADIUS);
	// 	g2.dispose();

	// 	super.paintComponent(g);  // draws the text
	// }
}
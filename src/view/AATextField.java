package view;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class AATextField extends JTextField {

	private boolean hover   = false;
	private boolean focused = false;

	/** Plain text field. */
	public AATextField(int columns) {
		this(columns, false, false);
	}

	/**
	 * @param numbersOnly  true to only accept digits
	 * @param allowDecimal only used when numbersOnly is true; allows one decimal point
	 */
	public AATextField(int columns, boolean numbersOnly, boolean allowDecimal) {
		super(columns);

		setOpaque(false);                         // let the background image show through
		setForeground(AppTheme.ACCENT_COLOUR_WHITE);
		setCaretColor(AppTheme.ACCENT_COLOUR_WHITE);
		setSelectionColor(new Color(
				AppTheme.ACCENT_COLOUR_PINK.getRed(),
				AppTheme.ACCENT_COLOUR_PINK.getGreen(),
				AppTheme.ACCENT_COLOUR_PINK.getBlue(), 120));
		setSelectedTextColor(AppTheme.ACCENT_COLOUR_WHITE);
		setFont(AppTheme.NormalFont(AppTheme.P_SIZE));
		setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

		addMouseListener(new java.awt.event.MouseAdapter() {
			@Override public void mouseEntered(java.awt.event.MouseEvent e) { hover = true;  repaint(); }
			@Override public void mouseExited (java.awt.event.MouseEvent e) { hover = false; repaint(); }
		});
		addFocusListener(new java.awt.event.FocusAdapter() {
			@Override public void focusGained(java.awt.event.FocusEvent e) { focused = true;  repaint(); }
			@Override public void focusLost  (java.awt.event.FocusEvent e) { focused = false; repaint(); }
		});

		if (numbersOnly) {
			installNumberFilter(allowDecimal ? "\\d*\\.?\\d*" : "\\d*");
		}
	}

	private void installNumberFilter(String pattern) {
		((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int offset, String s, AttributeSet a)
					throws BadLocationException {
				if (s != null) replace(fb, offset, 0, s, a);
			}

			@Override
			public void replace(FilterBypass fb, int offset, int length, String s, AttributeSet a)
					throws BadLocationException {
				String current = fb.getDocument().getText(0, fb.getDocument().getLength());
				String proposed = new StringBuilder(current)
						.replace(offset, offset + length, s == null ? "" : s).toString();
				if (proposed.matches(pattern)) {
					super.replace(fb, offset, length, s, a);
				}
			}
		});
	}

	/** Returns the number, or fallback if the field is empty or not a valid number. */
	public int getInt(int fallback) {
		try { return Integer.parseInt(getText().trim()); }
		catch (NumberFormatException e) { return fallback; }
	}

	public double getDouble(double fallback) {
		try { return Double.parseDouble(getText().trim()); }
		catch (NumberFormatException e) { return fallback; }
	}

	@Override
	protected void paintComponent(Graphics g) {
		AAStyle.paintBox(g, this, hover, focused);
		super.paintComponent(g);
	}
}
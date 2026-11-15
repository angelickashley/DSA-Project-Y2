package view;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import java.awt.*;
import java.awt.event.*;

public class AAComboBox<E> extends JComboBox<E> {

	// the popup is its own window, so it can't be see-through like the closed box
	private static final Color POPUP_BG = new Color(25, 25, 25);

	private boolean hover   = false;
	private boolean focused = false;

	public AAComboBox() {
		super();
		style();
	}

	public AAComboBox(E[] items) {
		super(items);
		style();
	}

	private void style() {
		setUI(new AAComboBoxUI());   // takes over the painting, arrow button and popup from the L&F
		setOpaque(false);            // let the background image show through
		setForeground(AppTheme.ACCENT_COLOUR_WHITE);
		setBackground(POPUP_BG);     // the popup list follows this; the closed box never paints it
		setFont(AppTheme.NormalFont(AppTheme.P_SIZE));
		setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setRenderer(new AARenderer());

		// hover: also listen on the arrow button, and ignore "exit" when moving onto it
		MouseAdapter hoverListener = new MouseAdapter() {
			@Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
			@Override public void mouseExited (MouseEvent e) {
				Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), AAComboBox.this);
				hover = contains(p);
				repaint();
			}
		};
		addMouseListener(hoverListener);
		for (Component c : getComponents()) c.addMouseListener(hoverListener);

		addFocusListener(new FocusAdapter() {
			@Override public void focusGained(FocusEvent e) { focused = true;  repaint(); }
			@Override public void focusLost  (FocusEvent e) { focused = false; repaint(); }
		});

		// keep the box highlighted while the popup is open
		addPopupMenuListener(new PopupMenuListener() {
			@Override public void popupMenuWillBecomeVisible(PopupMenuEvent e)   { repaint(); }
			@Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { repaint(); }
			@Override public void popupMenuCanceled(PopupMenuEvent e)            { repaint(); }
		});

		// Enter opens the popup (when it's already open, keep the normal "confirm choice" behaviour)
		Action original = getActionMap().get("enterPressed");
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "aaEnter");
		getActionMap().put("aaEnter", new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				if (isPopupVisible()) {
					if (original != null) original.actionPerformed(e);
				} else {
					showPopup();
				}
			}
		});
	}

	boolean isHighlighted() {
		return hover || focused || isPopupVisible();
	}

	/** Typed version of getSelectedItem(). */
	@SuppressWarnings("unchecked")
	public E getSelected() {
		return (E) getSelectedItem();
	}

	@Override
	protected void paintComponent(Graphics g) {
		AAStyle.paintBox(g, this, isHighlighted(), false);
		super.paintComponent(g);   // draws the selected value; the arrow button is a child
	}

	// ---------- how each item (and the closed value) is drawn ----------

	private static class AARenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, false);
			setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

			if (index == -1) {                       // the closed box: transparent
				setOpaque(false);
				setForeground(AppTheme.ACCENT_COLOUR_WHITE);
			} else {                                 // an item in the popup
				setOpaque(true);
				setBackground(isSelected ? blend(AppTheme.ACCENT_COLOUR_BRIGHT, POPUP_BG, 0.25f) : POPUP_BG);
				setForeground(isSelected ? AppTheme.ACCENT_COLOUR_BRIGHT : AppTheme.ACCENT_COLOUR_WHITE);
			}
			return this;
		}

		private static Color blend(Color top, Color bottom, float t) {
			return new Color(
				Math.round(top.getRed()   * t + bottom.getRed()   * (1 - t)),
				Math.round(top.getGreen() * t + bottom.getGreen() * (1 - t)),
				Math.round(top.getBlue()  * t + bottom.getBlue()  * (1 - t)));
		}
	}

	// ---------- the arrow on the right ----------

	private static class ArrowButton extends JButton {
		private final AAComboBox<?> combo;

		ArrowButton(AAComboBox<?> combo) {
			this.combo = combo;
			setContentAreaFilled(false);
			setBorderPainted(false);
			setFocusPainted(false);
			setOpaque(false);
			setFocusable(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(combo.isHighlighted() ? AppTheme.ACCENT_COLOUR_BRIGHT : AppTheme.ACCENT_COLOUR_WHITE);
			g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

			int cx = getWidth() / 2, cy = getHeight() / 2;
			g2.drawLine(cx - 5, cy - 2, cx, cy + 3);   // a small "v"
			g2.drawLine(cx, cy + 3, cx + 5, cy - 2);
			g2.dispose();
		}
	}

	// ---------- the UI delegate that wires it all together ----------

	private static class AAComboBoxUI extends BasicComboBoxUI {

		@Override
		protected JButton createArrowButton() {
			return new ArrowButton((AAComboBox<?>) comboBox);
		}

		@Override   // the L&F fills the box with a selection colour when focused; we don't want that
		public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) { }

		@Override   // draw the selected value ourselves so the L&F can't override our colours
		public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
			ListCellRenderer<? super Object> r = comboBox.getRenderer();
			Component c = r.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
			c.setFont(comboBox.getFont());
			currentValuePane.paintComponent(g, c, comboBox,
					bounds.x, bounds.y, bounds.width, bounds.height, c instanceof JPanel);
		}

		@Override
		protected ComboPopup createPopup() {
			BasicComboPopup popup = new BasicComboPopup(comboBox);
			popup.setBorder(BorderFactory.createLineBorder(AppTheme.ACCENT_COLOUR_BRIGHT, 1));
			popup.setOpaque(true);
			popup.setBackground(POPUP_BG);
			return popup;
		}
	}
}
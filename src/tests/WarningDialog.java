package tests;// Swing components (JDialog, JButton, JTextArea, ...) live in javax.swing
import javax.swing.*;
// AWT gives us layout managers, Color, Graphics and Window
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A warning pop-up for empty or invalid input, plus helper methods that check
 * what the user typed and show the warning for you.
 *
 * Quick use from any window:
 *
 *     Integer value = tests.WarningDialog.requireInteger(this, txtKey.getText(), "Key");
 *     if (value == null) return;     // the warning was already shown, so just stop
 *
 * Every require...() method returns null when the input is bad.
 */
public class WarningDialog extends JDialog {

    private static final Color AMBER = new Color(0xF5A623);

    // The dialog itself

    /** Private on purpose: use tests.WarningDialog.show(...) instead. */
    private WarningDialog(Window owner, String message) {
        // ModalityType.APPLICATION_MODAL = the user must close this before using the app again
        super(owner, "Warning", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JLabel icon = new JLabel(new WarningIcon(46));

        JTextArea text = new JTextArea(message);
        text.setEditable(false);
        text.setFocusable(false);
        text.setOpaque(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setColumns(26);
        text.setFont(text.getFont().deriveFont(Font.PLAIN, 14f));

        JButton ok = new JButton("OK");
        ok.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(ok);   // pressing Enter clicks OK

        // Pressing Escape also closes the dialog
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel body = new JPanel(new BorderLayout(16, 0));
        body.add(icon, BorderLayout.WEST);
        body.add(text, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttons.add(ok);

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setBorder(BorderFactory.createEmptyBorder(20, 22, 16, 22));
        content.add(body, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
        setContentPane(content);

        pack();                          // size the dialog to fit its content
        setLocationRelativeTo(owner);    // centre over the window that opened it
    }

    /** Shows the warning and waits until the user closes it. */
    public static void show(Component parent, String message) {
        // getWindowAncestor starts looking at the PARENT of what you give it,
        // so if "parent" already is a window (like a JFrame) we must use it directly.
        Window owner = parent instanceof Window w ? w : SwingUtilities.getWindowAncestor(parent);
        new WarningDialog(owner, message).setVisible(true);   // blocks until closed (modal)
    }

    // ---------- Input checks ----------
    // Each one returns the cleaned-up value, or null after showing a warning.

    /** Text must not be empty or only spaces. */
    public static String requireText(Component parent, String text, String fieldName) {
        String trimmed = text == null ? "" : text.trim();
        if (trimmed.isEmpty()) {
            show(parent, "Please enter a value for " + fieldName + ".\nThe field can't be left empty.");
            return null;
        }
        return trimmed;
    }

    /** Must be a whole number such as 42 or -7. */
    public static Integer requireInteger(Component parent, String text, String fieldName) {
        String trimmed = requireText(parent, text, fieldName);
        if (trimmed == null) return null;
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException ex) {
            // A string of only digits that still fails is simply too big for an int
            if (trimmed.matches("[+-]?\\d+")) {
                show(parent, fieldName + " is too large.\nUse a whole number between "
                        + Integer.MIN_VALUE + " and " + Integer.MAX_VALUE + ".");
            } else {
                show(parent, fieldName + " must be a whole number, like 42 or -7.\n"
                        + "You entered: \"" + shorten(trimmed) + "\"");
            }
            return null;
        }
    }

    /** Must be a whole number between min and max (both allowed). */
    public static Integer requireInteger(Component parent, String text, String fieldName, int min, int max) {
        Integer value = requireInteger(parent, text, fieldName);
        if (value == null) return null;
        if (value < min || value > max) {
            show(parent, fieldName + " must be between " + min + " and " + max + ".\nYou entered: " + value);
            return null;
        }
        return value;
    }

    /** Must be a number, decimals allowed, such as 72.5. */
    public static Double requireNumber(Component parent, String text, String fieldName) {
        String trimmed = requireText(parent, text, fieldName);
        if (trimmed == null) return null;
        try {
            double value = Double.parseDouble(trimmed);
            // parseDouble also accepts the words "NaN" and "Infinity", which aren't real values
            if (Double.isNaN(value) || Double.isInfinite(value)) throw new NumberFormatException();
            return value;
        } catch (NumberFormatException ex) {
            show(parent, fieldName + " must be a number, like 85 or 72.5.\n"
                    + "You entered: \"" + shorten(trimmed) + "\"");
            return null;
        }
    }

    /**
     * Reads a list such as "3, 9, 14 21" (commas and/or spaces between numbers).
     * Warns about the first entry that isn't a whole number.
     */
    public static List<Integer> requireIntegerList(Component parent, String text, String fieldName) {
        String trimmed = requireText(parent, text, fieldName);
        if (trimmed == null) return null;

        List<Integer> values = new ArrayList<>();
        // split on any run of commas and/or spaces
        for (String part : trimmed.split("[,\\s]+")) {
            if (part.isEmpty()) continue;
            try {
                values.add(Integer.parseInt(part));
            } catch (NumberFormatException ex) {
                show(parent, "\"" + shorten(part) + "\" in " + fieldName + " is not a whole number.\n"
                        + "Separate numbers with commas, like: 3, 9, 14, 21");
                return null;
            }
        }
        if (values.isEmpty()) {
            show(parent, "Please enter at least one number for " + fieldName + ".");
            return null;
        }
        return values;
    }

    /** Cuts very long input so the warning box stays a sensible size. */
    private static String shorten(String s) {
        return s.length() > 20 ? s.substring(0, 20) + "..." : s;
    }

    // ---------- The amber warning triangle ----------

    /** An Icon is anything that can draw itself at a given position. We draw ours with code. */
    private static class WarningIcon implements Icon {
        private final int size;

        WarningIcon(int size) { this.size = size; }

        @Override public int getIconWidth()  { return size; }
        @Override public int getIconHeight() { return size; }

        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Triangle: top middle, bottom right, bottom left
            Path2D triangle = new Path2D.Float();
            triangle.moveTo(x + size / 2.0, y + 3);
            triangle.lineTo(x + size - 2, y + size - 4);
            triangle.lineTo(x + 2, y + size - 4);
            triangle.closePath();
            g2.setColor(AMBER);
            g2.fill(triangle);
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(triangle);   // the outline makes the corners softly rounded

            // The exclamation mark: a bar and a dot
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(size / 11f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x + size / 2, y + size * 38 / 100, x + size / 2, y + size * 62 / 100);
            g2.fillOval(x + size / 2 - size / 22, y + size * 72 / 100, size / 11, size / 11);
            g2.dispose();
        }
    }
}
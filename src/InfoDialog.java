// Swing components (JDialog, JButton, JLabel, Timer, ...) live in javax.swing
import javax.swing.*;
// AWT gives us layout managers, Color, Graphics and Window
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * The "i" info pop-up: a small window (opens on click, not on hover) that explains
 * what the flashing colours mean during the search:
 *
 *     FLASHING RED    start and end values
 *     FLASHING PINK   mid value
 *     FLASHING YELLOW key is found
 *
 * How to use it on your search screen: add the round "i" button to any panel.
 *
 *     panel.add(InfoDialog.createInfoButton());
 *
 * The colours are public constants, search code can flash rows in
 * exactly the same colours the guide shows (InfoDialog.RED, .PINK, .YELLOW).
 */
public class InfoDialog extends JDialog {

    // The three highlight colours, shared with the rest of the program
    public static final Color RED = new Color(0xE53935);
    public static final Color PINK = new Color(0xFF7EB6);
    public static final Color YELLOW = new Color(0xFFD83B);

    private static final Color ACCENT = new Color(0x2F6FED);
    private static final Color OFF = new Color(0xE6E9F0);   // colour of a swatch while it is "off"
    private static final int FLASH_MS = 600;                 // time between flashes (about 1 flash per second)

    // Remember the open window so pressing "i" twice doesn't open two of them
    private static InfoDialog open;

    private final List<Swatch> swatches = new ArrayList<>();
    private final Timer flashTimer;
    private boolean lit = true;

    private InfoDialog(Window owner) {
        // No modality argument = a normal (non-modal) window: the user can keep using
        // the search screen while this guide stays open next to it.
        super(owner, "Colour Guide");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JLabel title = new JLabel("What the flashing colours mean");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setForeground(ACCENT);

        // One row per colour: a flashing swatch on the left, the explanation on the right
        JPanel rows = new JPanel(new GridLayout(3, 1, 0, 14));
        rows.add(buildRow(RED, "Flashing red", "Start and end values"));
        rows.add(buildRow(PINK, "Flashing pink", "Mid value"));
        rows.add(buildRow(YELLOW, "Flashing yellow", "Key is found"));

        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(close);
        // Escape closes the window too
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttons.add(close);

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
        content.add(title, BorderLayout.NORTH);
        content.add(rows, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
        setContentPane(content);

        // A Swing Timer runs code on a schedule, on the correct (Event Dispatch) thread.
        // Every tick we flip "lit" and repaint all the swatches, which makes them flash.
        flashTimer = new Timer(FLASH_MS, e -> {
            lit = !lit;
            for (Swatch s : swatches) s.setLit(lit);
        });
        flashTimer.start();

        // When the window closes, stop the timer (otherwise it keeps running invisibly)
        // and forget this window so the next click opens a fresh one.
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                flashTimer.stop();
                open = null;
            }
        });

        pack();                          // size the window to fit its content
        setLocationRelativeTo(owner);    // centre over the window that opened it
    }

    /** One legend row: [flashing swatch]  Heading / explanation. */
    private JPanel buildRow(Color color, String heading, String explanation) {
        Swatch swatch = new Swatch(color);
        swatches.add(swatch);

        // JLabel understands a little HTML, which gives us a bold heading over a second line
        JLabel text = new JLabel("<html><b>" + heading + "</b><br>" + explanation + "</html>");
        text.setFont(text.getFont().deriveFont(Font.PLAIN, 14f));

        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.add(swatch, BorderLayout.WEST);
        row.add(text, BorderLayout.CENTER);
        return row;
    }

    // Public API

    /** Opens the guide (or brings it to the front if it is already open). */
    public static void show(Component parent) {
        if (open != null && open.isDisplayable()) {
            open.toFront();
            return;
        }
        // If "parent" is already a window use it, otherwise find the window it sits in
        Window owner = parent instanceof Window w ? w : SwingUtilities.getWindowAncestor(parent);
        open = new InfoDialog(owner);
        open.setVisible(true);
    }

    /** Creates the round "i" button. Clicking it opens the guide. */
    public static JButton createInfoButton() {
        // An anonymous subclass of JButton: we replace its painting to draw a blue circle with an "i"
        JButton button = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Slightly darker while the mouse button is held down
                g2.setColor(getModel().isArmed() ? ACCENT.darker() : ACCENT);
                g2.fillOval(1, 1, getWidth() - 2, getHeight() - 2);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font(Font.SERIF, Font.BOLD | Font.ITALIC, 17));
                FontMetrics fm = g2.getFontMetrics();
                String s = "i";
                g2.drawString(s, (getWidth() - fm.stringWidth(s)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        button.setPreferredSize(new Dimension(30, 30));
        button.setContentAreaFilled(false);   // we paint the background ourselves
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.getAccessibleContext().setAccessibleName("Colour guide");   // for screen readers
        button.addActionListener(e -> show(button));   // passing the button lets show() find its window
        return button;
    }

    //  The flashing colour square

    /** A small rounded square that is either its colour (lit) or pale grey (off). */
    private static class Swatch extends JComponent {
        private final Color color;
        private boolean lit = true;

        Swatch(Color color) {
            this.color = color;
            setPreferredSize(new Dimension(56, 34));
        }

        void setLit(boolean lit) {
            this.lit = lit;
            repaint();   // ask Swing to redraw this component
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(lit ? color : OFF);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
            g2.dispose();
        }
    }

    //  Preview

    /** Preview only: a stand-in search screen with the "i" button. Delete this once it's in your real UI. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Binary Search (preview)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
            top.add(createInfoButton());
            frame.add(top, BorderLayout.NORTH);

            frame.setSize(420, 240);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
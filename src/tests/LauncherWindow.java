package tests;// Swing components (JFrame, JButton, JPanel, ...) live in javax.swing
import javax.swing.*;
// FontUIResource is the type Swing uses for its default fonts
import javax.swing.plaf.FontUIResource;
// ImageIO reads image files. PNG files keep their transparency.
import javax.imageio.ImageIO;
// AWT gives us layout managers, Color, Graphics and Font
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;

/**
 * A small 400 x 300 launcher window: a logo in the middle and two stacked buttons.
 *   New List      - deletes any old saved list, then opens the main window with an empty list
 *   Continue List - opens the main window with the saved list
 */
public class LauncherWindow extends JFrame {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private static final Color BACKGROUND = new Color(0xF4F7FE);   // very light blue so the logo's transparency shows
    private static final Color PLACEHOLDER = new Color(0xA9B6D0);  // soft blue-grey for the empty logo box

    public LauncherWindow() {
        super("Student Grade Tracker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);   // clicking X ends the program
        setResizable(false);                       // a launcher should stay a fixed size
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);               // centre on screen

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(16, 24, 20, 24));

        // BorderLayout.CENTER takes all the space the buttons don't need,
        // which keeps the logo in the middle of the window.
        root.add(new LogoPanel(), BorderLayout.CENTER);
        root.add(buildButtons(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildButtons() {
        JButton btnNew = new JButton("New List");
        JButton btnContinue = new JButton("Continue List");
        btnNew.addActionListener(e -> onNewList());
        btnContinue.addActionListener(e -> onContinueList());

        // GridLayout(2, 1, ...) = two rows, one column: the buttons are stacked, 10px apart.
        // Both get the same width and height because a grid makes every cell equal.
        JPanel buttons = new JPanel(new GridLayout(2, 1, 0, 10));
        buttons.setOpaque(false);   // let the window's background colour show through
        buttons.setPreferredSize(new Dimension(0, 94));   // height: 2 buttons of 42 + the 10px gap
        buttons.add(btnNew);
        buttons.add(btnContinue);
        return buttons;
    }

    // Button actions

    private void onNewList() {
        // Only ask if there is actually something that would be lost
        if (UI_1.exists()) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Starting a new list will delete the saved one.\nContinue?",
                    "Start new list", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) return;   // No, or the dialog was closed
        }

        try {
            UI_1.clear();   // removes the old list (does nothing if there isn't one)
        } catch (IOException ex) {
            WarningDialog.show(this, "Couldn't clear the old list:\n" + ex.getMessage());
            return;
        }
        openMainApp(false);
    }

    private void onContinueList() {
        if (!UI_1.exists()) {
            WarningDialog.show(this, "There is no saved list to continue.\nPress New List to start one.");
            return;
        }
        openMainApp(true);
    }

    /**
     * "continuing" is true when the user pressed Continue List.
     */
    private void openMainApp(boolean continuing) {
        // TODO: replace this message with main window, for example:
        //       new tests.GradeTrackerSwing().setVisible(true);
        // (load the saved list from ListStore.path() when continuing is true)
        JOptionPane.showMessageDialog(this,
                continuing ? "Main window would open with the saved list."
                        : "Main window would open with a new, empty list.");
        dispose();   // close the launcher
    }

    //  The logo
    private static class LogoPanel extends JPanel {

        private static final int MAX_LOGO_WIDTH = 240;
        private static final int MAX_LOGO_HEIGHT = 130;

        private final BufferedImage logo = loadLogo();

        LogoPanel() {
            setOpaque(false);   // don't paint a background, so the window colour shows through
        }

        /** Looks for logo.png in the resources, then in the working folder. Returns null if not found. */
        private static BufferedImage loadLogo() {
            try {
                URL resource = LauncherWindow.class.getResource("/logo.png");
                if (resource != null) return ImageIO.read(resource);

                File file = new File("logo.png"); /** PUT LOGO OR REPLACE NAME OF PNG */
                if (file.exists()) return ImageIO.read(file);
            } catch (IOException ex) {
                // unreadable image: fall through and use the placeholder instead
            }
            return null;
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();   // work on a copy so our settings don't leak out
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            if (logo != null) drawLogo(g2);
            else drawPlaceholder(g2);
            g2.dispose();
        }

        private void drawLogo(Graphics2D g2) {
            // Scale to fit inside the maximum size without stretching it out of shape
            double scale = Math.min((double) MAX_LOGO_WIDTH / logo.getWidth(),
                    (double) MAX_LOGO_HEIGHT / logo.getHeight());
            int w = (int) (logo.getWidth() * scale);
            int h = (int) (logo.getHeight() * scale);
            // Centre it. Transparent pixels in the PNG let the background show through.
            g2.drawImage(logo, (getWidth() - w) / 2, (getHeight() - h) / 2, w, h, null);
        }

        /** PLACEHOLDER */
        private void drawPlaceholder(Graphics2D g2) {
            int boxW = Math.min(MAX_LOGO_WIDTH, getWidth() - 2);
            int boxH = Math.min(MAX_LOGO_HEIGHT, getHeight() - 2);
            int x = (getWidth() - boxW) / 2;    // centre the box in the panel
            int y = (getHeight() - boxH) / 2;

            // A dashed outline: the float array is {dash length, gap length}
            g2.setColor(PLACEHOLDER);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10f, new float[]{6f, 5f}, 0f));
            g2.draw(new RoundRectangle2D.Float(x, y, boxW, boxH, 16, 16));

            // The word "LOGO" in the middle of the box
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
            FontMetrics fm = g2.getFontMetrics();
            String label = "LOGO";
            g2.drawString(label, x + (boxW - fm.stringWidth(label)) / 2,
                    y + (boxH + fm.getAscent() - fm.getDescent()) / 2);
        }
    }

    //  Start-up

    /** Native look and 14pt fonts, same as the main program. Must run before any component is created. */
    private static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // keep Swing's default look
        }
        for (Object key : Collections.list(UIManager.getDefaults().keys())) {
            if (UIManager.get(key) instanceof FontUIResource font) {
                UIManager.put(key, new FontUIResource(font.deriveFont(14f)));
            }
        }
    }

    public static void main(String[] args) {
        // Swing must be built on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            setupLookAndFeel();
            new LauncherWindow().setVisible(true);
        });
    }
}
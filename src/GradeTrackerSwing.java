// Swing components (JFrame, JButton, JTable, ...) live in javax.swing
import javax.swing.*;
// DefaultTableModel holds the data a JTable displays; the renderer controls how a cell looks
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
// FontUIResource is the type Swing uses for its default fonts
import javax.swing.plaf.FontUIResource;
// AWT gives us layout managers (BorderLayout, FlowLayout, ...), Font and Color
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Student Grade Tracker: add students, view them in a table,
 * run a step-by-step linear search, and compute the class average.
 * Requires Java 16+ (uses a record).
 *
 * A JFrame is the main window. By extending it, our class IS a window,
 * so we can call methods like add() and setSize() directly.
 */
public class GradeTrackerSwing extends JFrame {

    // A record is a short way to write a simple data class.
    // Java automatically creates the constructor and the getters name() and grade().
    private record Student(String name, int grade) {}

    // Constants (static final) so values are defined in one place, not scattered as "magic numbers"
    private static final int MIN_GRADE = 0;
    private static final int MAX_GRADE = 100;

    // --- Colour palette: one accent colour plus soft greys keeps the UI calm ---
    private static final Color ACCENT = new Color(0x2F6FED);        // blue, used for the title
    private static final Color SELECTION = new Color(0xDCE8FF);     // light blue for selected rows
    private static final Color BORDER_GREY = new Color(0xD9D9D9);   // thin outlines
    private static final Color LOG_BACKGROUND = new Color(0xF7F7F7);

    // The real data lives here. The table on screen is just a display of this list.
    private final List<Student> students = new ArrayList<>();

    // --- Table setup ---
    // JTable shows the data, but the TableModel actually stores the rows.
    // Here we create a model with 3 column headers and 0 starting rows.
    private final DefaultTableModel tableModel =
            new DefaultTableModel(new String[]{"#", "Name", "Grade"}, 0) {
                // By default, table cells can be edited by double-clicking.
                // Returning false makes the table read-only.
                @Override public boolean isCellEditable(int row, int col) { return false; }
            };
    private final JTable table = new JTable(tableModel);

    // --- Input fields ---
    // The number is a preferred width in "columns" (roughly characters).
    private final JTextField txtName = new JTextField(14);
    private final JTextField txtGrade = new JTextField(5);
    private final JTextField txtSearch = new JTextField(5);

    // JTextArea(rows, columns): a multi-line text box, used here as a message log
    private final JTextArea txtLog = new JTextArea(5, 30);

    public GradeTrackerSwing() {
        super("Student Grade Tracker");              // sets the window title
        setDefaultCloseOperation(EXIT_ON_CLOSE);     // clicking X ends the program

        // BorderLayout splits the window into NORTH, SOUTH, EAST, WEST, CENTER.
        // The two numbers are the gaps (in pixels) between regions.
        setLayout(new BorderLayout(0, 12));

        // Padding around the whole window so nothing touches the edges.
        // getContentPane() is the main area inside the window's frame.
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildHeaderPanel(), BorderLayout.NORTH);  // title + "add student" row
        add(buildTablePanel(), BorderLayout.CENTER);  // the table fills the middle
        add(buildBottomPanel(), BorderLayout.SOUTH);  // search, actions and log

        // Pressing Enter inside a text field fires its ActionListener.
        // "e -> ..." is a lambda: a short way to write "when this happens, run this code".
        txtName.addActionListener(e -> txtGrade.requestFocusInWindow()); // move the cursor to Grade
        txtGrade.addActionListener(e -> addStudent());
        txtSearch.addActionListener(e -> searchGrade());

        setSize(620, 560);
        setMinimumSize(new Dimension(560, 460));  // stops the window being shrunk until it breaks
        setLocationRelativeTo(null);              // passing null centres the window on screen
        setVisible(true);                         // windows are hidden until you call this
    }

    // ---------- UI construction ----------
    // Each method below builds one section of the window and returns it.
    // Splitting the work like this keeps the constructor short and readable.

    private JPanel buildHeaderPanel() {
        JLabel title = new JLabel("Student Grade Tracker");
        // deriveFont creates a copy of the current font with a new style and size
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setForeground(ACCENT);

        JButton btnAdd = new JButton("Add Student");
        btnAdd.addActionListener(e -> addStudent());

        // A JPanel is a container for grouping components.
        // FlowLayout places components left to right. We use 0 gaps and add our own
        // spacing with gap() so the row lines up exactly with the title above it.
        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        addRow.add(new JLabel("Name"));
        addRow.add(gap(6));
        addRow.add(txtName);
        addRow.add(gap(14));
        addRow.add(new JLabel("Grade"));
        addRow.add(gap(6));
        addRow.add(txtGrade);
        addRow.add(gap(14));
        addRow.add(btnAdd);

        // Panels can be nested inside other panels to build more complex layouts
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.add(title, BorderLayout.NORTH);
        panel.add(addRow, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane buildTablePanel() {
        table.setRowHeight(28);                       // taller rows are easier to read
        table.setShowVerticalLines(false);            // horizontal lines only = cleaner look
        table.setGridColor(BORDER_GREY);
        table.setSelectionBackground(SELECTION);
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);           // fill the area even when there are few rows
        table.getTableHeader().setReorderingAllowed(false); // don't let users drag columns around
        table.getTableHeader().setFont(
                table.getTableHeader().getFont().deriveFont(Font.BOLD));

        // A renderer decides how a cell is drawn. Here we just centre the text
        // in the "#" (column 0) and "Grade" (column 2) columns.
        DefaultTableCellRenderer centered = new DefaultTableCellRenderer();
        centered.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centered);
        table.getColumnModel().getColumn(2).setCellRenderer(centered);
        table.getColumnModel().getColumn(0).setMaxWidth(50); // keep the "#" column narrow
        table.getColumnModel().getColumn(2).setMaxWidth(100);

        // Tables don't scroll on their own. You must wrap them in a JScrollPane.
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_GREY));
        return scroll;
    }

    private JPanel buildBottomPanel() {
        // --- Left side of the toolbar: search ---
        JButton btnSearch = new JButton("Linear Search");
        btnSearch.addActionListener(e -> searchGrade());

        JPanel searchGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchGroup.add(new JLabel("Search grade"));
        searchGroup.add(gap(6));
        searchGroup.add(txtSearch);
        searchGroup.add(gap(8));
        searchGroup.add(btnSearch);

        // --- Right side of the toolbar: actions ---
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnAvg = new JButton("Average");
        JButton btnClear = new JButton("Clear All");

        // Each button gets a listener: code that runs when the button is clicked
        btnDelete.addActionListener(e -> deleteSelected());
        btnAvg.addActionListener(e -> showAverage());
        btnClear.addActionListener(e -> clearAll());

        JPanel actionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionGroup.add(btnDelete);
        actionGroup.add(gap(8));
        actionGroup.add(btnAvg);
        actionGroup.add(gap(8));
        actionGroup.add(btnClear);

        // WEST pins a component to the left edge, EAST to the right edge
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.add(searchGroup, BorderLayout.WEST);
        toolbar.add(actionGroup, BorderLayout.EAST);

        // --- Message log ---
        txtLog.setEditable(false);            // the user reads the log but can't type in it
        txtLog.setBackground(LOG_BACKGROUND);
        // A monospaced font makes the search output line up neatly
        txtLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        // Padding inside the text area so the text doesn't touch the border
        txtLog.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JScrollPane logScroll = new JScrollPane(txtLog);
        logScroll.setBorder(BorderFactory.createLineBorder(BORDER_GREY));

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(logScroll, BorderLayout.CENTER);
        return panel;
    }

    // ---------- Actions (what happens when the user clicks something) ----------

    private void addStudent() {
        // trim() removes accidental spaces at the start and end
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            showError("Please enter a student name.");
            txtName.requestFocusInWindow(); // put the cursor back in the field to fix
            return;                          // stop here; don't add anything
        }

        // parseGrade shows its own error message and returns null if the input is bad
        Integer grade = parseGrade(txtGrade.getText());
        if (grade == null) {
            txtGrade.requestFocusInWindow();
            return;
        }

        students.add(new Student(name, grade)); // 1. update the data
        refreshTable();                         // 2. update the screen to match
        log("Added: " + name + " | Grade: " + grade);

        // Clear the fields so the next student can be typed straight away
        txtName.setText("");
        txtGrade.setText("");
        txtName.requestFocusInWindow();
    }

    private void searchGrade() {
        Integer target = parseGrade(txtSearch.getText());
        if (target == null) {
            txtSearch.requestFocusInWindow();
            return;
        }

        log("--- Linear search for grade " + target + " ---");
        table.clearSelection(); // remove any old highlighting

        List<Integer> matches = linearSearch(target);

        if (matches.isEmpty()) {
            log("Grade " + target + " not found.");
            return;
        }
        for (int i : matches) {
            // Highlight row i in the table (the range is from row i to row i)
            table.addRowSelectionInterval(i, i);
            log("Found: " + students.get(i).name() + " (position " + (i + 1) + ")");
        }
    }

    /**
     * Linear search: look at every item in order, one by one, until the end.
     * It logs each step so you can see how the algorithm works,
     * and returns the positions (indices) of all matches.
     */
    private List<Integer> linearSearch(int target) {
        List<Integer> matches = new ArrayList<>();
        for (int i = 0; i < students.size(); i++) {
            int grade = students.get(i).grade();
            log("  Checking index " + i + " -> " + grade);
            if (grade == target) {
                matches.add(i);
            }
        }
        return matches;
    }

    private void showAverage() {
        if (students.isEmpty()) {
            log("No grades entered yet.");
            return;
        }
        // A stream processes a collection step by step:
        // mapToInt pulls out each grade, average() computes the mean.
        // orElse(0) is the fallback if the list were empty (it can't be, we checked above).
        double avg = students.stream().mapToInt(Student::grade).average().orElse(0);
        // %.2f means "a decimal number with 2 digits after the point"; %d means "a whole number"
        log(String.format("Class average: %.2f (%d students)", avg, students.size()));
    }

    private void deleteSelected() {
        // Returns the row numbers the user has highlighted, in ascending order
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            showError("Select one or more rows to delete.");
            return;
        }
        // Delete from the END of the list backwards.
        // If we deleted from the front, every later row would shift up by one
        // and the remaining row numbers would point at the wrong students.
        for (int i = rows.length - 1; i >= 0; i--) {
            students.remove(rows[i]);
        }
        refreshTable();
        log("Deleted " + rows.length + " student(s).");
    }

    private void clearAll() {
        // Only ask for confirmation if there is something to lose
        if (!students.isEmpty()) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Remove all students?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) return; // user clicked No or closed the dialog
        }
        students.clear();
        refreshTable();
        txtLog.setText("");
        txtName.setText("");
        txtGrade.setText("");
        txtSearch.setText("");
    }

    // ---------- Helpers ----------

    /**
     * Converts text to a grade and checks it is in range.
     * Returns an Integer (not int) so it can return null to mean "invalid input".
     */
    private Integer parseGrade(String text) {
        try {
            // parseInt throws NumberFormatException if the text isn't a whole number
            int grade = Integer.parseInt(text.trim());
            if (grade < MIN_GRADE || grade > MAX_GRADE) {
                showError("Grade must be between " + MIN_GRADE + " and " + MAX_GRADE + ".");
                return null;
            }
            return grade;
        } catch (NumberFormatException ex) {
            showError("Grade must be a whole number.");
            return null;
        }
    }

    /**
     * Rebuilds the table from the students list.
     * Simple approach: clear everything, then add every row again.
     * This keeps the table and the list always in sync.
     */
    private void refreshTable() {
        tableModel.setRowCount(0); // remove all existing rows
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            // One Object[] = one row. The table redraws automatically when the model changes.
            tableModel.addRow(new Object[]{i + 1, s.name(), s.grade()});
        }
    }

    /** Adds a line to the log and scrolls down so the newest message is visible. */
    private void log(String message) {
        txtLog.append(message + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength()); // move cursor to the end
    }

    /** Shows a pop-up warning dialog. "this" makes the dialog appear centred over our window. */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input error", JOptionPane.WARNING_MESSAGE);
    }

    /** An invisible spacer, used to put consistent gaps between components in a row. */
    private static Component gap(int width) {
        return Box.createHorizontalStrut(width);
    }

    /** Makes the app look native and readable.
     * This must run BEFORE any components are created, main() calls it first.*/
    private static void setupLookAndFeel() {
        try {
            // Use the look of the operating system (Windows, macOS, ...) instead of the old "Metal" style
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // If it fails, Swing simply keeps its default look. Nothing to fix.
        }

        // Make every default font 14pt so text is comfortable to read.
        // UIManager stores all default settings in one big key -> value table.
        for (Object key : Collections.list(UIManager.getDefaults().keys())) {
            if (UIManager.get(key) instanceof FontUIResource font) {
                UIManager.put(key, new FontUIResource(font.deriveFont(14f)));
            }
        }
    }

    public static void main(String[] args) {
        // Swing is not thread-safe: all GUI work should happen on the Event Dispatch Thread (EDT).
        // invokeLater schedules this code to run on that thread.
        SwingUtilities.invokeLater(() -> {
            setupLookAndFeel();
            new GradeTrackerSwing();
        });
    }
}
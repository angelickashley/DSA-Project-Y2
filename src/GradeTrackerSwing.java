// Swing components (JFrame, JButton, JTable, ...) live in javax.swing
import javax.swing.*;
// DefaultTableModel holds the data a JTable displays; the renderer controls how a cell looks
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
// FontUIResource is the type Swing uses for its default fonts
import javax.swing.plaf.FontUIResource;
// AWT gives us layout managers (BorderLayout, FlowLayout, CardLayout, ...), Font and Color
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Student Grade Tracker with two pages (see the UI sketch):
 *   Page 1 - add students (name, student number, average grade)
 *   Page 2 - linear search by average grade, with results shown in a table
 *
 * Requires Java 16+ (uses a record).
 *
 * A JFrame is the main window. By extending it, our class IS a window,
 * so we can call methods like add() and setSize() directly.
 */
public class GradeTrackerSwing extends JFrame {

    // A record is a short way to write a simple data class.
    // Java automatically creates the constructor and the getters name(), number() and grade().
    private record Student(String name, int number, double grade) {}

    // Constants (static final) so values are defined in one place, not scattered as "magic numbers"
    private static final double MIN_GRADE = 0;
    private static final double MAX_GRADE = 100;

    // Names for our two pages. CardLayout uses these names to know which page to show.
    private static final String PAGE_ADD = "add";
    private static final String PAGE_SEARCH = "search";

    // --- Colour palette: one accent colour plus soft greys keeps the UI calm ---
    private static final Color ACCENT = new Color(0x2F6FED);       // blue, used for titles
    private static final Color SELECTION = new Color(0xDCE8FF);    // light blue for selected rows
    private static final Color BORDER_GREY = new Color(0xD9D9D9);  // thin outlines
    private static final Color MUTED = new Color(0x6B6B6B);        // grey text for status messages

    // The real data lives here. The table on screen is just a display of this list.
    private final List<Student> students = new ArrayList<>();

    // --- CardLayout: shows ONE panel at a time, like a stack of cards ---
    // Perfect for switching between "pages" inside the same window.
    private final CardLayout cards = new CardLayout();
    private final JPanel cardPanel = new JPanel(cards);

    // --- Page 1 components ---
    // The number is a preferred width in "columns" (roughly characters).
    private final JTextField txtName = new JTextField(20);
    private final JTextField txtNumber = new JTextField(20);
    private final JTextField txtGrade = new JTextField(20);
    private final JLabel lblAddStatus = new JLabel(" ");

    // --- Page 2 components ---
    // The prompt is a field (not hard-coded text) because the sketch says it should
    // change later depending on what the user searches for (name, number or grade).
    private final JLabel lblSearchPrompt = new JLabel("Enter grade:");
    private final JTextField txtSearch = new JTextField(6);
    private final JLabel lblSearchStatus = new JLabel(" ");

    // JTable shows the data, but the TableModel actually stores the rows.
    // Here we create a model with 3 column headers and 0 starting rows.
    private final DefaultTableModel tableModel =
            new DefaultTableModel(new String[]{"Name", "Student No.", "Avg Grade"}, 0) {
                // By default, table cells can be edited by double-clicking.
                // Returning false makes the table read-only.
                @Override public boolean isCellEditable(int row, int col) { return false; }
            };
    private final JTable table = new JTable(tableModel);

    public GradeTrackerSwing() {
        super("Student Grade Tracker");              // sets the window title
        setDefaultCloseOperation(EXIT_ON_CLOSE);     // clicking X ends the program

        // Padding around the whole window so nothing touches the edges.
        // getContentPane() is the main area inside the window's frame.
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Add both pages to the card panel, each with its name.
        // Only the first page added is visible until we call showPage().
        cardPanel.add(buildAddPage(), PAGE_ADD);
        cardPanel.add(buildSearchPage(), PAGE_SEARCH);
        add(cardPanel); // a JFrame uses BorderLayout by default, so this fills the whole window

        // Pressing Enter inside a text field fires its ActionListener.
        // "e -> ..." is a lambda: a short way to write "when this happens, run this code".
        txtName.addActionListener(e -> txtNumber.requestFocusInWindow());  // move to next field
        txtNumber.addActionListener(e -> txtGrade.requestFocusInWindow());
        txtGrade.addActionListener(e -> addStudent());
        txtSearch.addActionListener(e -> searchGrade());

        setSize(560, 480);
        setMinimumSize(new Dimension(520, 420));  // stops the window being shrunk until it breaks
        setLocationRelativeTo(null);              // passing null centres the window on screen
        setVisible(true);                         // windows are hidden until you call this
    }

    // ---------- UI construction ----------
    // Each method below builds one page of the window and returns it.

    /** UI page 1: the "add student" form. */
    private JPanel buildAddPage() {
        JLabel title = makeTitle("Student Grade Tracker");

        // GridLayout(3, 2, ...) = 3 rows, 2 columns: a label on the left, a field on the right.
        // The last two numbers are the horizontal and vertical gaps between cells.
        JPanel form = new JPanel(new GridLayout(3, 2, 12, 12));
        form.add(new JLabel("Student"));
        form.add(txtName);
        form.add(new JLabel("Student Number"));
        form.add(txtNumber);
        form.add(new JLabel("Average Grade"));
        form.add(txtGrade);
        // GridLayout stretches cells to fill all space, so we give the form a fixed size
        // to stop the fields becoming very wide when the window is wide.
        form.setPreferredSize(new Dimension(440, 112));

        JButton btnAdd = new JButton("Add Student");
        btnAdd.addActionListener(e -> addStudent());

        // This button switches to page 2
        JButton btnGoSearch = new JButton("Search Grade");
        btnGoSearch.addActionListener(e -> showPage(PAGE_SEARCH));

        // FlowLayout places components left to right. LEFT aligns them to the left edge.
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttons.add(btnAdd);
        buttons.add(gap(10));
        buttons.add(btnGoSearch);

        // A small message line under the buttons ("Added: ...")
        lblAddStatus.setForeground(MUTED);

        // We stack title, form, buttons and status vertically.
        // GridBagLayout/BoxLayout can do this too, but nested BorderLayouts are easy to read.
        JPanel formWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // keeps form at its size
        formWrap.add(form);

        JPanel top = new JPanel(new BorderLayout(0, 20));
        top.add(title, BorderLayout.NORTH);
        top.add(formWrap, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(0, 12));
        bottom.add(buttons, BorderLayout.NORTH);
        bottom.add(lblAddStatus, BorderLayout.CENTER);

        // Pinning "top" to NORTH means the content stays at the top of the page
        // instead of stretching to fill all the empty space.
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.add(top, BorderLayout.NORTH);
        page.add(bottom, BorderLayout.CENTER);
        return page;
    }

    /** UI page 2: linear search by grade, plus a table of students. */
    private JPanel buildSearchPage() {
        // The sketch has no "back" button, but without one there is no way to return to page 1
        JButton btnBack = new JButton("\u2190 Back");   // \u2190 is a left arrow
        btnBack.addActionListener(e -> showPage(PAGE_ADD));

        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backRow.add(btnBack);

        // --- Search row: "Linear Search" on the left, "Enter grade: [  ] [Search]" on the right ---
        JLabel algorithm = makeTitle("Linear Search");

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchGrade());

        JPanel searchGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        searchGroup.add(lblSearchPrompt);
        searchGroup.add(gap(8));
        searchGroup.add(txtSearch);
        searchGroup.add(gap(8));
        searchGroup.add(btnSearch);

        // WEST pins a component to the left edge, EAST to the right edge
        JPanel searchRow = new JPanel(new BorderLayout());
        searchRow.add(algorithm, BorderLayout.WEST);
        searchRow.add(searchGroup, BorderLayout.EAST);

        JPanel header = new JPanel(new BorderLayout(0, 14));
        header.add(backRow, BorderLayout.NORTH);
        header.add(searchRow, BorderLayout.CENTER);

        lblSearchStatus.setForeground(MUTED);

        JPanel page = new JPanel(new BorderLayout(0, 14));
        page.add(header, BorderLayout.NORTH);
        page.add(buildTable(), BorderLayout.CENTER);
        page.add(lblSearchStatus, BorderLayout.SOUTH);
        return page;
    }

    /** Styles the table and wraps it in a scroll pane. */
    private JScrollPane buildTable() {
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
        // in the "Student No." (column 1) and "Avg Grade" (column 2) columns.
        DefaultTableCellRenderer centered = new DefaultTableCellRenderer();
        centered.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centered);
        table.getColumnModel().getColumn(2).setCellRenderer(centered);

        // Tables don't scroll on their own. You must wrap them in a JScrollPane.
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_GREY));
        return scroll;
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

        // These helpers show their own error message and return null if the input is bad
        Integer number = parseStudentNumber(txtNumber.getText());
        if (number == null) {
            txtNumber.requestFocusInWindow();
            return;
        }

        Double grade = parseGrade(txtGrade.getText());
        if (grade == null) {
            txtGrade.requestFocusInWindow();
            return;
        }

        students.add(new Student(name, number, grade));
        lblAddStatus.setText("Added: " + name + " (No. " + number + ", grade " + grade + ")");

        // Clear the fields so the next student can be typed straight away
        txtName.setText("");
        txtNumber.setText("");
        txtGrade.setText("");
        txtName.requestFocusInWindow();
    }

    private void searchGrade() {
        Double target = parseGrade(txtSearch.getText());
        if (target == null) {
            txtSearch.requestFocusInWindow();
            return;
        }

        table.clearSelection(); // remove any old highlighting
        List<Integer> matches = linearSearch(target);

        if (matches.isEmpty()) {
            lblSearchStatus.setText("Grade " + target + " not found.");
            return;
        }
        for (int i : matches) {
            // Highlight row i in the table (the range is from row i to row i)
            table.addRowSelectionInterval(i, i);
        }
        lblSearchStatus.setText("Found " + matches.size() + " student(s) with grade " + target + ".");
    }

    /**
     * Linear search: look at every item in order, one by one, until the end.
     * Returns the positions (indices) of all students whose grade matches.
     */
    private List<Integer> linearSearch(double target) {
        List<Integer> matches = new ArrayList<>();
        for (int i = 0; i < students.size(); i++) {
            // Double.compare is the safe way to compare two doubles for equality
            if (Double.compare(students.get(i).grade(), target) == 0) {
                matches.add(i);
            }
        }
        return matches;
    }

    /** Switches to the named page (PAGE_ADD or PAGE_SEARCH). */
    private void showPage(String page) {
        if (page.equals(PAGE_SEARCH)) {
            // Refresh the table so it includes any students added on page 1
            refreshTable();
            lblSearchStatus.setText(" ");
            txtSearch.requestFocusInWindow();
        } else {
            txtName.requestFocusInWindow();
        }
        cards.show(cardPanel, page); // CardLayout hides the current page and shows this one
    }

    // ---------- Helpers ----------

    /** Converts text to a student number. Returns null (after showing an error) if invalid. */
    private Integer parseStudentNumber(String text) {
        try {
            // parseInt throws NumberFormatException if the text isn't a whole number
            int number = Integer.parseInt(text.trim());
            if (number <= 0) {
                showError("Student number must be a positive whole number.");
                return null;
            }
            // anyMatch checks whether ANY student in the list satisfies the condition
            if (students.stream().anyMatch(s -> s.number() == number)) {
                showError("A student with number " + number + " already exists.");
                return null;
            }
            return number;
        } catch (NumberFormatException ex) {
            showError("Student number must be a whole number.");
            return null;
        }
    }

    /**
     * Converts text to a grade and checks it is in range.
     * Returns a Double (not double) so it can return null to mean "invalid input".
     */
    private Double parseGrade(String text) {
        try {
            double grade = Double.parseDouble(text.trim());
            // Double.parseDouble accepts the text "NaN" (not a number), so we reject it explicitly
            if (Double.isNaN(grade) || grade < MIN_GRADE || grade > MAX_GRADE) {
                showError("Grade must be a number between " + (int) MIN_GRADE
                        + " and " + (int) MAX_GRADE + ".");
                return null;
            }
            return grade;
        } catch (NumberFormatException ex) {
            showError("Grade must be a number, e.g. 85 or 72.5.");
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
        for (Student s : students) {
            // One Object[] = one row. The table redraws automatically when the model changes.
            tableModel.addRow(new Object[]{s.name(), s.number(), s.grade()});
        }
    }

    /** Shows a pop-up warning dialog. "this" makes the dialog appear centred over our window. */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input error", JOptionPane.WARNING_MESSAGE);
    }

    /** Creates a bold, blue heading label. */
    private static JLabel makeTitle(String text) {
        JLabel label = new JLabel(text);
        // deriveFont creates a copy of the current font with a new style and size
        label.setFont(label.getFont().deriveFont(Font.BOLD, 22f));
        label.setForeground(ACCENT);
        return label;
    }

    /** An invisible spacer, used to put consistent gaps between components in a row. */
    private static Component gap(int width) {
        return Box.createHorizontalStrut(width);
    }

    /**
     * Makes the app look native and readable.
     * This must run BEFORE any components are created, which is why main() calls it first.
     */
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
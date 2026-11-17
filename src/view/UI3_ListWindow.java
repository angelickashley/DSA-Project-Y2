package view;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;


import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.*;
import java.sql.Time;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.ResourceBundle.Control;
import java.util.function.BiFunction;

import javax.swing.Timer;

import model.BinarySearcher;
import model.Student;

public class UI3_ListWindow {
	private static final int WINDOW_WIDTH  = 800;
	private static final int WINDOW_HEIGHT = 600;

	private static final int MIN_WINDOW_WIDTH  = 560;
	private static final int MIN_WINDOW_HEIGHT = 500;

	private static boolean WINDOW_IS_OPEN = false;

	private static DefaultTableModel TableModel;
	private static JTable Table;
	private static boolean flashSwitch = false;
	private static BinarySearcher<Student> searcher;
	private static HashMap<Integer, Color> rowColours;
	private static int searchSteps = 0;
	// private static int searchState;
	
	public static boolean IsWindowOpen() { return WINDOW_IS_OPEN;}

	public static void StartListWindow() {
		WINDOW_IS_OPEN = true;
		JFrame win = new JFrame(AppTheme.APP_NAME + " - List View");
		win.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		win.setMinimumSize(new Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT));
		win.setLocationRelativeTo(null);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		rowColours = new HashMap<>();

		// searcher = new BinarySearcher<Student>(null, null, null)
		

		AAButton addStudentButton = AppTheme.ArtfulButton("Add New Student", AppTheme.P_SIZE, 0, 0, () -> {
			controller.AppSystem.StartWindow_NewStudentList();
		});
		AAButton searchButton = AppTheme.ArtfulButton("Start Binary Search by sorting", AppTheme.P_SIZE, 0, 0, null);
		AAButton infoButton = AppTheme.ArtfulButton("i", AppTheme.P_SIZE, 0, 0, null);
		Runnable onClickInfo = () -> {
			UI4_InfoDialog.show(win);
		};
		infoButton.addActionListener(e -> onClickInfo.run());

		JPanel spacer = new JPanel();
		spacer.setOpaque(false);

		JComboBox<String> toSearchByDropdown = new AAComboBox<String>(new String[]{"name", "number", "grade"});
		toSearchByDropdown.setVisible(true);

		JPanel searchBoxContainer = new JPanel();
		CardLayout searchBoxCards = new CardLayout();

		AATextField searchNameField   = new AATextField(20, false, false);
		AATextField searchNumberField = new AATextField(20, true,  false);
		AATextField searchGradeField  = new AATextField(20, true,  true);

		searchBoxContainer.setLayout(searchBoxCards);
		searchBoxContainer.add(searchNameField, "searchName");
		searchBoxContainer.add(searchNumberField, "searchNumber");
		searchBoxContainer.add(searchGradeField,  "searchGrade");
		searchBoxContainer.setOpaque(false);
		
		searchBoxCards.show(searchBoxContainer, "searchName");

		Runnable onClickDropdown = () -> {
			String selectedSearchBy = (String)toSearchByDropdown.getSelectedItem();
			switch (selectedSearchBy) {
				case "name":   searchBoxCards.show(searchBoxContainer, "searchName");   break;
				case "number": searchBoxCards.show(searchBoxContainer, "searchNumber"); break;
				case "grade":  searchBoxCards.show(searchBoxContainer, "searchGrade");  break;
				default: break;
			}
		};
		toSearchByDropdown.addActionListener(e -> onClickDropdown.run());

		Runnable onClickSearchButton = () -> {
			if (searcher != null) {
				if (searchSteps == 0) {
					searchButton.setText("Next Step");
				}
				else {
					searcher.stepAlgorithm();
				}
				rowColours = new HashMap<>();
				rowColours.put(searcher.start(), UI4_InfoDialog.RED);
				rowColours.put(searcher.mid(), UI4_InfoDialog.PINK);
				rowColours.put(searcher.end(), UI4_InfoDialog.RED);
				// if (searcher.foundKey())
				// rowColours.put(searcher.f(), UI4_InfoDialog.RED);
				if (searcher.internalState() != model.searchStateEnum.SEARCHING) {
					rowColours = new HashMap<>();
					if (searcher.foundKey()) {
						rowColours.put(searcher.getKeyIndex(), UI4_InfoDialog.YELLOW);
						searchButton.setText("Key Found - End Search");
					}
					else {
						searchButton.setText("Key Not Found - End Search");
					}
					searcher = null;
				}
				searchSteps++;
				return;
			}
			else if (searchSteps > 0) {
				rowColours = new HashMap<>();
				searchButton.setText("Start Binary Search by sorting");
				searchSteps = 0;
			}
			// set up searcher

			String name  = "null";
			int    num   = 0;
			double grade = 0;
			BiFunction<Student,Student,Integer> comparator = null;
			
			AATextField field = null;
			String selectedSearchBy = (String)toSearchByDropdown.getSelectedItem();
			switch (selectedSearchBy) {
				case "name": {
					field = searchNameField;
					name = field.getText();
					if (!name.isEmpty()) comparator = Student::compareName;
				}break;
				case "number": {
					field = searchNumberField;
					num = field.getInt(-1);
					if (num >= 0) comparator = Student::compareNumber;
				}break;
				case "grade": {
					field = searchGradeField;
					grade = field.getDouble(-1);
					if (grade >= 0) comparator = Student::compareGrade;
				}break;
				default: break;
			}
			
			if (comparator != null) {
				searcher = new BinarySearcher<Student>(new Student(name, num, grade), model.Database.Students, comparator);
				controller.AppSystem.SortStudents(comparator);
				searchButton.setText("Start Binary Search");
				refreshTable();
				Table.repaint();
				searchSteps = 0;
			} else {
				AppTheme.ShowError("Please enter a valid search query.");
				field.requestFocusInWindow();
			}
		};
		searchButton.addActionListener(e -> onClickSearchButton.run());

		// searchBoxCards.setOpaque(false);

		// JPanel tableContainer = new JPanel();
		// tableContainer.setOpaque(false);
		// JTable table = createTable();
		// JScrollPane pane = buildTable(table);
		// tableContainer.add(pane);
		// JLabel tablePlaceholder = AppTheme.ArtfulText("placeholder text", AppTheme.P_SIZE, AppTheme.ACCENT_COLOUR_WHITE);

		AAPanel body = new AAPanel(AppTheme.APP_BACKGROUND_IMAGE_PATH);
		body.setLayout(new GridBagLayout());
		GridBagConstraints bodyConstraints = new GridBagConstraints();
		bodyConstraints.insets = new Insets(0,0,0,0);
		bodyConstraints.fill = GridBagConstraints.BOTH;


		JPanel headerPanel = new JPanel(new BorderLayout());  headerPanel.setOpaque(false);
		JPanel searchPanel = new JPanel(new GridBagLayout()); searchPanel.setOpaque(false);
		JPanel tablePanel  = new JPanel(new BorderLayout());  tablePanel .setOpaque(false);
		
		headerPanel.add(addStudentButton, BorderLayout.WEST);

		GridBagConstraints searchGrid = new GridBagConstraints();
		searchGrid.insets = new Insets(5, 5, 5, 5);
		
		setGrid(searchGrid, 0,0,1,1, 0,0, GridBagConstraints.NONE); searchPanel.add(toSearchByDropdown, searchGrid);
		setGrid(searchGrid, 1,0,1,1, 1,0, GridBagConstraints.HORIZONTAL); searchPanel.add(searchBoxContainer, searchGrid);
		setGrid(searchGrid, 2,0,1,1, 0,0, GridBagConstraints.NONE); searchPanel.add(infoButton, searchGrid);

		setGrid(searchGrid, 0,1,3,1, 1,0, GridBagConstraints.HORIZONTAL); searchPanel.add(searchButton, searchGrid);

		// searchPanel.add(searchBoxContainer, searchGrid);

		headerPanel.add(searchPanel, BorderLayout.EAST);

		Table = createTable(body.backgroundImage);
		JScrollPane tablePane = buildTable(Table);
		refreshTable();

		
		Timer timer = new Timer(UI4_InfoDialog.FLASH_MS, e -> {
			flashSwitch = !flashSwitch;
			Table.repaint();
		});
		timer.start();

		tablePanel.add(tablePane, BorderLayout.CENTER);
		
		bodyConstraints.gridx = 0;
		bodyConstraints.gridwidth = 1;
		bodyConstraints.gridheight = 1;
		bodyConstraints.weightx = 1;
		
		bodyConstraints.gridy = 0;
		body.add(headerPanel, bodyConstraints);
		
		bodyConstraints.gridy = 1;
		body.add(Box.createVerticalStrut(20),bodyConstraints);

		bodyConstraints.gridy = 2;
		bodyConstraints.weighty = 1;
		body.add(tablePanel, bodyConstraints);

		win.add(body);
		// debugOutline(win.getContentPane());
		win.setVisible(true);
	}

	static JTable createTable(Image backgroundImage) {
		TableModel = new DefaultTableModel(new String[]{"Name", "Student No.", "Avg Grade"}, 0) {
			// By default, table cells can be edited by double-clicking.
			// Returning false makes the table read-only.
			@Override public boolean isCellEditable(int row, int col) { return false; }
		};
		JTable table = new JTable(TableModel) {
			// public boolean flashSwitch = true;
			{
				setOpaque(false);
			}
			@Override
			protected void paintComponent(Graphics g) {
				g.drawImage(backgroundImage, 0,0,getWidth(), getHeight(), this);
				super.paintComponent(g);
			}
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				c.setFont(AppTheme.NormalFont(AppTheme.TABLE_ENTRY_SIZE));

				if (!isRowSelected(row)) {
					Color col = rowColours.get(row);
					col = flashSwitch ? new Color(0,0,0, 120) : col;
					c.setBackground(col == null ? new Color(0,0,0, 120) : col);
					// switch (row) {
					// 	case 1: c.setBackground(flashSwitch ? getBackground() : UI4_InfoDialog.RED); break;
					// 	case 3: c.setBackground(flashSwitch ? getBackground() : UI4_InfoDialog.PINK); break;
					// 	case 5: c.setBackground(flashSwitch ? getBackground() : UI4_InfoDialog.RED); break;
						
					// 	default: c.setBackground(getBackground()); break;
					// }
				}
				return c;
			}
    };
		return table;
	}

	static JScrollPane buildTable(JTable table) {
		table.setRowHeight(28);            // taller rows
		table.setBackground(new Color(0,0,0, 120));
		table.setGridColor(AppTheme.ACCENT_COLOUR_WHITE);
		table.setSelectionBackground(AppTheme.ACCENT_COLOUR_BRIGHT);
		// table.set
		table.setOpaque(false);
		
		table.setForeground(AppTheme.ACCENT_COLOUR_WHITE);
		table.setSelectionForeground(AppTheme.ACCENT_COLOUR_DARK);
		table.setFillsViewportHeight(true);           // fill the area even when there are few rows
		
		// table.getTableHeader().setBackground(new Color(0,0,0,120));
		// table.getTableHeader().setForeground(AppTheme.ACCENT_COLOUR_WHITE);
		JTableHeader header = table.getTableHeader();
		header.setOpaque(false);
		header.setBackground(AppTheme.ACCENT_COLOUR_WHITE);
		header.setForeground(AppTheme.ACCENT_COLOUR_DARK);
		header.setReorderingAllowed(false); // don't let users drag columns around
		header.setFont(AppTheme.NormalFont(AppTheme.P_SIZE));
		// JTableHeader header = table.getTableHeader();

		DefaultTableCellRenderer headerCellRenderer = (DefaultTableCellRenderer)header.getDefaultRenderer();
		headerCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		header.getColumnModel().getColumn(0).setHeaderRenderer(headerCellRenderer);
		header.getColumnModel().getColumn(1).setHeaderRenderer(headerCellRenderer);
		header.getColumnModel().getColumn(2).setHeaderRenderer(headerCellRenderer);
		
		
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
		cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(1).setCellRenderer(cellRenderer);
		table.getColumnModel().getColumn(2).setCellRenderer(cellRenderer);

		// Tables don't scroll on their own - wrap them in a JScrollPane.
		JScrollPane scroll = new JScrollPane(table);
		scroll.setOpaque(false);
		// scroll.setBorder(BorderFactory.createLineBorder(AppTheme.ACCENT_COLOUR_PURPLE));
		return scroll;
	}

	public static void refreshTable() {
		if (TableModel == null) return;
		TableModel.setRowCount(0); // remove all existing rows

		int studentsLen = controller.AppSystem.GetNumberOfStudents();
		for (int i = 0; i < studentsLen; i++) {
			Student s = controller.AppSystem.GetStudent(i);
			TableModel.addRow(new Object[]{s.getName(), s.getNumber(), s.getGrade()});
		}
		// for (Student s : controller.AppSystem.) {
		// 	// One Object[] = one row. The table redraws automatically when the model changes.
		// 	tableModel.addRow(new Object[]{s.name(), s.number(), s.grade()});
		// }
	}

	static void setGrid(GridBagConstraints c, int x,int y,int w,int h, double weightx,double weighty, int fill) {
		c.fill = fill;
		c.gridx     = x; c.gridy      = y;
		c.gridwidth = w; c.gridheight = h;
		c.weightx = weightx;
		c.weighty = weighty;
	}

	static void debugOutline(Container root) {
		for (Component comp : root.getComponents()) {
			if (comp instanceof JComponent jc) {
				jc.setBorder(BorderFactory.createLineBorder(AppTheme.ACCENT_COLOUR_WHITE));
			}
			if (comp instanceof Container child) {
				debugOutline(child);   // also outline nested panels
			}
		}
	}
}

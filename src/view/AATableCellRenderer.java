package view;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import model.Student;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.*;
import java.awt.*;

public class AATableCellRenderer extends DefaultTableCellRenderer {
	private final int rowToBold;
	private final int colToBold;

	public AATableCellRenderer(int rowToBold, int colToBold) {
		this.rowToBold = rowToBold;
		this.colToBold = colToBold;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (row == rowToBold && column == colToBold) {
			cellComponent.setFont(cellComponent.getFont().deriveFont(Font.BOLD));
		} else {
			cellComponent.setFont(cellComponent.getFont().deriveFont(Font.PLAIN));
		}

		return cellComponent;
	}
}

//==============================================================================
//	
//	Copyright (c) 2008-
//
//	Chair for Software Engineering - University of Konstanz
//	Prof. Dr. Stefan Leue
//	www.se.inf.uni-konstanz.de
//
//	Authors of this File:
//	* Husain Aljazzar (University of Konstanz)
//	* Florian Leitner-Fischer (University of Konstanz)
//	* Dimitar Simeonov (University of Konstanz)
//------------------------------------------------------------------------------
//	
// This file is part of DiPro.
//
//    DiPro is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    DiPro is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with DiPro.  If not, see <http://www.gnu.org/licenses/>.
//	
//==============================================================================

package dipro.run.wizard;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import parser.ast.Expression;

public abstract class ConstPanel extends JPanel {

	JTable tableConst;
	ConstTableModel modelTableConst;

	public ConstPanel(String title) {
		setLayout(new BorderLayout(5, 5));
		initGUIComp(title);

	}

	private void initGUIComp(String title) {

		modelTableConst = new ConstTableModel(this);
		tableConst = new JTable(modelTableConst);
		tableConst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JPanel panelConstModel = new JPanel();
		panelConstModel.setBorder(BorderFactory.createTitledBorder(title));
		panelConstModel.add(tableConst, BorderLayout.CENTER);

		JScrollPane scrollP = new JScrollPane(tableConst);
		panelConstModel.add(scrollP);
		add(panelConstModel, BorderLayout.CENTER);

	}

	public abstract void fillTables();

	public abstract boolean checkValidity();

	public abstract void parseConstants();

	public ConstTableModel getModelTable() {
		return modelTableConst;
	}

	public void cleanTable() {
		modelTableConst.listConstants.clear();
		modelTableConst.fireTableDataChanged();
	}

	Object parseValue(int type, String constValue) {
		switch (type) {
		case Expression.INT:
			return new Integer(constValue);
		case Expression.DOUBLE:
			return new Double(constValue);
		case Expression.BOOLEAN:
			return new Boolean(constValue);
		default:
			return constValue;
		}
	}
}

class ConstTableModel extends AbstractTableModel implements TableModelListener {
	ArrayList<Constant> listConstants;
	private ConstPanel m_panel;

	public ConstTableModel(ConstPanel panel) {
		m_panel = panel;
		listConstants = new ArrayList<Constant>();
		addTableModelListener(this);
	}

	public void addConstant(String name, int type, Object value) {
		addConstant(new Constant(name, type, value));
	}

	public Constant getConstant(int i) {
		return listConstants.get(i);
	}

	public void addConstant(Constant c) {
		listConstants.add(c);
		fireTableRowsInserted(listConstants.size() - 1,
				listConstants.size() - 1);
	}

	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		return listConstants.size();
	}

	public boolean isUnDefConstInTable() {
		for (Constant c : listConstants)
			if (c.unDef)
				return true;

		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Constant c = (Constant) listConstants.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return c.name;
		case 1: {
			switch (c.type) {
			case Expression.INT:
				return "int";
			case Expression.DOUBLE:
				return "double";
			case Expression.BOOLEAN:
				return "boolean";
			default:
				return "";
			}
		}
		case 2:
			return c.value.toString();
		default:
			return "";
		}
	}

	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "Name";
		case 1:
			return "Type";
		case 2:
			return "Value";
		default:
			return "";
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 2)
			return true;
		else
			return false;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		if (columnIndex == 2) {
			String s = (String) aValue;
			Constant c = (Constant) listConstants.get(rowIndex);
			try {
				m_panel.parseValue(c.type, s);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(
						null,
						"Please specify value of "
								+ getValueAt(rowIndex, 1), "Type Error",
						JOptionPane.CANCEL_OPTION);
				return;
			}
			c.value = s;
			if (s.length() > 0)
				c.unDef = false;
			else
				c.unDef = true;
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}

	public String toString() {
		String str = "";
		for (int i = 0; i < listConstants.size(); i++) {
			Constant c = (Constant) listConstants.get(i);
			str += c.toString();
			if (i != listConstants.size() - 1)
				str += ",";
		}
		return str;
	}

	class Constant {
		String name;
		int type;
		Object value;
		boolean unDef;

		public Constant(String name, int type, Object value) {
			this(name, type, value, true);
		}

		public Constant(String name, int type, Object value, boolean unDef) {
			this.name = name;
			this.type = type;
			this.value = value;
			this.unDef = unDef;
		}

		public String toString() {
			return name + "=" + value.toString() + " " + unDef;
		}

	}

	public void tableChanged(TableModelEvent e) {
		ExperimentSetup.getInstance().enableNext();

	}

}

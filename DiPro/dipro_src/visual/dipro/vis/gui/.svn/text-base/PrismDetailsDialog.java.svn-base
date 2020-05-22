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

package dipro.vis.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import prism.PrismException;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dipro.run.Registry;
import dipro.stoch.prism.PrismState;
import dipro.stoch.prism.PrismTransition;
import dipro.stoch.prism.PrismTransitionData;
import dipro.vis.AbstractVisualizer;
import dipro.vis.Visualizer;

public class PrismDetailsDialog extends DetailsDialog {

	private static final long serialVersionUID = -4935327932515768068L;

	private Visualizer visualizer;
	private String[] vars;
	private int varFirstRow;
	private int varNameMaxLength;
	private int valueMaxLength;
	Collection<String> relevantVars;
	JTable v1Table = null;
	JTable eTable = null;
	JTable v2Table = null;
	private TableCellRenderer cellRenderer = new ColorRenderer();
	private JPanel contentPanel;

	public PrismDetailsDialog(Visualizer visualizer) throws Exception {
		super();
		contentPanel = new JPanel();
		setContentPane(contentPanel);
		this.visualizer = visualizer;
		varFirstRow = 3;
		List<String> labelNames = visualizer.getContext().getGraph()
				.getVertexLabels();
		vars = new String[labelNames.size()];
		try {
			relevantVars = visualizer.getContext().getProperty()
					.relevantLabels();
		} catch (Exception e) {
			relevantVars = new LinkedList<String>();
			Registry.getMain().handleError("Failed to get the list of the property relevant labels!", e);
		}
		int i = 0;
		for (String name : relevantVars) {
			vars[i] = name;
			if (vars[i].length() > varNameMaxLength)
				varNameMaxLength = vars[i].length();
			i++;
		}
		for (String name : labelNames) {
			if (!relevantVars.contains(name)) {
				vars[i] = name;
				if (vars[i].length() > varNameMaxLength)
					varNameMaxLength = vars[i].length();
				i++;
			}
		}
	}

	private JTable createTransitionTable(PrismTransition t) {
		Vector<Vector<String>> rowData = new Vector<Vector<String>>();
		PrismTransitionData tData = t.getTransitionData();
		Vector<String> row = new Vector<String>();
		row.add(0, "Prob./Rate");
		row.add(1, Float.toString(tData.getProbOrRate()));
		rowData.add(row);
		row = new Vector<String>();
		row.add(0, "Action");
		row.add(1, tData.getLabel());
		rowData.add(row);
		row = new Vector<String>();
		row.add(0, "Module");
		row.add(1, tData.getModuleName());
		rowData.add(row);
		row = new Vector<String>();
		row.add(0, "Assignment");
		row.add(1, tData.getAssignment());
		rowData.add(row);
		Vector<String> columnNames = new Vector<String>();
		columnNames.add(0, "Attribute");
		columnNames.add(1, "Value");
		JTable table = new JTable(rowData, columnNames);
		table.getColumn("Attribute").setPreferredWidth(80);
		table.getColumn("Value").setPreferredWidth(160);
		return table;
	}

	protected JTable createStateTable(PrismState s) {
		/* Variables */
		// Values values = ((PrismState)s).values();
		String[][] data = new String[vars.length + 3][2];
		// data[0][0] = "Vertex Id";
		// if(data[0][0].length()>varNameMaxLength) varNameMaxLength =
		// data[0][0].length();
		// data[0][1] = Integer.toString(s.getID());
		// if(data[0][1].length()>valueMaxLength) valueMaxLength =
		// data[0][1].length();
		double f = visualizer.getSearchTree().f(s);
		double g = visualizer.getSearchTree().g(s);
		double h = visualizer.getSearchTree().h(s);
		data[0][0] = "f-value";
		if (data[0][0].length() > varNameMaxLength)
			varNameMaxLength = data[0][0].length();
		data[0][1] = AbstractVisualizer.getFormat().format(f);
		if (data[0][1].length() > valueMaxLength)
			valueMaxLength = data[0][1].length();
		data[1][0] = "g-value";
		if (data[1][0].length() > varNameMaxLength)
			varNameMaxLength = data[1][0].length();
		data[1][1] = AbstractVisualizer.getFormat().format(g);
		if (data[1][1].length() > valueMaxLength)
			valueMaxLength = data[1][1].length();
		data[2][0] = "h-value";
		if (data[2][0].length() > varNameMaxLength)
			varNameMaxLength = data[2][0].length();
		data[2][1] = AbstractVisualizer.getFormat().format(h);
		if (data[2][1].length() > valueMaxLength)
			valueMaxLength = data[2][1].length();
		int row = 3;
		for (int j = 0; j < vars.length; j++) {
			String name = vars[j];
			data[row][0] = name;
			try {
				data[row][1] = s.getLabelValue(name).toString();
			} catch (PrismException e) {
				data[row][1] = new String("Unknown");
			}
			if (data[row][1].length() > valueMaxLength)
				valueMaxLength = data[row][1].length();
			row++;
		}
		String[] columnNames = { "Variable", "Value" };
		JTable table = new JTable(data, columnNames);
		table.getColumn("Variable").setPreferredWidth(120);
		table.getColumn("Value").setPreferredWidth(120);
		table.setDefaultRenderer(Object.class, cellRenderer);
		return table;
	}

	private void fill() {
		// FormLayout layout = new FormLayout("100dlu, 5dlu, 45dlu", "p, 3dlu,
		// p");
		// DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		// CellConstraints cc = new CellConstraints();
		// builder.add(pane, cc.xyw(1,1,3));
		// builder.add(viewButton, cc.xy(3, 3));
		// add(builder.getPanel());
		int rows = 1;
		rows = e != null ? rows + 1 : rows;
		rows = v2 != null ? rows + 1 : rows;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rows; i++) {
			if (i != 0)
				sb.append(", 3dlu, ");
			sb.append("p");
		}
		FormLayout layout = new FormLayout("200dlu", sb.toString());
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		CellConstraints cc = new CellConstraints();
		v1Table = createStateTable((PrismState) v1);
		eTable = null;
		v2Table = null;
		if (v2 != null) {
			v2Table = createStateTable((PrismState) v2);
		}
		if (e != null) {
			eTable = createTransitionTable((PrismTransition) e);
		}

		String title;
		if (eTable != null) {
			title = "Transition";
			// eTable.setPreferredSize(new Dimension(100,300));
			JPanel panel = new JPanel();
			panel.add(eTable);
			panel.setBorder(new TitledBorder(title));
			// eTable.setPreferredSize(eTable.getSize());
			// panel.setPreferredSize(eTable.getPreferredSize());
			// contentPanel.add(panel, BorderLayout.NORTH);
			builder.add(panel, cc.xy(1, 1));
//			System.out.println("eTable Size: " + eTable.getSize());
//			System.out.println("eTable Pref. Size: "
//					+ eTable.getPreferredSize());
//			System.out.println("ePanel Size: " + panel.getSize());
//			System.out
//					.println("ePanel Pref. Size: " + panel.getPreferredSize());
		}

		title = "State";
		if (e != null)
			title = "Origin " + title;
		else if (v2 != null)
			title = "First " + title;
		// v1Table.setBorder(new TitledBorder(title));
		// v1Table.setPreferredSize(new Dimension(100,300));
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(title));
		panel.add(v1Table);
		// v1Table.setPreferredSize(v1Table.getSize());
		// panel.setPreferredSize(v1Table.getPreferredSize());
		// contentPanel.add(panel,
		// e==null?BorderLayout.NORTH:BorderLayout.CENTER);
		int row = e != null ? 3 : 1;
		builder.add(panel, cc.xy(1, row));
//		System.out.println("v1Table Size: " + v1Table.getSize());
//		System.out.println("v1Table Pref. Size: " + v1Table.getPreferredSize());
//		System.out.println("v1Panel Size: " + panel.getSize());
//		System.out.println("v1Panel Pref. Size: " + panel.getPreferredSize());
		if (v2Table != null) {
			title = "State";
			if (e != null)
				title = "Target " + title;
			else
				title = "Second " + title;
			// v2Table.setPreferredSize(new Dimension(100,300));
			panel = new JPanel();
			panel.setBorder(new TitledBorder(title));
			panel.add(v2Table);
			// v2Table.setPreferredSize(v2Table.getSize());
			// panel.setPreferredSize(v2Table.getPreferredSize());
			// contentPanel.add(panel,
			// e==null?BorderLayout.CENTER:BorderLayout.SOUTH);
			row = row + 2;
			builder.add(panel, cc.xy(1, row));
//			System.out.println("v2Table Size: " + v2Table.getSize());
//			System.out.println("v2Table Pref. Size: "
//					+ v2Table.getPreferredSize());
//			System.out.println("v2Panel Size: " + panel.getSize());
//			System.out.println("v2Panel Pref. Size: "
//					+ panel.getPreferredSize());
		}
		contentPanel.add(builder.getPanel());
		// JScrollPane sp = new JScrollPane(contentPanel);
		// sp.setMaximumSize(contentPanel.getSize());
		// add(sp);
		// getContentPane().add(contentPanel);
		pack();
		// contentPanel.setPreferredSize(contentPanel.getSize());
	}

	// private void formatComparasion() {
	// for(int i=0; i< vars.length; i++) {
	// try {
	// Object o1 = ((PrismState)s1).values().getValueOf(vars[i]);
	// Object o2 = ((PrismState)s2).values().getValueOf(vars[i]);
	// if(!o1.equals(o2)) {
	// highlightRow(varFirstRow+i, s2Table);
	// }
	// } catch (PrismException e) {}
	// }
	// }

	@Override
	public void update() {
		contentPanel.removeAll();
		fill();
		// Container c = getParent();
		// setPreferredSize(new Dimension(c.getPreferredSize().width, 200));
		// this.getRootPane().updateUI();
		setVisible(true);
	}

	class ColorRenderer extends DefaultTableCellRenderer {

		public Component getTableCellRendererComponent(JTable table,
				Object obj, boolean isSelected, boolean hasFocus, int row,
				int column) {
			StringBuilder sb = new StringBuilder();
			sb.append("<HTML>");
			if (row < varFirstRow) {
				this.setBackground(Color.LIGHT_GRAY);
				sb.append(obj);
			} else {
				this.setBackground(Color.WHITE);
				int i = row - varFirstRow;
				if (relevantVars.contains(vars[i])) {
					sb.append("<B>");
					sb.append(obj);
					sb.append("</B>");
				} else
					sb.append(obj);
				if (table == v2Table) {
					try {

						Object o1 = v1.getLabelValue(vars[i]);
						Object o2 = v2.getLabelValue(vars[i]);
						if (!o1.equals(o2)) {
							setBackground(Color.YELLOW);
						}
					} catch (Exception e) {
					}
				}
			}
			sb.append("</HTML>");
			setText(sb.toString());
			return this;
		}
	}
}

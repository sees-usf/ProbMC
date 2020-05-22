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
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class GUITest extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3921195889714260868L;

	private JSplitPane mainPane;
	private JSplitPane topPane;
	private JTabbedPane drawTabs;
	private JScrollPane detailsPane;

	public GUITest() throws Exception {
		UIManager
				.setLookAndFeel("com.jgoodies.looks.plastic.PlasticLookAndFeel");
		// JPanel panel = new JPanel();
		buildPanel();
		getContentPane().add(mainPane);
		setSize(new Dimension(800, 600));
		setVisible(true);
	}

	protected void buildPanel() {
		mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		mainPane.setOneTouchExpandable(true);
		topPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		topPane.setOneTouchExpandable(true);
		mainPane.setTopComponent(topPane);
		buildDetailPanel();
		setLeft(buildDrawPanel());
		setRight(buildSidePanel());
		setBottom(buildBottomPanel());
	}

	protected void buildDetailsPanels() {
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		detailsPane.setLayout(layout);
		detailsPane.add(buildDetailPanel());
		validate();
		pack();
	}

	protected JPanel buildDetailPanel() {
		detailsPane = new JScrollPane();
		PanelBuilder builder = new PanelBuilder(new FormLayout(
				"3dlu, pref, 3dlu", "p, p, p"));
		CellConstraints cc = new CellConstraints();
		builder.add(new JLabel("Name"), cc.xy(1, 1));
		builder.addSeparator("Settings");
		return builder.getPanel();
	}

	protected void setLeft(Component left) {
		topPane.setLeftComponent(left);
	}

	protected void setRight(Component right) {
		topPane.setRightComponent(right);
	}

	protected void setBottom(Component bottom) {
		mainPane.setBottomComponent(bottom);
	}

	public JComponent buildDrawPanel() {
		JPanel panel = new JPanel();
		// panel.setPreferredSize(new Dimension(400, 400));
		panel.setBackground(new Color(255, 255, 255));
		panel.add(new JLabel("Test"));
		panel.setPreferredSize(new Dimension(800, 600));
		PanelBuilder builder = new PanelBuilder(new FormLayout("800dlu",
				"800dlu"));
		drawTabs = new JTabbedPane();
		drawTabs.setPreferredSize(new Dimension(800, 600));
		drawTabs.setMinimumSize(new Dimension(400, 400));
		drawTabs.addTab("Tab1", panel);
		drawTabs.addTab("Tab2", new JPanel());
		drawTabs.addTab("Tab 2.5", new JPanel());
		drawTabs.addTab("Tab3", detailsPane);
		return drawTabs;
	}

	public JPanel buildSidePanel() {
		FormLayout layout = new FormLayout("right:pref, 3dlu, 60dlu");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);

		CellConstraints cc = new CellConstraints();

		builder.appendSeparator("General");
		builder.append("Name", new JTextField("File V. 8"));
		builder.append("File", new JLabel("/usr/local/file"));
		builder.append("Icon", new JLabel("/usr/local/icon"));
		builder.append("Name", new JTextField("/usr/local/name"));
		builder.appendSeparator("Settings");
		builder.append("Setting", new JLabel("set"));
		builder.append("Setting", new JTextField("setting"));
		builder.append(detailsPane);
		return builder.getPanel();
	}

	public JComponent buildBottomPanel() {
		// FormLayout layout = new FormLayout("D:grow", "40dlu:grow");
		// PanelBuilder builder = new PanelBuilder(layout);
		// CellConstraints cc = new CellConstraints();
		// builder.add(, cc.xy(1, 1, "fill, fill"));
		JScrollPane pane = new JScrollPane();
		pane.add(new JTextArea("\n\n", 20, 30));
		// return pane;
		return new JTextArea();
	}

	public JPanel buildForm() {

		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, 50dlu, 7dlu, right:50dlu, 3dlu, 50dlu", // columns
				"p, 3dlu, p, 3dlu, p, 9dlu, p, 3dlu, p, 3dlu, p"); // rows

		// Specify that columns 1 & 5 as well as 3 & 7 have equal widths.

		layout.setColumnGroups(new int[][] { { 1, 5 }, { 3, 7 } });
		//	
		// Obtain a reusable constraints object to place components in the grid.
		CellConstraints cc = new CellConstraints();

		// Fill the grid with components; the builder can create
		// frequently used components, e.g. separators and labels.

		// Add a titled separator to cell (1, 1) that spans 7 columns.
		PanelBuilder builder = new PanelBuilder(layout);

		builder.addSeparator("General", cc.xyw(1, 1, 7));

		JLabel companyField = new JLabel();
		// companyField.setPreferredSize(new Dimension(100, 20));
		JLabel contactField = new JLabel();
		JTextField ptiField = new JTextField();
		JLabel powerField = new JLabel();
		JLabel radiusField = new JLabel();
		// radiusField.setPreferredSize(new Dimension(100, 20));
		JLabel diameterField = new JLabel();

		builder.addLabel("Company", cc.xy(1, 3));
		builder.add(companyField, cc.xyw(3, 3, 5));
		builder.addLabel("Contact", cc.xy(1, 5));
		builder.add(contactField, cc.xyw(3, 5, 5));

		builder.addSeparator("Propeller", cc.xyw(1, 7, 7));
		builder.addLabel("PTI [kW]", cc.xy(1, 9));
		builder.add(ptiField, cc.xy(3, 9));
		builder.addLabel("Power [kW]", cc.xy(5, 9));
		builder.add(powerField, cc.xy(7, 9));
		builder.addLabel("R [mm]", cc.xy(1, 11));
		builder.add(radiusField, cc.xy(3, 11));
		builder.addLabel("D [mm]", cc.xy(5, 11));
		builder.add(diameterField, cc.xy(7, 11));

		// The builder holds the layout container that we now return.
		return builder.getPanel();

	}

	public static void main(String[] args) {
		try {
			GUITest gUITest = new GUITest();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

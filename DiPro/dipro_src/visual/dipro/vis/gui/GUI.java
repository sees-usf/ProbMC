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
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
//import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;


import dipro.run.Config;
//import dipro.run.Registry;
import dipro.vis.Visualizer;

public class GUI extends JFrame {

	private final static int MAX_PREF_WIDTH = 1280;
	private final static int MAX_PREF_HEIGTH = 1024;

	private static final long serialVersionUID = -1469293832516671475L;
	private static GUI single = null;
//	private static JLabel dummy = new JLabel("No visualization view is loaded!");
	private static JLabel dummy = new JLabel(IconLoader.get("splash.png"));


	
	
	private MainMenuBar menuBar;
	//private JPanel pane;
	private JSplitPane mainPane;
	private JTextArea console;
	private boolean loaded;
	private VisualizationControl visControl;
	private ToolFrame toolFrame;

	public static GUI createSingleGUI() throws Exception {
		if (GUI.single == null) {
			single = new GUI();
		}
		return single;
		
	}
	
	private GUI() throws Exception {
		loaded = false;
	}
	
	
	public boolean isGUILoaded(){
		return loaded;
	}
	
	
	public void load() throws Exception {
		if (loaded)
			throw new IllegalStateException("GUI had been loaded before.");
		UIManager
				.setLookAndFeel("com.jgoodies.looks.plastic.PlasticLookAndFeel");
		setTitle(Config.programName);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		// System.out.println("Screen Size = "+d);
		d = new Dimension(Math.min(d.width, MAX_PREF_WIDTH), Math.min(d.height,
				MAX_PREF_HEIGTH));
		// System.out.println("Screen Size (adjusted) = "+d);
		setPreferredSize(new Dimension((int) (0.7 * d.width),
				(int) (0.7 * d.height)));
		// System.out.println("Preferred Size: "+getPreferredSize());
		// System.out.println("Size: "+getSize());
		setSize(getPreferredSize());
		// System.out.println("Size: "+getSize());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		
		// Set menu bar
		menuBar = new MainMenuBar();
		setJMenuBar(menuBar);
		// Add structure
		mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainPane.setOneTouchExpandable(true);
		mainPane.setResizeWeight(0.8f);
		add(mainPane);

		// JPanel dummy = new JPanel();
		// System.out.println("View Panel Pref. Size:
		// "+dummy.getPreferredSize());
		// System.out.println("View Panel Size: "+dummy.getSize());
		dummy.setPreferredSize(new Dimension(getPreferredSize().width,
				(int) (0.9 * getPreferredSize().height)));
		dummy.setSize(dummy.getPreferredSize());
		mainPane.setTopComponent(dummy);
		// System.out.println("Vis Panel Pref. Size:
		// "+dummy.getPreferredSize());
		// System.out.println("Vis Panel Size: "+dummy.getSize());
		// dummy.add(new JLabel());
		// visPanel = new JPanel();
		// visPanel.setPreferredSize(new Dimension(getPreferredSize().width,
		// (int) (0.9*getPreferredSize().height)));
		// visPanel.setSize(visPanel.getPreferredSize());
		// visPanel.add(dummy);
		// mainPane.setTopComponent(visPanel);

		/* Set text output */
		console = new JTextArea();
		console.setEditable(false);
		console.setBackground(Color.white);
		console.setSize(getPreferredSize().width, 100);
		// console.setPreferredSize(new Dimension(getPreferredSize().width,
		// getPreferredSize().height/10));
		// console.setSize(console.getPreferredSize());
		JScrollPane jPane = new JScrollPane(console);
		jPane.setWheelScrollingEnabled(true);
		// jPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// System.out.println("Text Panel Pref. Size:
		// "+jPane.getPreferredSize());
		// System.out.println("Text Panel Size: "+jPane.getSize());
		jPane.setPreferredSize(new Dimension(getPreferredSize().width,
				(int) (Math.max(100,0.25 * getPreferredSize().height))));
		jPane.setSize(jPane.getSize());
		// System.out.println("Text Panel Pref. Size:
		// "+jPane.getPreferredSize());
		// System.out.println("Text Panel Size: "+jPane.getSize());
		mainPane.setBottomComponent(jPane);
		int x = mainPane.getPreferredSize().height
				- 200;
		//x = 10;
		mainPane.setDividerLocation(x);

		// System.out.println("Divider Location:
		// "+mainPane.getDividerLocation());
		// mainPane.setDividerLocation(50);
		// System.out.println("Divider Location:
		// "+mainPane.getDividerLocation());
		setVisible(true);
		loaded = true;
	}
//	}
////For PrismCX
////	public void load(JPanel pane) throws Exception {
////		if (loaded)
////			throw new IllegalStateException("GUI had been loaded before.");
//////		UIManager
//////				.setLookAndFeel("com.jgoodies.looks.plastic.PlasticLookAndFeel");
////		setTitle(Config.programName);
////		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
////		// System.out.println("Screen Size = "+d);
////		d = new Dimension(Math.min(d.width, MAX_PREF_WIDTH), Math.min(d.height,
////				MAX_PREF_HEIGTH));
////		// System.out.println("Screen Size (adjusted) = "+d);
////		setPreferredSize(new Dimension((int) (0.7 * d.width),
////				(int) (0.7 * d.height)));
////		// System.out.println("Preferred Size: "+getPreferredSize());
////		// System.out.println("Size: "+getSize());
////		setSize(getPreferredSize());
////		// System.out.println("Size: "+getSize());
////		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////
////		
////		this.pane = pane;
////		
////		// Set menu bar
//////		menuBar = new MainMenuBar();
//////		setJMenuBar(menuBar);
////		// Add structure
////		mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
////		mainPane.setOneTouchExpandable(true);
////		mainPane.setResizeWeight(0.8f);
////		add(mainPane);
////
////		// JPanel dummy = new JPanel();
////		// System.out.println("View Panel Pref. Size:
////		// "+dummy.getPreferredSize());
////		// System.out.println("View Panel Size: "+dummy.getSize());
////		dummy.setPreferredSize(new Dimension(getPreferredSize().width,
////				(int) (0.9 * getPreferredSize().height)));
////		dummy.setSize(dummy.getPreferredSize());
////		mainPane.setTopComponent(dummy);
////		// System.out.println("Vis Panel Pref. Size:
////		// "+dummy.getPreferredSize());
////		// System.out.println("Vis Panel Size: "+dummy.getSize());
////		// dummy.add(new JLabel());
////		// visPanel = new JPanel();
////		// visPanel.setPreferredSize(new Dimension(getPreferredSize().width,
////		// (int) (0.9*getPreferredSize().height)));
////		// visPanel.setSize(visPanel.getPreferredSize());
////		// visPanel.add(dummy);
////		// mainPane.setTopComponent(visPanel);
////
////		/* Set text output */
////		console = new JTextArea();
////		console.setEditable(false);
////		console.setBackground(Color.white);
////		// console.setPreferredSize(new Dimension(getPreferredSize().width,
////		// getPreferredSize().height/10));
////		// console.setSize(console.getPreferredSize());
////		JScrollPane jPane = new JScrollPane(console);
////		jPane.setWheelScrollingEnabled(true);
////		// jPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
////		// System.out.println("Text Panel Pref. Size:
////		// "+jPane.getPreferredSize());
////		// System.out.println("Text Panel Size: "+jPane.getSize());
////		jPane.setPreferredSize(new Dimension(getPreferredSize().width,
////				(int) (0.25 * getPreferredSize().height)));
////		jPane.setSize(jPane.getSize());
////		// System.out.println("Text Panel Pref. Size:
////		// "+jPane.getPreferredSize());
////		// System.out.println("Text Panel Size: "+jPane.getSize());
////		mainPane.setBottomComponent(jPane);
////		int x = mainPane.getPreferredSize().height
////				- console.getPreferredSize().height;
////		mainPane.setDividerLocation(x);
////
////		// System.out.println("Divider Location:
////		// "+mainPane.getDividerLocation());
////		// mainPane.setDividerLocation(50);
////		// System.out.println("Divider Location:
////		// "+mainPane.getDividerLocation());
//////		setVisible(true);
////		loaded = true;
////	}
//
//	public void openVisualization(Visualizer visualizer) {
//		this.visControl = new VisualizationControl(visualizer);
//		Component visualizationComponent = visualizer
//				.getVisualizationComponent();
//		int x = mainPane.getDividerLocation();
//		pane.removeAll();
//		pane.add(((JSplitPane)visualizationComponent).getLeftComponent(), BorderLayout.CENTER);
////		addNavigationComponent(pane, (Graph2DView) ((JSplitPane)visualizationComponent).getLeftComponent());
//		pane.validate();
//		mainPane.setTopComponent(((JSplitPane)visualizationComponent).getRightComponent());
//		mainPane.setDividerLocation(x);
//		// System.out.println("New vis panel pref. size:
//		// "+visComponent.getPreferredSize());
//		// System.out.println("New vis panel size: "+visComponent.getSize());
//		// visPanel.remove(dummy);
//		// visPanel.add(visComponent);
//		// visComponent.setSize(visPanel.getSize());
////		 visPanel.updateUI();
//		
//	}
		
		public void openVisualization(Visualizer visualizer) {
			this.visControl = new VisualizationControl(visualizer);
			Component visualizationComponent = visualizer
					.getVisualizationComponent();
			int x = mainPane.getDividerLocation();
			mainPane.setTopComponent(visualizationComponent);
			mainPane.setDividerLocation(x);
		
		}

	public void setRootPane(JSplitPane rootPane){
		mainPane = rootPane;
}
	public void releaseVisualizationView() {
		// visPanel.removeAll();
		// visPanel.add(dummy);
		this.visControl = null;
		int x = mainPane.getDividerLocation();
		mainPane.setTopComponent(dummy);
		mainPane.setDividerLocation(x);
	}

	// public void setExperiment(ExperimentPanel e) {
	// // mainPane.setRightComponent(e);
	// // mainPanel.validate();
	// // menuBar.setExperimentSaveAble(true);
	// // getRootPane().updateUI();
	// // Registry.getMain().tech().println("Experiment panel added to gui");
	// }

	// public void setDetailsPanel(JPanel panel) {
	// ((ExperimentPanel)mainPane.getRightComponent()).setDsetDetailsPanel(panel);
	// getRootPane().updateUI();
	// }

	public void showErrorDialog(String errorMessage) {
		JOptionPane.showMessageDialog(this, errorMessage, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	public void showWarningDialog(String warningMessage) {
		JOptionPane.showMessageDialog(this, warningMessage, "Warning",
				JOptionPane.WARNING_MESSAGE);
	}
	
	public void showMessageDialog(String infoMessage) {
		JOptionPane.showMessageDialog(this, infoMessage, "Information",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public JTextArea getConsole() {
		return console;
	}

	public VisualizationControl getVisualizationControl() {
		return visControl;
	}
	
	public JFrame getToolFrame() {
		return toolFrame;
		
	}
	
	public void setToolFrame(ToolFrame toolFrame) {
		this.toolFrame = toolFrame;
	}
	
}

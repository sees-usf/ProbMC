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

import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

import dipro.run.Registry;
import dipro.run.VisMain;

public class ActionsManager {

	/* ***** File actions ***** */

	/* Graph file actions */

	// protected Action graphFileOpen;
	protected Action graphFileSave;
	protected Action graphFileSaveAs;
	protected Action graphImageSaveAs;

	/* Model file actions */

	protected Action modelFileSetOpen;
	protected Action modelFileOpen;
	protected Action propertiesFileOpen;
	protected Action configFileOpen;
	// protected Action modelFileSave;
	// protected Action modelFileSaveAs;

	/* Experiment actions */
	public Action openExperimentFile;
	public Action loadNextExperiment;
	public Action reloadExperiment;

	public Action newExperiment;
	public Action loadExperiment;
	public Action saveExperiment;
	public Action saveExperimentAs;

	public Action showTools;
	public Action exportImage;
	public Action visualizeFromScratch;

	/* Dot file actions */
	protected Action dotFileSave;
	protected Action dotFileSaveAs;

	/* Plain file actions */

	protected Action plainFileSave;
	protected Action plainFileSaveAs;

	/* ***** Interactive actions ***** */

	/* Graph interactions */

	protected Action setAlgorithmStartNode;

	/* Algorithm actions */

	protected Action runAlgorithm;
	protected Action stepAlgorithm;
	protected Action haltAlgorithm;

	/* Interactive actions */

	protected Action highlightCE;
	protected Action showLabels;
	protected Action hideLabels;
	protected Action switch2D;
	protected Action switch3D;

	protected Action cluster;
	protected Action collapseClusters;

	/* Global actions */

	public Action closeApplication;

	private static ActionsManager instance = new ActionsManager();

	// private GlobalControlGui gui() {
	// return (GlobalControlGui)DiPro.getGlobalControl();
	// }

	public ActionsManager() {
		super();
		init();
	}

	public void init() {

		openExperimentFile = new AbstractAction("Open file") {
			JFileChooser fc = new JFileChooser();

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int x = fc.showOpenDialog(((VisMain) Registry.getMain())
						.getGUI());
				if (x == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					Registry.getMain().loadExperiments(f.getAbsolutePath());
					Registry.getMain().runNextExperiment();
				}
			}
		};

		newExperiment = new AbstractAction("New experiment") {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				int n = JOptionPane.showConfirmDialog(null,
						"Discard current experiment/configuration?", "Confirmation",
						JOptionPane.YES_NO_OPTION);

				if (n == JOptionPane.YES_OPTION)
					((VisMain) Registry.getMain()).run(false);
				else
					((VisMain) Registry.getMain()).run(true);

			}

		};

		loadNextExperiment = new AbstractAction("Load next") {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Registry.getMain().runNextExperiment();
			}

		};

		reloadExperiment = new AbstractAction("Reload experiment") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Registry.getMain().reRunExperiment();
			}
		};

		exportImage = new AbstractAction("Export image") {
			@Override
			public void actionPerformed(ActionEvent e) {
				VisualizationControl vc = ((VisMain) Registry.getMain())
						.getGUI().getVisualizationControl();
				vc.exportImage();
			}
		};

		visualizeFromScratch = new AbstractAction("Visualize from scratch") {
			public void actionPerformed(ActionEvent e) {
				VisualizationControl vc = ((VisMain) Registry.getMain())
						.getGUI().getVisualizationControl();
				vc.visualizeFromScratch();
			}
		};

		saveExperiment = new AbstractAction("Save Experiment") {

			/**
			 * 
			 */
			private static final long serialVersionUID = -6741035831269877486L;

			public void actionPerformed(ActionEvent e) {
				// DiPro.getGlobalControl().saveCurrentExperiment();
			}

		};

		saveExperimentAs = new AbstractAction("Save experiment as") {

			/**
			 * 
			 */
			private static final long serialVersionUID = -6741035831269877486L;

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}

		};

		modelFileSetOpen = new AbstractAction("Open model file set") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 4995058666502932539L;

			public void actionPerformed(ActionEvent e) {
				// JFileChooser fileChooser = new JFileChooser();
				//
				// /* Add file filters */
				// Iterator<FileFilter> i = gui.getModelFileFilters();
				// while(i.hasNext())
				// fileChooser.addChoosableFileFilter(i.next());
				// /* If approved by user, load model file set */
				// int exitCode =
				// fileChooser.showOpenDialog(gui.getMainWindow());
				// if(exitCode == JFileChooser.APPROVE_OPTION) {
				// // if (!gui.loadModelFileSet(fileChooser.getSelectedFile(),
				// fileChooser.getFileFilter())) {
				// // gui.errorMessage("No model file set loaded!");
				// // }
				//
				// }

			}
		};

		graphFileSaveAs = new AbstractAction("Save graph as") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1302426560023398790L;

			public void actionPerformed(ActionEvent e) {
				// JFileChooser fileChooser = new JFileChooser();
				// fileChooser.setFileFilter(gui.getVisFileFilter());
				// int exitCode =
				// fileChooser.showSaveDialog(gui.getMainFrame());
				// if (exitCode == JFileChooser.APPROVE_OPTION) {
				// gui.saveGraph(fileChooser.getSelectedFile().getAbsolutePath());
				// } else {
				// gui.errorMessage("Error saving graph to " +
				// fileChooser.getSelectedFile());
				// }
			}

		};

		graphImageSaveAs = new AbstractAction("Save image") {

			/**
			 * 
			 */
			private static final long serialVersionUID = -6741035831269877486L;

			public void actionPerformed(ActionEvent e) {
				// JFileChooser fileChooser = new JFileChooser();
				// fileChooser.setFileFilter(gui.getImageFileFilter());
				// int exitCode =
				// fileChooser.showSaveDialog(gui.getMainFrame());
				// if (exitCode == JFileChooser.APPROVE_OPTION) {
				// gui.saveImage(fileChooser.getSelectedFile().getAbsolutePath());
				// } else {
				// gui.errorMessage("Error saving image to " +
				// fileChooser.getSelectedFile().getAbsolutePath());
				// }

			}

		};

		closeApplication = new AbstractAction("Close") {

			/**
			 * 
			 */
			private static final long serialVersionUID = -6741035831269877486L;

			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}

		};

		runAlgorithm = new AbstractAction("Run algorithm") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 590388966385658737L;

			public void actionPerformed(ActionEvent e) {
				// gui.runExperiment();
			}

		};

		stepAlgorithm = new AbstractAction("Do one step") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 590388966385658737L;

			public void actionPerformed(ActionEvent e) {
				// gui().executeOneStep();
			}

		};

		haltAlgorithm = new AbstractAction("Halt algorithm") {

			/**
			 * 
			 */
			private static final long serialVersionUID = -7994642900993084092L;

			public void actionPerformed(ActionEvent e) {
				// gui.haltAlgorithm();
			}

		};

		highlightCE = new AbstractAction("Highlight counterexample") {

			/**
			 * 
			 */
			private static final long serialVersionUID = -1511820601386908895L;

			public void actionPerformed(ActionEvent e) {
				// gui.highlightCE();
			}

		};

		hideLabels = new AbstractAction("Hide Labels") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 957146161454539584L;

			public void actionPerformed(ActionEvent e) {
				// gui.showLabels(false);
			}

		};

		switch2D = new AbstractAction("Switch to 2D View") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 3767969466179967534L;

			public void actionPerformed(ActionEvent e) {
				// gui.set2DViewer();
			}

		};

		switch3D = new AbstractAction("Switch to 3D View") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 7376000972422600276L;

			public void actionPerformed(ActionEvent e) {
				// gui.set3DViewer();
			}

		};

	}

	public JMenu getAlgorithmMenu() throws SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		JMenu menu = new JMenu("Algorithm");
		JMenu subMenu = getAlgorithmSelectionMenu();
		menu.add(subMenu);
		// menu.addSeparator();
		return menu;
	}

	public JMenu getViewerMenu() {
		JMenu menu = new JMenu("View");
		menu.add(switch2D);
		menu.add(switch3D);
		return menu;
	}

	protected JMenu getAlgorithmSelectionMenu() throws SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		return new AlgorithmMenu();
	}

	public static ActionsManager getInstance() {
		return instance;
	}

}

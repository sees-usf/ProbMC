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

package dipro.vis.jung;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dipro.alg.BF;
import dipro.run.Registry;
import dipro.run.VisMain;
import dipro.util.ExploredGraph;
import dipro.vis.gui.GUI;
import dipro.vis.gui.IconLoader;
import dipro.vis.gui.VisualizationControl;

public class JungVisualizerControlPanel extends JPanel implements Observer {

	// Tasks
	// private static final String EDIT_SETTINGS ="Edit Settings";
//	static final String CHECK_MODEL = "Check Model";
//	static final String GEN_CX = "Write CX";
	static final String TERMINATE_ALGORITHM = "Term. Algorithm";
	// private static final String FLUSH_SOLUTION ="Flush Solution";
//	static final String CHECK_SOLUTION = "Check CX";
//	static final String CLOSE_VISUALIZATION = "Close Visualization";

	private JungVisualizer visualizer;

	// private long states;
	// private long transitions;
	// private long targets;
	// private long dsgStates;
	// private long startTime;
	//	
	// private JLabel name;
	private JLabel status;
	private JLabel iterationsNumber;
	private JLabel exploredNumber;
	private JLabel transitionsNumber;
	private JLabel solutionValue;
	private JLabel solutionSize;
	
	private JButton runButton;
	private Vector<String> availableTasks;
	private JComboBox tasksBox;
	private JButton doTaskButton;
	private JCheckBox stepByStepCheckBox;

	private JPanel statusPanel;
	private JComboBox layouterBox;
	private JPanel algControlPanel;
	private JPanel visControlPanel;

	public JungVisualizerControlPanel(JungVisualizer visualizer) {
		this.visualizer = visualizer;
		init();
	}

	private void init() {

		status = new JLabel();
		updateStatusLabel(visualizer.getAlgStatus());
		exploredNumber = new JLabel("0");
		transitionsNumber = new JLabel("0");
		iterationsNumber = new JLabel("0");
		solutionValue = new JLabel("0");
		solutionSize = new JLabel("0");
		statusPanel = createStatusPanel();
		algControlPanel = createAlgControlPanel();
		visControlPanel = createVisControlPanel();

		FormLayout layout = new FormLayout("3dlu, 22dlu, 10dlu, 120dlu",
				"p, 3dlu, p, 2dlu, p, 3dlu, p");
		CellConstraints cc = new CellConstraints();
		PanelBuilder builder = new PanelBuilder(layout);
		builder.add(statusPanel, cc.xyw(2, 1, 3));
		builder.addSeparator("Control", cc.xyw(1, 3, 4));
		builder.add(algControlPanel, cc.xyw(2, 5, 3));
		builder.add(visControlPanel, cc.xyw(2, 7, 3));
		add(builder.getPanel());
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		runButton.setEnabled(enabled);
		tasksBox.setEnabled(enabled);
		doTaskButton.setEnabled(enabled);
		stepByStepCheckBox.setEnabled(enabled);
	}
	
	private JPanel createStatusPanel() {
		FormLayout statusLayout = new FormLayout(
				"right:pref, 2dlu,right:pref, 5dlu, right:pref, 2dlu,right:pref",
				"p, p, p,p");
		CellConstraints statusConstraints = new CellConstraints();
		PanelBuilder statusBuilder = new PanelBuilder(statusLayout);
		statusBuilder.addLabel("Status", statusConstraints.xy(1, 1));
		statusBuilder.addLabel("Iterations", statusConstraints.xy(1, 2));
		statusBuilder.addLabel("Vertices", statusConstraints.xy(1, 3));
		statusBuilder.addLabel("Edges", statusConstraints.xy(1, 4));
		statusBuilder.add(status, statusConstraints.xy(3, 1));
		statusBuilder.add(iterationsNumber, statusConstraints.xy(3, 2));
		statusBuilder.add(exploredNumber, statusConstraints.xy(3, 3));
		statusBuilder.add(transitionsNumber, statusConstraints.xy(3, 4));
		statusBuilder.addLabel("Sol. Value", statusConstraints.xy(5, 2));
		statusBuilder.addLabel("Sol. Size", statusConstraints.xy(5, 3));
		statusBuilder.add(solutionValue, statusConstraints.xy(7, 2));
		statusBuilder.add(solutionSize, statusConstraints.xy(7, 3));
		// JButton fromScratchBtn = new JButton("From Scratch");
		// fromScratchBtn.addActionListener(new ActionListener(){
		// public void actionPerformed(ActionEvent e) {
		// getVisualizationControl().visualizeFromScratch();
		// }
		// });
		// statusBuilder.add(fromScratchBtn, statusConstraints.xy(7, 3));
		// JButton fromScratchBtn = new JButton("From Scratch");
//		JButton exportImgBtn = new JButton("Export Image");
//		exportImgBtn.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				getVisualizationControl().exportImage();
//			}
//		});
//		statusBuilder.add(exportImgBtn, statusConstraints.xy(7, 3));
		return statusBuilder.getPanel();
	}

	private JPanel createAlgControlPanel() {
		/* Insert fields in layout */
		FormLayout layout = new FormLayout("50dlu, 2dlu, 50dlu, 2dlu,35dlu",
				"35px,5dlu,p");
		CellConstraints cc = new CellConstraints();

		PanelBuilder builder = new PanelBuilder(layout);

		runButton = new JButton("Start", IconLoader.get("lrun_obj.gif"));
		runButton.addActionListener(new RunActionListener());
		availableTasks = new Vector<String>(4);
		// availableTasks.add(EDIT_SETTINGS);
//		availableTasks.add(GEN_CX);
		availableTasks.add(TERMINATE_ALGORITHM);
//		availableTasks.add(CLOSE_VISUALIZATION);
		tasksBox = new JComboBox(availableTasks);
		doTaskButton = new JButton("Do");
		doTaskButton.addActionListener(new TaskActionListener());
		stepByStepCheckBox = new JCheckBox("Step-By-Step");
		boolean isInSimulationModus = visualizer.getContext().getConfig().isInStepByStepModus;
		stepByStepCheckBox.setSelected(isInSimulationModus);
		stepByStepCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				visualizer.getContext().getConfig().isInStepByStepModus = 
					stepByStepCheckBox.isSelected();
//				// Change the layout when stepByStep 
//				if(visualizer.getContext().getConfig().isInSimulationModus) {
//					layouterBox.setSelectedIndex(YVisualizer.INCREMENTAL_HIERARCHICAL_LAYOUTER_INDEX);
//					visualizer.changeLayouter(YVisualizer.INCREMENTAL_HIERARCHICAL_LAYOUTER_INDEX);
//				}
			}
		});
		builder.add(runButton, cc.xy(1, 1));
		builder.add(tasksBox, cc.xy(3, 1));
		builder.add(doTaskButton, cc.xy(5, 1));
		builder.add(stepByStepCheckBox, cc.xyw(1, 3, 4));
		return builder.getPanel();
	}

	private JPanel createVisControlPanel() {
		FormLayout fl = new FormLayout("left:pref, 2dlu, 45dlu, 2dlu, 45dlu",
				"20dlu, 20dlu, 20dlu");
		CellConstraints cc = new CellConstraints();
		PanelBuilder builder = new PanelBuilder(fl);
		layouterBox = new JComboBox(visualizer
				.getSupportedLayouters());
		layouterBox.setSelectedIndex(JungVisualizer.DEFAULT_LAYOUTER_INDEX);
		JButton doLayoutBtn = new JButton("Layout");
		doLayoutBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				visualizer.changeLayouter(layouterBox.getSelectedIndex());
				getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
		final JLabel lblSpeed = new JLabel(Integer.toString(visualizer
				.getMorphDuration())
				+ " ms");
		/* Create JSlider */
		final JSlider animSlider = new JSlider(JSlider.HORIZONTAL);
		animSlider.setMinimum(0);
		animSlider.setMaximum(1000);
		animSlider.setMinorTickSpacing(10);
		animSlider.setMajorTickSpacing(100);
		animSlider.setValue(visualizer.getMorphDuration());
		animSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				visualizer.setMorphDuration(animSlider.getValue());
				lblSpeed.setText(Integer
						.toString(visualizer.getMorphDuration())
						+ " ms");
			}
		});
		final JLabel lblGradient = new JLabel(Integer.toString(visualizer
				.getGradient()));
		/* Create JSlider */
		final SpinnerModel spinnerModel = new SpinnerNumberModel(100, 0, 10000, 1);
		final JSpinner spinner = new JSpinner(spinnerModel);
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				visualizer.setGradient((Integer)spinner.getValue());
				lblGradient.setText(Integer.toString(visualizer.getGradient()));
				visualizer.update();
				// System.out.println("Emphasis factor =
				// "+visualizer.getGradient());
			}
		});
//		final JSlider gradSlider = new JSlider(JSlider.HORIZONTAL);
//		gradSlider.setMinimum(0);
//		gradSlider.setMaximum(10000);
//		gradSlider.setMinorTickSpacing(10);
//		gradSlider.setMajorTickSpacing(100);
//		gradSlider.setValue(visualizer.getGradient());
//		gradSlider.addChangeListener(new ChangeListener() {
//			public void stateChanged(ChangeEvent arg0) {
//				visualizer.setGradient(gradSlider.getValue());
//				lblGradient.setText(Integer.toString(visualizer.getGradient()));
//				visualizer.update();
//				// System.out.println("Emphasis factor =
//				// "+visualizer.getGradient());
//			}
//		});
		builder.addLabel("Layouter:", cc.xy(1, 1));
		builder.add(layouterBox, cc.xy(3, 1));
		builder.add(doLayoutBtn, cc.xy(5, 1));
		builder.addLabel("Animation speed:", cc.xy(1, 2));
		builder.add(animSlider, cc.xy(3, 2));
		builder.add(lblSpeed, cc.xy(5, 2));
		builder.addLabel("Emphasis factor:", cc.xy(1, 3));
		builder.add(spinner,cc.xy(3, 3));
//		builder.add(gradSlider, cc.xy(3, 3));
		builder.add(lblGradient, cc.xy(5, 3));
		return builder.getPanel();
	}

	public void update(Observable o, Object arg) {
		// BF alg = GUI.getCurrentVisThread().getAlgorithm();
		// assert o==GUI.getCurrentVisThread().getAlgorithm();
		int s = visualizer.getAlgStatus();
		switch (s) {
		case BF.NOT_INITIALIZED:
			runButton.setText("Start");
			runButton.setIcon(IconLoader.get("lrun_obj.gif"));
			break;
		case BF.READY:
			runButton.setText("Start");
			runButton.setIcon(IconLoader.get("lrun_obj.gif"));
			break;
		case BF.RUNNING:
			runButton.setText("Pause");
			runButton.setIcon(IconLoader.get("suspend_co.gif"));
			break;
		case BF.PAUSED:
			runButton.setText("Resume");
			runButton.setIcon(IconLoader.get("lrun_obj.gif"));
			break;
		case BF.TO_TERMINATE:
			runButton.setEnabled(false);
			availableTasks.remove(TERMINATE_ALGORITHM);
//			tasksBox.setSelectedIndex(0);
			break;
		case BF.TERMINATED:
			runButton.setEnabled(false);
			availableTasks.remove(TERMINATE_ALGORITHM);
//			tasksBox.setSelectedIndex(0);
			break;
		default:
//			System.out.println("status " + s);
			throw new IllegalStateException();
		}

//		if (!visualizer.isSolutionEmpty() && !availableTasks.contains(CHECK_SOLUTION)) {
//			availableTasks.add(availableTasks.size(), CHECK_SOLUTION);
//			tasksBox.repaint();
//		}
		updateLabels();
	}

	private void updateLabels() {
		// BF alg = GUI.getCurrentVisThread().getAlgorithm();
		int s = visualizer.getAlgStatus();
		updateStatusLabel(s);
		// if(s== BF.CLEANED_UP) {
		// exploredNumber.setText("?");
		// transitionsNumber.setText("?");
		// targetNumber.setText("?");
		// }
		// else {
		ExploredGraph explGraph = visualizer.getExploredGraph();
		iterationsNumber.setText(Long.toString(visualizer.getAlgorithm()
				.getNumIterations()));
		exploredNumber.setText(Integer.toString(explGraph.numVertices()));
		transitionsNumber.setText(Integer.toString(explGraph.numEdges()));
		double sv = visualizer.getSolutionValue();
		solutionValue.setText(JungVisualizer.getFormat().format(sv));
		solutionSize.setText(Integer.toString(visualizer.getNumSolutionTraces()));
		// }
	}

	private void updateStatusLabel(int s) {
		switch (s) {
		case BF.NOT_INITIALIZED:
			status
					.setText("<html><font color=\"RED\"><B>Not Initialized</B></font></html>");
			break;
		case BF.READY:
			status
					.setText("<html><font color=\"GREEN\"><B>Ready</B></font></html>");
			break;
		case BF.RUNNING:
			ImageIcon icon = IconLoader.get("running.gif");
			icon.setImageObserver(status);
			status.setIcon(icon);
			status
					.setText("<html><font color=\"BLUE\"><B>Running</B></font></html>");
			break;
		case BF.PAUSED:
			status
					.setText("<html><font color=\"GREEN\"><B>Paused</B></font></html>");
			status.setIcon(null);
			break;
		case BF.TO_TERMINATE:
			status
					.setText("<html><font color=\"RED\"><B>Terminating</B></font></html>");
			status.setIcon(null);
			break;
		case BF.TERMINATED:
			status
					.setText("<html><font color=\"BLACK\"><B>Terminated</B></font></html>");
			status.setIcon(null);
			break;
		default:
//			System.out.println("status " + s);
			throw new IllegalStateException();
		}
	}

	protected VisualizationControl getVisualizationControl() {
		GUI gui = ((VisMain) Registry.getMain()).getGUI();
		return gui.getVisualizationControl();
	}

	public JButton getRunButton(){
		return runButton;
	}
	class RunActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JungVisualizerControlPanel.this.setEnabled(false);
			int s = getVisualizationControl().getAlgStatus();
			switch (s) {
			case BF.READY:
				getVisualizationControl().start();
				break;
			case BF.RUNNING:
				getVisualizationControl().requestPause();
				break;
			case BF.PAUSED:
				getVisualizationControl().requestResume();
				break;
			}
		}
	}

	
	
	
	
	class TaskActionListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			String selectedTask = (String) tasksBox.getSelectedItem();
			// if(selectedTask == EDIT_SETTINGS) {
			// AlgorithmControlPanel.this.actionPerformed(event);
			// return;
			// }
//			if (selectedTask == YVisualizerControlPanel.GEN_CX) {
//				try {
//					getVisualizationControl()performModelChecking();
//				} catch (Exception e) {
//					Registry.getMain().handleError(e);
//				} finally {
//					getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//				}
//				return;
//			}
			if (selectedTask == JungVisualizerControlPanel.TERMINATE_ALGORITHM) {
				getVisualizationControl().requestTermination();
				getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				return;
			}
//			if (selectedTask == YVisualizerControlPanel.CLOSE_VISUALIZATION) {
//				try {
//					getVisualizationControl().closeVisualization();
//				} catch (Exception e) {
//					Registry.getMain().handleError(e);
//				}
//				return;
//			}
			// if(selectedTask == FLUSH_SOLUTION) {
			// try {
			// if(control.getSearchFacade() instanceof BFSearchFacade)
			// ((BFSearchFacade)control.getSearchFacade()).flushSolution();
			// } catch (Exception e) {
			// DiPro.getGlobalControl().handleException(e);
			// }
			// return;
			// }
//			if (selectedTask == YVisualizerControlPanel.CHECK_SOLUTION) {
//				try {
//					
//					getVisualizationControl().modelCheckSolution();
//				} catch (Exception e) {
//					Registry.getMain().handleError(e);
//				} finally {
//					getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//				}
//				return;
//			}
		}
	}
}

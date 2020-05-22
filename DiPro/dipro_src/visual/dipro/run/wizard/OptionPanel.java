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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import parser.ast.ModulesFile;
import dipro.run.Config;
import dipro.util.SolutionTracesRecorder;

public class OptionPanel extends PartPanel {

	final static int MAX_TIME = 0;
	final static int MAX_ITER = 1;
	final static int ALG_LOG = 2;
	final static int REP_LOG = 3;
	final static int CX_INCR = 4;
	final static int K_VALUE = 5;
	final static int MC_SOL = 6;
	final static int PRUNE_BOUND = 7;
	final static int PI = 8;
	final static int MC = 9;
	final static int VIS = 10;
	final static int VIS_ONLINE = 11;
	final static int SIM_MODE = 12;

	private JPanel m_searchPanel, m_loggingPanel, m_modelPanel, m_visPanel;
	private JCheckBox m_mrmcCheckBox, m_piCheckBox, m_mcCheckBox,
			m_prismCheckBox, m_visCheckBox, m_onlineVisCheckBox,
			m_inSimModeCheckBox;

	private ButtonGroup m_buttonGroupModelMC;

	private JComboBox m_algoListComboBox, m_algoLogLevelComboBox,
			m_traceListComboBox;

	private JTextField m_maxTimeField, m_maxIterField, m_heuristicField,
			m_algLogField, m_repLogField, m_cxIncrementRatioField, m_kField,
			m_mcSolField, m_pruneBoundField, m_traceField;

	public OptionPanel(Config config) {
		super(config);
		initGUIComp();
	}

	private void initGUIComp() {
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP,
				JTabbedPane.HORIZONTAL);

		JPanel advance = new JPanel(new BorderLayout(5, 5));
		advance.add(initSearchOptions(), BorderLayout.CENTER);
		advance.add(initVisOptions(), BorderLayout.SOUTH);
		advance.add(initModelOptions(), BorderLayout.WEST);
		advance.add(initLoggingOptions(), BorderLayout.EAST);

		tabbedPane.addTab("Simple", initSimpleOptions());
		tabbedPane.addTab("Advance", advance);

		add(tabbedPane);
	}

	private JPanel initSimpleOptions() {
		JPanel simple = new JPanel(new GridLayout(0, 1));
		JPanel simpleSearch = new JPanel(new GridLayout(0, 2));

		simpleSearch.setBorder(BorderFactory
				.createTitledBorder("Search Options"));

		JComboBox algoListComboBox = new JComboBox();
		algoListComboBox.setModel(m_algoListComboBox.getModel());
		simpleSearch.add(new JLabel("Alg.:"));
		simpleSearch.add(algoListComboBox);

		simpleSearch.add(new JLabel("Prism:"));

		JCheckBox mrmcCheckBox = new JCheckBox();
		JCheckBox prismCheckBox = new JCheckBox();
		prismCheckBox.setSelected(true);
		mrmcCheckBox.setSelected(false);

		prismCheckBox.setModel(m_prismCheckBox.getModel());
		mrmcCheckBox.setModel(m_mrmcCheckBox.getModel());

		simpleSearch.add(prismCheckBox);

		simpleSearch.add(new JLabel("Mrmc:"));

		simpleSearch.add(mrmcCheckBox);

		JPanel simpleViz = new JPanel(new GridLayout(0, 2));

		simpleViz.setBorder(BorderFactory
				.createTitledBorder("Visualization Options"));

		simpleViz.add(new JLabel("Visualize CX:"));
		JCheckBox visCheckBox = new JCheckBox();

		JCheckBox onlineVisCheckBox = new JCheckBox();

		onlineVisCheckBox.setModel(m_onlineVisCheckBox.getModel());

		visCheckBox.setModel(m_visCheckBox.getModel());

		simpleViz.add(visCheckBox);

		simpleViz.add(new JLabel("Online Visualization:"));
		simpleViz.add(onlineVisCheckBox);

		simple.add(simpleSearch);
		simple.add(simpleViz);
		return simple;
	}

	private JPanel initVisOptions() {
		m_visPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel(new GridLayout(0, 2));
		m_visPanel.setBorder(BorderFactory
				.createTitledBorder("Visualization Options"));

		panel.add(new JLabel("Visualize CX:"));
		m_onlineVisCheckBox = new AdvanceCheckBox(m_config.onlineVisualization,
				OptionPanel.VIS_ONLINE);
		m_visCheckBox = new AdvanceCheckBox(m_config.isVisualizationEnabled,
				OptionPanel.VIS);
		m_visCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					m_onlineVisCheckBox.setEnabled(true);
					m_inSimModeCheckBox.setEnabled(true);
				} else {
					m_onlineVisCheckBox.setEnabled(false);
					m_inSimModeCheckBox.setEnabled(false);
				}

			}
		});

		panel.add(m_visCheckBox);

		panel.add(new JLabel("Online Visualization:"));

		panel.add(m_onlineVisCheckBox);

		m_visPanel.add(panel);
		return m_visPanel;
	}

	private JPanel initLoggingOptions() {
		String logLevel[] = { "ALG_LOG_DISABLED", "ALG_LOG_BASIC",
				"ALG_LOG_NORMAL", "ALG_LOG_DETAILED", "ALG_LOG_VERBOSE",
				"ALG_LOG_DEBUG" };

		m_loggingPanel = new JPanel(new GridBagLayout());
		m_loggingPanel.setBorder(BorderFactory
				.createTitledBorder("Logging Options"));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1, 0, 1, 0);

		// Log level
		c.gridx = 0;
		c.gridy = 0;
		m_loggingPanel.add(new JLabel("Log level:"), c);

		c.gridx = 1;
		m_algoLogLevelComboBox = new JComboBox(logLevel);
		m_algoLogLevelComboBox.setSelectedIndex(m_config.getLogLevel());
		m_loggingPanel.add(m_algoLogLevelComboBox, c);
		m_algoLogLevelComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (m_algoLogLevelComboBox.getSelectedIndex() == Config.ALG_LOG_DISABLED)
					m_algLogField.setEnabled(false);
				else
					m_algLogField.setEnabled(true);

			}
		});

		// Alg. Log
		c.gridx = 0;
		c.gridy = 1;
		m_loggingPanel.add(new JLabel("Alg. log:"), c);

		c.gridx = 1;
		m_algLogField = new AdvanceTextField(true, m_config.algLogName,
				OptionPanel.ALG_LOG);
		m_loggingPanel.add(m_algLogField, c);

		// Rep. Log
		c.gridy = 2;
		c.gridx = 0;
		m_loggingPanel.add(new JLabel("Rep. log:"), c);

		c.gridx = 1;
		m_repLogField = new AdvanceTextField(m_config.report,
				m_config.reportName, OptionPanel.REP_LOG);
		m_loggingPanel.add(m_repLogField, c);

		// Trace Log
		c.gridy = 3;
		c.gridx = 0;
		m_loggingPanel.add(new JLabel("Trace log:"), c);

		String traceFile[] = { "No Traces", "Text File", "XML File",
				"Diag. Path" };
		c.gridx = 1;

		m_traceField = new JTextField(5);
		m_traceListComboBox = new JComboBox(traceFile);
		m_traceListComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				switch (m_traceListComboBox.getSelectedIndex()) {
				case 1:
					m_config.solutionTrace = SolutionTracesRecorder.CX_FILE;
					m_traceField.setEditable(true);
					break;
				case 2:
					m_config.solutionTrace = SolutionTracesRecorder.CX_XML_FILE;
					m_traceField.setEditable(true);
					break;
				case 3:
					m_config.solutionTrace = SolutionTracesRecorder.DIAG_PATH;
					m_traceField.setEditable(false);
					break;
				case 0:
				default:
					m_config.solutionTrace = SolutionTracesRecorder.NO_TRACES;
					m_traceField.setEditable(false);
					break;
				}

			}

		});
		m_loggingPanel.add(m_traceListComboBox, c);

		c.gridy = 4;
		m_traceField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				changedUpdate(e);

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				m_config.cxFileName = "" + m_traceField.getText();

			}
		});
		m_loggingPanel.add(m_traceField, c);

		return m_loggingPanel;
	}

	private JPanel initModelOptions() {
		m_modelPanel = new JPanel(new GridLayout(0, 2));
		m_modelPanel.setBorder(BorderFactory
				.createTitledBorder("Model Options"));

		m_modelPanel.add(new JLabel("Prism:"));

		m_mrmcCheckBox = new JCheckBox();
		m_prismCheckBox = new JCheckBox();
		m_prismCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					m_config.modelType = Config.PRISM_MODEL;
					if (!m_visCheckBox.isEnabled()) {
						m_visCheckBox.setEnabled(true);
						m_visCheckBox.setSelected(true);
					}
				} else {
					m_config.modelType = Config.MRMC_MODEL;
					m_visCheckBox.setEnabled(false);
					m_visCheckBox.setSelected(false);

				}
			}
		});
		m_prismCheckBox.setSelected(true);
		m_prismCheckBox.setEnabled(true);

		m_mrmcCheckBox.setSelected(false);
		m_mrmcCheckBox.setEnabled(true);

		m_buttonGroupModelMC = new ButtonGroup();
		m_buttonGroupModelMC.add(m_prismCheckBox);
		m_buttonGroupModelMC.add(m_mrmcCheckBox);

		m_modelPanel.add(m_prismCheckBox);

		m_modelPanel.add(new JLabel("Mrmc:"));

		m_modelPanel.add(m_mrmcCheckBox);

		m_modelPanel.add(new JLabel("Heuristic:"));

		m_heuristicField = new AdvanceTextField(m_config.heuristicName);

		m_modelPanel.add(m_heuristicField);

		m_modelPanel.add(new JLabel("MC:"));

		m_mcCheckBox = new AdvanceCheckBox(m_config.mc, OptionPanel.MC);
		m_modelPanel.add(m_mcCheckBox);

		m_modelPanel.add(new JLabel("CX Incr."));

		m_cxIncrementRatioField = new AdvanceTextField(1.0d, 2.0d,
				m_config.cxIncrementRatio, OptionPanel.CX_INCR);
		m_modelPanel.add(m_cxIncrementRatioField);

		m_modelPanel.add(new JLabel("MC Sol.:"));

		m_mcSolField = new AdvanceTextField(0, Integer.MAX_VALUE,
				m_config.mcsol, OptionPanel.MC_SOL);
		m_modelPanel.add(m_mcSolField);

		return m_modelPanel;

	}

	private JPanel initSearchOptions() {

		m_searchPanel = new JPanel();
		m_searchPanel.setBorder(BorderFactory
				.createTitledBorder("Search options"));

		m_searchPanel.setLayout(new GridLayout(0, 2));

		// Algo list
		m_algoListComboBox = new JComboBox();
		m_algoListComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (m_algoListComboBox.getItemCount() == 3
						&& m_algoListComboBox.getSelectedIndex() == 0)
					m_piCheckBox.setEnabled(true);

				else
					m_piCheckBox.setEnabled(false);

				if (m_algoListComboBox.getSelectedIndex() == 2
						|| (m_algoListComboBox.getItemCount() == 2 && m_algoListComboBox
								.getSelectedIndex() == 1))
					m_heuristicField.setEnabled(false);
				else
					m_heuristicField.setEnabled(true);
				// If Stoch XBF, KSTAR, EPPSTEIN
				// else KSTAR, EPPSTEIN
				if (m_algoListComboBox.getItemCount() == 3) {
					switch (m_algoListComboBox.getSelectedIndex()) {
					case 0:
						m_config.algType = Config.XBF;
						break;
					case 1:
						m_config.algType = Config.K_STAR;
						break;
					case 2:
						m_config.algType = Config.EPPSTEIN;
						break;
					}
				} else if(m_algoListComboBox.getItemCount() == 1) {
					m_config.algType = Config.XBF;
					
				} else {
					switch (m_algoListComboBox.getSelectedIndex()) {
					case 0:
						m_config.algType = Config.K_STAR;
						break;
					case 1:
						m_config.algType = Config.EPPSTEIN;
						break;
					}

				}
			}

		});

		m_searchPanel.add(new JLabel("Alg.:"));
		m_searchPanel.add(m_algoListComboBox);
		// Pi
		m_searchPanel.add(new JLabel("Pi:"));
		m_piCheckBox = new AdvanceCheckBox(m_config.isUsePi(), OptionPanel.PI);
		m_searchPanel.add(m_piCheckBox);

		// Max Iteration
		m_searchPanel.add(new JLabel("Max Iter.:"));

		m_maxIterField = new AdvanceTextField(0, Integer.MAX_VALUE,
				m_config.maxIter, OptionPanel.MAX_ITER);
		m_searchPanel.add(m_maxIterField);

		// Max Time
		m_searchPanel.add(new JLabel("Max Time:"));

		m_maxTimeField = new AdvanceTextField(0, Integer.MAX_VALUE,
				m_config.maxTime, OptionPanel.MAX_TIME);
		m_searchPanel.add(m_maxTimeField);

		// PruneBound
		m_searchPanel.add(new JLabel("Prune Bound:"));

		m_pruneBoundField = new AdvanceTextField(-1.0d, Double.MAX_VALUE - 1,
				m_config.pruneBound, OptionPanel.PRUNE_BOUND);
		m_searchPanel.add(m_pruneBoundField);

		// k
		m_searchPanel.add(new JLabel("Uniform Rate:"));

		m_kField = new AdvanceTextField(0, Double.MAX_VALUE,
				m_config.uniformRate, OptionPanel.K_VALUE);
		m_searchPanel.add(m_kField);

		// Step-by-Step
		m_searchPanel.add(new JLabel("In Simulation Mode:"));
		m_inSimModeCheckBox = new AdvanceCheckBox(m_config.isInStepByStepModus,
				OptionPanel.SIM_MODE);
		m_searchPanel.add(m_inSimModeCheckBox);

		return m_searchPanel;

	}

	private class AdvanceCheckBox extends JCheckBox implements ItemListener {

		private int m_option;

		public AdvanceCheckBox(Boolean source, int option) {
			m_option = option;
			addItemListener(this);
			setSelected(source);
		}

		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				setValue2Config(m_option, "true");
			} else {
				setValue2Config(m_option, "false");
			}
		}

	}

	private class AdvanceTextField extends JTextField implements ItemListener,
			DocumentListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JCheckBox m_enabled;
		private JLabel m_label;
		private double m_min, m_max;
		private static final int columns = 5;
		private boolean heuristics = false;
		private int m_option;

		AdvanceTextField(boolean isEnabled, Object source, int option) {
			super(columns);
			m_option = option;
			setLayout(new BorderLayout());
			m_enabled = new JCheckBox();
			m_enabled.addItemListener(this);
			m_enabled.setSelected(isEnabled);
			add(m_enabled, BorderLayout.EAST);
		}

		AdvanceTextField(Object source) {
			super(10);
			heuristics = true;
			setLayout(new BorderLayout());
			m_label = new JLabel("(!)", JLabel.CENTER);
			m_label.setForeground(Color.red);
			add(m_label, BorderLayout.EAST);
			getDocument().addDocumentListener(this);
			m_label.setToolTipText("The given heuristic(" + getText()
					+ ") is not available. Using default value!");
			setText("" + source);
		}

		AdvanceTextField(double min, double max, Object source, int option) {
			super(columns);
			setLayout(new BorderLayout());
			m_label = new JLabel("(!)", JLabel.CENTER);
			m_label.setForeground(Color.red);
			add(m_label, BorderLayout.EAST);
			getDocument().addDocumentListener(this);
			m_min = min;
			m_max = max;
			m_label.setToolTipText("The possible value range is:[" + min + ":"
					+ max + "]. Using min value!");
			setText("" + source);
			m_option = option;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				setEnabled(true);
				setValue2Config(m_option, getText());
			} else {
				setEnabled(false);
			}

		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			changedUpdate(e);

		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);

		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			String str = getText();
			if (heuristics) {
				if (!str.equals("null")) {
					try {
						Class.forName(str);
					} catch (ClassNotFoundException ex) {
						m_label.setVisible(true);
						m_config.heuristicName = "null";
						return;
					}
					m_config.heuristicName = str;
				}
			}

			else {

				if (str.isEmpty()) {
					m_label.setVisible(true);
					return;
				}

				double i = Double.parseDouble(str);
				if (i > m_max || i < m_min) {
					m_label.setVisible(true);
					setValue2Config(m_option, String.valueOf(m_min));
				} else {
					setValue2Config(m_option, str);
				}

			}
			m_label.setVisible(false);

		}

	}

	boolean isValidConf() {
		return false;
	}

	void parseConstants() {
		m_algoListComboBox.removeAllItems();
		if (ModulesFile.STOCHASTIC == m_config.getModel().getType())
			m_algoListComboBox.addItem(new String("XBF"));
		if (m_config.propId != -1) {
			m_algoListComboBox.addItem(new String("K_STAR"));
			m_algoListComboBox.addItem(new String("EPPSTEIN"));
		}
		m_algoListComboBox.setSelectedIndex(0);

		m_traceField.setText(m_config.modelName);
		m_traceField
				.setEditable(m_config.solutionTrace != SolutionTracesRecorder.NO_TRACES
						&& m_config.solutionTrace != SolutionTracesRecorder.DIAG_PATH);

	}

	void setValue2Config(int option, String value) {
		switch (option) {
		case MAX_TIME:
			m_config.maxTime = Float.parseFloat(value);
			break;
		case MAX_ITER:
			m_config.maxIter = Integer.parseInt(value);
			break;
		case ALG_LOG:
			m_config.algLogName = value;
			break;
		case REP_LOG:
			m_config.reportName = value;
			break;
		case CX_INCR:
			m_config.cxIncrementRatio = Double.parseDouble(value);
			break;
		case K_VALUE:
			m_config.k = Integer.parseInt(value);
			break;
		case MC_SOL:
			m_config.mcsol = Integer.parseInt(value);
			break;
		case PRUNE_BOUND:
			m_config.pruneBound = Double.parseDouble(value);
			break;
		case PI:
			m_config.usePi = Boolean.parseBoolean(value);
			break;
		case MC:
			m_config.mc = Boolean.parseBoolean(value);
			break;
		case VIS:
			m_config.isVisualizationEnabled = Boolean.parseBoolean(value);
		case VIS_ONLINE:
			if (m_config.isVisualizationEnabled)
				m_config.onlineVisualization = Boolean.parseBoolean(value);
			else
				m_config.onlineVisualization = false;
			break;
		case SIM_MODE:
			if (m_config.isVisualizationEnabled)
				m_config.isInStepByStepModus = Boolean.parseBoolean(value);
			else
				m_config.isInStepByStepModus = false;
			break;
		default:
			return;

		}

	}

}

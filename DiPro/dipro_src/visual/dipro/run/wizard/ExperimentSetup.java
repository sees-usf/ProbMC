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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import parser.Values;
import prism.Prism;
import prism.PrismFileLog;
import dipro.run.Config;
import dipro.run.DiPro;
import dipro.run.PrismDefaultContext;
import dipro.run.Registry;

public class ExperimentSetup extends JDialog {

	private static final long serialVersionUID = 1L;

	private Action actionNext, actionBack, actionCancel;
	private JButton buttonNext, buttonBack, buttonCancel;
	private boolean m_generateCX;

	private ArrayList<PartPanel> panelSet;
	private ExchangeablePanel panelExchangeableMain;
	private JFileChooser fileChooser;

	private Prism prism;
	private static ExperimentSetup instance = null;
	private DiPro m_dipro;
	private static Config m_staticConfig;
	private Config m_config;
	private ConstTableModel mf_constModel;

	private ExperimentSetup(DiPro dipro) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		m_dipro = dipro;

		if (m_staticConfig == null)
			m_config = m_dipro.initConfig();
		else
			m_config = m_staticConfig;

		setTitle("Experiment - Setup");
		setLayout(new BorderLayout(5, 5));
		setResizable(false);
		prism = new Prism(new PrismFileLog(), new PrismFileLog());

		fileChooser = new JFileChooser();

		panelSet = new ArrayList<PartPanel>();
		panelSet.add(new ModelPanel(m_config));
		panelSet.add(new PropertyPanel(m_config));
		panelSet.add(new OptionPanel(m_config));
		panelExchangeableMain = new ExchangeablePanel(panelSet);
		add(panelExchangeableMain, BorderLayout.CENTER);
		initActions();
		initGUIComp();
		pack();
		justifyCenter();
	}

	public static Config createInstance(DiPro dipro, boolean oldConfig) {
		if (!oldConfig) {
			instance = null;
			m_staticConfig = null;
		}
		if (instance == null)
			instance = new ExperimentSetup(dipro);
		instance.setVisible(true);

		return m_staticConfig;
	}

	static ExperimentSetup getInstance() {
		return instance;
	}

	Prism getPrism() {
		return prism;
	}

	JFileChooser getFileChooser() {
		return fileChooser;
	}

	public ConstTableModel getMf_constModel() {
		return mf_constModel;
	}

	public void setMf_constModel(ConstTableModel mf_constModel) {
		this.mf_constModel = mf_constModel;
	}

	Config getConfig() {
		return m_config;
	}

	private void enableNext(boolean value) {
		buttonNext.setEnabled(value);

	}

	void enableNext() {

		enableNext(panelExchangeableMain.current().isValidConf());
	}

	void enableBack(boolean value) {
		buttonBack.setEnabled(value);
	}

	void simulateCX() {
		panelExchangeableMain.nextPanel();
		panelExchangeableMain.current().parseConstants();
		actionNext.putValue(Action.NAME, "Simulate CX");
		actionNext.setEnabled(true);
		m_generateCX = true;

		validate();
		pack();
		justifyCenter();
	}

	private void initActions() {
		actionNext = new AbstractAction("Next") {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (m_generateCX) {
					generateCX();
					panelExchangeableMain.current().isValidConf();
					return;
				}
				actionBack.setEnabled(true);
				panelExchangeableMain.current().parseConstants();
				panelExchangeableMain.nextPanel();

				if (panelExchangeableMain.isLast()) {
					panelExchangeableMain.current().parseConstants();
					actionNext.putValue(Action.NAME, "Generate CX");
					m_generateCX = true;
				} else
					enableNext(panelExchangeableMain.current().isValidConf());

				validate();
				pack();
				justifyCenter();
			}

		};
		actionNext.setEnabled(false);

		actionBack = new AbstractAction("Back") {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (panelExchangeableMain.isLast()) {
					actionNext.putValue(Action.NAME, "Next");
					m_generateCX = false;
				}
				panelExchangeableMain.backPanel();

				if (panelExchangeableMain.isFirst())
					actionBack.setEnabled(false);
				else
					actionBack.setEnabled(true);
				actionNext.setEnabled(true);
				validate();
				pack();
				justifyCenter();
			}
		};
		actionBack.setEnabled(false);

		actionCancel = new AbstractAction("Cancel") {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};
	}

	private void generateCX() {
		Values constantValues = new Values();
		constantValues.addValues(m_config.getModel().getConstantValues());
		if (m_config.propId != -1)
			constantValues.addValues(m_config.getProp().getConstantValues());
		m_config.setConstantValues(constantValues);
		m_config.commit();
		if (m_config.modelType == Config.MRMC_MODEL) {
			convertToMRMC();
		}
		m_staticConfig = m_config;
		dispose();
	}

	private void convertToMRMC() {
		Config config = m_config;
		config.modelType = Config.PRISM_MODEL;
		PrismDefaultContext context = null;
		boolean excp = false;
		try {
			DiPro dipro = new DiPro();
			context = (PrismDefaultContext) dipro.loadContext(1, config);
			context.init();
			context.convertToMRMC();
		} catch (Exception e) {
			Registry.getMain().handleWarning(e.getMessage());
			excp = true;
		}

		if (!excp) {
			String type = "";
			switch (m_config.getModel().getModelType()) {
				case CTMC:
					type = "csl";
					break;
				case LTS:
				case DTMC:
					type = "pctl";
					break;
			}

			m_config.parameters = config.parameters;
			m_config.modelType = Config.MRMC_MODEL;
			m_config.parameters.add(type);

		}

	}

	void justifyCenter() {
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension panelsize = getSize();
		int x = (int) (screensize.getWidth() - panelsize.getWidth()) / 2;
		int y = (int) (screensize.getHeight() - panelsize.getHeight()) / 2;
		setLocation(x, y);

	}

	private void initGUIComp() {
		buttonBack = new JButton(actionBack);
		buttonCancel = new JButton(actionCancel);
		buttonNext = new JButton(actionNext);

		JPanel panelRight = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panelRight.add(buttonBack);
		panelRight.add(buttonNext);

		JPanel panelButton = new JPanel(new BorderLayout());
		panelButton.setBorder(BorderFactory.createTitledBorder(""));
		Box b = Box.createHorizontalBox();
		b.add(buttonCancel);
		panelButton.add(b, BorderLayout.WEST);
		panelButton.add(panelRight, BorderLayout.EAST);

		add(panelButton, BorderLayout.SOUTH);

	}

}

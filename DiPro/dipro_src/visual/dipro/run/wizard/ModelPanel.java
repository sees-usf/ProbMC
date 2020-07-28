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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import parser.ast.ModulesFile;
//import prism.PrismException;
import prism.PrismLangException;
import prism.UndefinedConstants;
import dipro.run.Config;
//import dipro.run.Registry;

class ModelPanel extends PartPanel {

	private Action actionLoadModel;
	private JButton buttonLoadModel;
	private JTextField fieldModel, fieldProp;
	private ConstPanel modelConstPanel;
	private ModulesFile m_modulesFiles;

	ModelPanel(Config config) {
		super(config);
		initActions();
		initGUIComp();

	}

	private void initGUIComp() {
			buttonLoadModel = new JButton(actionLoadModel);

			fieldModel = new JTextField(35);
			fieldProp = new JTextField(35);

			JPanel panelModel = new JPanel(new FlowLayout(FlowLayout.LEADING));
			panelModel.setBorder(BorderFactory.createTitledBorder("Model"));
			panelModel.add(fieldModel);
			panelModel.add(buttonLoadModel);

			ModelPanel.this.add(panelModel, BorderLayout.NORTH);

			JPanel panelMainProp = new JPanel(new BorderLayout(5, 5));

			panelMainProp.setBorder(BorderFactory
					.createTitledBorder("Properties"));

			JPanel panelProp = new JPanel(new FlowLayout(FlowLayout.LEADING));
			panelProp.add(fieldProp);
			panelMainProp.add(panelProp, BorderLayout.SOUTH);

			modelConstPanel = new ModelConstPanel();
			ModelPanel.this.add(modelConstPanel, BorderLayout.CENTER);
			JPanel simPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

			ModelPanel.this.add(simPanel, BorderLayout.SOUTH);
	}

	// String getProperty(int index) {
	// if (m_propertiesFiles != null)
	// return ((Expression) m_propertiesFiles.getProperty(index)).toString();
	//
	// return "";
	// }

	private void initActions() {
		actionLoadModel = new AbstractAction("Load Model") {

			public void actionPerformed(ActionEvent e) {
				loadModel();
			}
		};
	}

	public String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	private void loadModel() {
		ExperimentSetup setup = ExperimentSetup.getInstance();
		int returnValue = setup.getFileChooser().showOpenDialog(this);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = setup.getFileChooser().getSelectedFile();
			boolean exception = false;
			try {
				fieldModel.setText("Loading...");
				m_modulesFiles = setup.getPrism().parseModelFile(file);
			} catch (Exception e) {
				exception = true;
				if (e instanceof PrismLangException)
					JOptionPane.showMessageDialog(null,e.getMessage(),"Syntax Error", JOptionPane.CANCEL_OPTION);
					fieldModel.setText(e.getMessage());
			}
			if (!exception) {
				fieldModel.setText(file.getName());
				modelConstPanel.fillTables();
				getConfig().setModelName(file.getName());
				getConfig().setCxFileName(file.getName());
			}

		}

	}

	boolean isValidConf() {
		if (m_modulesFiles == null)
			return false;
		return modelConstPanel.checkValidity();

	}

	// Finds the undefined constants of the constant panel
	class ModelConstPanel extends ConstPanel {
		private UndefinedConstants modelUnConst;

		public ModelConstPanel() {
			super("Model Constants");
		}
		// Fills the tables with constants
		public void fillTables() {
			ExperimentSetup setup = ExperimentSetup.getInstance();
			if (getConfig().propFile == null)
				modelUnConst = new UndefinedConstants(m_modulesFiles, null);
			else
				modelUnConst = new UndefinedConstants(m_modulesFiles, getConfig().propFile);
			for (int i = 0; i < modelUnConst.getMFNumUndefined(); i++) {
				modelTableConst.addConstant(modelUnConst.getMFUndefinedName(i),modelUnConst.getMFUndefinedType(i), "");
			}

			setup.enableNext();
		}

		public boolean checkValidity() {
//			return (modelUnConst.getMFNumUndefined() > 0) ? false : true;
			return !modelTableConst.isUnDefConstInTable();
		}

		@Override
		public void parseConstants() {
			// int size = modelTableConst.getRowCount();
			// System.out.println("Before parse mf:"+modelUnConst.getMFNumUndefined());
			// try {
			// String constName, constValue;
			// int type;
			// for (int i = 0; i < size; i++) {
			// constName = modelTableConst.getConstant(i).name;
			// constValue = "" + modelTableConst.getConstant(i).value;
			// type = modelUnConst.getMFUndefinedType(i);
			// modelUnConst.defineConstant(constName,
			// "" + parseValue(type, constValue));
			// System.out.println("Constant("+i+"):"+constName+"="+parseValue(type,
			// constValue) );
			//
			// }
			// modelUnConst.checkAllDefined();
			// modelUnConst.initialiseIterators();
			//
			// m_modulesFiles.setUndefinedConstants(modelUnConst.getMFConstantValues());
			ExperimentSetup.getInstance().setMf_constModel(modelTableConst);
			getConfig().setModel(m_modulesFiles);

			// } catch (PrismException e) {
			// Registry.getMain().handleWarning(e.getMessage());
			// }
		}

	}

	@Override
	void parseConstants() {
		modelConstPanel.parseConstants();
	}

}

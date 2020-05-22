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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import parser.ast.Expression;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import prism.PrismException;
import prism.PrismLangException;
import prism.UndefinedConstants;
import dipro.run.Config;
import dipro.run.Registry;
import dipro.run.VisMain;
import dipro.stoch.prism.PrismUntil;

class PropertyPanel extends PartPanel {

	private Action actionLoadProp,actionSimProp;
	private JButton buttonLoadProp, buttonSimProp;
	private JTextField fieldProp;
	private PropertiesFile m_propertiesFile;
	private PropertiesList listProp;
	private PropConstPanel mdPp;
	private boolean selectedProp;

	PropertyPanel(Config config) {
		super(config);
		selectedProp = false;
		initActions();
		initGUIComp();
	}

	void initGUIComp() {
		buttonLoadProp = new JButton(actionLoadProp);
		buttonSimProp = new JButton(actionSimProp);
		fieldProp = new JTextField(35);

		JPanel panelMainProp = new JPanel(new BorderLayout(5, 5));
		listProp = new PropertiesList();

		JScrollPane scrollPane = new JScrollPane(listProp);
		panelMainProp.setBorder(BorderFactory.createTitledBorder("Properties"));

		JPanel panelProp = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panelProp.add(fieldProp);
		panelProp.add(buttonLoadProp);
		panelProp.add(buttonSimProp);
		panelMainProp.add(scrollPane, BorderLayout.CENTER);
		panelMainProp.add(panelProp, BorderLayout.SOUTH);

		PropertyPanel.this.add(panelMainProp, BorderLayout.NORTH);
		mdPp = new PropConstPanel();
		PropertyPanel.this.add(mdPp, BorderLayout.CENTER);
	}

	String getProperty(int index) {
		if (m_propertiesFile != null)
			return ((Expression) m_propertiesFile.getProperty(index))
					.toString();

		return "";
	}

	private void initActions() {
		actionLoadProp = new AbstractAction("Load Properties") {

			public void actionPerformed(ActionEvent e) {
				loadProp();

			}

		};
		
		actionSimProp = new AbstractAction("Simulate") {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				m_config.propId = -1;
				UndefinedConstants consts = new UndefinedConstants(m_config.getModel(),null);
				
				ConstTableModel modelTable = ExperimentSetup.getInstance()
						.getMf_constModel();
				int size = modelTable.getRowCount();
				try {
					String constName, constValue;
					int type;
					for (int i = 0; i < size; i++) {
						constName = modelTable.getConstant(i).name;
						constValue = "" + modelTable.getConstant(i).value;
						type = consts.getMFUndefinedType(i);
						consts.defineConstant(constName,
								"" + mdPp.parseValue(type, constValue));

					}
					consts.checkAllDefined();
					consts.initialiseIterators();
					m_config.getModel().setUndefinedConstants(
							consts.getMFConstantValues());
				} catch (PrismException e) {
					JOptionPane.showMessageDialog(null,e.getMessage(),"Constant error", JOptionPane.CANCEL_OPTION);
					return;
				}
				consts.initialiseIterators();
				ExperimentSetup.getInstance().simulateCX();
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

	private void loadProp() {
		ExperimentSetup setup = ExperimentSetup.getInstance();

		int returnValue = setup.getFileChooser().showOpenDialog(this);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = setup.getFileChooser().getSelectedFile();
			boolean exception = false;
			ModulesFile modulesFile = m_config.getModel();
			try {
				fieldProp.setText("Loading...");
				if (modulesFile != null)
					m_propertiesFile = setup.getPrism().parsePropertiesFile(
							modulesFile, file);
				else {
					exception = true;
				}
			} catch (Exception e) {
				exception = true;
				if (e instanceof PrismLangException) {
					fieldProp.setText("File:" + file.getName()
							+ " is not valid for this model.");
					JOptionPane
							.showMessageDialog(
									null,
									"File:"
											+ file.getName()
											+ " is not a valid property file for this model.",
									"Inavalid Property File",
									JOptionPane.CANCEL_OPTION);
					Registry.getMain().handleWarning(
							e.getMessage() + " " + modulesFile.toString());
					listProp.cleanList();
					m_propertiesFile = null;
					listProp.validate();
				}
			}
			if (!exception) {
				listProp.addProperty(m_propertiesFile);
				fieldProp.setText(file.getName());
				m_config.setPropName(file.getName());
				mdPp.fillTables();

			}
		}

	}

	class PropertiesList extends JList {

		DefaultListModel modelList;
		PropertyCellRenderer labelProp;

		public PropertiesList() {
			modelList = new DefaultListModel();
			setModel(modelList);

			labelProp = new PropertyCellRenderer();
			setCellRenderer(labelProp);

			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}

		public void addProperty(PropertiesFile propfile) {
			for (int i = 0; i < propfile.getNumProperties(); i++) {
				modelList.addElement(propfile.getProperty(i));
			}
		}

		public void cleanList() {
			modelList.removeAllElements();
		}

		public Object getProperty(int index) {
			return modelList.get(index);
		}

		class PropertyCellRenderer extends JLabel implements ListCellRenderer {

			JCheckBox checkBoxProp;

			PropertyCellRenderer() {
				setLayout(new BorderLayout());
				setBorder(BorderFactory.createTitledBorder(""));
				checkBoxProp = new JCheckBox();
				add(checkBoxProp, BorderLayout.EAST);

			}

			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				Expression prop = (Expression) getProperty(index);

				boolean isSupported = PrismUntil.isSupportedProperty(prop);

				setText(prop.toString());
				setEnabled(isSupported);
				checkBoxProp.setEnabled(isSupported);

				if (isSelected) {
					checkBoxProp.setSelected(isSupported);
					selectedProp = isSupported;
					m_config.setPropId(index);
					ExperimentSetup.getInstance().enableNext();
				} else {
					checkBoxProp.setSelected(false);
				}
				return this;
			}

		}
	}

	class PropConstPanel extends ConstPanel {

		private UndefinedConstants unDefConst;

		public PropConstPanel() {
			super("Property Constants");
		}

		public void fillTables() {
			ExperimentSetup setup = ExperimentSetup.getInstance();

			unDefConst = new UndefinedConstants(m_config.getModel(),
					m_propertiesFile);
			for (int i = 0; i < unDefConst.getPFNumUndefined(); i++) {
				modelTableConst.addConstant(unDefConst.getPFUndefinedName(i),
						unDefConst.getPFUndefinedType(i), "");
			}
			setup.enableNext();
		}

		public boolean checkValidity() {
			// return (unDefConst.getPFNumUndefined() > 0) ? false : true;
			return !modelTableConst.isUnDefConstInTable();
		}

		public void parseConstants() {
			ConstTableModel propTableConst = getModelTable();
			ConstTableModel modelTable = ExperimentSetup.getInstance()
					.getMf_constModel();
			int size = modelTable.getRowCount();
			try {
				String constName, constValue;
				int type;
				for (int i = 0; i < size; i++) {
					constName = modelTable.getConstant(i).name;
					constValue = "" + modelTable.getConstant(i).value;
					type = unDefConst.getMFUndefinedType(i);
					unDefConst.defineConstant(constName,
							"" + parseValue(type, constValue));

				}
				size = propTableConst.getRowCount();
				for (int i = 0; i < size; i++) {
					constName = propTableConst.getConstant(i).name;
					constValue = "" + propTableConst.getConstant(i).value;
					type = unDefConst.getPFUndefinedType(i);
					unDefConst.defineConstant(constName,
							"" + parseValue(type, constValue));

				}
			
				unDefConst.checkAllDefined();
				unDefConst.initialiseIterators();

				m_propertiesFile.setUndefinedConstants(unDefConst
						.getPFConstantValues());
				m_config.getModel().setUndefinedConstants(
						unDefConst.getMFConstantValues());
				m_config.setProp(m_propertiesFile);
			} catch (PrismException e) {
				Registry.getMain().handleWarning(e.getMessage());
			}

		}

	}

	@Override
	boolean isValidConf() {
		if (m_config.getProp() != null)
			try {
				m_config.getProp().semanticCheck(m_config.getModel(),
						m_config.getProp());
			} catch (Exception e) {
				listProp.cleanList();
				m_config.setModel(null);
				mdPp.cleanTable();
				return false;
			}
		if (!selectedProp)
			return false;
		return mdPp.checkValidity();
	}

	@Override
	void parseConstants() {
		mdPp.parseConstants();
	}

}

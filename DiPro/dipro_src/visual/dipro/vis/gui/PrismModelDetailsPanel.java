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

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import parser.Values;
import parser.ast.LabelList;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dipro.run.Context;
import dipro.stoch.CTMC;
import dipro.stoch.MarkovModel;
import dipro.stoch.UniformCTMC;
import dipro.stoch.prism.PrismModel;

public class PrismModelDetailsPanel extends ModelDetailsPanel {

	private static final long serialVersionUID = -3016802229194880337L;

	protected JPanel constantsPanel;
	protected JPanel labelsPanel;

	public PrismModelDetailsPanel(Context context) {
		super(context);
	}

	protected void init() {
		propPanel = new JPanel();
		propPanel.setBorder(new TitledBorder("Safety Property Being Checked"));
		constantsPanel = new JPanel();
		constantsPanel.setBorder(new TitledBorder("Model Constants"));
		labelsPanel = new JPanel();
		labelsPanel.setBorder(new TitledBorder("Model Labels"));
		loadPropPanel();
		loadConstantsPanel();
		loadLabelsPanel();
		FormLayout layout = new FormLayout("3dlu, pref, 3dlu, pref, 3dlu",
				"p, 3dlu, top:p");
		CellConstraints cc = new CellConstraints();
		PanelBuilder builder = new PanelBuilder(layout);
		builder.add(propPanel, cc.xyw(2, 1, 3));
		builder.add(constantsPanel, cc.xy(2, 3));
		builder.add(labelsPanel, cc.xy(4, 3));
		add(builder.getPanel());
	}

	protected void loadConstantsPanel() {
		PrismModel model = prismModel();
		Values constValues = model.constantValues();
		String[][] data = new String[constValues.getNumValues()][2];
		for (int i = 0; i < constValues.getNumValues(); i++) {
			data[i][0] = constValues.getName(i);
			data[i][1] = constValues.getValue(i).toString();
		}
		JTable table = new JTable(data, new String[] { "Constant", "Value" });
		constantsPanel.add(table);
	}

	private PrismModel prismModel() {
		MarkovModel m = (MarkovModel) context.getGraph();
		if (m instanceof UniformCTMC) {
			m = (CTMC) m.getRawProbModel();
		}
		return (PrismModel) m.getRawProbModel();
	}

	protected void loadLabelsPanel() {
		PrismModel model = prismModel();
		LabelList labels = model.propertiesFile().getLabelList();
		String[][] data = new String[labels.size()][2];
		int max0 = 0, max1 = 0;
		for (int i = 0; i < labels.size(); i++) {
			data[i][0] = labels.getLabelName(i);
			data[i][1] = labels.getLabel(i).toString();
			int w0 = data[i][0].length();
			int w1 = data[i][1].length();
			if (max0 < w0)
				max0 = w0;
			if (max1 < w1)
				max1 = w1;
		}
		JTable table = new JTable(data, new String[] { "Label", "Expression" });
		table.getColumn("Label").setPreferredWidth(10 * max0);
		table.getColumn("Expression").setPreferredWidth(7 * max1);
		labelsPanel.add(table);
	}
}

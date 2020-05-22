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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import dipro.run.Context;

public class ModelDetailsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected JPanel propPanel;
	protected Context context;

	public ModelDetailsPanel(Context context) {
		this.context = context;
		init();
	}

	protected void init() {
		propPanel = new JPanel();
		propPanel.setBorder(new TitledBorder("Property Being Checked"));
		loadPropPanel();
		add(propPanel);
	}

	protected void loadPropPanel() {
		propPanel.add(new JLabel(context.getProperty().toString()));
	}

}

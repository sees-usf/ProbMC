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

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import dipro.run.Registry;
import dipro.vis.InteractiveView;

public class VisualizationPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6315667459070654821L;
	Component viewer;

	/**
	 * This is the default constructor
	 */
	public VisualizationPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(600, 400);
	}

	public void setViewer(InteractiveView vis) {
		if (viewer != null) {
			remove(viewer);
			Registry.getMain().tech()
					.println("Viewer (" + viewer + ") removed");
		}
		viewer = (Component) vis.getViewer();
		add(viewer, BorderLayout.CENTER);

		/* Print log message */
		Registry.getMain().tech().println("Viewer changed to " + vis);

		/* Update all Layouts */
		getRootPane().updateUI();
		validate();
	}

}

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

import java.lang.reflect.InvocationTargetException;

import javax.swing.JMenu;

public class AlgorithmMenu extends JMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8764760494336194024L;

	public AlgorithmMenu() throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		super("Select Algorithm");

		// Enumeration<AlgorithmSettings> e =
		// AlgorithmTemplateList.getInstance().getAlgorithmSettings();

		// while(e.hasMoreElements()) {
		// AlgorithmSettings settings = e.nextElement();
		// HA: Anpassen
		// Action loadAction = new
		// LoadAlgorithm(settings.getValue(AlgorithmSettings.NAME),
		// settings.getValue(AlgorithmSettings.ALGORITHM_CLASS));
		// add(loadAction);
		// }
	}

	// public class LoadAlgorithm extends AbstractAction {
	//
	// /**
	// *
	// */
	// private static final long serialVersionUID = -5325878226115376204L;
	// protected String algorithmClass;
	//		
	// public LoadAlgorithm(String text, String algoClass) {
	// super(text);
	// algorithmClass = algoClass;
	// }
	//		
	// public void actionPerformed(ActionEvent e) {
	// System.out.println("LoadAlgorithm has been performed, should load " +
	// algorithmClass);
	// }
	//		
	// }

}

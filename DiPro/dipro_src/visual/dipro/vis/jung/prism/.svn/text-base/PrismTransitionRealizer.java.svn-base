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

package dipro.vis.jung.prism;

import dipro.stoch.prism.PrismTransition;
import dipro.stoch.prism.PrismTransitionData;
import dipro.vis.jung.JungEdgeRealizer;
import dipro.vis.jung.JungVisualizer;

public class PrismTransitionRealizer extends JungEdgeRealizer {

	protected PrismTransitionRealizer(JungVisualizer vis, PrismTransition t) {
		super(vis, t);
		PrismTransitionData tData = ((PrismTransition) edge)
				.getTransitionData();
		String lbl = tData.getLabel();
		if (lbl == null) {
			System.out.println("label==null\n " + tData);
		}
		assert lbl.length() >= 2 && lbl.charAt(0) == '['
				&& lbl.charAt(lbl.length() - 1) == ']';
		lbl = lbl.substring(1, lbl.length() - 1);
		//if (!lbl.equals(""))
	//		setLabelText(lbl);
		// EdgeLabel label = createEdgeLabel();
		// label.setText(tData.getLabel());
		// label.setModel(EdgeLabel.TWO_POS);
		// label.setPosition(EdgeLabel.HEAD);
		// addLabel(label);
		// System.out.println("Label: "+tData.getLabel());
	}

	// public void paint(Graphics2D gfx) {
	// super.paint(gfx);
	// }
}

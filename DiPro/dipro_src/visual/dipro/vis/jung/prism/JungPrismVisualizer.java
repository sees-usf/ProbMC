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

import dipro.alg.BF;
import dipro.graph.DirectedEdge;
import dipro.run.VisContext;
import dipro.stoch.prism.PrismTransition;
import dipro.vis.gui.DetailsDialog;
import dipro.vis.gui.PrismDetailsDialog;
import dipro.vis.jung.JungEdgeRealizer;
import dipro.vis.jung.JungVisualizer;

public class JungPrismVisualizer extends JungVisualizer {

	public JungPrismVisualizer(VisContext context, BF alg) throws Exception {
		super(context, alg);
	}

	protected DetailsDialog createDetailsDialog() throws Exception {
		return new PrismDetailsDialog(this);
	}

	@Override
	protected void registerViewModes() {
		super.registerViewModes();
//		view.addViewMode(new PrismVisualDebuggingMode(this));
	}

	protected JungEdgeRealizer createEdgeRealizer(DirectedEdge uv) {
		return new PrismTransitionRealizer(this, (PrismTransition) uv);
	}
}

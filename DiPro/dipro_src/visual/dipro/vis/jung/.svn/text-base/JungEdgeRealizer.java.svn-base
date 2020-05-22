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

package dipro.vis.jung;

import java.awt.Color;
import java.awt.Graphics2D;

import dipro.graph.DirectedEdge;

public class JungEdgeRealizer  {

	protected static final double W = 5;
	protected static final int normalFontSize = 6;

	protected JungVisualizer visualizer;
	protected DirectedEdge edge;

	protected JungEdgeRealizer(JungVisualizer vis, DirectedEdge e) {
		this.visualizer = vis;
		this.edge = e;
	}

	protected double computeLineWidth() {
		double f = visualizer.normalize(f());
		double x = f - 1;
		double y = Math.exp(x);
		return (float) (y * W);
	}

	protected double f() {
		return visualizer.getSearchTree().f(edge.target());
	}

	public String getLabel(String label) {
		double f =   visualizer.getSearchTree().f(edge.target());
		Color color = visualizer.color(f);
		boolean b = visualizer.belongsToSolution(edge);
		if(b) color = JungVisualizer.solColor;
		double c = visualizer.getSolutionCardinality(edge);
		if (c > 0) {
			int size = normalFontSize
					+ (int) Math.round(visualizer.getGradient() * c
							/ visualizer.getSolutionValue());
			 return "<html><font size="+size+"+ color="+ color +">"+label;
		}
		 return "<html><font size="+normalFontSize+" + color="+ color +">"+label;
	}
	

	public DirectedEdge getDiProEdge() {
		return edge;
	}
}

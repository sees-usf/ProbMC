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

package dipro.util;

import dipro.alg.BF;
import dipro.alg.KSPAlgorithm;
import dipro.alg.PathGraph;
import dipro.graph.DirectedEdge;

public class KEvaluationFunction extends EvaluationFunction {

//	protected final Attribute DEPTH_TO_TARGET = new Attribute("DEPTH_TO_TARGET");

	public KEvaluationFunction(KSPAlgorithm alg) {
		super(alg);
	}

	protected int computeDepth(BF.SearchMark uMark, DirectedEdge uv,
			BF.SearchMark vMark) {
		if(uMark==null) {
			//Then it is the dummy node
			return -1;
		}
		int d = (Integer) uMark.depth();
		PathGraph pGraph = (PathGraph) alg.getGraph();
		if(pGraph.isCrossEdge(uv)) d = d+1;
		return d;
	}

}

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

package dipro.stoch;

import dipro.alg.BF;
import dipro.alg.XBF;
import dipro.graph.DirectedEdge;
import dipro.util.DTMCEvaluationFunction;

public class BoundedDTMCEvaluationFunction extends DTMCEvaluationFunction {

	protected int maxDepth;

	public BoundedDTMCEvaluationFunction(XBF alg) throws Exception {
		super(alg);
		maxDepth = -1;
	}

	protected double computeG(BF.SearchMark uMark, DirectedEdge uv,
			BF.SearchMark vMark) throws Exception {
		double g;
		if (maxDepth < 0)
			calculateMaxDepth();
		if (vMark.depth() > getMaxDepth())
			g = 0.0d;
		else {
			g = super.computeG(uMark, uv, vMark);
		}
		// System.out.println("Depth = "+vDepth+" -> g = "+g);
		return g;
	}

	public int getMaxDepth() {
		assert maxDepth > 0;
		return maxDepth;
	}


	protected void calculateMaxDepth() throws Exception {
		assert alg.getProperty() instanceof StochTBoundedUntil;
		assert alg.getGraph().getClass() == DTMC.class;
		assert maxDepth < 0;
		double t = ((StochTBoundedUntil) alg.getProperty()).timeBound();
		assert t - (int) t == 0;
		maxDepth = (int) t;
	}
}

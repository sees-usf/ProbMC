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
import dipro.util.MultiplicativeEvaluationFunction;

public class CTMCEvaluationFunction extends MultiplicativeEvaluationFunction {

	public CTMCEvaluationFunction(XBF alg) throws Exception {
		super(alg);
	}

	protected double computeG(BF.SearchMark uMark, DirectedEdge uv,
			BF.SearchMark vMark) throws Exception {
		assert alg instanceof XBF;
		double g = 1.0;
		if (uv != null) {
			assert uv.source().equals(uMark.vertex());
			g = uMark.g();
//			float p =  ((CTMC) alg.getGraph()).weight(uv);
			float p = weight((StochasticTransition)uv);
//			double parentE = ((CTMC) alg.getGraph()).totalExitRate((State) uMark
//					.vertex());
//			parentE = parentE - ((CTMC) alg.getGraph()).loopRate((State) uMark
//					.vertex());
//			double rate = ((StochasticTransition) uv).getProbOrRate();
//			BigDecimal p = new BigDecimal(rate);
//			p = p.divide(new BigDecimal(parentE), MathContext.DECIMAL128);
//			g = g.multiply(p, MathContext.DECIMAL128);
			g = g * p;
		}
		return g;
	}
	
	private float weight(StochasticTransition t) throws Exception {
//		State s = (State)t.source();
//		float r = t.getProbOrRate();
//		float e = totalExitRate(s);
//		return (float)(r/e);
		return alg.getGraph().weight(t);
	}
	
//	private float totalExitRate(State s) {
//		float exitRate = 0.0f;
//		Iterator<? extends DirectedEdge> iter = alg.getExploredGraph().outgoingEdges(s);
//		while (iter.hasNext()) {
//			StochasticTransition t = (StochasticTransition)iter.next();
//			assert t.source().equals(s);
//			exitRate = exitRate + t.getProbOrRate();
//		}
//		return exitRate;
//	}
}

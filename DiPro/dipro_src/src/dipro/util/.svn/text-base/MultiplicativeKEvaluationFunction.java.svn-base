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
import dipro.graph.DirectedEdge;
import dipro.run.Registry;

public class MultiplicativeKEvaluationFunction extends KEvaluationFunction {

	public MultiplicativeKEvaluationFunction(KSPAlgorithm alg) {
		super(alg);
	}

	protected double computeG(BF.SearchMark uMark, DirectedEdge uv,
			BF.SearchMark vMark) throws Exception {
		double g = 1.0d;
		if (uv != null) {
			assert uv.source().equals(uMark.vertex());
			double w = alg.getGraph().weight(uv);
			g = uMark.g() * w;
			if (g <= 0.0d) {
				Registry.getMain().handleWarning("Inaccuracy in computing with doubles: "
										+ uMark.g() + " * "
										+ alg.getGraph().weight(uv) + " = " + g);
				assert g > -1e-6;
				g = 0.0;
			}
		}
		return g;
	}

	public double f(BF.SearchMark vMark) {
		double f;
		if (alg.getConfig().greedy)
			f = h(vMark);
		else
			f = g(vMark) * h(vMark);
		return f;
	}

	public double computeH(BF.SearchMark vMark) throws Exception {
		if (alg.getHeuristic() == null)
			return 1.0d;
		return alg.getHeuristic().evaluate(vMark.vertex());
	}

	public double computeTraceValue(Trace trace) throws Exception {
		double g = 1.0d;
		for (int i = 0; i < trace.length() - 1; i++) {
			g = g * alg.getGraph().weight(trace.getEdge(i));
		}
		return g;
	}
}

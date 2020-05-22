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

import dipro.alg.XBF;

public class DTMCEvaluationFunction extends MultiplicativeEvaluationFunction {

	public DTMCEvaluationFunction(XBF alg) throws Exception {
		super(alg);
	}

	// protected double computeG(BF.SearchMark uMark, DirectedEdge uv,
	// BF.SearchMark vMark) throws Exception {
	// assert alg instanceof XBF;
	// double g = 1.0d;
	// if(uv!=null) {
	// assert uv.source().equals(uMark.vertex());
	// float parentLoop = 0.0f;
	// if(uMark.has(((XBF)alg).SELF_LOOP)){
	// parentLoop =
	// alg.getGraph().weight((StochasticTransition)uMark.get(((XBF)alg).SELF_LOOP));
	// }
	// float p = alg.getGraph().weight(uv) + alg.getGraph().weight(uv)/(1 -
	// parentLoop) * parentLoop;
	// g = uMark.g() * p;
	// if(uMark.g()>0 && p>0 && g<=0.0d) {
	// Registry.getMain().handleWarning("Inaccuracy in computing with doubles:
	// "+uMark.g()+" * "+alg.getGraph().weight(uv)+" = "+g);
	// }
	// }
	// return g;
	// }

//	protected double computeG(BF.SearchMark uMark, DirectedEdge uv,
//			BF.SearchMark vMark) throws Exception {
//		assert alg instanceof XBF;
//		BigDecimal g = BigDecimal.ONE;
//		if (uv != null) {
//			assert uv.source().equals(uMark.vertex());
//			g = new BigDecimal(uMark.g());
////			float parentLoop = 0.0f;
////			if (uMark.has(((XBF) alg).SELF_LOOP)) {
////				parentLoop = alg.getGraph()
////						.weight(
////								(StochasticTransition) uMark
////										.get(((XBF) alg).SELF_LOOP));
////			}
//			BigDecimal p = new BigDecimal(alg.getGraph().weight(uv));
////			BigDecimal x = BigDecimal.ONE.subtract(new BigDecimal(parentLoop),
////					alg.getConfig().mathContext);
////			p = p.divide(x, alg.getConfig().mathContext);
//			g = g.multiply(p, alg.getConfig().mathContext);
//			// if(uMark.g()>0 && p>0 && g<=0.0d) {
//			// Registry.getMain().handleWarning("Inaccuracy in computing with
//			// doubles: "+uMark.g()+" * "+alg.getGraph().weight(uv)+" = "+g);
//			// }
//		}
//		return g.doubleValue();
//	}
}

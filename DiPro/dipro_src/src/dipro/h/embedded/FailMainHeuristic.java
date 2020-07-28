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

package dipro.h.embedded;

import prism.PrismException;
import dipro.alg.BF;
import dipro.graph.Vertex;
import dipro.run.Context;
//import dipro.stoch.UniformCTMC;
import dipro.stoch.prism.PrismState;

/** 
 * fail_sensors = i=2&s<MIN_SENSORS
 * @author hxa
 *
 */
public class FailMainHeuristic extends EmbeddedHeuristic {

	public FailMainHeuristic(Context settings, BF alg) throws PrismException {
		super(settings, alg);
	}

	public double evaluate(Vertex v) throws Exception {
		int s = ((PrismState) v).values().getIntValueOf(str_s);
		int i = ((PrismState) v).values().getIntValueOf(str_i);
		int o = ((PrismState) v).values().getIntValueOf(str_o);
		int a = ((PrismState) v).values().getIntValueOf(str_a);
		int m = ((PrismState) v).values().getIntValueOf(str_m);
		if(m==0) return 1;
		double e = e(s, i, o, a, m);
		double h = lambda_p/e;
//		double h = 0.5;
		return h;
	}
}

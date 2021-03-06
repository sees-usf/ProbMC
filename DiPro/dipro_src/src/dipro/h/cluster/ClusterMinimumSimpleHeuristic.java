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

package dipro.h.cluster;

import prism.PrismException;
import dipro.alg.BF;
import dipro.graph.Vertex;
import dipro.h.PrismHeuristic;
import dipro.run.Context;
import dipro.stoch.prism.PrismState;

public class ClusterMinimumSimpleHeuristic extends PrismHeuristic {
	
	public ClusterMinimumSimpleHeuristic(Context settings, BF alg) throws PrismException {
		super(settings, alg);
	}
	
	/** This method computes the heuristic value of the given state. 
	 * The current heuristic computes an estimate of each failure mode: 
	 * sensor failure, actuator failure, input/output processor failure, 
	 * and main processor failure. The total heuristic estimate is the 
	 * defined as the sum of the individual estimates. The sum is 
	 * justified by the fact that all failure modes are independent of 
	 * each other. 
	 * @param the considered state
	 * @return the heuristic value of the state
	 */
	public double evaluate(Vertex v) throws Exception {
		PrismState s = (PrismState)v;
		double h1 = 1.0d;
		boolean Toleft_n = (Boolean)getValue(s, "Toleft_n");
		if(Toleft_n) {
			h1 = 0.5;
		}
		double h2=1.0d;
		boolean Toright_n = (Boolean)getValue(s, "Toright_n");
		if(Toright_n) {
			h2 = 0.5;
		}
		double h = h1 * h2;
		return h;
	}

}

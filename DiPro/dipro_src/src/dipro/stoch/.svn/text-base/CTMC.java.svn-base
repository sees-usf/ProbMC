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

import java.util.Iterator;

import dipro.graph.Edge;
import dipro.graph.State;
import dipro.graph.StateSpace;

public class CTMC extends MarkovModel {

	public CTMC(StateSpace raw) {
		super(raw);
	}

	public String toString() {
		return "CTMC: \n" + rawProbModel.toString();
	}

	/** This method returns the total exit rate of a state of the CTMC. 
	 * The total exit rate includes also the rate of self loops. 
	 * Notice that we allow self loops in the original CTMC.
	 * @param s the state of which we want to compute the exit rate.
	 * @return the total exit rate.
	 */
	private double totalExitRate(State s) {
		double exitRate = 0.0d;
		Iterator<? extends StochasticTransition> iter = outgoingEdges(s);
		while (iter.hasNext()) {
			StochasticTransition t = iter.next();
			assert t.source().equals(s);
			exitRate = exitRate + t.getProbOrRate();
		}
		return exitRate;
	}

	/** This method returns the total rate of all self transition of a state of the CTMC. 
	 * @param s the state of which we want to compute the loop rate.
	 * @return the total rate of all self loops of s. 
	 */
	public double loopRate(State s) {
		double loopRate = 0.0d;
		Iterator<? extends StochasticTransition> iter = outgoingEdges(s);
		while (iter.hasNext()) {
			StochasticTransition t = iter.next();
			assert t.source().equals(s);
			if (t.target().equals(s)) {
				// only self loops
				loopRate = loopRate + t.getProbOrRate();
			}
		}
		return loopRate;
	}
	
	/** Returns the weight of a transition. The weight of a 
	 * transition is defined here as the rate of the transition 
	 * devided by the total exit rate of the transition source state.
	 * @param e the transition we want to compute its weight
	 * @return the weight of e
	 */
	public float weight(Edge e) {
		StochasticTransition uv = (StochasticTransition) e;
		double rate = uv.getProbOrRate();
		double parentE = totalExitRate((State)uv.source());
		assert rate <= parentE;
//		double l = loopRate((State)uv.source());
//		parentE = parentE - l;
//		if(rate>parentE) {
//			System.out.println(uv);
//			System.out.println(uv.source());
//			System.out.println(uv.target());
//			System.out.println("E = "+parentE);
//			System.out.println("L = "+l);
//		}
//		assert rate <= parentE;
		double w = rate / parentE;
		assert w <= 1.0d || w>=0.0d;
		return (float)w;
	}
}

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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import dipro.graph.Edge;
import dipro.graph.OnTheFlyStateSpace;
import dipro.graph.State;
import dipro.graph.Transition;
import dipro.graph.Vertex;
import dipro.util.Attribute;

public class UniformCTMC extends DTMC {

	public final Attribute UNIFORM_LOOP = new Attribute("UNIFORM_LOOP");
	
	private double uniformRate;

	public UniformCTMC(CTMC ctmc, double q) {
		super(ctmc);
		this.uniformRate = q;
	}

	public CTMC getCTMC() {
		return (CTMC) rawProbModel;
	}

	public double getUniformRate() {
		return uniformRate;
	}

	public String toString() {
		return "CTMC (uniformised, q=" + uniformRate + "): \n"
				+ getCTMC().getRawProbModel().toString();
	}

	/** Returns the weight of a transition. The weight of a 
	 * transition is defined here as the rate of the transition 
	 * devided by the uniformization rate of the transition source state.
	 * @param e the transition we want to compute its weight
	 * @return the weight of e
	 */
	public float weight(Edge e) {
		assert e instanceof StochasticTransition;
		float r = ((StochasticTransition) e).getProbOrRate();
		assert r > 0.0d;
		return (float) (r / getUniformRate());
	}

	private double totalExitRate(State s) {
//		return getCTMC().totalExitRate(s);
		Iterator<? extends StochasticTransition> iter = getCTMC().outgoingEdges(s);
		double exitRate = 0.0d;
		while (iter.hasNext()) {
			StochasticTransition t = iter.next();
			assert t.source().equals(s);
			exitRate = exitRate + t.getProbOrRate();
		}
		return exitRate;
	}

	public double loopRate(State s) {
		return getCTMC().loopRate(s);
	}

	@SuppressWarnings("unchecked")
	public Iterator<? extends StochasticTransition> outgoingEdges(Vertex v) {
		Collection<StochasticTransition> trans;
		if (rawProbModel instanceof OnTheFlyStateSpace) {
			trans = (Collection<StochasticTransition>) ((OnTheFlyStateSpace) rawProbModel)
					.generateOutgoingTransitions((State) v);
		} else {
			Iterator<? extends Transition> iter = rawProbModel.outgoingEdges(v);
			trans = new LinkedList<StochasticTransition>();
			while (iter.hasNext()) {
				trans.add((StochasticTransition) iter.next());
			}
		}
		process((State) v, trans);
		return trans.iterator();
	}

	/** This method adds to the outgoing transitions of the given state a uniformization loop. 
	 * The rate of this loop is equal to the difference between the uniformization rate and 
	 * the total exit rate of the state. A uniformization rate will be added even if its rate 
	 * is equal to zero. 
	 * @param s the state
	 * @param transitions the outgoing transitions of the given state s
	 */
	protected void process(State s, Collection<StochasticTransition> transitions) {
		double exitRate = 0.0d;
//		LinkedList<StochasticTransition> loops = new LinkedList<StochasticTransition>();
		for (StochasticTransition t : transitions) {
			assert t.source().equals(s);
//			if (!t.target().equals(s)) {
				exitRate = exitRate + t.getProbOrRate();
//			} else {
//				loops.add(t);
//			}
		}
//		transitions.removeAll(loops);
		double uniformLoopRate = uniformRate - exitRate;
		assert uniformLoopRate >= 1e-10; // Could be negative because of numerical inaccuracy
		uniformLoopRate = Math.max(uniformLoopRate, 0.0d);
//		if (uniformLoopRate > 0.0d) {
			StochasticTransition loop = (StochasticTransition) createTransition(
					s, s);
			loop.setProbOrRate((float) (uniformLoopRate));
			transitions.add(loop);
//		}
	}

}

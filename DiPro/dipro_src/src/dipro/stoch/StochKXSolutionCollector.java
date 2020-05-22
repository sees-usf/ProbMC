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

import dipro.alg.KSPAlgorithm;
import dipro.alg.BF.SearchMark;
import dipro.graph.DirectedEdge;
import dipro.graph.State;
import dipro.graph.Vertex;
import dipro.util.SearchTree;
import dipro.util.Trace;

public class StochKXSolutionCollector extends StochXSolutionCollector {

	public StochKXSolutionCollector(KSPAlgorithm alg) throws Exception {
		super(alg);
		q = 1.2;
	}
	
	protected Trace constructSolTrace(SearchMark mark) {
		Trace trace = ((KSPAlgorithm) alg).constructTrace(mark);
		return trace;
	}
	
	protected float totalExitRate(State s) {
		float exitRate = 0.0f;
		Iterator<? extends DirectedEdge> iter = ((KSPAlgorithm)alg).getBasicSearchAlgorithm().getExploredGraph().outgoingEdges(s);
		while (iter.hasNext()) {
			StochasticTransition t = (StochasticTransition)iter.next();
			assert t.source().equals(s);
			exitRate = exitRate + t.getProbOrRate();
		}
		return exitRate;
	}
	
	protected synchronized void processTrace(SearchMark targetMark) throws Exception {
		Trace trace = constructSolTrace(targetMark);
		if(firstTarget==null) firstTarget = (State) trace.getVertex(trace.length()-1);
		if (trace.length() > 0) {
			Vertex v = trace.getFirstVertex();
			incrementSolCardinality(v, targetMark.f());
			solutionGraph.addVertex(v);
		}
		Iterator<DirectedEdge> edges = trace.getEdges();
		while (edges.hasNext()) {
			DirectedEdge e = edges.next();
			incrementSolCardinality(e, targetMark.f());
			incrementSolCardinality(e.target(), targetMark.f());
			/* < ForDebugging > */
//			if(!solutionGraph.contains(e)) {
//				System.out.println("\nNew Solution Edge: "+e);
//			} 
			/* </ ForDebugging > */
			solutionGraph.addEdge(e);
		}
		
		recordTrace(trace);
		modelCheckSolutionIfNecessary(true);
	}
	
	protected SearchTree getExploredGraph() {
		return ((KSPAlgorithm)alg).getBasicSearchAlgorithm().getSearchTree();
	}
}

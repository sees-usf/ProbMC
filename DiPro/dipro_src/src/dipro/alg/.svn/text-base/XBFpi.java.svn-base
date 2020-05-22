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

package dipro.alg;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import dipro.graph.DirectedEdge;
import dipro.graph.Vertex;
import dipro.run.Context;
import dipro.stoch.PiEvaluationFunction;
import dipro.stoch.PiInterface;
import dipro.stoch.StochasticTransition;
import dipro.stoch.UniformCTMC;
import dipro.util.DiProException;
import dipro.util.EvaluationFunction;
import dipro.util.InverseComparator;
import dipro.util.Safety;

/** A specialization of XBF which uses PiEvaluationFunction. For more details 
 * see XZpi and XUZpi in Aljazzar and Leue TSE 2009 (submitted). 
 * It is only applicable to uniformized CTMCs.
 * @author aljazzar
 */
public class XBFpi extends XBFstoch {

	protected XBFpi() {
		super();
	}

	public void bind(Context context, Comparator<Double> comparator,
			EvaluationFunction evaluationFunction) {
		assert comparator instanceof InverseComparator;
		assert evaluationFunction instanceof PiEvaluationFunction;
		super.bind(context, comparator, evaluationFunction);
	}

	protected void vertexExpanded(SearchMark uMark) throws Exception {
		super.vertexExpanded(uMark);
		((PiEvaluationFunction) evaluationFunction)
				.removeTermporalAttributes(uMark);
	}

	protected RelaxationInfo relaxOpen(SearchMark newVMark, SearchMark oldVMark)
			throws Exception {
		int newDepth = newVMark.depth();
		int oldDepth = oldVMark.depth();
		if (newDepth <= oldDepth
				|| property.check(newVMark.vertex()) == Safety.TRUE) {
			((PiEvaluationFunction) evaluationFunction).addParentPis(
					newVMark, oldVMark);
		}
		return super.relaxOpen(newVMark, oldVMark);
	}

	protected int computeSearchMemory() throws DiProException {
		int searchMarkSize = 0;
		/* 8 bytes for g (double) */
		searchMarkSize = searchMarkSize + 8; 
		/* 4 bytes for depth (double) */
		searchMarkSize = searchMarkSize + 4; 
		/* The heuristic value does not need to be stored. */
		/* space for the parent pointer (i.e. tree edge). 
		 * The tree edge is a reference (4 bytes) to an edge 
		 * in the graph.*/
		searchMarkSize = searchMarkSize + 4; 
		
		/* space of the hashtables */
		/* closed: each entry consists of a reference (4 byte) + a search mark */
		int x = searchTree.numClosedVertices() * (4 + searchMarkSize);

		/* open: each entry consists of 
		 * a reference (4 byte) + a search mark + pi (a transient probability vector).
		 * Since the length of pi is varaible depending on the depth of each state, 
		 * we compute its size for each state separately.  
		 */
		int y = 0;
		Iterator<Vertex> vIter = searchTree.getOpenVertices();
		while (vIter.hasNext()) {
			int m = 4 + searchMarkSize;
			Vertex v = vIter.next();
			SearchMark vMark = searchTree.isOpen(v);
			assert vMark != null;
			PiInterface probs = ((PiEvaluationFunction) evaluationFunction)
					.getParentPi(vMark);
			// assert probs!=null;
			if (probs != null)
				m = m + probs.memory();
			y = y + m;
			// System.out.println("Pi memory = "+probs.memory());
		}

		int m = x + y;
		return m;
	}
	
	protected Iterator<? extends StochasticTransition> processOutgoingEdges(SearchMark uMark, Iterator<? extends DirectedEdge> outgoingEdges) {
		assert graph instanceof UniformCTMC;
		UniformCTMC uCTMC = (UniformCTMC)graph;
		LinkedList<StochasticTransition> trans = new LinkedList<StochasticTransition>();
		while (outgoingEdges.hasNext()) {
			StochasticTransition t = (StochasticTransition) outgoingEdges.next();
			trans.addLast(t);
		}
		assert trans.size() > 0;
		StochasticTransition uniformLoop = trans.removeLast();
		assert uniformLoop.source().equals(uMark.vertex());
		assert uniformLoop.source().equals(uniformLoop.target());
		if(uniformLoop.getProbOrRate() > 0) {
			assert !uMark.has(uCTMC.UNIFORM_LOOP);
			uMark.set(uCTMC.UNIFORM_LOOP, uniformLoop);
		}
		return trans.iterator();
	}
}

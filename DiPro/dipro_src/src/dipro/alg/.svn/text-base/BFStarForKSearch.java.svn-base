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

import dipro.run.Context;
import dipro.util.EvaluationFunction;

public class BFStarForKSearch extends BFStar {

	protected KSPAlgorithm kSearch;

	protected BFStarForKSearch(KSPAlgorithm kStar) {
		super();
		this.kSearch = kStar;
	}

	public void bind(Context context, Comparator<Double> comparator,
			EvaluationFunction evaluationFunction) {
		super.bind(context, comparator, evaluationFunction);
	}

	protected void handleRelaxation(RelaxationInfo relax) throws Exception {
		super.handleRelaxation(relax);
		kSearch.pGraphRecordRelaxation(relax);
	}

	protected void newTargetFound(SearchMark vMark) throws Exception {
		super.newTargetFound(vMark);
		kSearch.pGraphRecordNewTarget(vMark.vertex());
	}
	
//	protected int computeModelMemory() throws Exception {
//		/* space for the vertices */
//		int m = exploredGraph.numVertices() * graph.vertexSize();
//		/* space for the transitions. Notice that we consider all explored 
//		 * transitions since they are needed in KSP search. */
//		m = m + exploredGraph.numEdges() * graph.edgeSize();
//		return m;
//	}
	
	/** This copy of computeModelMemory() returns 0. Just use it 
	 * for the case you have an Offline State Space. In this case 
	 * the size of the entire model should be used instead.
	 */
	protected int computeModelMemory() throws Exception {
		return 0;
	}
	
	public void notifyObservers() {}
	protected void report() {}
	protected void demandForResume(){}
	
	protected void startCountingOffTime()  {
		throw new UnsupportedOperationException();
	}

	protected void stopCountingOffTime() {
		throw new UnsupportedOperationException();
	}
	
}

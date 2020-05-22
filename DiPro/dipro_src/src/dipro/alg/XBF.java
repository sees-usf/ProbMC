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

import dipro.graph.Vertex;
import dipro.util.XSolutionCollector;

public class XBF extends BF {

	protected XBF() {
		super();
	}

	protected void handleRelaxation(RelaxationInfo relax) throws Exception {
		if (relax.flag() == RelaxationInfo.NEW_VERTEX_PRUNED) {
			super.handleRelaxation(relax);
			return;
		}
		Vertex v = relax.getNewMark().vertex();
		assert v != null;
		assert searchTree.isExplored(v) != null;
		switch(relax.flag) {
		case RelaxationInfo.NEW_VERTEX: 
			assert relax.getOldMark()==null;
			assert searchTree.isExplored(v) == relax.getNewMark();
			break;
		case RelaxationInfo.OPEN_VERTEX:
		case RelaxationInfo.CLOSED_VERTEX:
			assert relax.getOldMark()!=null;
			assert relax.getNewMark()!=null;
			assert searchTree.isExplored(v) == relax.getOldMark();
			if (((XSolutionCollector) solutionCollector)
					.doesBelonToSolution(relax.getOldMark())) {
				((XSolutionCollector) solutionCollector)
						.receivePartSolution(relax.getOldMark());
			}
			break;
		case RelaxationInfo.OPEN_VERTEX_RELAXED:
		case RelaxationInfo.CLOSED_VERTEX_RELAXED:
			assert relax.getOldMark()!=null;
			assert relax.getNewMark()!=null;
			assert searchTree.isExplored(v) == relax.getNewMark();
			if (((XSolutionCollector) solutionCollector)
					.doesBelonToSolution(relax.getNewMark())) {
				((XSolutionCollector) solutionCollector)
						.receivePartSolution(relax.getNewMark());
			}
			break;
		case RelaxationInfo.NEW_VERTEX_PRUNED: 
			break;
		default: throw new IllegalArgumentException("Unknown type of vertex relaxation.");
		}
		super.handleRelaxation(relax);
	}

	protected void iterationDone() throws Exception {
		((XSolutionCollector) solutionCollector).signalizeIterationDone();
		super.iterationDone();
	}
	
	protected int computeModelMemory() throws Exception {
		/* space for the vertices */
		int m = exploredGraph.numVertices() * graph.vertexSize();
		/* space for the transitions. In the opposite of BF, we consider here 
		 * all explored transitions, because they are a part of XBF search. */
		m = m + exploredGraph.numEdges() * graph.edgeSize();
		return m;
	}
	
//	/** This copy of computeModelMemory() returns 0. Just use it 
//	 * for the case you have an Offline State Space. In this case 
//	 * the size of the entire model should be used instead.
//	 */
//	protected int computeModelMemory() throws Exception {
//		return 0;
//	}
}

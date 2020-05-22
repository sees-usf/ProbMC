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

import dipro.alg.PathGraph.PNode;

public class Eppstein extends KSPAlgorithm {

	protected Eppstein() {
		super();
	}

	protected PathGraph createPathGraph() {
		return new PathGraph(this);
	}
	private PathGraph pathGraph() {
		return (PathGraph) graph;
	}
	
	@Override
	protected void searchUntil() throws Exception {
		while (!shouldTerminate() && !bf.getSearchTree().isOpenEmpty()) {
			demandForResume();
			bf.doOneIteration();
			iterationDone();
			if (getConfig().isInStepByStepModus) {
				setStatus(PAUSED);
				notifyObservers();
			}
		}
		pathGraph().establish();
		if (targetTreeEdge != null) {
			PNode root = pathGraph().getRoot();
			start = root;
			arrangeGraph();
		}
		while (!shouldTerminate() && !searchTree.isOpenEmpty()) {
			// System.out.println("Dijkstra Iteration:
			// "+explored.getOptimalOpen().f());
			doOneIteration();
			iterationDone();
		}
	}
}

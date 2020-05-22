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

import java.util.Iterator;

import dipro.alg.BF;
import dipro.alg.BF.SearchMark;
import dipro.graph.DirectedEdge;
import dipro.graph.Vertex;

public class XSolutionCollector extends DefaultSolutionCollector {

	public XSolutionCollector(BF alg) throws Exception {
		super(alg);
	}

	public void receivePartSolution(BF.SearchMark solVertexMark)
			throws Exception {
		numTraces++;
		assert solutionGraph.contains(solVertexMark.vertex());
		backtrack(solVertexMark);
	}

//	public double getSolutionValue() {
//		double solutionProb = 0.0d;
//		for (BF.SearchMark targetMark : traces.keySet()) {
//			solutionProb = solutionProb + targetMark.f();
//		}
//		return solutionProb;
//	}

	@Override
	protected void processTrace(SearchMark targetMark) throws Exception {
		backtrack(targetMark);
//		traces.put(targetMark, null);
	}

	protected void backtrack(SearchMark vMark) throws Exception {
		Vertex v = vMark.vertex();
		if (solutionGraph.contains(v)) {
//			Collection<DirectedEdge> incomingEdges = alg.getExploredGraph().getIncomingEdges(v);
			Iterator<? extends DirectedEdge> inIter = alg.getExploredGraph().incomingEdges(v);
//			for (DirectedEdge uv : incomingEdges) {
			while(inIter.hasNext()) {
				DirectedEdge uv = (DirectedEdge)inIter.next();
				SearchMark uMark = alg.getSearchTree().isExplored(uv.source());
				assert uMark != null;
				retrace(uMark);
				solutionGraph.addEdge(uv);
			}
		} else {
			retrace(vMark);
		}
//		checkSolutionGraphValidity();
	}
	//ForDebugging
//	protected void checkSolutionGraphValidity() {
//		Iterator<DirectedEdge> iter = alg.getExploredGraph().edges();
//		while(iter.hasNext()) {
//			DirectedEdge uv = iter.next();
////			if(uv.toString().equals(">>[output_reboot]>6> 0.033333335>o'=2, comp'=true, reqi'=true, reqo'=false>")) {
////				if(uv.source().toString().equals("[11852]")) {
////					if(uv.target().toString().equals("[11867]")) {
////						System.out.println("This one");
////					}
////				}
////			}
////			if(solutionGraph.contains(uv.target())) {
////				if(!solutionGraph.contains(uv)) {
////					System.out.println("!!: "+uv.source());
////					System.out.println("!!: "+uv);
////					System.out.println("!!: "+uv.target());
////				}
////				assert solutionGraph.contains(uv);
////				assert solutionGraph.contains(uv.source());
////			}
//		}
//	}

	protected void retrace(SearchMark vMark) throws Exception {
		assert vMark != null;
//		Vertex v = vMark.vertex();
		if (!solutionGraph.contains(vMark.vertex())) {
			//ForDebugging
//			System.out.println("Retrace at depth "+vMark.depth());
			solutionGraph.addVertex(vMark.vertex());
//			Collection<DirectedEdge> incomingEdges = alg.getExploredGraph()
//					.getIncomingEdges(vMark);
//			for (DirectedEdge uv : incomingEdges) {
			Iterator<? extends DirectedEdge> inIter = alg.getExploredGraph().incomingEdges(vMark.vertex());
//				for (DirectedEdge uv : incomingEdges) {
			while(inIter.hasNext()) {
				DirectedEdge uv = (DirectedEdge)inIter.next();
				SearchMark uMark = alg.getSearchTree().isExplored(
						uv.source());
				assert uMark != null;
				retrace(uMark);
				solutionGraph.addEdge(uv);
			}
		}
	}


	public boolean doesBelonToSolution(SearchMark vMark) {
		return solutionGraph.contains(vMark.vertex());
	}
	
	public void signalizeIterationDone() throws Exception {}

//	public void commit() throws Exception {
//	}

	public boolean isTragetVertex(Vertex vertex) {
		int x = alg.getContext().getProperty().check(vertex);
		if (x == Proposition.TRUE)
			return true;
		return false;
	}
	
	public synchronized int computeUsedMemory() {
		int memory = 0;
		/* Space to store solution vertices; a reference for every vertex.*/
		memory = memory + solutionGraph.numVertices() * 4;
		/* Space to store solution edges; a reference for every edge.*/
		memory = memory + solutionGraph.numEdges() * 4;

		return memory;
	}
	
	public synchronized double getSolCardinality(DirectedEdge edge) {
		if(solutionGraph.contains(edge)) return 1.0d;
		return 0.0d;
	}

	public synchronized double getSolCardinality(Vertex vertex) {
		if(solutionGraph.contains(vertex)) return 1.0d;
		return 0.0;
	}
}

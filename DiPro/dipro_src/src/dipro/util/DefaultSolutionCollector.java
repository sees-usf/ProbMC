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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import dipro.alg.BF;
import dipro.graph.AdjacenceHashGraph;
import dipro.graph.DirectedEdge;
import dipro.graph.GraphItem;
import dipro.graph.Vertex;
import dipro.run.Config;

public class DefaultSolutionCollector extends SolutionCollector {

	protected SolutionTracesRecorder traceRecorder;
	protected AdjacenceHashGraph solutionGraph;
	protected HashMap<GraphItem, Double> solCardinalitiesTable;
	protected double maxSolutionCardinality;
//	protected Vector<SolutionTraceEntry> solutionTraceEntries;

	
	public DefaultSolutionCollector(BF alg) throws Exception {
		super(alg);
//		traceRecorder = new SolutionTracesRecorder(alg.getContext().getSolutionFileName()+".traces.xml",SolutionTracesRecorder.CX_XML_FILE);
		traceRecorder = new SolutionTracesRecorder(alg.getContext().getSolutionFileName(),getConfig().solutionTrace);
		
		solutionGraph = new AdjacenceHashGraph();
		solCardinalitiesTable = new HashMap<GraphItem, Double>();
		diagnosticPaths = new ArrayList<DiagnosticPath>();
	}

	public synchronized void clear() throws Exception {
//		traces.clear();
		solutionGraph.clear();
		solCardinalitiesTable.clear();
		super.clear();
	}

	protected void incrementSolCardinality(GraphItem item, double f) {
		Double c = solCardinalitiesTable.get(item);
		if (c == null) {
			c = new Double(f);
		} else {
			c = new Double(c.doubleValue() + f);
		}
		solCardinalitiesTable.put(item, c);
	}

	protected synchronized void processTrace(BF.SearchMark targetMark) throws Exception {
		Trace trace = constructSolTrace(targetMark);
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
			solutionGraph.addEdge(e);
		}
		recordTrace(trace);
	}

	public synchronized int computeUsedMemory() {
		int memory = super.computeUsedMemory();
		/* solutionGraph and solCardinalitiesTable are ignored because
		 * they are not necessary for the implementation.
		 * They are just used for the purpose of visualization.
		 */
		memory = memory + traceRecorder.getUsedMemory();
		return memory;
	}

	protected Trace constructSolTrace(BF.SearchMark mark) {
		return getSearchTree().backtrack(mark);
	}

	@Override
	public synchronized void commit() throws Exception {
		traceRecorder.close();
	}

	public synchronized boolean belongsToSolution(Vertex vertex) {
		return solutionGraph.contains(vertex);
	}
	
	public synchronized boolean belongsToSolution(DirectedEdge edge) {
		return solutionGraph.contains(edge);
	}
	
	public synchronized double getSolutionCardinality(DirectedEdge edge) {
		return getSolCard(edge);
	}
	
	public synchronized double getSolutionCardinality(Vertex vertex) {
		return getSolCard(vertex);
	}
	
	public synchronized double getMaxSolutionCardinality() {
		return maxSolutionCardinality;
	}
	
	protected double getSolCard(GraphItem item) {
		Double c = solCardinalitiesTable.get(item);
		if (c == null) {
			assert !solutionGraph.contains(item);
			return 0.0d;
		}
		assert solutionGraph.contains(item);
		assert c.doubleValue() > 0.0d;
		return c.doubleValue();
	}
	
	public boolean isTragetVertex(Vertex vertex) {
		return alg.getContext().getProperty().check(vertex) == Proposition.TRUE;
	}
	
	public int getSolutionSize() {
		return getNumSolutionVertices() + getNumSolutionEdges();
	}

	public int getNumSolutionVertices() {
		return solutionGraph.numVertices();
	}

	public int getNumSolutionEdges() {
		return solutionGraph.numEdges();
	}
	
	
	protected Config getConfig() {
		return alg.getContext().getConfig();
	}
	
	public void recordTrace(Trace trace) throws FileNotFoundException {
		switch (traceRecorder.getType()) {
		case SolutionTracesRecorder.CX_FILE:
		case SolutionTracesRecorder.CX_XML_FILE:
		case SolutionTracesRecorder.DIAG_PATH:
			traceRecorder.record(trace);
			break;
//		case SolutionTracesRecorder.DIAG_PATH:
//			DiagnosticPath dp = traceRecorder.record(trace,	new ArrayList<String>());
////			if (dp.isMinimalPath(diagnosticPaths, false))
//			diagnosticPaths.add(dp);
//			break;
		default:
			break;

		}
	}
	
	public int getSolutionTraceRecorderType()
	{
		return this.traceRecorder.getType();
	}
	
//	public synchronized Iterator<Trace> getSolutionTraces() {
//	if(solutionTraceEntries == null) {
//		Iterator<SolutionTraceEntry> iter = new Iterator<SolutionTraceEntry>() {
//			Iterator<BF.SearchMark> mIter = traces.keySet().iterator();
//			Iterator<SolutionTraceEntry> iter = null;
//			public boolean hasNext() {
//				if (iter == null || !iter.hasNext()) {
//					while (mIter.hasNext()) {
//						SearchMark mark = mIter.next();
//						iter = traces.get(mark).iterator();
//						if (iter.hasNext())
//							return true;
//					}
//				}
//				return iter != null && iter.hasNext();
//			}
//			public SolutionTraceEntry next() {
//				if (hasNext())
//					return iter.next();
//				return null;
//			}
//			public void remove() {
//				throw new UnsupportedOperationException();
//			}
//		};
//		solutionTraceEntries = new Vector<SolutionTraceEntry>(numTraces);
//		while(iter.hasNext()) {
//			solutionTraceEntries.add(iter.next());
//		}
//		Collections.sort(solutionTraceEntries, new Comparator<SolutionTraceEntry>() {
//			@Override
//			public int compare(SolutionTraceEntry o1, SolutionTraceEntry o2) {
//				return (o1.order < o2.order)? -1 : (o1.order==o2.order)? 0: 1;
//			}
//		});
//	}
//	return new Iterator<Trace>() {
//		Iterator<SolutionTraceEntry> iter = solutionTraceEntries.iterator(); 
//		@Override
//		public boolean hasNext() {
//			return iter.hasNext();
//		}
//		@Override
//		public Trace next() {
//			SolutionTraceEntry entry = iter.next();
//			return entry.trace;
//		}
//		@Override
//		public void remove() {
//			throw new UnsupportedOperationException();
//		}
//	};
//	return null;
//}
	
//	protected class SolutionTraceEntry {
//		public int order;
//		public Trace trace;
//		public SolutionTraceEntry() {}
//	}
}

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dipro.alg.PathGraph.PNode;
import dipro.graph.DefaultDirectedEdge;
import dipro.graph.DirectedEdge;
import dipro.graph.DirectedGraph;
import dipro.graph.Vertex;
import dipro.h.Heuristic;
import dipro.run.Config;
import dipro.run.Context;
import dipro.stoch.DTMC;
import dipro.stoch.PoissonProcess;
import dipro.stoch.StochTBoundedUntil;
import dipro.stoch.UniformCTMC;
import dipro.util.DiProException;
import dipro.util.DummySolutionCollector;
import dipro.util.EvaluationFunction;
import dipro.util.ExploredGraph;
import dipro.util.KEvaluationFunction;
import dipro.util.MultiplicativeEvaluationFunction;
import dipro.util.MultiplicativeKEvaluationFunction;
import dipro.util.Proposition;
import dipro.util.SolutionCollector;
import dipro.util.Trace;

public abstract class KSPAlgorithm extends Dijkstra {

//	public final Attribute SELF_LOOP = new Attribute("SELF_LOOP");
	
	final Vertex TARGET = new Vertex() {
		@Override
		public Object getLabelValue(String labelName) throws Exception {
			return null;
		}
		public String toString() {
			return "TARGET";
		}
	};
	
	protected BFStarForKSearch bf;
	protected DirectedEdge targetTreeEdge;
	protected int maxDepthBound;

	protected KSPAlgorithm() {
		super();
		targetTreeEdge = null;
		bf = new BFStarForKSearch(this);
	}

	public void bind(Context context, Comparator<Double> comparator,
			EvaluationFunction evaluationFunction) {
		assert evaluationFunction instanceof KEvaluationFunction;
		EvaluationFunction bfEvaluationFunction;
		if (evaluationFunction instanceof MultiplicativeKEvaluationFunction) {
			bfEvaluationFunction = new MultiplicativeEvaluationFunction(bf);
		} else {
			bfEvaluationFunction = new EvaluationFunction(bf);
		}
		bf.bind(context, comparator, bfEvaluationFunction);
		super.bind(context, comparator, evaluationFunction);
	}

	public void init(DirectedGraph graph, Vertex start, Proposition property,
			Heuristic heuristic, SolutionCollector solutionCollector)
			throws Exception {
		reset();
		bf.init(graph, start, property, heuristic, new DummySolutionCollector(
				bf));
		this.graph = createPathGraph();
		this.start = null;
		this.property = null;
		this.heuristic = null;
		searchTree = createExploredSpace();
		exploredGraph = new ExploredGraph();
		this.solutionCollector = solutionCollector;
		pGraphSetStartVertex(bf.getStartVertex());
		maxDepthBound = computeMaxDepth();
		setStatus(READY);
		notifyObservers();
	}

	protected int computeMaxDepth() throws Exception {
		int maxDepth = Integer.MAX_VALUE - 1;
		if (context.getProperty() instanceof StochTBoundedUntil) {
			double t = ((StochTBoundedUntil) context.getProperty()).timeBound();
			if (context.getGraph() instanceof UniformCTMC) {
				maxDepth = PoissonProcess.approximateDiscreteTBound(context);
			} else {
				if(context.getGraph().getClass() == DTMC.class) {
					maxDepth = (int) t;
				}
			}
		} 
		return maxDepth;
	}


	protected void reset() throws Exception {
		targetTreeEdge = null;
		super.reset();
	}

	protected void arrangeGraph() throws Exception {
		if (start == null)
			return;
		SearchMark sMark = createSearchMark(start);
		evaluationFunction.evaluate(null, null, sMark);
		evaluationFunction.setG(sMark, bf.getBestTraceValue());
		searchTree.open(sMark);
	}
	
	
	abstract protected void searchUntil() throws Exception;

	abstract protected PathGraph createPathGraph();

	public void pGraphRecordNewTarget(Vertex v) throws Exception {
		DirectedEdge vt = new DefaultDirectedEdge(v, TARGET);
		int relaxationFlag = BF.RelaxationInfo.CLOSED_VERTEX;
		if (targetTreeEdge == null) {
			targetTreeEdge = vt;
			relaxationFlag = BF.RelaxationInfo.NEW_VERTEX;
		} else {
			Vertex u = targetTreeEdge.source();
			if(bf.comparator.compare(bf.f(v), bf.f(u))<0) {
				// A shorter solution path is found
				targetTreeEdge = vt;
				relaxationFlag = BF.RelaxationInfo.CLOSED_VERTEX_RELAXED;
			}
		}
		pGraphRecordEdge(v, vt, TARGET, relaxationFlag);
	}

	public void pGraphRecordRelaxation(RelaxationInfo relaxInfo)
			throws Exception {
		if (relaxInfo.flag() == BF.RelaxationInfo.NEW_VERTEX_PRUNED)
			return;
		DirectedEdge uv = relaxInfo.getEdge();
//		System.out.println("recordRelaxation:"+relaxInfo);
		pGraphRecordEdge(uv.source(), uv, uv.target(), relaxInfo.flag());
	}

	public void pGraphSetStartVertex(Vertex startVertex) throws Exception {
		pathGraph().recordStartVertex(startVertex);
	}

	protected void pGraphRecordEdge(Vertex u, DirectedEdge uv, Vertex v,
			int relaxationFlag) throws Exception {
		pathGraph().recordEdge(u, uv, v, relaxationFlag);
	}

	protected boolean isTreeEdge(DirectedEdge uv) {
		boolean isTreeEdge;
		Vertex v = uv.target();
		if (v == TARGET) {
			isTreeEdge = uv.equals(targetTreeEdge);
		} else {
			isTreeEdge = uv.equals(bf.getSearchTree().getTreeEdge(v));
		}
		return isTreeEdge;
	}

	protected boolean isSearchCompleted() {
		return searchTree.isOpenEmpty() && bf.getSearchTree().isOpenEmpty();
	}

	protected void vertexToExpand(SearchMark uMark) throws Exception {
		double g = uMark.g();
		if(comparator.compare(g, getConfig().pruneBound)>=0) {
//			System.out.println("Next optimal path: "+g+", request termination!");
			if (getConfig().logLevel >= Config.ALG_LOG_NORMAL) {
				log("Next optimal path: "+g+", prune bound = "+getConfig().pruneBound+"\n"+
					 "Thus, request termination.");
			}
			requestTermination();
			return;
		}
		Vertex u = uMark.vertex();
		searchTree.close(uMark);
		double uDist = uMark.f();
		if (getConfig().logLevel >= Config.ALG_LOG_VERBOSE)
			log(this + ": To expand: " + u + ", distance=" + uDist);
		newTargetFound(uMark);
	}

	protected Iterator<? extends DirectedEdge> getOutgoingEdgesOfVertex(Vertex v) {
		if(v==TARGET) return Collections.EMPTY_LIST.iterator();
		if(bf.getProperty().check(v)==Proposition.TRUE) {
			ArrayList<DirectedEdge> l = new ArrayList<DirectedEdge>(1);
			l.add(new DefaultDirectedEdge(v, TARGET));
			return l.iterator();
		}
		return bf.getOutgoingEdges(v);
	}
	
	protected boolean shouldExpand(SearchMark vMark) throws Exception {
		// The depth here is equal to the number of side edges
		int d = vMark.depth();
		boolean b = d<=maxDepthBound;
		return b;
	}
	
//	protected Iterator<DirectedEdge> processOutgoingEdges(SearchMark uMark,
//			Iterator<DirectedEdge> outgoingEdges) {
//		LinkedList<DirectedEdge> edges = new LinkedList<DirectedEdge>();
//		while (outgoingEdges.hasNext()) {
//			DirectedEdge e = outgoingEdges.next();
//			if (uMark.vertex().equals(e.target())) {
//				assert uMark.vertex().equals(e.source());
//				assert !uMark.has(SELF_LOOP);
//				uMark.set(SELF_LOOP, e);
//			} else
//				edges.add(e);
//		}
//		return edges.iterator();
//	}

	public int getMaxDepthBound() {
		return maxDepthBound;
	}
	
	public double computeTraceValue(Trace trace) throws Exception {
		return bf.getEvaluationFunction().computeTraceValue(trace);
	}

	public BFStarForKSearch getBasicSearchAlgorithm() {
		return bf;
	}

	public int getNumIterations() {
		return iterations + bf.getNumIterations();
	}

	public int maxOpenSize() {
		return bf.maxOpenSize();
	}

	public int numClosedVertices() {
		return bf.numClosedVertices();
	}

	public int numEdges() {
//		assert bf.numEdges() == bf.numVertices() -1;
//		return bf.numEdges();
		int m = pathGraph().getNumSidetrackEdges();
		m = m + bf.numEdges();
		return m;
	}

	public int numOpenVertices() {
		return bf.numOpenVertices();
	}

	public int numVertices() {
		return bf.numVertices();
	}

	public int computeSearchMemory() throws DiProException {
		int bfMemory = bf.computeSearchMemory();
		int kopGraphSize = ((PathGraph) graph).getMemory();
		int kopSize = super.computeSearchMemory();
		int memory = bfMemory + kopGraphSize + kopSize;
		return memory;
	}

	protected int computeModelMemory() throws Exception {
		return bf.computeModelMemory();
	}

	public Trace constructTrace(SearchMark nMark) {
		LinkedList<DirectedEdge> seq = extractEdgeSequence(nMark);
		Vertex endVertex = null;
		if (!seq.isEmpty() && seq.getFirst().target() == TARGET) {
			endVertex = seq.getFirst().source();
			seq.removeFirst();
		} else {
			endVertex = targetTreeEdge.source();
		}
		Trace trace = new Trace(endVertex);
		fillTrace(seq, trace);
		return trace;
	}
	
	protected LinkedList<DirectedEdge> extractEdgeSequence(SearchMark nMark) {
		LinkedList<DirectedEdge> seq = new LinkedList<DirectedEdge>();
		SearchMark xMark = nMark;
		DirectedEdge xy = null;
		SearchMark yMark = null;
		// System.out.print("P(G) path = ["+xMark.vertex());
		while (xMark != null) {
			PNode x = (PNode) xMark.vertex();
			// if( (xy==null || pGraph().isCrossEdge(xy)) &&
			// (x.isSideEdge() || x.edge().target()==JOINING_TARGET)) {
			if (xy == null || pathGraph().isCrossEdge(xy)) {
				if(x != start) seq.addFirst(x.edge());
			}
			yMark = xMark;
			xy = searchTree.getTreeEdge(yMark);
			xMark = xy != null ? searchTree.isExplored(xy.source()) : null;
			// System.out.print(xy+", ");
		}
		// System.out.println("]");
		// System.out.println(seq);
		return seq;
	}

	protected void fillTrace(List<DirectedEdge> seq, Trace trace) {
		// System.out.println(seq);
		for (DirectedEdge edge : seq) {
			while (!trace.getFirstVertex().equals(edge.target())) {
				Vertex v = trace.getFirstVertex();
				DirectedEdge e = bf.getSearchTree().getTreeEdge(v);
				// if(e==null) {
				// System.out.println("Achtung: "+v+", "+edge);
				// System.out.println("Trace: "+trace);
				// }
				assert e != null;
				trace.preAppend(e);
			}
			assert trace.getFirstVertex().equals(edge.target());
			trace.preAppend(edge);
		}
		while (!trace.getFirstVertex().equals(bf.getStartVertex())) {
			Vertex v = trace.getFirstVertex();
			DirectedEdge e = bf.getSearchTree().getTreeEdge(v);
			// if(e==null) {
			// System.out.println("Achtung: "+v+", incomming edge: "+e);
			// System.out.println("Trace: "+trace);
			// }
			assert e != null;
			trace.preAppend(e);
		}
		assert trace.getFirstVertex().equals(bf.getStartVertex());
	}

	public double delta(DirectedEdge e) throws Exception {
		assert e!=null;
		Vertex v1 = e.source();
		Vertex v2 = e.target();
		double g1, g2;
		g1 = bf.g(v1);
		if (v2 == TARGET) {
			g2 = bf.getBestTraceValue();
		} else
			g2 = bf.g(v2);
//		w = weight(e);
		return delta(e, g1, g2);
	}

	protected double delta(DirectedEdge uv, double uG, double vG) throws Exception {
		double w = weight(uv);
		double d;
		if (isMultiplicative()) {
			d = (uG * w) / vG;
			// Correct computation inaccuracy
			d = Math.min(d, 1.0d);
			d = Math.max(d, 0.0d);
			assert d >= 0.0d && d <= 1.0d;
		} else {
			d = uG + w - vG;
		}
		return d;
	}
	
	protected boolean isMultiplicative() {
		return evaluationFunction instanceof MultiplicativeKEvaluationFunction;
	}

	private float weight(DirectedEdge uv) throws Exception {
		float w;
		if (uv.target() == TARGET) {
			w = isMultiplicative() ? 1.0f : 0.0f;
		} else
			w = bf.getGraph().weight(uv);
		return w;
	}

	protected void handleRelaxation(RelaxationInfo relax) throws Exception {
		super.handleRelaxation(relax);
		if (relax.flag() == RelaxationInfo.NEW_VERTEX_PRUNED)
			return;
		// Because that P(G) is unfolded, every node is 
		// considered as a new one.
		assert relax.flag() == RelaxationInfo.NEW_VERTEX;
	}

	private PathGraph pathGraph() {
		return (PathGraph)graph;
	}
}

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

package dipro.graph;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;

import dipro.alg.Dijkstra;
import dipro.h.Heuristic;
import dipro.util.EvaluationFunction;
import dipro.util.Proposition;
import dipro.util.SolutionCollector;

public class HeuristicSimulation extends Dijkstra {

	private HashMap<Integer, Double> hMap;

	public HeuristicSimulation() throws Exception {
		super();
		hMap = new HashMap<Integer, Double>();
	}

	public void init() throws Exception {
		Heuristic h = null;
		SolutionCollector sc = null;
		Proposition prop = Proposition.FALSE_PROP;
		Vertex start = null;
		BackwardsDirectedGraph graph = new BackwardsDirectedGraph(
				(ExternDirectedGraph) context.getGraph());
		this.init(graph, start, prop, h, sc);
	}

	protected void arrangeGraph() throws Exception {
		SetPredicate p = (SetPredicate) context.getProperty();
		Set<Integer> targets = p.getVertexIds();
		for (Integer id : targets) {
			SearchMark mark = createSearchMark(new DefaultVertex(id));
			evaluationFunction.evaluate(null, null, mark);
			searchTree.open(mark);
		}
	}

	protected boolean shouldTerminate() throws Exception {
		return false;
	}

	protected void terminate() throws Exception {
		int n = graph.numVertices();
		PrintStream out = null;
		for (int id = 0; id < n; id++) {
			if (id % 1000 == 0) {
				if (out != null)
					out.close();
				String dir = ((ExternDirectedGraph) context.getGraph())
						.getDirName();
				out = new PrintStream(new FileOutputStream(dir + "/heuristic_"
						+ id + ".txt"));
			}
			// System.out.println(id+" from "+hMap);
			Double h = hMap.get(id);
			if (h == null) {
				h = Double.POSITIVE_INFINITY;
			}
			out.println(id + "\t" + h);
		}
		if (out != null)
			out.close();
		System.gc();
	}

	protected void vertexToExpand(SearchMark vMark) throws Exception {
		super.vertexToExpand(vMark);
		int id = ((DefaultVertex) vMark.vertex()).getId();
		WeightedDirectedEdge uv = (WeightedDirectedEdge) searchTree.getTreeEdge(vMark);
		double h = 0;
		if (uv != null) {
			DefaultVertex u = (DefaultVertex) uv.source();
			double uH = hMap.get(u.getId());
			double max = uv.getWeight() + uH;
			double factor = Math.random() / 2 + 0.5d;
			h = factor * max;
		}
		hMap.put(id, h);
	}

	public void summerize(PrintStream out) {
	}

	protected EvaluationFunction createEvaluationFunction() throws Exception {
		EvaluationFunction f = new EvaluationFunction(this);
		return f;
	}
}

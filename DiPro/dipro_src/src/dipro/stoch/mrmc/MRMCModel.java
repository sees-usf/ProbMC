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

package dipro.stoch.mrmc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dipro.graph.DirectedEdge;
import dipro.graph.Edge;
import dipro.graph.State;
import dipro.graph.StateSpace;
import dipro.graph.Transition;
import dipro.graph.Vertex;
import dipro.run.MRMCContext;
import dipro.run.Registry;
import dipro.stoch.CTMC;
import dipro.stoch.DTMC;
import dipro.stoch.MarkovModel;

public class MRMCModel implements StateSpace {

	private MRMCContext settings;

	protected MRMCState initState; 
	
	protected Hashtable<MRMCState, StateInfo> base;

	protected LinkedList<String> allLabels;

	protected int numEdges;

	protected double maxOutRate;

	public MRMCModel(MRMCContext settings) throws IOException {
		this.settings = settings;
		initState = null;
		base = new Hashtable<MRMCState, StateInfo>();
		allLabels = new LinkedList<String>();
		numEdges = 0;
		maxOutRate = 0.0d;
		init();
	}

	private void init() throws IOException {
		// read transitions
		BufferedReader in = new BufferedReader(new FileReader(settings.getTraFileName()));

		String s = null;
		s = in.readLine();
		s = in.readLine();
		s = in.readLine();

		while (s != null) {
			String[] tokens = s.split("\\s");
			int id = Integer.parseInt(tokens[0]);
			MRMCState s1 = new MRMCState(this, id);
			MRMCState s2 = new MRMCState(this, Integer.parseInt(tokens[1]));
			float w = Float.parseFloat(tokens[2]);
			StateInfo info = base.get(s1);
			if (info == null) {
				info = new StateInfo(s1);
				base.put(s1, info);
			}
			MRMCTransition t = new MRMCTransition(s1, s2, w);
			assert !info.out.contains(t);
			info.out.add(t);
			double r = info.sumOutRate();
			if (maxOutRate < r)
				maxOutRate = r;
			s = in.readLine();
		}

		// read labels
		BufferedReader labFile = new BufferedReader(new FileReader(settings.getLabFileName()));

		s = labFile.readLine();
		s = labFile.readLine();
		String[] labels = s.split("\\s");
		for (int i = 1; i < labels.length; i++) {
			allLabels.add(labels[i]);
		}
		s = labFile.readLine();
		s = labFile.readLine();
		while (s != null) {
			String[] tokens = s.split("\\s");
			int id = Integer.parseInt(tokens[0]);
			MRMCState state = new MRMCState(this, id);
			StateInfo info = base.get(state);
			assert info.labels != null;

			for (int i = 1; i < tokens.length; i++) {
				if(tokens[i].equals("init")) {
					if(initState!=null) {
						Registry.getMain().handleWarning("Multiple initial states!! The one selected as a start point of the search is: "+initState);
					}
					else {
						initState = info.state;
					}
				}
				info.labels.add(tokens[i]);
			}
			s = labFile.readLine();
		}
		labFile.close();
		in.close();
	}

	public MarkovModel createMarkovModel() {
		String logic = settings.getLogic();
		if (logic.equals(MRMCContext.CSL)) {
			return new CTMC(this);
		}
		if (logic.equals(MRMCContext.PCTL)) {
			return new DTMC(this);
		}
		throw new IllegalStateException("Unsupported logic type: " + logic);
	}

	public double getMaxOutRate() {
		return maxOutRate;
	}

	public MRMCTransition createTransition(MRMCState s1, MRMCState s2, float w) {
		return new MRMCTransition(s1, s2, w);
	}

	@Override
	public Transition createTransition(State s1, State s2) {
		return new MRMCTransition((MRMCState) s1, (MRMCState) s2);
	}

	@Override
	public State getInitialState() {
//		return new MRMCState(this, 1);
		return initState;
	}

	@Override
	public Class getVertexLabelType(String label) throws Exception {
		return Boolean.class;
	}

	public boolean checkLabel(MRMCState state, String label) {
		if (label.equals("tt"))
			return true;
		if (label.equals("ff"))
			return false;
		return base.get(state).labels.contains(label);
	}

	@Override
	public List<String> getVertexLabels() {
		return allLabels;
	}

	@Override
	public Iterator<? extends Transition> incomingEdges(Vertex v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isInitialState(State s) {
		assert s instanceof MRMCState;
		return initState.equals(s);
	}

	@Override
	public Iterator<? extends Transition> outgoingEdges(Vertex v) {
		assert v instanceof MRMCState;

		StateInfo info = base.get((MRMCState) v);
		assert info != null;
		return info.out.iterator();
	}

	public Iterator<? extends Transition> adjacentEdges(Vertex v) {
		throw new UnsupportedOperationException();
	}

	// the number of edges which start at the Vertex v
	public int degree(Vertex v) {
		assert v instanceof MRMCState;
		StateInfo info = base.get((MRMCState) v);
		assert info != null;
		return info.out.size();
		// throw new UnsupportedOperationException();
	}

	public int edgeSize() {
		return 2 + 2 + 4;
	}

	public Iterator<MRMCTransition> edges() {
		return new Iterator<MRMCTransition>() {
			Iterator<? extends Vertex> iter = vertices();

			HashSet<MRMCTransition> out;

			Iterator<MRMCTransition> outIterator = out.iterator();

			public boolean hasNext() {

				while ((out == null || !outIterator.hasNext())
						&& iter.hasNext()) {
					Vertex v = iter.next();
					out = base.get(v).out;

				}
				return (out != null && outIterator.hasNext());
			}

			public MRMCTransition next() {
				if (hasNext()) {
					return outIterator.next();
				} else
					return null;
			}

			public void remove() {
				throw new UnsupportedOperationException("remove");
			}
		};
	}

	public int numEdges() {
		return numEdges;
	}

	public int numVertices() {
		return base.size();
	}

	public int vertexSize() {
		return 2;
	}

	public Iterator<MRMCState> vertices() {
		return base.keySet().iterator();
	}

	public float weight(Edge e) {
		assert e instanceof MRMCTransition;
		return ((MRMCTransition) e).getWeight();

	}

	public void addVertex(Vertex v) {
		assert base.get(v) == null;
		assert v instanceof MRMCState;
		MRMCState s = (MRMCState) v;
		base.put(s, new StateInfo(s));
	}

	public boolean contains(Vertex v) {
		boolean b = base.containsKey((MRMCState) v);
		return b;
	}

	public Iterator<? extends DirectedEdge> connectingEdges(Vertex v1, Vertex v2) {
		LinkedList<DirectedEdge> list = new LinkedList<DirectedEdge>();
		HashSet<MRMCTransition> l = base.get(v1).out;
		Iterator<MRMCTransition> it = l.iterator();

		while (it.hasNext()) {
			MRMCTransition e = it.next();
			if (((MRMCTransition) (e.target())).equals(v1))
				list.add(e);
		}
		return list.iterator();
	}

	@Override
	public void clear() {
		base.clear();
		allLabels.clear();
	}

	public String toString() {
		return settings.getTraFileName() + " and " + settings.getLabFileName();
	}

	public class StateInfo {

		MRMCState state;
		HashSet<MRMCTransition> out = new HashSet<MRMCTransition>();
		// Set of labels which are valid in the corresponding state
		HashSet<String> labels = new HashSet<String>();

		StateInfo(MRMCState state) {
			this.state = state;
			// the outgoing edge
			out = new HashSet<MRMCTransition>();
			// labels for the current state
			labels = new HashSet<String>();
		}

		public double sumOutRate() {
			double r = 0.0d;
			for (MRMCTransition t : out) {
				r = r + t.getWeight();
			}
			return r;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Out: ");
			sb.append(out);
			sb.append(", Labels: ");
			sb.append(labels);
			return sb.toString();
		}
	}
}

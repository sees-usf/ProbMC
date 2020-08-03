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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dipro.util.DiProException;

public class ExternDirectedGraph implements DirectedGraph {

	protected String dirName;
	protected File dir;
	protected int n;
	protected int m;
	protected int t;

	public ExternDirectedGraph(String dirName) throws DiProException,
			IOException {
		dir = new File(dirName);
		if (!dir.exists()) {
			throw new DiProException("Folder can't be found: " + dirName);
		}
		this.dirName = dir.getCanonicalPath();
		String mainFileName = this.dirName + "/main.txt";
		BufferedReader in = new BufferedReader(new FileReader(mainFileName));
		String line = in.readLine();
		String[] tokens = line.split(" ");
		n = Integer.parseInt(tokens[1]);
		line = in.readLine();
		tokens = line.split(" ");
		m = Integer.parseInt(tokens[1]);
		line = in.readLine();
		tokens = line.split(" ");
		t = Integer.parseInt(tokens[1]);
		in.close();
	}

	@Override
	public Iterator<WeightedDirectedEdge> incomingEdges(Vertex v) {
		assert v instanceof DefaultVertex;
		DefaultVertex t = (DefaultVertex) v;
		int id = t.getId();
		LinkedList<WeightedDirectedEdge> edges = new LinkedList<WeightedDirectedEdge>();
		String fn = dirName + "/in_" + id + ".txt";
		try {
			BufferedReader in = new BufferedReader(new FileReader(fn));
			String line = in.readLine();
			while (line != null) {
				String[] tokens = line.split(" ");
				int i = Integer.parseInt(tokens[0]);
				int j = Integer.parseInt(tokens[1]);
				int w = Integer.parseInt(tokens[2]);
				assert j == id;
				DefaultVertex s = new DefaultVertex(i);
				WeightedDirectedEdge st = new WeightedDirectedEdge(s, t, w);
				edges.add(st);
				line = in.readLine();
			}
			in.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return edges.iterator();
	}

	@Override
	public Iterator<WeightedDirectedEdge> outgoingEdges(Vertex v) {
		System.out.println("outgoingEdges - ExternDirectedGraph");
		assert v instanceof DefaultVertex;
		DefaultVertex s = ((DefaultVertex) v);
		int id = s.getId();
		LinkedList<WeightedDirectedEdge> edges = new LinkedList<WeightedDirectedEdge>();
		String fn = dirName + "/out_" + id + ".txt";
		try {
			BufferedReader in = new BufferedReader(new FileReader(fn));
			String line = in.readLine();
			while (line != null) {
				String[] tokens = line.split(" ");
				int i = Integer.parseInt(tokens[0]);
				int j = Integer.parseInt(tokens[1]);
				int w = Integer.parseInt(tokens[2]);
				assert i == id;
				DefaultVertex t = new DefaultVertex(j);
				WeightedDirectedEdge st = new WeightedDirectedEdge(s, t, w);
				edges.add(st);
				line = in.readLine();
			}
			in.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return edges.iterator();
	}

	@Override
	public int numEdges() {
		return m;
	}

	@Override
	public void clear() {
		dir = null;
		dirName = null;
		n = 0;
		m = 0;
		t = 0;
	}

	@Override
	public int edgeSize() {
		return 2;
	}

	@Override
	public int numVertices() {
		return n;
	}

	@Override
	public int vertexSize() {
		return 2;
	}

	public String getDirName() {
		return dirName;
	}

	public List<String> getVertexLabels() {
		ArrayList<String> l = new ArrayList<String>(1);
		l.add(DefaultVertex.VERTEX_ID_LABEL_NAME);
		return l;
	}

	public Class getVertexLabelType(String label) {
		if (label.equals(DefaultVertex.VERTEX_ID_LABEL_NAME))
			return Integer.class;
		throw new IllegalArgumentException("Invalid vertex label: " + label);
	}

	@Override
	public int degree(Vertex v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<WeightedDirectedEdge> edges() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<WeightedDirectedEdge> adjacentEdges(Vertex v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<? extends Vertex> vertices() {
		throw new UnsupportedOperationException();
	}

	@Override
	public float weight(Edge e) {
		assert e instanceof WeightedDirectedEdge;
		return ((WeightedDirectedEdge) e).getWeight();
	}
}

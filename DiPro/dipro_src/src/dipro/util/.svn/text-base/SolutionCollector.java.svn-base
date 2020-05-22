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

import java.util.ArrayList;
import java.util.Comparator;

import dipro.alg.BF;
import dipro.graph.DirectedEdge;
import dipro.graph.Vertex;

public abstract class SolutionCollector {

	protected BF alg;
	protected int numTraces;
	protected double bestTraceValue;
	protected ArrayList<DiagnosticPath> diagnosticPaths;

	public SolutionCollector(BF alg) {
		this.alg = alg;
		numTraces = 0;
		bestTraceValue = -1.0d;
	}

	public void clear() throws Exception {
		numTraces = 0;
		bestTraceValue = -1.0d;
	}

	public void receiveSolution(BF.SearchMark targetMark) throws Exception {
		numTraces++;
		if (bestTraceValue == -1
				|| getComparator().compare(targetMark.f(), bestTraceValue) < 0) {
			bestTraceValue = targetMark.f();
		}
		processTrace(targetMark);
	}

	public int computeUsedMemory() {
		/* int + double (numTraces and bestTraceValue) */
		return 4 + 8;
	}

	public boolean belongsToSolution(Vertex vertex) {
		return false;
	}
	
	public boolean belongsToSolution(DirectedEdge edge) {
		return false;
	}
	
	public double getSolutionCardinality(Vertex vertex) {
		return 0.0d;
	}

	public double getSolutionCardinality(DirectedEdge edge) {
		return 0.0d;
	}
	
	public double getMaxSolutionCardinality() {
		return 0.0d;
	}

	public boolean isTragetVertex(Vertex vertex) {
		return false;
	}

	public double getBestTraceValue() {
		return bestTraceValue;
	}

	public double getSolutionValue() {
		return getBestTraceValue();
	}

	public int getNumTraces() {
		return numTraces;
	}
	
	
	public boolean isSolutionEmpty() {
		return getNumTraces() == 0;
	}

	protected SearchTree getSearchTree() {
		return alg.getSearchTree();
	}

	protected Comparator<Double> getComparator() {
		return alg.getComparator();
	}

	protected abstract void processTrace(BF.SearchMark targetMark)
			throws Exception;

	public abstract void commit() throws Exception;

	public abstract int getSolutionSize();

	public abstract int getNumSolutionVertices();

	public abstract int getNumSolutionEdges();
	
	public ArrayList<DiagnosticPath> getDiagnosticPath(){
		return diagnosticPaths;
	}

}

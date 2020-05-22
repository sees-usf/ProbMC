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

package dipro.run;

import java.io.PrintStream;

import dipro.alg.BF;
import dipro.graph.DirectedGraph;
import dipro.graph.Vertex;
import dipro.h.Heuristic;
import dipro.util.AlgReporter;
import dipro.util.Proposition;
import dipro.util.SearchTree;
import dipro.util.SolutionCollector;

public interface Context {
	
	public void init() throws Exception;

	public Heuristic loadHeuristic(BF alg) throws Exception;

	public DirectedGraph getGraph();

	public Vertex getStart();

	public Proposition getProperty();

	public Config getConfig();

	public void setGraph(DirectedGraph graph);

	public void setProperty(Proposition property);

	public void setStart(Vertex start);

	public DiPro getDiPro();

	public PrintStream getAlgLog();
	
	public AlgReporter getAlgReporter();
	
	public boolean isReporterEnabled();

	public void cleanup() throws Exception;

	public String toString();

	public SearchTree createExploredGraph(BF alg);

	public BF loadAlgorithm() throws Exception;

	public SolutionCollector createSolutionCollector(BF alg) throws Exception;

	public void performModelChecking() throws Exception;

	public Object getModelCheckingResult() throws Exception;
	
	public String getSolutionFileName() throws Exception; 
}

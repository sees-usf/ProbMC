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

package dipro.vis;

import java.awt.Color;
import java.awt.Component;
import java.util.Observer;

import dipro.alg.BF;
import dipro.graph.GraphItem;
import dipro.graph.Vertex;
import dipro.run.VisContext;
import dipro.util.ExploredGraph;
import dipro.util.SearchTree;

public interface Visualizer extends Observer {

	public void init() throws Exception;

	public SearchTree getSearchTree();
	
	public ExploredGraph getExploredGraph();

	public int getPositionInQueue(Vertex v);

	public VisContext getContext();

//	public SolutionCollector getSolutionCollector();

	public BF getAlgorithm();

	public int getAlgStatus();

	public void close() throws Exception;

	public Component getVisualizationComponent();

	public void start() throws Exception;

	public void update();

	public void requestPause();

	public void requestResume();

	public void requestTermination();

	public void performModelChecking() throws Exception;

//	public void modelCheckSolution() throws Exception;

	public double normalize(double f);

	public Color color(double f);

	public String toString();

	public void visualizeFromScratch();

	public void exportImage() throws Exception;
	
	public boolean isSolutionEmpty();
	
	public double getSolutionValue();
	
	public double getSolutionCardinality(GraphItem item);
	
	public int getNumSolutionTraces();
	
	public boolean isTargetVertex(Vertex vertex);
}

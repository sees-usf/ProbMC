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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;

import dipro.alg.BF;
import dipro.graph.DirectedGraph;
import dipro.graph.Vertex;
import dipro.h.Heuristic;
import dipro.util.AlgReporter;
import dipro.util.DiProException;
import dipro.util.Proposition;
import dipro.util.SearchTree;

public abstract class AbstractContext implements Context {

	protected int id;
	protected Config config;
	protected DirectedGraph graph;
	protected Vertex start;
	protected Proposition property;
	protected long tStamp;
	protected AlgReporter reporter;
	protected boolean isReporterEnabled;

	// Constructor
	public AbstractContext(int id, Config config) throws Exception {
		this.id = id;
		this.config = config;
		this.reporter = null;
		this.isReporterEnabled = false;
		tStamp = System.currentTimeMillis();
		readParameters();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.run.Context_#init()
	 */
	public void init() throws Exception {
		loadModel();
		System.out.println("Abstract Context - init");
		/*
		 * if (config.mc) performModelChecking();
		 */
	}

	public int getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.run.Context_#loadHeuristic(dipro.core.algo.BF)
	 */
	public Heuristic loadHeuristic(BF alg) throws Exception {
		System.out.println("loadHeuristic - AbstractContext");
		if (getConfig().heuristicName == null) {
			System.out.println("Returned a null - AbstractContext");
			return null;
		}
		Class<?> hClass = Class.forName(getConfig().heuristicName);
		Constructor<?> c = hClass.getConstructor(new Class[] { Context.class, BF.class });
		Object h = c.newInstance(this, alg);
		return (Heuristic) h;
	}

	protected void attachReporter(BF alg) throws FileNotFoundException {
		String reportFileName = getReportFileName();
		// System.out.println("Report: "+reportFileName);
		PrintStream repOut = new PrintStream(new FileOutputStream(reportFileName));
		AlgReporter reporter = new AlgReporter(alg, repOut);
		/*
		 * The reporter can be either registered in the context or be run as a parallel
		 * thread.
		 */
		/* Register it in the context. */
		this.reporter = reporter;
		/* Running the reporter as a parallel thread. */
//		reporter.start();
		isReporterEnabled = true;
	}

	public boolean isReporterEnabled() {
		return isReporterEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.run.Context_#getGraph()
	 */
	public DirectedGraph getGraph() {
		return graph;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.run.Context_#getStart()
	 */
	public Vertex getStart() {
		return start;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.run.Context_#getProperty()
	 */
	public Proposition getProperty() {
		return property;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.run.Context_#getConfig()
	 */
	public Config getConfig() {
		return config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.run.Context_#setGraph(dipro.core.graph.DirectedGraph)
	 */
	public void setGraph(DirectedGraph graph) {
		this.graph = graph;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.run.Context_#setProperty(dipro.core.algo.util.Proposition)
	 */
	public void setProperty(Proposition property) {
		this.property = property;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.run.Context_#setStart(dipro.core.graph.Vertex)
	 */
	public void setStart(Vertex start) {
		this.start = start;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.run.Context_#getDiPro()
	 */
	public DiPro getDiPro() {
		return config.getDiPro();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.run.Context_#log()
	 */
	public PrintStream getAlgLog() {
		return config.getDiPro().getAlgLog();
	}

	public AlgReporter getAlgReporter() {
		return reporter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.run.Context_#cleanup()
	 */
	public void cleanup() throws Exception {
		System.out.println("cleanup - AbstractContext - context cleanup");
		if (graph == null) {
			assert start == null;
			assert property == null;
			return;
		}
		graph.clear();
		System.out.println("cleanup - AbstractContext - context cleanup");
		graph = null;
		start = null;
		property = null;
		System.gc();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.run.Context_#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Context --\n");
		sb.append("Model:    ");
		sb.append(graph);
		sb.append("\n");
		sb.append("Start:    ");
		sb.append(start);
		sb.append("\n");
		sb.append("Property: ");
		sb.append(property);
		sb.append("\n");
		// if(heuristic!=null) {
		// sb.append("Heuristic: "); sb.append(heuristic.getClass());
		// sb.append("\n");
		// }
		sb.append("--\n");
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.run.Context_#createExploredGraph(dipro.core.algo.BF)
	 */
	public SearchTree createExploredGraph(BF alg) {
		System.out.println("createExploredGraph - AbstractContext");
		return new SearchTree(alg);
	}

	protected String getReportFileName() {
		assert config.report;
		return config.getDiPro().makeAbsoluteFileName(config.reportName);
	}

	protected abstract void readParameters() throws DiProException;

	protected abstract void loadModel() throws Exception;

}

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
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Vector;

import dipro.alg.BF;
import dipro.alg.KSPAlgorithm;
import dipro.alg.BF.SearchMark;
import dipro.graph.DirectedEdge;
import dipro.graph.GraphItem;
import dipro.graph.Vertex;
import dipro.run.Config;
import dipro.run.Registry;
import dipro.run.VisContext;
import dipro.run.VisMain;
import dipro.util.ExploredGraph;
import dipro.util.SearchTree;

/**
 * This class describes objects capable of visualizing how any algorithm
 * explores a statespace.
 * 
 */
public abstract class AbstractVisualizer extends Observable implements
		Visualizer {

	protected VisContext context;
	protected BF alg;
	protected ColorScale colorScale;
	protected int gradient;

	private static DecimalFormat format = null;

	
	public static DecimalFormat getFormat() {
		if(format == null) {
			DecimalFormatSymbols symb = new DecimalFormatSymbols();
			symb.setDecimalSeparator('.');
			format = new DecimalFormat("0.0##E0", symb);
		}
		return format;
	}
	
	/**
	 * This is the only constructor for this class.
	 * 
	 * @param settings
	 *            the settings for this visSettings
	 * @throws IOException
	 * @throws Exception
	 */
	public AbstractVisualizer(VisContext context, BF alg) {
		this.context = context;
		this.alg = alg;
		gradient = 1;
		if(context.getConfig().onlineVisualization) {
			alg.addObserver(this);
		}
	}

	// protected ExploredStateSpacePerspective createVisualizationGraph(Object
	// hint) {
	// return new ExploredStateSpacePerspective(search, hint);
	// // return new Clustering(search, hint);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.vis.Visualizer2#init()
	 */
	public void init() throws Exception {
		/* Create color scale */
		colorScale = createColorScale();
		// this.search = search;
		// navigator.init(search);
		// visGraph = createVisualizationGraph(new Integer(30));
		// visGraph.addObserver(this);
		setChanged();
		notifyObservers();
	}

	/**
	 * @param obj
	 *            new state, close state, reopen state
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void update(Observable observable, Object obj) {
		assert alg == observable;
		assert isOnline();
		VisSearchTree visSearchTree = (VisSearchTree)getSearchTree();
		updateVisualization(visSearchTree);
//		if (isOnline()) {
//			updateVisualization(visSearchTree);
//		} else {
//			// Visualize only after the search was terminated
//			int x = alg.getStatus();
//			if (x == BF.TERMINATED) {
//				visualizeFromScratch();
//			}
//		}
		setChanged();
		notifyObservers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.vis.Visualizer2#getVisExploredGraph()
	 */
	public SearchTree getSearchTree() {
		SearchTree tree;
		if (alg instanceof KSPAlgorithm) {
			tree = ((KSPAlgorithm) alg).getBasicSearchAlgorithm()
					.getSearchTree();
		} else
			tree = alg.getSearchTree();
		return tree;
	}
	
	public ExploredGraph getExploredGraph() {
		ExploredGraph g;
		if (alg instanceof KSPAlgorithm) {
			g = ((KSPAlgorithm) alg).getBasicSearchAlgorithm().getExploredGraph();
		} else
			g = alg.getExploredGraph();
		return g;
	}

//	public SolutionCollector getSolutionCollector() {
//		return alg.getSolutionCollector();
//	}

	protected synchronized void updateVisualization(VisSearchTree visExplGraph) {
		assert isOnline();
		// System.out.println("Visualizer: process events "+events.toString());
		List<VisualizationEvent> events = visExplGraph.deliverVisEvents();
		if (events.isEmpty())
			return;
		for (VisualizationEvent event : events) {
			try {
				visualizeChange(event);
			} catch (Exception e) {
				Registry.getMain().handleError("Visualisation failure!", e);
			}
		}
		update();
	}

	public boolean isOnline() {
		return getConfig().onlineVisualization;
	}

	protected void visualizeChange(VisualizationEvent event) throws Exception {
		switch (event.getEventType()) {
		// Normal Events
		case VisualizationEvent.NEW_STATE_EVENT:
			handleNewStateEvent(event);
			break;
		// case VisualizationEvent.NEW_TRANSITION_EVENT:
		// handleNewTransitionEvent(event);
		// break;
		case VisualizationEvent.CLOSE_STATE_EVENT:
			handleCloseStateEvent(event);
			break;
		case VisualizationEvent.REOPEN_STATE_EVENT:
			handleReopenStateEvent(event);
			break;
		case VisualizationEvent.RELAX_OPEN_STATE_EVENT:
			handleRelaxOpenStateEvent(event);
			break;
		case VisualizationEvent.INITIAL_STATE_EVENT:
			handleInitialStateEvent(event);
			break;
		}

	}

	protected Collection<String> relevantStateLabels() throws Exception {
		return context.getProperty().relevantLabels();
	}

	public ColorScale colorScale() {
		return colorScale;
	}

	protected ColorScale createColorScale() throws IOException {
		return new RGBColorScale(getConfig().colorScaleFileName);
	}

	public int getGradient() {
		return gradient;
	}

	public int setGradient(int x) {
		return gradient = x;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.vis.Visualizer2#color(float)
	 */
	public Color color(double f) {
		int c = (int) Math.round(normalize(f) * (colorScale.numColor() - 1));
		return colorScale.color(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.vis.Visualizer2#getPositionInQueue(dipro.core.graph.Vertex)
	 */
	public int getPositionInQueue(Vertex v) {
		Vector<SearchMark> firstThree = getSearchTree().peekThreeAhead();
		assert firstThree.size() <= 3;
		for (int i = 0; i < firstThree.size(); i++) {
			Vertex w = firstThree.get(i).vertex();
			if (w == v)
				return i;
		}
		return 3;
	}

	
	public Iterator<? extends DirectedEdge> getOutgoingEdges(Vertex vertex) {
		BF bf = alg;
		if (alg instanceof KSPAlgorithm)
			bf = ((KSPAlgorithm) alg).getBasicSearchAlgorithm();
		return bf.getExploredGraph().outgoingEdges(vertex);
	}
	
	
	public int scheduleForExpansion(Iterator<Vertex> iter) {
		BF bf = alg;
		if (alg instanceof KSPAlgorithm)
			bf = ((KSPAlgorithm) alg).getBasicSearchAlgorithm();
		return bf.getSearchTree().scheduleAsNextForExpansion(iter);
	}
	
	public boolean scheduleForExpansion(Vertex vertex) {
		BF bf = alg;
		if (alg instanceof KSPAlgorithm)
			bf = ((KSPAlgorithm) alg).getBasicSearchAlgorithm();
		return bf.getSearchTree().scheduleAsNextForExpansion(vertex);
	}
	
	public Iterator<? extends DirectedEdge> getIncomingEdges(Vertex vertex) {
		BF bf = alg;
		if (alg instanceof KSPAlgorithm)
			bf = ((KSPAlgorithm) alg).getBasicSearchAlgorithm();
		return bf.getExploredGraph().incomingEdges(vertex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.vis.Visualizer2#getContext()
	 */
	public VisContext getContext() {
		return context;
	}
	
	public boolean isSolutionEmpty() {
		return alg.isSolutionEmpty();
	}
	
	public double getSolutionValue() {
		return alg.getSolutionValue();
	}
	
	public boolean belongsToSolution(GraphItem item) {
		if(item instanceof DirectedEdge)
			return alg.belongsToSolution((DirectedEdge)item);
		else {
			assert item instanceof Vertex;
			return alg.belongsToSolution((Vertex)item);
		}
	}
	
	public double getMaxSolutionCardinality() {
		return alg.getMaxSolutionCardinality();
	}
	
	public double getSolutionCardinality(GraphItem item) {
		if(item instanceof DirectedEdge)
			return alg.getSolutionCardinality((DirectedEdge)item);
		else {
			assert item instanceof Vertex;
			return alg.getSolutionCardinality((Vertex)item);
		}
	}
	
	public int getNumSolutionTraces() {
		return alg.getNumSolutionTraces();
	}
	
	
	public boolean isTargetVertex(Vertex vertex) {
		return alg.isTragetVertex(vertex);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.vis.Visualizer2#start()
	 */
	public void start() throws Exception {
		if(alg.getConfig().logLevel>= Config.ALG_LOG_NORMAL) 
			alg.log("Start is requested by the user.");
		alg.execute();
		String algSummary = alg.getSummaryReport();
		Registry.getMain().out().println(algSummary);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.vis.Visualizer2#requestPause()
	 */
	public void requestPause() {
		if(alg.getConfig().logLevel>= Config.ALG_LOG_VERBOSE) 
			alg.log("Pause is requested by the user.");
		alg.requestPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.vis.Visualizer2#requestResume()
	 */
	public void requestResume() {
		if(alg.getConfig().logLevel>= Config.ALG_LOG_VERBOSE) 
			alg.log("Resume is requested by the user.");
		alg.requestResume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.vis.Visualizer2#requestTermination()
	 */
	public void requestTermination() {
		if(alg.getConfig().logLevel>= Config.ALG_LOG_NORMAL) 
			alg.log("Termination is requested by the user.");
		alg.requestTermination();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.vis.Visualizer2#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + " for " + alg;
	}

	protected Config getConfig() {
		return context.getConfig();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.vis.Visualizer2#performModelChecking()
	 */
	public void performModelChecking() throws Exception {
		synchronized (alg) {
			context.performModelChecking();
			alg.notify();
		}
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see dipro.vis.Visualizer2#modelCheckSolution()
//	 */
//	public void modelCheckSolution() throws Exception {
//		synchronized (alg) {
//			context.modelCheckSolution();
//			alg.notify();
//		}
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.vis.Visualizer2#getAlgStatus()
	 */
	public int getAlgStatus() {
		return alg.getStatus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dipro.vis.Visualizer2#closeVisualization()
	 */
	public void close() throws Exception {
		if(alg.getConfig().logLevel>= Config.ALG_LOG_NORMAL) {
			alg.log("Visualization is being closed by the user.");
			alg.log("Request Termination!");
		}
		alg.requestTermination();
		synchronized (alg) {
			alg.getContext().cleanup();
			alg.notify();
		}
		((VisMain) Registry.getMain()).closeVisualization();
	}

	@Override
	public BF getAlgorithm() {
		return alg;
	}

	protected abstract void handleRelaxOpenStateEvent(VisualizationEvent event)
			throws Exception;

	protected abstract void handleReopenStateEvent(VisualizationEvent event)
			throws Exception;

	protected abstract void handleCloseStateEvent(VisualizationEvent event)
			throws Exception;

	protected abstract void handleNewStateEvent(VisualizationEvent event)
			throws Exception;

	protected abstract void handleInitialStateEvent(VisualizationEvent event)
			throws Exception;
}

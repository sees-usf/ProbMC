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
import java.util.Comparator;
import java.util.Iterator;
import java.util.Observable;

import dipro.graph.DirectedEdge;
import dipro.graph.DirectedGraph;
import dipro.graph.Vertex;
import dipro.h.Heuristic;
import dipro.run.Config;
import dipro.run.Context;
import dipro.run.Registry;
import dipro.stoch.StochXSolutionCollector;
import dipro.util.Decoration;
import dipro.util.DefaultSolutionCollector;
import dipro.util.DiProException;
import dipro.util.DiagnosticPath;
import dipro.util.EvaluationFunction;
import dipro.util.ExploredGraph;
import dipro.util.Proposition;
import dipro.util.SearchTree;
import dipro.util.SolutionCollector;
import dipro.util.SolutionTracesRecorder;
import dipro.util.Trace;

public class BF extends Observable {

	public static final int NOT_INITIALIZED = 0;

	public static final int READY = 1;

	public static final int RUNNING = 2;

	public static final int PAUSED = 3;

	public static final int TO_TERMINATE = 4;

	public static final int TERMINATED = 5;

	public static final int CLEANED_UP = 6;

	/* instance variables */
	protected Context context;
	protected DirectedGraph graph;
	protected Vertex start;
	protected Proposition property;
	protected Comparator<Double> comparator;
	protected Heuristic heuristic;
	protected EvaluationFunction evaluationFunction;
	protected SearchTree searchTree;
	protected ExploredGraph exploredGraph;
	protected SolutionCollector solutionCollector;
	protected int iterations;
	protected int reopenings;
	protected long startTimePoint;
	protected long endTimePoint;
	protected long offTime;
	private long startOffTime;
	private long endOffTime;
	protected int memory;
	private int status;

	/**
	 * This is the unique constructor in this class. After creating an algorithm
	 * object, the method bind(Context,Comparable<Double>, EvaluationFunction)
	 * must be called to associate the algorithm with all settings needed.
	 * Before executing the algorithm one of the initialization methods init()
	 * or init(DirectedGraph, Vertex, Proposition,Heuristic, SolutionCollector)
	 * must be called. The search is triggered then using the method execute().
	 * 
	 * @throws Exception
	 */
	public BF() {
		graph = null;
		start = null;
		property = null;
		comparator = null;
		heuristic = null;
		evaluationFunction = null;
		searchTree = null;
		exploredGraph = null;
		solutionCollector = null;
		iterations = 0;
		reopenings = 0;
		this.startTimePoint = -1l;
		this.endTimePoint = -1l;
		this.offTime = 0l;
		this.startOffTime = -1l;
		this.endOffTime = -1l;
		memory = 0;
		setStatus(NOT_INITIALIZED);
		notifyObservers();

	}

	public void bind(Context context, Comparator<Double> comparator,
			EvaluationFunction evaluationFunction) {
		this.context = context;
		this.comparator = comparator;
		this.evaluationFunction = evaluationFunction;
	}

	public void init() throws Exception {
		System.out.println("init - BF");
		Heuristic h = context.loadHeuristic(this);
		SolutionCollector sc = context.createSolutionCollector(this);
		this.init(context.getGraph(), context.getStart(), context.getProperty(), h, sc);
	}

	// Initializes values w/ parameters
	public void init(DirectedGraph graph, Vertex start, Proposition property,
			Heuristic heuristic, SolutionCollector solutionCollector) throws Exception {
		System.out.println("init2 - BF");
		reset();
		this.graph = graph;
		this.start = start;
		this.property = property;
		// comparator = createComparator();
		this.heuristic = heuristic;
		// evaluationFunction = createEvaluationFunction();
		searchTree = createExploredSpace();
		exploredGraph = new ExploredGraph();
		System.out.println("Num Edges: " +  searchTree.numEdges() + " Num Vertices: " + searchTree.numVertices());
		this.solutionCollector = solutionCollector;
		arrangeGraph();
		System.out.println("after arrangeGraph Num Edges: " +  searchTree.numEdges() + " Num Vertices: " + searchTree.numVertices());
		setStatus(READY);
		notifyObservers();
	}

	// Resets everything to an initial value
	protected void reset() throws Exception {
		offTime = 0l;
		endTimePoint = -1l;
		startTimePoint = -1l;
		startOffTime = -1l;
		endOffTime = -1l;
		reopenings = 0;
		iterations = 0;
		if (solutionCollector != null) {
			solutionCollector.clear();
		}
		solutionCollector = null;
		if (searchTree != null) {
			searchTree.clear();
		}
		searchTree = null;
		if (exploredGraph != null) {
			exploredGraph.clear();
		}
		exploredGraph = null;
		// evaluationFunction = null;
		heuristic = null;
		// comparator = null;
		property = null;
		start = null;
		graph = null;
	}

	//
	protected void arrangeGraph() throws Exception {
		if (start == null)
			return;
		SearchMark sMark = createSearchMark(start);
		evaluationFunction.evaluate(null, null, sMark);
		if (comparator.compare(sMark.f(), getConfig().pruneBound) >= 0) {
			if (getConfig().logLevel >= Config.ALG_LOG_NORMAL)
				log("Start vertex pruned " + sMark);
		} else {
			searchTree.open(sMark);
			exploredGraph.addVertex(start); 
		}
	}

	public final void execute() throws Exception {
		if (getConfig().logLevel >= Config.ALG_LOG_BASIC)
			log("Execute algorithm");
		offTime = 0l;
		startTimePoint = System.currentTimeMillis();
		setStatus(RUNNING);
		notifyObservers();
		System.out.println("in execute - BF");
		search();
		System.out.println(" after search - BF");
		terminate();
		System.out.println("after terminate - BF");
		if (getConfig().logLevel >= Config.ALG_LOG_BASIC) {
			log("Summary \n" + "======= \n" + getSummaryReport());
		}
	}

	protected final void search() throws Exception {
		if (getConfig().logLevel >= Config.ALG_LOG_BASIC)
			log("Search started ...");
		System.out.println("\nsearch - BF");
		searchUntil();
		System.out.println("\nafter searchUntil - BF");
		if (getConfig().logLevel >= Config.ALG_LOG_BASIC) {
			log("Search ended.");
			if (isSearchCompleted()) {
				if (getConfig().logLevel >= Config.ALG_LOG_NORMAL)
					log("Graph has been completely searched (search front is empty).");
			}
		}
	}

	/**
	 * Repeatedly calls method doOneIteration() until either the priority queue
	 * is empty or method shouldContinue() returns false.
	 * 
	 * @throws Exception
	 */
	protected void searchUntil() throws Exception {
		System.out.println("searchUntil - BF");
		while (!searchTree.isOpenEmpty()) {
			System.out.println("\nIteration: "+ getNumIterations()+ "\nNumEdges: " + searchTree.numEdges() + " Num Vertices: " + searchTree.numVertices());
			if (shouldTerminate()) {
				System.out.println("SearchUntil - BF- Should terminate");
				if (getConfig().logLevel >= Config.ALG_LOG_BASIC)
					log("Algorithm should terminate.");
				return;
			}
			demandForResume();
			System.out.println("SearchUntil - BF-  after Demands for Resume - Iteration Count : " + getNumIterations());
			doOneIteration();
			System.out.println("SearchUntil - BF- after doOneIteration -  Iteration Count: " + getNumIterations());
			iterationDone();
			System.out.println("SearchUntil - BF- after iterationDone - Iteration Count:" + getNumIterations());
			if (getConfig().isInStepByStepModus) {
				setStatus(PAUSED);
				notifyObservers();
			}
		}
		if (getConfig().logLevel >= Config.ALG_LOG_NORMAL)
			log("Search front is empty; search ended.");
	}

	/**
	 * Can be called manually to single-step the algorithm, but you must call
	 * init(.) before the first call to this method. Finishes one vertex and
	 * updates all adjacent vertices. If the vertex that gets finished was
	 * reachable from the source, this method expands the shortest-path tree by
	 * one vertex.
	 * 
	 * @throws Exception
	 */
	protected final void doOneIteration() throws Exception {
		System.out.println("doOneIteration - BF");
		iterations++; // Increments iteration @ beginning
		if (getConfig().logLevel >= Config.ALG_LOG_DEBUG)
			log("Search Iteration " + iterations);
		// remove a vertex with minimum distance from the source
		SearchMark uMark = searchTree.getOptimalOpen();
		System.out.println("after getOptimalOpen\n");
		vertexToExpand(uMark);
		System.out.println("after vertexToExpand\n");
		
		if (shouldExpand(uMark)) {
			System.out.println("doOneIteration after shouldExpand - BF - Iteration Count: " + getNumIterations());
			Vertex u = uMark.vertex();
			
			// examine all the neighbors of u and update their distances
			Iterator<? extends DirectedEdge> iter = getOutgoingEdges(u);
			System.out.println("in doOneIteration Num Edges: " +  searchTree.numEdges() + " Num Vertices: " + searchTree.numVertices());
			while (iter.hasNext()) { // while u has more edges
				System.out.println("u has more edges");
				DirectedEdge uv = iter.next();
				Vertex v = uv.target();
				RelaxationInfo relax = relax(uMark, uv, v);
				handleRelaxation(relax);
			}
			vertexExpanded(uMark); // Prints stuff to log if condition is true
		} else {
			if (getConfig().logLevel >= Config.ALG_LOG_DETAILED)
				log("Excluded from expansion: " + uMark.v + ", Distance= "
						+ uMark.f());
		}
	}

	// Checks for Outgoing Edges
	@SuppressWarnings("unchecked")
	public Iterator<? extends DirectedEdge> getOutgoingEdges(Vertex vertex) {
		SearchMark mark = searchTree.isExplored(vertex);
		System.out.println("getOutgoignEdges - Mark to string - BF: " + mark.toString());
		Iterator<? extends DirectedEdge> iter = (Iterator<DirectedEdge>) getGraph().outgoingEdges(vertex);
		iter = processOutgoingEdges(mark, iter);
		return iter;
	}

	protected void iterationDone() throws Exception {
		 System.out.println("In Iteration Done - BF");
		setChanged();
		notifyObservers();
		report();
	}

	public void requestTermination() {
		switch (status) {
		case NOT_INITIALIZED:
		case READY:
		case RUNNING:
		case PAUSED:
			setStatus(TO_TERMINATE);
			notifyObservers();
		}
		if (getConfig().logLevel >= Config.ALG_LOG_NORMAL) {
			log("Termination Requested.");
		}
	}

	public void requestPause() {
		switch (status) {
		case RUNNING:
			setStatus(PAUSED);
			notifyObservers();
		}
		if (getConfig().logLevel >= Config.ALG_LOG_VERBOSE) {
			log("Pause Requested.");
		}
	}

	public void requestResume() {
		switch (status) {
		case PAUSED:
			setStatus(RUNNING);
			notifyObservers();
		}
		if (getConfig().logLevel >= Config.ALG_LOG_VERBOSE) {
			log("Resume Requested.");
		}
	}

	protected boolean shouldTerminate() throws Exception {
		if (getStatus() == TO_TERMINATE) {
			if (getConfig().logLevel >= Config.ALG_LOG_NORMAL)
				log("Termination request is in execution.");
			return true;
		}
		if (getConfig().maxIter > 0
				&& getNumIterations() >= getConfig().maxIter) {
			if (getConfig().logLevel >= Config.ALG_LOG_NORMAL)
				log("Search should terminat; maximal iteration number reached ("
						+ getNumIterations() + " iterations)");
			return true;
		}
		long time = computeRuntime();
		long timeInMin = time / 1000 / 60;
		// ForDebugging
		// long off = getOffTime();
		// System.out.println("Runtime = "+time+" (offtime = "+off+"), runtime in min = "+timeInMin);
		if (getConfig().maxTime > 0 && timeInMin >= getConfig().maxTime) {
			if (getConfig().logLevel >= Config.ALG_LOG_NORMAL)
				log("Search should terminat; maximal time reached ("
						+ timeInMin + " min)");
			return true;
		}
		if (!getConfig().complete
				&& solutionCollector.getNumTraces() >= getConfig().k) {
			if (getConfig().logLevel >= Config.ALG_LOG_NORMAL)
				log("Search should terminate; enough solution traces found ("
						+ solutionCollector.getNumTraces() + " traces)");
			return true;
		}
		// return solutionCollector.getNumTraces() >0;
		return false;
	}

	protected void terminate() throws Exception {
		endTimePoint = System.currentTimeMillis();
		setStatus(TERMINATED);
		notifyObservers();
		System.out.println("terminate - BF");
		if (solutionCollector != null) {
			System.out.println("solutionCollector - BF");
			//solutionCollector.commit();
			System.out.println("solutionCollector - BF");
			report();
		}
		if (getConfig().logLevel >= Config.ALG_LOG_BASIC)
			log("Algorithm terminated");

		if (getConfig().solutionTrace == SolutionTracesRecorder.DIAG_PATH) {
			DiagnosticPath.readCXXML(getConfig().getModelName()
					+ ".sol.traces.xml", new ArrayList<String>(), this);
		}

	}

	public void cleanup() throws Exception {
		System.out.println("cleanUp - BF");
		if (getStatus() == CLEANED_UP)
			return;
		
		if (getStatus() != TERMINATED)
			terminate();
		System.out.println("cleanUp - BF - after terminate");
		reset();
		System.out.println("cleanUp - BF - after reset");
		setStatus(CLEANED_UP);
		notifyObservers();
		if (getConfig().logLevel >= Config.ALG_LOG_NORMAL)
			log("Cleaning up.");
	}

	synchronized protected void demandForResume() throws InterruptedException {
		startCountingOffTime();
		while (getStatus() == PAUSED) {
			this.wait();
		}
		stopCountingOffTime();
	}

	public int getStatus() {
		return status;
	}

	protected synchronized void setStatus(int newStatus) {
		if (status != newStatus)
			setChanged();
		status = newStatus;
		notifyAll();
	}

	public Config getConfig() {
		return context.getConfig();
	}

	public SearchTree getSearchTree() {
		return searchTree;
	}

	public ExploredGraph getExploredGraph() {
		return exploredGraph;
	}

	public Comparator<Double> getComparator() {
		return comparator;
	}

	public EvaluationFunction getEvaluationFunction() {
		return evaluationFunction;
	}

	public Proposition getProperty() {
		return property;
	}

	public Vertex getStartVertex() {
		return start;
	}

	public DirectedGraph getGraph() {
		return graph;
	}

	public Context getContext() {
		return context;
	}

	// public SolutionCollector getSolutionCollector() {
	// return solutionCollector;
	// }

	public int getNumIterations() {
		return iterations;
	}

	public int getNumReopenings() {
		return reopenings;
	}

	public void notifyObservers() {
		if (countObservers() > 0) {
			// ForDebugging
			// System.out.println("start counting offtime to report");
			startCountingOffTime();
			super.notifyObservers();
			stopCountingOffTime();
		}
		System.out.println("notifyObservers - BF");
	}

	public double computeTraceValue(Trace trace) throws Exception {
		return evaluationFunction.computeTraceValue(trace);
	}

	public double f(Vertex v) {
		return searchTree.f(v);
	}

	public double g(Vertex v) {
		return searchTree.g(v);
	}

	public double h(Vertex v) {
		return searchTree.h(v);
	}

	public Heuristic getHeuristic() {
		return heuristic;
	}

	public String toString() {
		String s = this.getClass().getSimpleName();
		return s;
	}

	protected boolean checkRelaxOpen(SearchMark newSearchMark,
			SearchMark oldSearchMark) {
		double newDist = newSearchMark.f();
		double oldDist = oldSearchMark.f();
		if (evaluationFunction.isLengthBased()) {
			return comparator.compare(newDist, oldDist) > 0;
		} else {
			return comparator.compare(newDist, oldDist) < 0;
		}
	}

	protected boolean checkRelaxClosed(SearchMark newSearchMark,
			SearchMark oldSearchMark) {
		return checkRelaxOpen(newSearchMark, oldSearchMark);
	}

	protected RelaxationInfo relax(SearchMark uMark, DirectedEdge uv, Vertex v)
			throws Exception {
		RelaxationInfo relax;
		SearchMark newVMark = createSearchMark(v);
		double newDist = evaluationFunction.evaluate(uMark, uv, newVMark);
		searchTree.setTreeEdge(newVMark, uv);
		SearchMark oldVMark = searchTree.isOpen(v);
		if (oldVMark != null) {
			// v is an open vertex
			assert oldVMark.v.equals(v);
			if (checkRelaxOpen(newVMark, oldVMark)) {
				// relax
				relax = relaxOpen(newVMark, oldVMark);
			} else {
				relax = createRelaxationInfo(RelaxationInfo.OPEN_VERTEX,
						newVMark, oldVMark);
			}
		} else {
			oldVMark = searchTree.isClosed(v);
			if (oldVMark != null) {
				// v is a closed vertex
				assert oldVMark.v.equals(v);
				if (checkRelaxClosed(newVMark, oldVMark)) {
					// reopen
					relax = reopen(newVMark, oldVMark);
				} else {
					relax = createRelaxationInfo(RelaxationInfo.CLOSED_VERTEX,
							newVMark, oldVMark);
				}
			} else {
				// v is a new vertex
				if (comparator.compare(newDist, getConfig().pruneBound) < 0) {
					relax = relaxNew(newVMark);
				} else {
					relax = createRelaxationInfo(
							RelaxationInfo.NEW_VERTEX_PRUNED, newVMark, null);
				}
			}
		}
		if (relax.flag != RelaxationInfo.NEW_VERTEX_PRUNED) {
			exploredGraph.addEdge(uv);
		}
		/*
		 * < ForDebugging > if((v instanceof USRoadNode) &&
		 * ((USRoadNode)v).getId()==55310) { System.out.println();
		 * System.out.println(relax); } /* </ ForDebugging >
		 */
		return relax;
	}

	protected RelaxationInfo reopen(SearchMark newVMark, SearchMark oldVMark)
			throws Exception {
		searchTree.reopen(oldVMark, newVMark);
		RelaxationInfo relax = createRelaxationInfo(
				RelaxationInfo.CLOSED_VERTEX_RELAXED, newVMark, oldVMark);
		reopenings++;
		return relax;
	}

	protected RelaxationInfo relaxOpen(SearchMark newVMark, SearchMark oldVMark)
			throws Exception {
		// newVMark.takeOnAttributes(oldVMark);
		searchTree.replaceOpen(newVMark, oldVMark);
		RelaxationInfo relax = createRelaxationInfo(
				RelaxationInfo.OPEN_VERTEX_RELAXED, newVMark, oldVMark);
		return relax;
	}

	protected RelaxationInfo relaxNew(SearchMark newVMark) throws Exception {
		searchTree.open(newVMark);
		RelaxationInfo relax = createRelaxationInfo(RelaxationInfo.NEW_VERTEX,
				newVMark, null);
		return relax;
	}

	protected RelaxationInfo createRelaxationInfo(int flag, SearchMark newMark,
			SearchMark oldMark) {
		return new RelaxationInfo(flag, newMark, oldMark);
	}

	protected Iterator<? extends DirectedEdge> processOutgoingEdges(SearchMark uMark, Iterator<? extends DirectedEdge> outgoingEdges) {
		return outgoingEdges;
	}

	protected void handleRelaxation(RelaxationInfo relax) throws Exception {
		logRelaxation(relax);
		// switch (relax.flag()) {
		// case RelaxationInfo.NEW_VERTEX:
		// searchTree.incrementNumEdges(1);
		// break;
		// default:
		// break;
		// }

		if (relax.flag() == RelaxationInfo.NEW_VERTEX) {
			if (getConfig().propId == -1)
				newTraceFound(relax.getNewMark());
			else {
				int x = property.check(relax.getNewMark().v);
				if (x == Proposition.TRUE) {
					newTargetFound(relax.getNewMark());
				}
			}
		}
	}

	protected void logRelaxation(RelaxationInfo relax) {
		switch (relax.flag()) {
		case RelaxationInfo.NEW_VERTEX_PRUNED:
			if (getConfig().logLevel >= Config.ALG_LOG_DETAILED)
				log(relax);
			break;
		case RelaxationInfo.CLOSED_VERTEX_RELAXED:
			if (getConfig().logLevel >= Config.ALG_LOG_VERBOSE)
				log(relax);
			break;
		case RelaxationInfo.OPEN_VERTEX_RELAXED:
			if (getConfig().logLevel >= Config.ALG_LOG_VERBOSE)
				log(relax);
			break;
		default:
			if (getConfig().logLevel >= Config.ALG_LOG_DEBUG)
				log(relax);
			break;
		}
	}

	protected boolean isSearchCompleted() {
		return searchTree.isOpenEmpty();
	}

	/**
	 * This method is called when a vertex is selected to be expanded. It
	 * indicates whether the algorithm should expand the vertex or not. The
	 * default implementation just returns <i>true</i>.
	 * 
	 * @param v
	 *            the vertex which is considered for the expansion
	 * @return true if the vertex should be expanded or false otherwise.
	 * @throws Exception
	 */
	protected boolean shouldExpand(SearchMark vMark) throws Exception {
		System.out.println("should expand - BF");
		/* < ForDebugging > */
		if (comparator.compare(vMark.f(), getConfig().pruneBound) >= 0) {
			System.out.println(vMark.f() + " must not be worse than "
					+ getConfig().pruneBound);
		}
		/* </ ForDebugging > */
		assert comparator.compare(vMark.f(), getConfig().pruneBound) < 0;
		int x = property.check(vMark.v);
		System.out.println("shouldExpand - BF - after check");
		switch (x) {
			case Proposition.TRUE:
				if (getConfig().logLevel >= 3)
					log("Vertex should not be expanded because it is a target vertex: "
							+ vMark.v);
				return false;
			case Proposition.NEVER:
				if (getConfig().logLevel >= 3)
					log("Vertex should not be expanded because it never leads to a target: "
							+ vMark.v);
				return false;
		}
		assert x == Proposition.FALSE;
		return true;
	}

	protected void newTargetFound(SearchMark vMark) throws Exception {
		solutionCollector.receiveSolution(vMark);
		if (getConfig().logLevel >= Config.ALG_LOG_DETAILED)
			log("New target found: " + vMark.v + ", Distance = " + vMark.f());
	}
	
	
	protected void newTraceFound(SearchMark vMark) throws Exception {
		Trace traceTmp = getSearchTree().backtrack(vMark);
		((StochXSolutionCollector)solutionCollector).recordTrace(traceTmp);
	}

	/**
	 * Can be overridden to give you a notification when a vertex is going to be
	 * expanded. Normally, at this point the shortest path to a vertex is
	 * determined. The algorithm calls this method at most once per vertex,
	 * after the vertex has been "finished" (i.e., when the path from s to the
	 * vertex is known). Dijkstra will never again consider the vertex. However
	 * some (heuristic) algorithms, e.g. A* and Best First, may reopen the
	 * vertex, i.e. the vertex will be considered again, if a new path to the
	 * vertex is found which is better than the old one.
	 * <p>
	 * Note after the call of this method the algorithm calls the method
	 * <i>shouldExpand(Vertex v)</i> which indicates whether the algorithm
	 * should expand the vertex or not.
	 * 
	 * @param v
	 *            Vertex that the algorithm just finished
	 * @param vDist
	 *            Distance of v from the source
	 * @throws Exception
	 */
	protected void vertexToExpand(SearchMark vMark) throws Exception {
		System.out.println("vertexToExpand - BF");
		Vertex v = vMark.vertex();
		/*
		 * < ForDebugging > if((v instanceof USRoadNode) &&
		 * (((USRoadNode)v).getId()==168180)) { System.out.println("Expand "+v);
		 * } /* </ ForDebugging >
		 */
		searchTree.close(vMark);
		double f = vMark.f();
		double g = vMark.g();
		double h = vMark.h();
		if (getConfig().logLevel >= Config.ALG_LOG_VERBOSE)
			log("To expand: " + v + ", g=" + g + ", h=" + h + ", f=" + f);
	}

	protected void vertexExpanded(SearchMark vMark) throws Exception {
		if (getConfig().logLevel >= Config.ALG_LOG_VERBOSE)
			log("Expanded: " + vMark.vertex());
	}

	/**
	 * Can be overridden to supply a data structure of your choosing to store
	 * the explored part of the state space. The default implementation, which
	 * gives an empty dipro.core.algo.ExploredSpace.
	 * 
	 * @return ExploredSpace object to be used by the algorithm.
	 */
	protected SearchTree createExploredSpace() {
		System.out.println("CreateExploredSpace -  BF");
		return context.createExploredGraph(this);
	}

	public String getSummaryReport() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("Search Summary: \n");
		sb.append("Search Algorithm =     \t " + toString() + "\n");
		if (heuristic != null) {
			sb.append("Heuristic Function =   \t "
					+ heuristic.getClass().getName() + "\n");
		}
		sb.append("Search Iterations =    \t " + getNumIterations() + "\n");
		sb.append("Reopenings =           \t " + getNumReopenings() + "\n");
		sb.append("Explored Vertices =    \t " + numVertices() + "\n");
		sb.append("Explored Edges =       \t " + numEdges() + "\n");
		sb.append("Model Memory =         \t " + computeModelMemory() + "\n");
		sb.append("Search Memory =        \t " + computeSearchMemory() + "\n");
		sb.append("Solution Memory =      \t " + computeSolutionMemory() + "\n");
		computeMemory();
		sb.append("Maximal Used Memory =  \t " + getMemory() + "\n");
		sb.append("Runtime=               \t " + computeRuntime() + "\n");
		sb.append("Off Time=              \t " + getOffTime() + "\n");
		sb.append("Solution Data: \n");
		sb.append("Solution value =       \t " + getSolutionValue() + "\n");
		sb.append("Solution size =        \t " + getSolutionSize() + "\n");
		sb.append("Solution basic traces =\t " + getNumSolutionTraces() + "\n");
		
		System.out.println("getSummaryReport - BF");
		if(((DefaultSolutionCollector)this.solutionCollector).getSolutionTraceRecorderType()==SolutionTracesRecorder.DIAG_PATH 
				|| ((DefaultSolutionCollector)this.solutionCollector).getSolutionTraceRecorderType()==SolutionTracesRecorder.CX_XML_FILE)
		{
			ArrayList<DiagnosticPath> dps = this.solutionCollector.getDiagnosticPath();
			double dpSum = 0.0d;
			for(DiagnosticPath dp : dps)
			{
				dpSum += dp.getProbability();
			}
			sb.append("Probability Sum of Diagnostic Paths: " +dpSum);
		}
		
		return sb.toString();
	}

	public void log(Object msg) {
		context.getAlgLog().println(this + ": " + msg);
	}

	protected void report() throws Exception {
		System.out.println("Report - BF");
		if (context.isReporterEnabled()) {
			startCountingOffTime();
			computeMemory();
			if (context.getAlgReporter() != null)
				context.getAlgReporter().report();
			stopCountingOffTime();
		}
	}

	public SearchMark createSearchMark(Vertex v) {
		SearchMark vMark = new SearchMark(v);
		return vMark;
	}

	protected void startCountingOffTime() {
		if (startOffTime >= 0)
			return;
		else
			startOffTime = System.currentTimeMillis();
	}

	protected void stopCountingOffTime() {
		// assert startOffTime >= 0;
		long t0;
		if (startOffTime >= 0)
			t0 = startOffTime;
		else {
			assert endOffTime >= 0;
			t0 = endOffTime;
		}
		startOffTime = -1l;
		endOffTime = System.currentTimeMillis();
		long t = endOffTime - t0;
		assert t >= 0l;
		if (startTimePoint < 0)
			return;
		offTime = offTime + t;
		// ForDebugging
		// long time = System.currentTimeMillis() - startTimePoint;
		// System.out.println("On = "+(time -
		// offTime)+", off = "+offTime+" (added: "+t+"), total ="+time);
		assert offTime <= (System.currentTimeMillis() - startTimePoint);
	}

	public long getOffTime() {
		return offTime;
	}

	public long computeRuntime() {
		if (startTimePoint < 0)
			return 0l;
		long t1 = startTimePoint;
		long t2 = System.currentTimeMillis();
		switch (status) {
		case TERMINATED:
		case CLEANED_UP:
			assert endTimePoint >= startTimePoint;
			t2 = endTimePoint;
		}
		long runtime = t2 - t1;
		runtime = runtime - getOffTime();
		// if(runtime<0) {
		// System.out.println();
		// System.out.println("Alg. status: "+statusToString());
		// System.out.println("Start: "+startTimePoint);
		// System.out.println("t1: "+t1);
		// System.out.println("t2: "+t2);
		// System.out.println("End: "+endTimePoint);
		// System.out.println("Duration: "+(t2 - t1));
		// System.out.println("Off time: "+offTime);
		// System.out.println("Runtime: "+(t2 - t1 -offTime));
		// }
		// assert runtime >= 0;
		if (runtime < 0) {
			Registry.getMain().handleWarning("Negative Runtime: " + runtime);
			runtime = 0;
		}
		return runtime;
	}

	protected int computeSearchMemory() throws DiProException {
		int searchMarkSize = 0;
		/* 8 bytes for g (double) */
		searchMarkSize = searchMarkSize + 8;
		/* 4 bytes for depth (double) */
		searchMarkSize = searchMarkSize + 4;
		/* The heuristic value does not need to be stored. */

		/*
		 * space for the parent pointer (i.e. tree edge). The tree edge is a
		 * reference (4 bytes) to an edge in the graph.
		 */
		searchMarkSize = searchMarkSize + 4;

		/* space of the hashtables */
		/* closed: each entry consists of a reference (4 byte) + a search mark */
		int x = searchTree.numClosedVertices() * (4 + searchMarkSize);
		/* open: each entry consists of a reference (4 byte) + a search mark */
		int y = searchTree.numOpenVertices() * (4 + searchMarkSize);
		int memory = x + y;
		return memory;
	}

	protected int computeModelMemory() throws Exception {
		/* space for the vertices */
		int m = exploredGraph.numVertices() * graph.vertexSize();
		/*
		 * space for the transitions. Notice that we consider here only the
		 * transition of the search tree. Other transitions stored in
		 * exploredGraph are ignored here, since they are not necessary for the
		 * search.
		 */
		m = m + searchTree.numEdges() * graph.edgeSize();
		return m;
	}

	protected int computeSolutionMemory() {
		return solutionCollector.computeUsedMemory();
	}

	protected void computeMemory() throws Exception {
		int search = computeSearchMemory();
		int model = computeModelMemory();
		int solution = computeSolutionMemory();
		memory = Math.max(memory, search + model + solution);
	}

	public int getMemory() {
		return memory;
	}

	// protected int openSearchMarkSize() {
	// return closedSearchMarkSize();
	// }
	//
	// protected int closedSearchMarkSize() {
	// int s = 0;
	// s = s + 8; // g
	// if(heuristic!=null) s = s + 8; //h
	// s = s + 2; // depth
	// return s;
	// }

	public int numVertices() {
		return exploredGraph.numVertices();
	}

	public int numEdges() {
		return exploredGraph.numEdges();
	}

	public int numOpenVertices() {
		return searchTree.numOpenVertices();
	}

	public int numClosedVertices() {
		return searchTree.numClosedVertices();
	}

	public int maxOpenSize() {
		return searchTree.maxOpenSize();
	}

	public double getBestTraceValue() {
		return solutionCollector.getBestTraceValue();
	}

	public int getNumSolutionEdges() {
		return solutionCollector.getNumSolutionEdges();
	}

	public int getNumSolutionVertices() {
		return solutionCollector.getNumSolutionVertices();
	}

	public int getNumSolutionTraces() {
		return solutionCollector.getNumTraces();
	}

	public double getMaxSolutionCardinality() {
		return solutionCollector.getMaxSolutionCardinality();
	}

	public boolean belongsToSolution(Vertex vertex) {
		return solutionCollector.belongsToSolution(vertex);
	}

	public boolean belongsToSolution(DirectedEdge edge) {
		return solutionCollector.belongsToSolution(edge);
	}

	public double getSolutionCardinality(DirectedEdge edge) {
		return solutionCollector.getSolutionCardinality(edge);
	}

	public double getSolutionCardinality(Vertex vertex) {
		return solutionCollector.getSolutionCardinality(vertex);
	}

	public int getSolutionSize() {
		return solutionCollector.getSolutionSize();
	}

	public ArrayList<DiagnosticPath> getDiagnosticPath() {
		return solutionCollector.getDiagnosticPath();
	}

	public double getSolutionValue() {
		return solutionCollector.getSolutionValue();
	}

	public boolean isSolutionEmpty() {
		return solutionCollector.isSolutionEmpty();
	}

	public boolean isTragetVertex(Vertex vertex) {
		return solutionCollector.isTragetVertex(vertex);
	}

	protected String statusToString() {
		switch (status) {
		case NOT_INITIALIZED:
			return "Not initialized";
		case READY:
			return "Ready";
		case RUNNING:
			return "Running";
		case PAUSED:
			return "Paused";
		case TO_TERMINATE:
			return "Terminating";
		case TERMINATED:
			return "Terminated";
		case CLEANED_UP:
			return "Cleaned up";
		}
		return "Unknown State";
	}

	public class RelaxationInfo {

		public static final int NEW_VERTEX = 1;
		public static final int OPEN_VERTEX = 2;
		public static final int CLOSED_VERTEX = 3;
		public static final int OPEN_VERTEX_RELAXED = 4;
		public static final int CLOSED_VERTEX_RELAXED = 5;
		public static final int NEW_VERTEX_PRUNED = 6;

		protected int flag;
		protected SearchMark newMark;
		protected SearchMark oldMark;

		protected RelaxationInfo(int flag, SearchMark newMark,
				SearchMark oldMark) {
			assert oldMark == null || newMark.v.equals(oldMark.v);
			this.flag = flag;
			this.newMark = newMark;
			this.oldMark = oldMark;
		}

		public SearchMark getNewMark() {
			return newMark;
		}

		public SearchMark getOldMark() {
			return oldMark;
		}

		public DirectedEdge getEdge() {
			return searchTree.getTreeEdge(newMark);
		}

		public int flag() {
			return flag;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			switch (flag) {
			case NEW_VERTEX:
				sb.append("New vertex");
				break;
			case CLOSED_VERTEX:
				sb.append("Closed vertex");
				break;
			case CLOSED_VERTEX_RELAXED:
				sb.append("Relaxed closed vertex");
				break;
			case OPEN_VERTEX:
				sb.append("Open vertex");
				break;
			case OPEN_VERTEX_RELAXED:
				sb.append("Relaxed open vertex");
				break;
			case NEW_VERTEX_PRUNED:
				sb.append("Pruned new vertex");
				break;
			default:
				throw new IllegalStateException("Invalid relaxation flag: "
						+ flag);
			}
			sb.append("\n\tNew vertex: ");
			sb.append(newMark.v);
			sb.append(", ");
			sb.append("g = ");
			sb.append(newMark.g());
			sb.append(", ");
			sb.append("h = ");
			sb.append(newMark.h());
			sb.append(", ");
			sb.append("f = ");
			sb.append(newMark.f());
			sb.append(", ");
			sb.append("Parent: ");
			DirectedEdge e = searchTree.getTreeEdge(newMark);
			if (e != null) {
				sb.append(e.source());
				sb.append(", Parent Edge: ");
				sb.append(e);
			} else
				sb.append("null");
			if (oldMark != null) {
				sb.append("\n\tOld vertex: ");
				sb.append(oldMark.v);
				sb.append(", ");
				sb.append("g = ");
				sb.append(oldMark.g());
				sb.append(", ");
				sb.append("h = ");
				sb.append(oldMark.h());
				sb.append(", ");
				sb.append("f = ");
				sb.append(oldMark.f());
				sb.append(", ");
				sb.append("Parent: ");
				e = searchTree.getTreeEdge(oldMark);
				if (e != null) {
					sb.append(e.source());
					sb.append(", Parent Edge: ");
					sb.append(e);
				} else
					sb.append("null");
			}
			return sb.toString();
		}
	}

	public class SearchMark extends Decoration {

		protected Vertex v;

		protected SearchMark(Vertex v) {
			this.v = v;
		}

		public Vertex vertex() {
			return v;
		}

		public double f() {
			System.out.println("f - BF");	
			return evaluationFunction.f(this);
		}

		public double g() {
			return evaluationFunction.g(this);
		}

		public double h() {
			return evaluationFunction.h(this);
		}

		public int depth() {
			return evaluationFunction.depth(this);
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(f());
			sb.append("_");
			sb.append(v);
			return sb.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((v == null) ? 0 : v.hashCode());
			return result;
		}
	}

}

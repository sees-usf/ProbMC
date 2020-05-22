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

import java.util.HashSet;
import java.util.Iterator;

import dipro.alg.PathGraph.PNode;
import dipro.graph.DirectedEdge;
import dipro.graph.Vertex;
import dipro.run.Config;

public class KStar extends KSPAlgorithm {

	private static final int COMPLETE = 1; 
	private static final int PASSED = 0; 
	private static final int NOT_PASSED = -1; 
	
	private boolean isVirginDijkstra; 
	private int numGlobalIteration;
//	private double expansionFactor = 2.0;
//	private double expansionFactor = 1.2;
	
	/** This set contains those nodes from the path graph 
	 * which have been expanded but at the time of expansion
	 * not all their children reference were occupied. These 
	 * nodes are checked after each space expansion (done by 
	 * A*) on new children. If new children were added, then 
	 * these must be inserted into open (just as their siblings).
	 */
	private HashSet<SearchMark> maybeIncompleteExpansion;
	private HashSet<PathGraphEntry> visitedHeapNodes;
	
	private double dijkstraNextValue; 
	
	protected KStar() {
		super();
		isVirginDijkstra = true;
		numGlobalIteration = 0;
		maybeIncompleteExpansion = new HashSet<SearchMark>();
		visitedHeapNodes = new HashSet<PathGraphEntry>();
	}
	
	@Override
	protected void searchUntil() throws Exception {
		bf.offTime = 0l;
		bf.startTimePoint = System.currentTimeMillis();
		bf.setStatus(RUNNING);
		while (!shouldTerminate() && !bf.getSearchTree().isOpenEmpty()) {
			((KStarPathGraph)pathGraph()).prepareForSpaceExpansion();
			resumeAStar();
			/* < ForDebugging > 
			if(targetTreeEdge!=null) {
				Trace trace = bf.getSearchTree().backtrack(targetTreeEdge.source());
				System.out.println(trace);
			}
			System.out.println("Run Dijkstra");
			/* </ ForDebugging > */
			if(shouldTerminate()) break;
			boolean isDijkstraMaintained = tryToMaintainDijkstraSearch();
			if(!isDijkstraMaintained) {
				if(getConfig().logLevel>=Config.ALG_LOG_DETAILED) log("Dijkstra was not maintained, resume A*.");
				continue;
			}
			setChanged();
			notifyObservers();
			report();
			
			/* < ForDebugging > 
			System.out.println("Run Dijkstra");
			/* </ ForDebugging > */
			if(getConfig().logLevel>= Config.ALG_LOG_NORMAL) {
				log("Dijkstra is resumed (global iteration "+numGlobalIteration+")");
			}
			while (!shouldTerminate() && shouldContinueDijkstra() && !searchTree.isOpenEmpty()) {
				demandForResume();
				doOneIteration();
				iterationDone();
			}
			if(getConfig().logLevel>= Config.ALG_LOG_NORMAL) {
				log("Dijkstra is suspended (global iteration "+numGlobalIteration+")");
			}
			// ForDebugging
//			System.out.println("Dijkstra iterations: " + dIter
//					+ " (sol. value = " + solutionCollector.getSolutionValue()
//					+ ", sol. size=" + solutionCollector.getSolutionSize()
//					+ ")");
//			firstGlobalIteration = false;
			numGlobalIteration++;
		}
	}
	
	/** This method realizes the scheduling mechanism between A*
	 * and Dijkstra's search (see the paper for more details). 
	 * @return true if Dijkstra can be resumed or false if A* should 
	 * be resumed. 
	 * @throws Exception
	 */
	protected boolean shouldContinueDijkstra() throws Exception {
		SearchMark vMark = bf.getSearchTree().getOptimalOpen();
		if (vMark == null) {
			// System.out.println(", A* optimal: NULL");
			return true;
		}
		SearchMark mMark = searchTree.getOptimalOpen();
		if (mMark == null) {
			// System.out.println("Dijkstra optimal: NULL");
			return false;
		}
		if(solutionCollector.getNumTraces() + searchTree.numOpenVertices() >= getConfig().k) {
			if(getConfig().logLevel>=Config.ALG_LOG_DETAILED) {
				log("The scheduler allows Dijkstra to continue: \n" +
						"Including open vertices, we will have enough traces.");
			}
			return true;
		}
		Vertex m = mMark.vertex();
		double max = mMark.f();
		Iterator<? extends DirectedEdge> iter = getOutgoingEdges(m);
		while(iter.hasNext()) {
			DirectedEdge mn = iter.next();
			Vertex n = mn.target();
			SearchMark nMark = createSearchMark(n);
			double f = evaluationFunction.evaluate(mMark, mn, nMark);
			/* < ForDebugging > 
			if(((USRoadNode)((PNode)n).edge().target()).getId()==55310) {
				System.out.println(n+" "+f);
			}
			 /* </ ForDebugging > */
			if(max==-1 || bf.comparator.compare(max, f)<0) max = f;
		}
		double f1 = max;
		dijkstraNextValue = max;
		// System.out.print("Dijkstra optimal: "+f1);

		double f2 = vMark.f();
		// System.out.println(", A* optimal: "+f2);
		boolean b = bf.getComparator().compare(f1, f2) <= 0;
		if(!b && getConfig().logLevel>=Config.ALG_LOG_DETAILED) {
			log("The scheduler does not allow Dijkstra to continue: \n"+
					"Head of A* f = "+f2+", the node to be explored next by Dijkstra f = "+f1);
		}
		return b;
	}
	
	
//	protected boolean shouldSuspendAStar() {
//		return false;
//	}
	
	
//	protected boolean shouldSuspendAStar() {
//		SearchMark vMark = bf.getSearchTree().getOptimalOpen();
//		if (vMark == null) {
//			// System.out.println(", A* optimal: NULL");
//			return true;
//		}
//		double f = vMark.f();
//		boolean b = bf.getComparator().compare(dijkstraNextValue, f) <= 0;
//		return b;
//	}
	
	protected void resumeAStar() throws Exception {
		boolean firstRun = true;
		if(bf.getSearchTree().numClosedVertices()>0) {
			firstRun = false;
		}
		int numVerticesLimit = Integer.MAX_VALUE;
		int numEdgesLimit= Integer.MAX_VALUE;
		int numVertices = bf.getExploredGraph().numVertices();
		int numEdges=  pathGraph().getNumSidetrackEdges() + numVertices - 1;
		assert numVertices>0 || numEdges==0;
		if(!firstRun) {
			numVerticesLimit = (int)(getConfig().cxIncrementRatio * numVertices);
			numEdgesLimit = (int)(getConfig().cxIncrementRatio * numEdges);
		}
		boolean shouldSuspendAStar = false; 
//		System.out.println("Run A*");
		while (!shouldSuspendAStar && !shouldTerminate() && !bf.getSearchTree().isOpenEmpty()) {
			demandForResume();
			bf.doOneIteration();
			numVertices = bf.getSearchTree().numVertices();
			numEdges =  pathGraph().getNumSidetrackEdges() + numVertices - 1;
			shouldSuspendAStar = false; 
			shouldSuspendAStar = shouldSuspendAStar || numEdges >= numEdgesLimit;
			shouldSuspendAStar = shouldSuspendAStar || numVertices >= numVerticesLimit;
			if (firstRun && targetTreeEdge != null) {
				// When the first Target is found, suspend A* and run
				// Dijkstra on P(G)
				shouldSuspendAStar = true;
			}
//			else {
//				shouldSuspendAStar = shouldSuspendAStar();
//			}
			if(shouldSuspendAStar && getConfig().logLevel>= Config.ALG_LOG_NORMAL) {
				log("A* is suspended (global iteration "+numGlobalIteration+")");
			}
			if (getConfig().isInStepByStepModus) {
				setStatus(PAUSED);
				notifyObservers();
			}
			iterationDone();
		}
	}
	
	/** This methods is called after A* extended the path graph. It 
	 * tries to bring Dijkstra's search in a consistent status. It 
	 * establishes the path graph and explores those nodes, which 
	 * are added into the path graph after their parent nodes have 
	 * been expanded. 
	 * @returns true if all new nodes, the parents of which had been expanded, 
	 *  have been explored (added into the search queue). If any any of 
	 *  these new nodes can not be explored because the scheduling mechanism 
	 *  does not allow that, then the method returns false.
	 */
	protected boolean tryToMaintainDijkstraSearch() throws Exception {
		if (isVirginDijkstra) {
			if(getConfig().logLevel>=Config.ALG_LOG_NORMAL) {
				log("Initialize Dijkstra search");
			}
			pathGraph().establish();
			if (targetTreeEdge != null) {
				PNode root = pathGraph().getRoot();
				start = root;
				arrangeGraph();
			}
			isVirginDijkstra = false;
			return true;
		}
		else {
			boolean maintained = true; 
			pathGraph().establish();
			HashSet<SearchMark> completed = new HashSet<SearchMark>();
			for(SearchMark mark: maybeIncompleteExpansion) {
				int status = tryToCatchUpNewChildren(mark);
				switch(status) {
				case COMPLETE:
					completed.add(mark);
					break;
				case PASSED:
					break;
				case NOT_PASSED:
					maintained = false;
					break;
				}
				if(!maintained) break;
			}
			maybeIncompleteExpansion.removeAll(completed);
			/* < ForDebugging > 
			boolean b = checkDijkstraCorrectness();
			/* Sometimes b is false because of nodes with the same
			 * delta-values on different levels in the heap may swap 
			 * their levels. This causes that some path graph edges, 
			 * which are explored by Dijkstra, don't exist any more.  
			 * However, this does not harm the correctness. */
			/* assert b;
			 /* </ ForDebugging > */
			return maintained;
		}
	}
	
	
	protected int tryToCatchUpNewChildren(SearchMark mark) throws Exception {
		PNode node = (PNode)mark.vertex();
		pathGraph().correctReference(node);
		boolean isComplete = pathGraph().isComplete(node);
		Iterator<? extends DirectedEdge> nOutEdges = getOutgoingEdges(node);
		while(nOutEdges.hasNext()) {
			DirectedEdge e = nOutEdges.next();
			PNode child = (PNode) e.target();
			// Has child been explored before?
//			TreeHeapNode n; 
//			if(child.getClass() == PathGraph.HeapInPNode.class) {
//				n = ((PathGraph.HeapInPNode)child).node;
//			}
//			else {
//				assert child.getClass() == PathGraph.HeapTPNode.class;
//				n = ((PathGraph.HeapTPNode)child).node;
//			}
			PathGraphEntry cEntry = new PathGraphEntry(child.edge(),child.getHeapOwner());
			boolean wasExplored = visitedHeapNodes.contains(cEntry);
			// If not, then explore it now if possible, i.e.
			// if f(child) is not worse than the head of A* queue.
			if(!wasExplored) {
				SearchMark aMark = bf.getSearchTree().getOptimalOpen();
				if(aMark != null) {
					SearchMark childMark = createSearchMark(child);
					double f1 = evaluationFunction.evaluate(mark, e, childMark);
					double f2 = aMark.f();
					if(bf.getComparator().compare(f1, f2)>0) {
						if(getConfig().logLevel>=Config.ALG_LOG_DETAILED) {
							log("The scheduler does not allow a child to be catched up: \n"+
									"Head of Dijkstra f="+f1+", head of A* f="+f2);
							log("The scheduler does not allow a child to be catched up: \n"+
									"Head of A* f = "+f2+", Dijkstra f of child = "+f1);
						}
						return NOT_PASSED;
					}
				}
				RelaxationInfo relaxInfo = relax(mark, e, child);
				handleRelaxation(relaxInfo);
				// ForDebugging
//				System.out.println("Catch Up: "+e);
			}
		}
		if(isComplete) return COMPLETE;
		else return PASSED;
	}
	
	@Override
	protected void vertexToExpand(SearchMark vMark) throws Exception {
		PNode node = (PNode)vMark.vertex();
		pathGraph().correctReference(node);
		super.vertexToExpand(vMark);
	}
	
	
	protected void vertexExpanded(SearchMark vMark) throws Exception {
		PNode node = (PNode)vMark.vertex();
		if(!pathGraph().isComplete(node)) maybeIncompleteExpansion.add(vMark);
		super.vertexExpanded(vMark);
	}
	
	protected void handleRelaxation(RelaxationInfo relax) throws Exception {
		PNode node = (PNode)relax.getNewMark().vertex();
		visitedHeapNodes.add(new PathGraphEntry(node.edge(), node.getHeapOwner()));
		super.handleRelaxation(relax);
	}
	
	@Override
	protected PathGraph createPathGraph() {
		return new KStarPathGraph(this);
	}

	private KStarPathGraph pathGraph() {
		return (KStarPathGraph) graph;
	}
	
	/** This methods is used for debugging. It checks the consistency of 
	 * Dijkstra's search after the path graph was incremented and 
	 * re-established. Concretely, it checks the all explored edges of 
	 * the path graph still exist after the update. For debugging this 
	 * method is used in an assertion just before resuming Dijkstra. 
	 */
	boolean checkDijkstraCorrectness() {
//		Iterator<? extends Vertex> iter = searchTree.getOpenVertices();
//		boolean b = true;
//		while(iter.hasNext()) {
//			PNode n = (PNode) iter.next();
//			b = b && checkDijkstraCorrectness(n);
//			assert b;
//		}
//		return true;
		Iterator<? extends DirectedEdge> iter = exploredGraph.edges();
		while(iter.hasNext()) {
			DirectedEdge e = iter.next();
			boolean b = pathGraph().checkEdgeValidity((PNode)e.source(), (PNode)e.target());
			if(!b) {
				System.out.println("The explored edge "+e+" is invalid.");
				return false;
			}
		}
		return true;
	}

//	private boolean checkDijkstraCorrectness(PNode n) {
//		SearchMark mark = searchTree.isExplored(n);
//		LinkedList<SearchMark> path = new LinkedList<SearchMark>();
//		SearchMark cursor = mark; 
//		while(cursor != null) {
//			path.addFirst(cursor);
//			DirectedEdge e = searchTree.getTreeEdge(cursor);
//			cursor = e!=null? searchTree.isExplored(e.source()) : null;
//			assert e==null || cursor!=null;
//		}
//		assert !path.isEmpty();
//		boolean b = checkPathValidity(path);
//		assert b;
//		return true;
//	}
//
//	private boolean checkPathValidity(LinkedList<SearchMark> path) {
//		if(path.isEmpty()) return true;
//		Iterator<SearchMark> iter = path.iterator();
//		SearchMark parent = null;
//		while(iter.hasNext()) {
//			SearchMark mark = iter.next();
//			if(parent==null) {
//				assert mark.vertex() == pathGraph().getRoot();
//			}
//			else {
//				boolean b = checkEdgeValidity((PNode)parent.v, (PNode)mark.v);
//				assert b; 
//			}
//			parent = mark;
//		}
//		return true;
//	}
//
//	private boolean checkEdgeValidity(PNode n1, PNode n2) {
//		boolean b = pathGraph().checkEdgeValidity(n1,n2);
//		assert b;
//		return true;
//	}
	
	private class PathGraphEntry {
		DirectedEdge e; 
		Vertex heapOwner;
		
		PathGraphEntry(DirectedEdge e, Vertex v) {
			this.e = e; 
			this.heapOwner = v;
		}

		@Override
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + ((e == null) ? 0 : e.hashCode());
			result = PRIME * result + ((heapOwner == null) ? 0 : heapOwner.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final PathGraphEntry other = (PathGraphEntry) obj;
			if (e == null) {
				if (other.e != null)
					return false;
			} else if (!e.equals(other.e))
				return false;
			if (heapOwner == null) {
				if (other.heapOwner != null)
					return false;
			} else if (!heapOwner.equals(other.heapOwner))
				return false;
			return true;
		}
		
		public String toString() {
			return "Entry "+e.toString()+" from heap("+heapOwner+")";
		}
	}
}

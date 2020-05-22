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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dipro.alg.BF.RelaxationInfo;
import dipro.graph.DefaultDirectedEdge;
import dipro.graph.DefaultVertex;
import dipro.graph.DirectedEdge;
import dipro.graph.Edge;
import dipro.graph.Vertex;
import dipro.run.Registry;
import dipro.util.TreeHeapNode;

public class PathGraph extends AbstractPathGraph {

	private final RootNode STAR = new RootNode();
	protected HashMap<Vertex, IndexEntry> indexTable;
	protected HashMap<Vertex, EppsteinHeapT> heapTIndexTable;
	protected RootNode root;
	protected DeltaComparator deltaComparator;
	protected DeltaComparatorHeapT deltaComparatorHeapT;
	private int pNodeIdsCounter;
	protected boolean isEstablished;
	protected int numSidetrackEdges;
	protected int heapTMemory; 
	
	PathGraph(KSPAlgorithm kSearch) {
		super(kSearch);
		indexTable = new HashMap<Vertex, IndexEntry>();
		heapTIndexTable = new HashMap<Vertex, EppsteinHeapT>();
		root = null;
		deltaComparator = new DeltaComparator();
		deltaComparatorHeapT = new DeltaComparatorHeapT();
		pNodeIdsCounter = 1;
		isEstablished = false;
		numSidetrackEdges = 0;
		heapTMemory = 0;
	}
	
	/** This method computes the delta value of an edge based 
	 * on the registered g-value of the source vertex and the 
	 * current g-value of the target vertex.
	 * @param uv
	 * @return
	 * @throws Exception
	 */
	protected double delta(DirectedEdge uv) throws Exception {
		Vertex u = uv.source();
		Vertex v = uv.target();
		IndexEntry uEntry = indexTable.get(u);
		assert uEntry != null;
		double uG = uEntry.g;
		/* < ForDebugging >
		double ugg = kSearch.getBasicSearchAlgorithm().g(u);
		if(ugg!=uG) {
			System.out.println("Should it be?");
		}
		/* </ ForDebugging > */
		double vG =v==kSearch.TARGET? kSearch.getBasicSearchAlgorithm().getBestTraceValue(): 
			kSearch.getBasicSearchAlgorithm().g(v);
		double d = kSearch.delta(uv, uG, vG);
		/* <ForDebugging> 
		if(d != kSearch.delta(uv)) {
			System.out.println("Delta 1="+d+", delta 2 = "+kSearch.delta(uv));
		}
		/* <ForDebugging> */
		return d;
	}
	
	public void recordStartVertex(Vertex startVertex) {
		assert indexTable.isEmpty();
		IndexEntry entry = new IndexEntry();
		entry.g = kSearch.getBasicSearchAlgorithm().g(startVertex);
		entry.treeEdge = null;
		entry.heapIn = null;
		indexTable.put(startVertex, entry);
	}
	
	public void recordEdge(Vertex u, DirectedEdge uv, Vertex v,
			int relaxationFlag) throws Exception {
		assert !isEstablished;
		/* < ForDebugging > 
		if((v instanceof USRoadNode) && ((USRoadNode)v).getId()==55310) {
			System.out.println("Working in Heap_in of "+v);
		}
		/* </ ForDebugging > */
		IndexEntry uEntry = indexTable.get(u);
		assert uEntry != null;
		uEntry.g = kSearch.getBasicSearchAlgorithm().g(u);
		IndexEntry vEntry = indexTable.get(v);
		if(vEntry == null) {
			/* v is a fresh vertex. */
			assert relaxationFlag == RelaxationInfo.NEW_VERTEX;
			/* Then, (u,v) must be the tree edge of v */
			vEntry = new IndexEntry();
			vEntry.g = v==kSearch.TARGET? kSearch.getBasicSearchAlgorithm().getBestTraceValue(): 
						kSearch.getBasicSearchAlgorithm().g(v);
			vEntry.treeEdge = uv;
			vEntry.heapIn = null;
			indexTable.put(v, vEntry);
		} 
		else {
			/* v is an already known vertex. */ 
			assert relaxationFlag == RelaxationInfo.CLOSED_VERTEX 
				|| relaxationFlag == RelaxationInfo.OPEN_VERTEX
				|| relaxationFlag == RelaxationInfo.CLOSED_VERTEX_RELAXED
				|| relaxationFlag == RelaxationInfo.OPEN_VERTEX_RELAXED;
//			vEntry.g = v==kSearch.TARGET? kSearch.getBasicSearchAlgorithm().getBestTraceValue(): 
//				kSearch.getBasicSearchAlgorithm().g(v);
			DirectedEdge vOldTreeEdge = vEntry.treeEdge;
			boolean uvIsTreeEdge = kSearch.isTreeEdge(uv);
			if(uvIsTreeEdge) {
				/* In this case (u,v) is the current tree edge of v. */
				/* Notice that this case can not occur if v is the start
				 * vertex. Hence, vOldTreeEdge must be different of null.
				 */
				assert vOldTreeEdge != null;
				/* A tree edge causes no detour. */
				/* < ForDebugging > 
				if((kSearch.isMultiplicative() && delta(uv) != 1.0d) ||  (!kSearch.isMultiplicative() && delta(uv) != 0.0d)) {
					System.out.println("Tree edge "+uv+", delta value "+delta(uv));
				}
				/* </ ForDebugging > */
				assert kSearch.isMultiplicative()? delta(uv) == 1.0d : delta(uv) == 0.0d;

				/* First, (u,v) is set as a tree edge of v. */
				vEntry.treeEdge = uv;
				/* (u,v) can be identical with the old tree edge. This can happen,  
				 * for instance, when u has been reopened. In this case we do 
				 * not need to do anything. 
				 * However, we have to handel the case when (u,v) is a new tree 
				 * edge of v. 
				 */
				if( !uv.equals(vOldTreeEdge)) {
					/* < ForDebugging > 
					System.out.println("The new tree edge of "+v+" is now "+uv);
					System.out.println("The old tree edge was "+vOldTreeEdge);
					/* </ ForDebugging > */
					
					/* It could be that (u,v) was a sidetrack edge before. 
					 * In such a case, (u,v) exists in the H_in(v).   
					 */
					boolean b = vEntry.heapIn != null && vEntry.heapIn.refresh(uv);
					if(b) {
						/* < ForDebugging > 
						System.out.println(uv+" was a sidetrack edge before. It will be removed from heapIn of "+v);
						/* </ ForDebugging > */
						/* In this case, (u,v) is already in H_in(v). The call 
						 * vEntry.heapIn.refresh(uv) ensures that it is at the right 
						 * position in the heap H_in(v).
						 */
						 /* Since (u,v) is now the tree edge of v, it must be the top element 
						  * in H_in(v).
						  */
						assert vEntry.heapIn.peek().equals(uv);
						/* Then we remove the top element. */ 
						vEntry.heapIn.remove();
						/* Notice that vOldTreeEdge will be added into H_in(v) and the 
						 * variable numSidetrackEdges will be incremented. So its value 
						 * will stay the same at the end.  
						 */
						numSidetrackEdges--;
//						System.out.println(uv+"has been removed from heapIn("+uv.target()+")");
					}
					/* < ForDebugging > 
					System.out.println("The old tree edge is added into the tree heap of "+v);
					/* </ ForDebugging > */
					/* Now we add the old tree edge of v, if exists, into H_in(v). */
					/* < ForDebugging >
					if( delta(vOldTreeEdge) <= 0.0d) {
						System.out.println("delta of old tree edge = "+delta(vOldTreeEdge)+", delta of new tree edge = "+delta(uv));
					}
					/* </ ForDebugging > */
					/* vOldTreeEdge is not a tree edge any more. Thus, it must cause 
					 * some detour.
					 */
					assert kSearch.isMultiplicative()? delta(vOldTreeEdge) < 1.0d : delta(vOldTreeEdge) > 0.0d;
					if(vEntry.heapIn == null) vEntry.heapIn = new EppsteinHeapIn(deltaComparator);
					vEntry.heapIn.add(vOldTreeEdge);
					numSidetrackEdges++;
				}
			} else {
				/* In this case (u,v) is just a normal sidetrack edge. It must'nt be equal 
				 * to the tree edge of v. 
				 */
				assert !uv.equals(vOldTreeEdge);
				boolean b = (vEntry.heapIn != null) && vEntry.heapIn.refresh(uv);
				/* If b is true, then (u,v) is already in H_in(v). The call vEntry.heapIn.refresh(uv) 
				 * guarantees that it is now at the right position in H_in(v). We don't need to do 
				 * any thing more. 
				 */
				if(!b) {
					/* In this case (u,v) is definitely a new sidetrack edge which should be added into H_in(v). */
					if(vEntry.heapIn == null) vEntry.heapIn = new EppsteinHeapIn(deltaComparator);
					vEntry.heapIn.add(uv);
					numSidetrackEdges++;
				} 
			}
		}
	}

	public PNode getRoot() {
		if(!isEstablished) throw new IllegalStateException("The path graph is in incrementation modus (not established).");
		if(root == null) throw new IllegalStateException("The root of the path graph has not been initialized yet.");
		return root;
	}
	
	public DirectedEdge getCurrentTreeEdge(Vertex v) {
		IndexEntry vEntry = indexTable.get(v);
		if(vEntry==null) return null;
		return vEntry.treeEdge;
	}
	
	public void establish() {
		assert !isEstablished; 
		isEstablished = true;
		if(indexTable.get(kSearch.TARGET)==null) {
//			throw new IllegalStateException("Target vertex has not been reached yet.");
			root = null;
		}
		else {
			root = STAR;
		}
	}

	public Iterator<? extends DirectedEdge> outgoingEdges(Vertex v) {
		PNode n = (PNode) v;
		return n.getOutgoingEdges().iterator();
	}

//	protected List<DirectedEdge> getReferingEdges(DirectedEdge uv) {
//		PNode uvPNode = getPNodeOf(uv);
//		return uvPNode.getReferingEdges();
//	}
	
	public Vertex getHeapOwner(PNode pNode) {
		return pNode.getHeapOwner();
	}
	
	public boolean isCrossEdge(DirectedEdge xy) {
		assert xy instanceof PEdge;
		return xy instanceof CrossEdge;
	}

	protected void buildHeapT(Vertex v) {
		assert heapTIndexTable.get(v) == null;
		EppsteinHeapT  vHeapT;
		if(v.equals(kSearch.getBasicSearchAlgorithm().getStartVertex())) {
			vHeapT = new EppsteinHeapT(v, deltaComparatorHeapT);
		} 
		else {
			DirectedEdge uv = kSearch.getBasicSearchAlgorithm()
					.getSearchTree().getTreeEdge(v);
			if (uv == null) {
				assert v == kSearch.TARGET;
				uv = kSearch.targetTreeEdge;
			}
			Vertex u = uv.source();
			EppsteinHeapT uHeapT = heapTIndexTable.get(u);
			if (uHeapT == null) {
				buildHeapT(u);
				uHeapT = heapTIndexTable.get(u);
			}
			assert uHeapT != null;
			vHeapT = uHeapT.createEppsteinCopy(v);
		}
		vHeapT.setVertex(v);
		IndexEntry vEntry = indexTable.get(v);
		assert vEntry != null;
		if(vEntry.heapIn != null) {
			assert vEntry.heapIn.size() > 0; 
			TreeHeapNode<DirectedEdge> vHeapInRoot = vEntry.heapIn.getRoot();
			/* < ForDebugging > 
			if((v instanceof USRoadNode) && ((USRoadNode)v).getId()==55310) {
				System.out.println("Root of H_in("+v+") is "+vHeapInRoot);
			}
			/* </ ForDebugging > */
			vHeapT.doPreservingAddition(vHeapInRoot);
			heapTMemory = heapTMemory + vHeapT.getNumNewNodes() * (3*4);
		}
		heapTIndexTable.put(v, vHeapT);
		/* < ForDebugging > 
		if(v == kSearch.TARGET) {
			System.out.println("Tree heap of "+v);
			System.out.println(vHeapT.printInLevels());
		}
		/* </ ForDebugging > */
	}

	@Override
	public int vertexSize() throws Exception {
		return 4;
	}

	public int getNumSidetrackEdges() {
		return numSidetrackEdges;
	}

	@Override
	public float weight(Edge e) throws Exception {
		assert e instanceof PEdge;
		return ((PEdge)e).weight();
	}
	
	public int getMemory() {
		int memory = 0;
		memory = memory + heapTIndexTable.size() * (4+4);
		memory = memory + indexTable.size() * (4+2*4+8); 
		int heapInMemory = numSidetrackEdges * 4;
		memory = memory + heapInMemory;
		memory = memory + heapTMemory;
		return memory;
	}
	

	
//	protected boolean isRootOfTARGETHeapT(DirectedEdge uv){
//		EppsteinHeapT<TreeHeapNode<DirectedEdge>> targetHeapT = heapTIndexTable.get(kSearch.TARGET);
//		boolean result = false;
//		if(targetHeapT != null) {
//			TreeHeapNode<TreeHeapNode<DirectedEdge>> root = targetHeapT.getRoot();
//			if(root != null) result = root.getElement().getElement().equals(uv);
//		}
//		return result;
//	}
	
//	boolean checkValidity(PNode node) {
//		boolean b = true;
//		if(node instanceof RootNode) {
//			b = node.edge() == null;
//			assert b; 
//		}
//		else {
//			if(node instanceof HeapTPNode) {
//				HeapTPNode n = (HeapTPNode)node;
//				Vertex vertex = n.node.getVertex();
//				HeapT heapT = heapTIndexTable.get(vertex);
//				assert heapT != null;
//				b = heapT.checkIndexValidity(n.node);
//				assert b;
//			}
//			else {
//				if(node instanceof HeapInPNode) {
//					HeapInPNode n = (HeapInPNode)node;
//					Heap<DirectedEdge> heapIn = whichHeapIn(n.node);
//					assert heapIn != null;
//					b = heapIn.checkIndexValidity(n.node);
//					assert b;
//				}
//			}
//		}
//		return b;
//	}
	
	protected class IndexEntry {
		double g;
		DirectedEdge treeEdge; 
		EppsteinHeapIn heapIn;
	}
		
	public abstract class PNode extends DefaultVertex {

		public PNode(int id) {
			super(id);
		}

		protected PNode getCrossChild() {
			Vertex u = edge().source();
			EppsteinHeapT uHeapT = heapTIndexTable.get(u);
			if (uHeapT == null) {
				buildHeapT(u);
				uHeapT = heapTIndexTable.get(u);
			}
			assert uHeapT != null;
			HeapTPNode c;
			if (uHeapT.size() == 0) {
				// All vertices on the T path to u do not
				// have any incomming sidetrack edges
				c = null;
			} else {
				HeapTNode uHeapTRoot = (HeapTNode) uHeapT.getRoot();
				c = new HeapTPNode(uHeapTRoot, u);
			}
			// System.out.println("Cross Child of "+this+" is "+c);
			return c;
		}
		
//		public String toString() {
//			return edge().toString();
//		}

		public abstract DirectedEdge edge();

		protected abstract List<PEdge> getOutgoingEdges();
		protected abstract boolean isComplete();
		protected abstract Vertex getHeapOwner();
	}
	

	protected class RootNode extends PNode {
		 
		private RootNode() {
			super(0);
		}

		@Override
		public DirectedEdge edge() {
			return null;
		}

		protected PNode getCrossChild() {
			Vertex t = kSearch.TARGET;
			EppsteinHeapT tHeapT = heapTIndexTable.get(t);
			if (tHeapT == null) {
				buildHeapT(t);
				tHeapT = heapTIndexTable.get(t);
			}
			assert tHeapT != null;
			if(tHeapT.size() == 0 ) return null;
			HeapTNode tHeapTRoot = (HeapTNode) tHeapT.getRoot();
			return new HeapTPNode(tHeapTRoot, t);
		}
		
		@Override
		protected List<PEdge> getOutgoingEdges() {
			LinkedList<PEdge> edges = new LinkedList<PEdge>();
			HeapTPNode child = (HeapTPNode) getCrossChild();
			if(child != null) edges.add(new CrossEdge(this, child));
			return edges;
		}
		
		protected boolean isComplete() {
			EppsteinHeapT tHeapT = heapTIndexTable.get(kSearch.TARGET);
			assert tHeapT != null;
			if(tHeapT.size() == 0 ) return false;
			assert tHeapT.getRoot() != null;
			return true;
		}
		
		public String toString() {
			return "*";
		}

		@Override
		protected Vertex getHeapOwner() {
			return null;
		}
	}
	
	protected class HeapInPNode extends PNode {

		HeapInNode node;

		HeapInPNode(HeapInNode node) {
			super(pNodeIdsCounter);
			pNodeIdsCounter++;
			assert node != null;
			this.node = node;
		}

		@Override
		public DirectedEdge edge() {
			return node.getElement();
		}

		protected List<PEdge> getOutgoingEdges() {
			LinkedList<PEdge> edges = new LinkedList<PEdge>();
			HeapTPNode c = (HeapTPNode) getCrossChild();
			if (c != null)
				edges.add(new CrossEdge(this, c));
//			TreeHeap<DirectedEdge> heapIn = whichHeapIn(node);
			HeapInNode l = (HeapInNode) node.getLeft();
			HeapInNode r = (HeapInNode) node.getRight();
			if (l != null)
				edges.add(new HeapEdge(this, new HeapInPNode(l)));
			if (r != null)
				edges.add(new HeapEdge(this, new HeapInPNode(r)));
			return edges;
		}

		@Override
		protected boolean isComplete() {
			HeapTPNode c = (HeapTPNode) getCrossChild();
			if(c==null) return false;
			if(node.getLeft()==null) return false;
			if(node.getRight()==null) return false;
			return true;
		}
		
		protected void refreshReference(HeapInNode newNodeRef) {
			assert newNodeRef.getElement().equals(node.getElement());
			node = newNodeRef;
		}

		@Override
		protected Vertex getHeapOwner() {
			return edge().target();
		}
		
		public String toString() {
			return node.toString();
		}

	}

	protected class HeapTPNode extends PNode {
		HeapTNode node;
		Vertex heapOwner; 
		
		public HeapTPNode(HeapTNode node, Vertex heapOwner) {
			super(pNodeIdsCounter);
			pNodeIdsCounter++;
			assert node!=null;
			this.node = node;
			this.heapOwner = heapOwner;
		}

		protected List<PEdge> getOutgoingEdges() {
			LinkedList<PEdge> edges = new LinkedList<PEdge>();
			HeapTPNode c = (HeapTPNode) getCrossChild();
			// assert c!=null;
			if (c != null)
				edges.add(new CrossEdge(this, c));
//			HeapT heapT = heapTIndexTable.get(node.getVertex());
//			assert heapT != null;
			HeapTNode l = (HeapTNode) node.getLeft();
			HeapTNode r = (HeapTNode) node.getRight();
			if (l != null)
				edges.add(new HeapEdge(this, new HeapTPNode(l, heapOwner)));
			if (r != null)
				edges.add(new HeapEdge(this, new HeapTPNode(r, heapOwner)));
			HeapInNode heapInRoot = (HeapInNode) node.getElement();
			HeapInNode heapInLeft = (HeapInNode) heapInRoot.getLeft();
			HeapInNode heapInRight = (HeapInNode) heapInRoot.getRight();
			if(heapInLeft!=null) 
				edges.add(new HeapEdge(this, new HeapInPNode(heapInLeft)));
//			if(heapInRight!=null) 
//				edges.add(new HeapEdge(this, new HeapInPNode(heapInRight)));
			assert heapInRight==null;
			return edges;
		}

		@Override
		public DirectedEdge edge() {
			return node.getElement().getElement();
		}

		@Override
		protected boolean isComplete() {
			if(getCrossChild()==null) return false;
			if(node.getLeft()==null) return false;
			if(node.getRight()==null) return false;
			HeapInNode heapInRoot = (HeapInNode) node.getElement();
			if(heapInRoot.getLeft()==null) return false;
			return true;
		}

		protected void refreshReference(HeapTNode newNodeRef) {
			assert newNodeRef.getElement().getElement().equals(node.getElement().getElement());
			node = newNodeRef;
		}

		@Override
		protected Vertex getHeapOwner() {
			return heapOwner;
		}
		
		public String toString() {
			return node.toString();
		}
	}
	
	private class DeltaComparatorHeapT implements Comparator<TreeHeapNode<DirectedEdge>> {
		
		public int compare(TreeHeapNode<DirectedEdge> o1, TreeHeapNode<DirectedEdge> o2) {
			return deltaComparator.compare(o1.getElement(), o2.getElement());
		}
	}

	private class DeltaComparator implements Comparator<DirectedEdge> {
		public int compare(DirectedEdge o1, DirectedEdge o2) {
			try {
				return kSearch.getComparator().compare(delta(o1),delta(o2));
			} catch (Exception e) {
				Registry.getMain().handleError("Error while comparing delta values!", e);
			}
			return 0;
		}
	}
	
	private abstract class PEdge extends DefaultDirectedEdge {

		PEdge(PNode n1, PNode n2) {
			super(n1, n2);
		}
		public abstract float weight() throws Exception; 
		
		abstract boolean isCrossEdge();
	}

	private class HeapEdge extends PEdge {

		public HeapEdge(PNode n1, PNode n2) {
			super(n1, n2);
		}

		boolean isCrossEdge() {
			return false;
		}

		public String toString() {
			return "heap" + super.toString();
		}

		@Override
		public float weight() throws Exception {
			double d1 = delta(((PNode)source).edge());
			double d2 = delta(((PNode)target).edge());
			double d; 
			if(kSearch.isMultiplicative()) {
				d = d2 / d1;
			}
			else {
				d = d2 - d1; 
			}
			assert d >= 0; 
			return (float)d;
		}
	}

	private class CrossEdge extends PEdge {

		public CrossEdge(PNode n1, HeapTPNode n2) {
			super(n1, n2);
		}

		boolean isCrossEdge() {
			return true;
		}

		public String toString() {
			return "cross" + super.toString();
		}

		@Override
		public float weight() throws Exception {
			double d = delta(((PNode)target).edge());
			assert d >= 0; 
			return (float)d;
		}
	}
}

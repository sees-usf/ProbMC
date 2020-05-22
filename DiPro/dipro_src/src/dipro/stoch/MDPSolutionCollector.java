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

package dipro.stoch;

import java.util.Iterator;

import prism.PrismException;
import dipro.alg.KSPAlgorithm;
import dipro.alg.BF.SearchMark;
import dipro.run.Config;
import dipro.stoch.prism.PrismUntil;
import dipro.util.AndOrTree;
import dipro.util.DiProException;
import dipro.util.SolutionCollector;
import dipro.util.SolutionTracesRecorder;
import dipro.util.Trace;

public class MDPSolutionCollector extends SolutionCollector {

	private SolutionTracesRecorder cxTraceRecorder;
	private AndOrTree tree;	
	
	public MDPSolutionCollector(KSPAlgorithm alg) throws Exception {
		super(alg);
		cxTraceRecorder = new SolutionTracesRecorder(alg.getContext().getSolutionFileName(),alg.getConfig().solutionTrace);
	}

	public void clear() throws Exception {
		tree = null;
//		traces.clear();
		super.clear();
	}

	private boolean shouldTerminate() throws PrismException, DiProException {
		double pr = tree.getSolutionProb();
		PrismUntil formula = property();
		boolean b = false;
		if (formula.isUpperBounded()) {
			double diff = formula.getProbBound() - pr;
			if (diff < alg.getConfig().pruneBound) {
				b = true;
			}
//			System.out.println("diff = "+diff);
			// String relOp = formula.pctlFormula().getRelOp();
			// // String str = pr+" >";
			// if (relOp.equals("<")) {
			// // str = str+"= ";
			// b = pr >= formula.getProbBound();
			// } else {
			// b = pr > formula.getProbBound();
			// }
			// str=str+formula.getProbBound()+" => "+b;
			// System.out.println(str);
		}
		// b = b || computeUsedMemory()> 104857600;
		return b;
	}

	private PrismUntil property() {
		return (PrismUntil) ((KSPAlgorithm) alg).getBasicSearchAlgorithm()
				.getProperty();
	}

	@Override
	public void commit() throws Exception {
		Iterator<Trace> cxTracesIter = tree.constructSolution();
		while(cxTracesIter.hasNext()) {
			cxTraceRecorder.record(cxTracesIter.next());
		}
	}

	public AndOrTree getTree() {
		return tree;
	}

	@Override
	protected void processTrace(SearchMark targetMark) throws Exception {
		Trace trace = ((KSPAlgorithm) alg).constructTrace(targetMark);
		if (trace.length() - 1 > ((KSPAlgorithm) alg).getMaxDepthBound()) {
			// System.out.println("Ignore trace of length "+trace.length());
			return;
		}
//		traces.addLast(trace);
		// System.out.println("Store trace of length "+trace.length());
		if (tree == null) {
			// PrismUntil formula = property();
			// boolean max = formula.isMaxProb();
			// tree = new AndOrTree(max);
			tree = new AndOrTree(true);
		}
		// System.out.println("New path: "+targetMark.f());
		tree.add(trace, targetMark.f());
		// System.out.println("Trace added - prob= "+targetMark.f()+"\t
		// "+trace);
		// System.out.println("Solution prob = "+tree.getSolutionProb());
		boolean b = shouldTerminate();
		if (b) {
			if (getConfig().logLevel >= Config.ALG_LOG_NORMAL) {
				alg.log("Counterexample found, CX probability is enough= "+tree.getSolutionProb()+" <= "+property().getProbBound()+"\n"+
						"Thus, request termination.");
			}
			alg.requestTermination();
		}
	}

	protected Config getConfig() {
		return alg.getContext().getConfig();
	}
	
	public double getSolutionValue() {
		if (tree == null)
			return 0.0d;
		return tree.getSolutionProb();
	}

	public int getNumTraces() {
		if (tree == null)
			return 0;
		return tree.getNumSolutionTraces();
	}
	
	public int getNumAllTraces() {
		if (tree == null)
			return 0;
		return tree.getNumAllTraces();
	}

	public int computeUsedMemory() {
		if (tree == null)
			return 0;
		return tree.getMemorySize();
	}

	public int getSolutionSize() {
//		return -1;
		return getNumAllTraces();
	}

	@Override
	public int getNumSolutionEdges() {
		return -1;
	}

	@Override
	public int getNumSolutionVertices() {
		return -1;
	}

}

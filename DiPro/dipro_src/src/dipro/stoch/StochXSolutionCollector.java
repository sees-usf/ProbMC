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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import parser.ast.Expression;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import prism.Model;
import prism.Prism;
import prism.Result;
import dipro.alg.BF;
import dipro.alg.BF.SearchMark;
import dipro.graph.DirectedEdge;
import dipro.graph.State;
import dipro.graph.Vertex;
import dipro.run.AbstractPrismContext;
import dipro.run.Config;
import dipro.run.MRMCContext;
import dipro.run.Registry;
import dipro.run.VisMain;
import dipro.stoch.mrmc.MRMCModelChecker;
import dipro.stoch.prism.PrismModelChecker;
import dipro.stoch.prism.PrismState;
import dipro.stoch.prism.PrismTBoundedUntil;
import dipro.stoch.prism.PrismTransition;
import dipro.stoch.prism.PrismUntil;
import dipro.util.DiProException;
import dipro.util.Proposition;
import dipro.util.Safety;
import dipro.util.SearchTree;
import dipro.util.SolutionTracesRecorder;
import dipro.util.Trace;
import dipro.util.XSolutionCollector;

public class StochXSolutionCollector extends XSolutionCollector {

	protected int initId;
	protected double solutionProb;
	protected int sizeOnLastCheck;
	protected double sizeLimitForNextCheck;
	/** The ratio of CX grow required for the next model checking step. */
	protected double q;
	protected String solPropFileName;
	protected double probabilityUpperBound;
	protected boolean isStrictBound;
	protected State firstTarget;
	protected PrismModelChecker prismModelChecker;
	protected Hashtable<PrismState, Integer> prismStateIds;
	protected MRMCTransitionOrder order;
	protected MRMCModelChecker mrmcModelChecker;
	protected Hashtable<State, Integer> mrmcStateIds;
	private double oldSol;

	public StochXSolutionCollector(BF alg) throws Exception {
		super(alg);
		solutionProb = 0.0d;
		sizeOnLastCheck = 0;
		sizeLimitForNextCheck = 0.0;
		probabilityUpperBound = -1.0d;
		isStrictBound = false;
		firstTarget = null;
		prismModelChecker = null;
		q = getConfig().cxIncrementRatio;
		// q = 2.0;
		// prismModelChecker = new PrismModelChecker();
		// prismStateIds = new Hashtable<PrismState, Integer>();
		// order = new MRMCTransitionOrder();
		// mrmcStateIds = new Hashtable<State, Integer>();
		if (getConfig().mrmcsol || alg.getContext() instanceof MRMCContext) {
			order = new MRMCTransitionOrder();
			mrmcStateIds = new Hashtable<State, Integer>();
		} else {
			// boolean isCTMC = getModel() instanceof CTMC || getModel()
			// instanceof UniformCTMC;
			// String ext = isCTMC ? "csl" : "pctl";
			// solPropFileName = alg.getContext().getSolutionFileName() + "." +
			// ext;
			prismModelChecker = new PrismModelChecker();
			prismStateIds = new Hashtable<PrismState, Integer>();
		}
		cleanCXFiles();
	}

	protected void processTrace(SearchMark targetMark) throws Exception {
		if (firstTarget == null)
			firstTarget = (State) targetMark.vertex();
		super.processTrace(targetMark);
			Trace trace = constructSolTrace(targetMark);
			recordTrace(trace);
	}

	protected Trace constructSolTrace(BF.SearchMark mark) {
		Trace traceTmp = getSearchTree().backtrack(mark);
		this.sizeLimitForNextCheck = 0;
		try {
			this.modelCheckSolution(true);
		} catch (Exception e) {
			Registry.getMain().handleError(e.toString());
		}
		
		traceTmp.setSolutionValue(this.solutionProb-oldSol);
		
		this.oldSol = this.solutionProb;
		
		return traceTmp;
	}

	public void signalizeIterationDone() throws Exception {
		modelCheckSolutionIfNecessary(true);
	}

	protected void modelCheckSolutionIfNecessary(boolean isForIntermediateCheck)
			throws Exception {
		if (!isChanged()) {
			// ForDebugging
			// System.out.println("Solution was not change (states="+solutionGraph.numVertices()+", transitions="+solutionGraph.numEdges()+")");
			return;
		}
		int iter = alg.getNumIterations();
		int size = solutionGraph.numEdges();
		boolean shouldModelCheck = false;
		if (getConfig().mcsol > 0) {
			if (iter % getConfig().mcsol == 0)
				shouldModelCheck = true;
		}
		shouldModelCheck = shouldModelCheck || (size >= sizeLimitForNextCheck);
		
		//only do model checking here if not recording diagnostic paths
		if (shouldModelCheck 
				&& !(this.getSolutionTraceRecorderType() == SolutionTracesRecorder.DIAG_PATH 
						|| this.getSolutionTraceRecorderType() == SolutionTracesRecorder.CX_XML_FILE)) {
			// solPropFileName = null;	
			modelCheckSolution(isForIntermediateCheck);
			/* <ForDebugging> */
			// double p1 = solutionProb;
			// modelCheckSolution(!isForIntermediateCheck);
			// double p2 = solutionProb;
			// if(isForIntermediateCheck) {
			// System.out.println("Model Checking => "+p2+", Reduced Model Checking => "+p1);
			// } else {
			// System.out.println("Model Checking => "+p1+", Reduced Model Checking => "+p2);
			// }
			/* </ForDebugging> */
		}
	}

	public double getSolutionValue() {
		return solutionProb;
	}

	private boolean isChanged() {
		assert solutionGraph.numEdges() >= sizeOnLastCheck;
		boolean isChanged = solutionGraph.numEdges() > sizeOnLastCheck;
		return isChanged;
	}

	public void writeCounterexample(boolean isForIntermediateCheck)
			throws Exception {
		if (getConfig().mrmcsol || alg.getContext() instanceof MRMCContext)
			writeIntoMRMCFiles(isForIntermediateCheck);
		else
			writeIntoPrismFiles(isForIntermediateCheck);
	}

	public void commit() throws Exception {
		/*
		 * <ForDebugging> checkSolutionGraphValidity(); solPropFileName = null;
		 * modelCheckSolution(true); double p1 = solutionProb; /*</ForDebugging>
		 */
		modelCheckSolution(false);
		super.commit();
	}

	public void clear() throws Exception {
		if (solPropFileName != null) {
			(new File(solPropFileName)).delete();
		}
		super.clear();
	}

	public void cleanCXFiles() throws Exception {
		String stafName = alg.getContext().getSolutionFileName() + ".sta";
		String trafName = alg.getContext().getSolutionFileName() + ".tra";
		String labfName = alg.getContext().getSolutionFileName() + ".lab";
		(new File(stafName)).delete();
		(new File(trafName)).delete();
		(new File(labfName)).delete();
		// String sfName = alg.getContext().getSolutionFileName() +".s";
		// String tfName = alg.getContext().getSolutionFileName() +".t";
		// String lfName = alg.getContext().getSolutionFileName() +".l";
		// (new File(sfName)).delete();
		// (new File(tfName)).delete();
		// (new File(lfName)).delete();
	}

	protected void modelCheckSolution(boolean isForIntermediateCheck)
			throws Exception {
		if (probabilityUpperBound == -1.0d) {
			computeProbUpperBound();
		}
		// checkSolutionGraphValidity();
		// writeIntoMRMCFiles();
		// long t1 = System.currentTimeMillis();
		// double r1 = mrmcModelCheckSolution();
		// t1 = System.currentTimeMillis() - t1;
		// writeIntoPrismFiles();
		// long t2 = System.currentTimeMillis();
		// double r2 = prismModelCheckSolution(false);
		// t2 = System.currentTimeMillis() - t2;
		// long t3 = System.currentTimeMillis();
		// double r3 = prismModelCheckSolution(true);
		// t3 = System.currentTimeMillis() - t3;
		// System.out.println("CX Model Checking");
		// System.out.println("MRMC = "+r1+" (RT:"+t1+")");
		// System.out.println("PRISM ="+r2+"(RT:"+t2+")");
		// // System.out.println("PRISM (Sparse)="+r3+"(RT:"+t3+")");
		// double result = r2;
		if (Registry.getMain() instanceof VisMain && alg.getConfig().modelType != Config.MRMC_MODEL ) {
			assert solCardinalitiesTable != null;
			writeIntoPrismFiles(false);
			double result = prismModelCheckSolution(false);
			solutionProb = result;
			if (getProperty() instanceof StochTBoundedUntil) {
				double time = ((StochTBoundedUntil) getProperty()).timeBound();
				HashMap<PrismState, Double> pi = prismTransientAnalysis(time);
				Iterator<? extends Vertex> iter = solutionGraph.vertices();
				while (iter.hasNext()) {
					PrismState s = (PrismState) iter.next();
					if (((PrismUntil) getProperty()).check(s) == Safety.TRUE) {
						// System.out.print("Look up state "+s+" => ");
						double p = pi.get(s);
						// System.out.println("pi = "+p);
						solCardinalitiesTable.put(s, p);
					}
				}
			}
			maxSolutionCardinality = 0;
			for (Double c : solCardinalitiesTable.values()) {
				maxSolutionCardinality = Math.max(maxSolutionCardinality, c);
			}
		} else {
			writeCounterexample(isForIntermediateCheck);
			double result = modelCheckSolutionAux(isForIntermediateCheck);
			solutionProb = result;
		}
		sizeOnLastCheck = solutionGraph.numEdges();
		sizeLimitForNextCheck = q * solutionGraph.numEdges();
		boolean enough = false;
		if (isStrictBound)
			enough = enough || (solutionProb >= probabilityUpperBound);
		else
			enough = enough || (solutionProb > probabilityUpperBound);
		if (enough) {
			String comp = isStrictBound ? " < " : " <= ";
			if (getConfig().logLevel >= Config.ALG_LOG_NORMAL) {
				alg.log("Counterexample found, CX probability is enough= "
						+ solutionProb + comp + probabilityUpperBound + "\n"
						+ "Thus, request termination.");
			}
			alg.requestTermination();
		} else {
			if (getConfig().logLevel >= Config.ALG_LOG_DETAILED) {
				alg.log("CX checked, prob.=" + solutionProb + ", iter ="
						+ alg.getNumIterations() + ", CX size= "
						+ getNumSolutionVertices() + " vertices & "
						+ getNumSolutionEdges() + " edges");
			}
		}
	}

	protected double modelCheckSolutionAux(boolean isForIntermediateCheck)
			throws Exception {
		double result;
		if (getConfig().mrmcsol || alg.getContext() instanceof MRMCContext)
			result = mrmcModelCheckSolution(isForIntermediateCheck);
		else
			result = prismModelCheckSolution(isForIntermediateCheck);
		return result;
	}

	public double mrmcModelCheckSolution(boolean isForIntermediateCheck)
			throws Exception {
		if (mrmcModelChecker == null) {
			mrmcModelChecker = new MRMCModelChecker();
		}
		String labFile = alg.getContext().getSolutionFileName() + ".lab";
		String traFile = alg.getContext().getSolutionFileName() + ".tra";
		String modelType;
		if (getModel().getClass() == DTMC.class) {
			modelType = "dtmc";
		} else {
			if (getModel().getClass() == UniformCTMC.class
					|| getModel().getClass() == CTMC.class) {
				modelType = "ctmc";
			} else {
				throw new DiProException("The model type "
						+ getModel().getClass() + " is not supported by MRCM");
			}
		}
		String prop;
		if (getProperty() instanceof StochTBoundedUntil) {
			prop = "P{ <= 1.0 }[ tt U[ 0, ";
			if (getModel().getClass() == UniformCTMC.class
					|| getModel().getClass() == CTMC.class) {
				double timeBound = ((StochTBoundedUntil) getProperty())
						.timeBound();
				prop = prop + timeBound;
			} else {
				int timeBound = (int) ((StochTBoundedUntil) getProperty())
						.timeBound();
				prop = prop + timeBound;
			}
			prop = prop + " ] target ]";
		} else {
			prop = "P{ <= 1.0 }[ tt U target ]";
		}
		double prob;
		prob = mrmcModelChecker.performModelChecking(traFile, labFile,
				modelType, prop, initId);
		return prob;
	}

	public double prismModelCheckSolution(boolean isForIntermediateCheck)
			throws Exception {
		assert alg.getContext() instanceof AbstractPrismContext;
		AbstractPrismContext prismContext = (AbstractPrismContext) alg
				.getContext();
		boolean isInitStateATarget = ((PrismUntil) getProperty())
				.check((PrismState) prismContext.getStart()) == Proposition.TRUE;
		if (isInitStateATarget)
			return 1.0d;
		File ftStatesFile;
		File ftTransFile;
		File ftLblFile;
		ftStatesFile = new File(alg.getContext().getSolutionFileName() + ".sta");
		ftTransFile = new File(alg.getContext().getSolutionFileName() + ".tra");
		ftLblFile = new File(alg.getContext().getSolutionFileName() + ".lab");
		if (!ftStatesFile.exists()) {
			// accurateSolutionProb = 0.0d;
			return 0.0d;
		}
		if (isForIntermediateCheck) {
			if (solPropFileName == null) {
				boolean isCTMC = getModel() instanceof CTMC
						|| getModel() instanceof UniformCTMC;
				String ext = isCTMC ? "csl" : "pctl";
				solPropFileName = alg.getContext().getSolutionFileName() + "."
						+ ext;
				prismCreateIntermediateCXProbFile(solPropFileName);
			}
		} else {
			if (solPropFileName == null) {
				boolean isCTMC = getModel() instanceof CTMC
						|| getModel() instanceof UniformCTMC;
				String ext = isCTMC ? "csl" : "pctl";
				solPropFileName = alg.getContext().getSolutionFileName() + "."
						+ ext;
			}
			((PrismUntil) getProperty())
					.constructSolutionPropertiesFile(solPropFileName);
		}
		Prism prism = prismContext.getPrism();
		/*
		 * Prism prism = new Prism(prismContext.getPrismMainLog(),
		 * prismContext.getPrismTechLog()); prism.initialise();
		 */
		// prism.setEngine(Prism.SPARSE);
		// prism.setCUDDEpsilon(1e-6);
		ModulesFile mf = prism.parseExplicitModel(ftStatesFile, ftTransFile,
				ftLblFile, prismContext.getPrismModel().type());
		Model m = prism.buildExplicitModel();
		PropertiesFile pf = prism.parsePropertiesFile(mf, new File(
				solPropFileName));
		// mf.setUndefinedConstants(constValues);
		pf.setUndefinedConstants(prismContext.getPrismModel().constantValues());
		Expression f = pf.getProperty(0);
		// accurateSolutionProb = (Double) prism.modelCheck(m, pf, f);
		Result result = prism.modelCheck(m, pf, f);
		double d = ((Double) result.getResult()).doubleValue();
		prismContext.getPrismMainLog().flush();
		prismContext.getPrismTechLog().flush();
		m.clear();
		m = null;
		pf = null;
		mf = null;
		ftStatesFile = null;
		ftTransFile = null;
		// prism.closeDown();
		prism = null;
		System.gc();
		return d;
	}

	protected HashMap<PrismState, Double> prismTransientAnalysis(double time)
			throws Exception {
		AbstractPrismContext prismContext = (AbstractPrismContext) alg
				.getContext();
		File ftStatesFile;
		File ftTransFile;
		File ftLblFile;
		ftStatesFile = new File(alg.getContext().getSolutionFileName() + ".sta");
		ftTransFile = new File(alg.getContext().getSolutionFileName() + ".tra");
		ftLblFile = new File(alg.getContext().getSolutionFileName() + ".lab");
		assert ftStatesFile.exists();
		Prism prism = prismContext.getPrism();
		// int engine = prism.getEngine();
		// if(useSpraseMatrix) prism.setEngine(Prism.SPARSE);
		// prism.setCUDDEpsilon(1e-6);
		prism.parseExplicitModel(ftStatesFile, ftTransFile, ftLblFile,
				prismContext.getPrismModel().type());
		Model model = prism.buildExplicitModel();
		HashMap<PrismState, Double> result = prismModelChecker
				.performTransientAnalysis(prism, model, time);
		return result;
	}

	protected float totalExitRate(State s) {
		float exitRate = 0.0f;
		Iterator<? extends DirectedEdge> iter = alg.getExploredGraph()
				.outgoingEdges(s);
		while (iter.hasNext()) {
			StochasticTransition t = (StochasticTransition) iter.next();
			assert t.source().equals(s);
			exitRate = exitRate + t.getProbOrRate();
		}
		return exitRate;
	}

	public void writeIntoMRMCFiles(boolean isForIntermediateCheck)
			throws Exception {
		if (solutionGraph.numVertices() == 0)
			return;
		// assert !traces.isEmpty();
		assert solutionGraph.numVertices() > 0;
		String stafName = alg.getContext().getSolutionFileName() + ".sta";
		String trafName = alg.getContext().getSolutionFileName() + ".tra";
		String labfName = alg.getContext().getSolutionFileName() + ".lab";
		(new File(trafName)).delete();
		(new File(labfName)).delete();
		DecimalFormatSymbols symb = new DecimalFormatSymbols();
		symb.setDecimalSeparator('.');
		DecimalFormat form = new DecimalFormat(
				"######0.0############################################################",
				symb);
		// First, write all states into the states file
		PrintStream sta = new PrintStream(new File(stafName));
		sta.println("id:state");
		PrintStream lab = new PrintStream(new File(labfName));
		lab.println("#DECLARATION");
		lab.println("init target sink");
		lab.println("#END");
		Iterator<Vertex> iter = solutionGraph.vertices();
		int nextId = 1;
		// Hashtable<State, Integer> ht = new Hashtable<State, Integer>();
		mrmcStateIds.clear();
		int n = 0;
		while (iter.hasNext()) {
			State s = (State) iter.next();
			int id = nextId;
			nextId++;
			mrmcStateIds.put(s, id);
			sta.println(id + ":" + s);
			StringBuilder labEntry = new StringBuilder();
			labEntry.append(id);
			boolean add = false;
			if (getModel().isInitialState(s)) {
				labEntry.append(" init");
				add = true;
				initId = id;
			}
			int x = getProperty().check(s);
			if (x == Proposition.TRUE) {
				labEntry.append(" target");
				add = true;
			}
			if (add)
				lab.println(labEntry);
			n++;
		}
		assert n == solutionGraph.numVertices();
		// Write an extra absorbing state
		int sinkId = nextId;
		lab.println(sinkId + " sink");
		sta.close();
		lab.close();

		// Second, write the transitions into the transitions file
		LinkedList<String> transitions = new LinkedList<String>();
		iter = solutionGraph.vertices();
		int m = 0;
		while (iter.hasNext()) {
			ArrayList<String> outTrans = new ArrayList<String>();
			State s1 = (State) iter.next();
			int s1MrmcId = mrmcStateIds.get(s1);
			double exit = 0.0d;
			Iterator<? extends DirectedEdge> outEdges = solutionGraph
					.outgoingEdges(s1);
			while (outEdges.hasNext()) {
				StochasticTransition t = (StochasticTransition) outEdges.next();
				exit = exit + t.getProbOrRate();
				State s2 = (State) t.target();
				int s2MrmcId = mrmcStateIds.get(s2);
				outTrans.add(s1MrmcId + " " + s2MrmcId + " "
						+ form.format(t.getProbOrRate()));
				m++;
			}
			double totalExit;
			if ((getModel() instanceof CTMC)
					|| (getModel() instanceof UniformCTMC)) {
				totalExit = totalExitRate(s1);
			} else {
				// The model is a DTMC
				totalExit = 1.0d;
			}
			double toSink = totalExit - exit;
			if (toSink < 0) {
				if (alg.getConfig().logLevel >= Config.ALG_LOG_DEBUG) {
					StringBuilder sb = new StringBuilder();
					sb.append("WARNING: Rate to sink is negative: totalExit=");
					sb.append(totalExit);
					sb.append(", exit=");
					sb.append(exit);
					sb.append(", toSink = ");
					sb.append(toSink);
					sb.append("\nTransition rates:");
					Iterator<? extends DirectedEdge> tempIter = solutionGraph
							.outgoingEdges(s1);
					while (tempIter.hasNext()) {
						StochasticTransition t = (StochasticTransition) tempIter
								.next();
						sb.append(form.format(t.getProbOrRate()));
						sb.append(" : ");
						sb.append(t);
						sb.append("\n");
					}
					alg.log(sb.toString());
				}
			}
			// assert toSink >= -1e-6;
			if (toSink < 0)
				toSink = 0.0d;
			assert toSink >= 0.0d;
			if (toSink != 0) {
				outTrans.add(s1MrmcId + " " + sinkId + " "
						+ form.format(toSink));
			}
			Collections.sort(outTrans, order);
			transitions.addAll(outTrans);
		}
		if (getModel().getClass() == DTMC.class) {
			transitions.add(sinkId + " " + sinkId + " 1.0");
		}
		assert m == solutionGraph.numEdges();
		PrintStream tra = new PrintStream(new File(trafName));
		tra.println("STATES " + (solutionGraph.numVertices() + 1));
		tra.println("TRANSITIONS " + transitions.size());
		for (String t : transitions) {
			tra.println(t);
		}
		tra.close();
		// System.out.println("n="+n+", m="+m+", l="+l+", m+l="+(m+l));
		System.gc();
	}

	public void writeIntoPrismFiles(boolean isForIntermediateCheck)
			throws Exception {
		String sfName = alg.getContext().getSolutionFileName() + ".sta";
		String tfName = alg.getContext().getSolutionFileName() + ".tra";
		String lfName = alg.getContext().getSolutionFileName() + ".lab";
		(new File(sfName)).delete();
		(new File(tfName)).delete();
		(new File(lfName)).delete();
		if (solutionGraph.numVertices() == 0)
			return;
		DecimalFormatSymbols symb = new DecimalFormatSymbols();
		symb.setDecimalSeparator('.');
		DecimalFormat form = new DecimalFormat(
				"######0.0############################################################",
				symb);
		PrintStream lout = new PrintStream(new File(lfName));
		lout.println("0=\"init\" 1=\"deadlock\" 2=\"target\" 3=\"sink\"");
		// First, write all states into the states file
		PrintStream out = new PrintStream(new File(sfName));
		if (isForIntermediateCheck)
			out.println("(id,isTarget)");
		else
			out.println(getStateHeader());
		Iterator<Vertex> iter = solutionGraph.vertices();
		int nextPrismId = 1;
		// Hashtable<PrismState, Integer> ht = new Hashtable<PrismState,
		// Integer>();
		prismStateIds.clear();
		int n = 0;
		while (iter.hasNext()) {
			StringBuilder labels = new StringBuilder();
			PrismState s = (PrismState) iter.next();
			int prismId;
			if (getModel().isInitialState(s)) {
				prismId = 0;
				labels.append("0");
			} else {
				prismId = nextPrismId;
				nextPrismId++;
			}
			prismStateIds.put(s, prismId);
			int x = getProperty().check(s) == Proposition.TRUE ? 1 : 0;
			if (x == 1) {
				if (labels.length() > 0)
					labels.append(" ");
				labels.append("2");
			}
			String sStr;
			if (isForIntermediateCheck) {
				sStr = "(" + prismId + "," + x + ")";
			} else {
				sStr = convertPrismState(s, x);
			}
			out.println(prismId + ":" + sStr);
			if (labels.length() > 0)
				lout.println(prismId + ": " + labels);
			n++;
		}
		assert n == solutionGraph.numVertices();

		// Write an extra absorbing state
		int sinkId = nextPrismId;
		// assert !traces.keySet().isEmpty();
		// Iterator<SearchMark> solTermIter = traces.keySet().iterator();
		// PrismState target = (PrismState) solTermIter.next().vertex();
		assert firstTarget != null;
		String sinkStr;
		if (isForIntermediateCheck) {
			sinkStr = "(" + sinkId + ",0)";
		} else {
			sinkStr = convertPrismState((PrismState) firstTarget, 0);
		}
		out.println(sinkId + ":" + sinkStr);
		lout.println(sinkId + ": 3");
		out.close();
		lout.close();

		// Second, write the transitions into the transitions file
		out = new PrintStream(new File(tfName));
		out.println();
		iter = solutionGraph.vertices();
		int m = 0;
		while (iter.hasNext()) {
			PrismState s1 = (PrismState) iter.next();
			int s1PrismId = prismStateIds.get(s1);
			double exit = 0.0d;
			Iterator<? extends DirectedEdge> outEdges = solutionGraph
					.outgoingEdges(s1);
			while (outEdges.hasNext()) {
				PrismTransition t = (PrismTransition) outEdges.next();
				exit = exit + t.getProbOrRate();
				PrismState s2 = (PrismState) t.target();
				int s2PrismId = prismStateIds.get(s2);
				out.println(s1PrismId + " " + s2PrismId + " "
						+ form.format(t.getProbOrRate()));
				m++;
			}
			double totalExit;
			if ((getModel() instanceof CTMC)
					|| (getModel() instanceof UniformCTMC)) {
				totalExit = totalExitRate(s1);
			} else {
				// The model is a DTMC
				totalExit = 1.0d;
			}
			double toSink = totalExit - exit;
			if (toSink < 0) {
				if (alg.getConfig().logLevel >= Config.ALG_LOG_DEBUG) {
					StringBuilder sb = new StringBuilder();
					sb.append("WARNING: Rate to sink is negative: totalExit=");
					sb.append(totalExit);
					sb.append(", exit=");
					sb.append(exit);
					sb.append(", toSink = ");
					sb.append(toSink);
					sb.append("\nTransition rates:");
					Iterator<? extends DirectedEdge> tempIter = solutionGraph
							.outgoingEdges(s1);
					while (tempIter.hasNext()) {
						PrismTransition t = (PrismTransition) tempIter.next();
						sb.append(form.format(t.getProbOrRate()));
						sb.append(" : ");
						sb.append(t);
						sb.append("\n");
					}
					alg.log(sb.toString());
				}
			}
			/*
			 * < ForDebugging > if(toSink < -1e-6) {
			 * System.out.println("Negative probability or rate to Sink "
			 * +toSink); } /* </ ForDebugging >
			 */
			// assert toSink >= -1e-6;
			if (toSink < 0)
				toSink = 0.0d;
			assert toSink >= 0.0d;
			if (toSink != 0) {
				out.println(s1PrismId + " " + sinkId + " "
						+ form.format(toSink));
			}
		}
		if (getModel().getClass() == DTMC.class) {
			out.println(sinkId + " " + sinkId + " 1.0");
		}
		assert m == solutionGraph.numEdges();
		out.close();
		System.gc();
	}

	protected String convertPrismState(PrismState s, int isTarget) {
		StringBuffer sb = new StringBuffer("(");
		for (int i = 0; i < ((PrismState) s).values().getNumValues(); i++) {
			if (i != 0)
				sb.append(",");
			sb.append(((PrismState) s).values().getValue(i));
		}
		sb.append("," + isTarget);
		sb.append(")");
		return sb.toString();
	}

	protected String getStateHeader() throws Exception {
		List<String> varNames = getModel().getVertexLabels();
		StringBuffer sb = new StringBuffer("(");
		int i = 0;
		for (String varName : varNames) {
			if (i != 0)
				sb.append(",");
			sb.append(varName);
			i++;
		}
		sb.append(",");
		sb.append(Safety.TARGET_FLAG_NAME);
		sb.append(")");
		return sb.toString();
	}

	protected void computeProbUpperBound() throws Exception {
		assert probabilityUpperBound == -1.0d;
		StochUntil u = getProperty();
		if (u.isUpperBounded()) {
			probabilityUpperBound = u.getProbBound();
			isStrictBound = u.isStrictUpperBounded();
		} else {
			Object o = alg.getContext().getModelCheckingResult();
			if (o != null) {
				if (o instanceof Result) {
					Result result = (Result) o;
					probabilityUpperBound = ((Double) result.getResult())
							.doubleValue();
					isStrictBound = false;
				}
			} else {
				probabilityUpperBound = 1.0d;
				isStrictBound = true;
			}
		}
	}

	@Override
	public int computeUsedMemory() {
		int memory = super.computeUsedMemory();
		/* size of attribute: private double solutionProb */
		memory = memory + 8;
		return memory;
	}

	protected MarkovModel getModel() {
		return (MarkovModel) alg.getContext().getGraph();
	}

	protected StochUntil getProperty() {
		return (StochUntil) alg.getContext().getProperty();
	}

	protected Config getConfig() {
		return alg.getContext().getConfig();
	}

	protected SearchTree getSearchTree() {
		return alg.getSearchTree();
	}

	private void prismCreateIntermediateCXProbFile(String fileName)
			throws Exception {
		PrismUntil until = (PrismUntil) getProperty();
		StringBuilder sb = new StringBuilder("P=? [true U");
		if (until instanceof PrismTBoundedUntil) {
			sb.append("<=");
			double t = ((PrismTBoundedUntil) until).timeBound();
			if (t == (int) t)
				sb.append((int) t);
			else
				sb.append(t);
		}
		sb.append(" (isTarget=1) ]");
		File f = new File(fileName);
		f.createNewFile();
		PrintStream fout = new PrintStream(new FileOutputStream(f));
		fout.println(sb);
		fout.close();
	}

	private class MRMCTransitionOrder implements Comparator<String> {

		public int compare(String arg0, String arg1) {
			String[] tokens1 = arg0.split(" ");
			String[] tokens2 = arg1.split(" ");
			int s11 = Integer.parseInt(tokens1[0]);
			int s21 = Integer.parseInt(tokens2[0]);
			if (s11 < s21)
				return -1;
			if (s11 > s21)
				return 1;
			int s12 = Integer.parseInt(tokens1[1]);
			int s22 = Integer.parseInt(tokens2[1]);
			if (s12 < s22)
				return -1;
			if (s12 > s22)
				return 1;
			return 0;
		}

	}
}

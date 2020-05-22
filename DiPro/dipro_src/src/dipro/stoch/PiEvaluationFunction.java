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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;

import dipro.alg.BF;
import dipro.alg.XBF;
import dipro.alg.XBFpi;
import dipro.alg.BF.SearchMark;
import dipro.graph.DirectedEdge;
import dipro.graph.State;
import dipro.run.Config;
import dipro.util.Attribute;
import dipro.util.DiProException;

/** An evaluation function based on the pi-vectors. It is only applicable for uniformized 
 * CTMCs. For more details see Aljazzar & Leue TSE 2009 (submitted)
 * @author aljazzar
 */
public class PiEvaluationFunction extends BoundedDTMCEvaluationFunction {

	protected final Attribute PARENT_PI = new Attribute("PARENT_PI");
	protected final Attribute PI = new Attribute("PI");
	
	protected PoissonProcess poisson;
//	protected UniformCTMC uCTMC;
	

	public PiEvaluationFunction(XBF alg) throws Exception {
		super(alg);
//		assert alg.getGraph() instanceof UniformCTMC;
//		uCTMC = (UniformCTMC)alg.getGraph();
		assert alg instanceof XBFpi;
		poisson = null;
	}

	protected double computeG(BF.SearchMark uMark, DirectedEdge uv,
			BF.SearchMark vMark) throws Exception {
		if (maxDepth < 0)
			calculateMaxDepth();
		if (vMark.depth() > maxDepth)
			return 0.0d;
		return computeGApproximationByPPMaxBound(uMark, uv, vMark);
	}

	/** Accurate computation */
	protected double computeGAccurate(BF.SearchMark uMark, DirectedEdge uv,
			BF.SearchMark vMark) throws Exception {
		BigDecimal gamma = BigDecimal.ZERO;
		if (uv == null)
			gamma = BigDecimal.ONE;
		else {
			PiInterface uPi = providePi(uMark);
			vMark.set(PARENT_PI, uPi);
			for (int k = 0; k < maxDepth; k++) {
				BigDecimal probUpToK = BigDecimal.ZERO;
				BigDecimal pp = new BigDecimal(poisson.getProb(k));
				for (int i = 0; i < k; i++) {
					BigDecimal p = new BigDecimal(uPi.getProb(i));
					p = p.multiply(new BigDecimal(uCTMC().weight(uv)),
							alg.getConfig().mathContext);
					probUpToK = probUpToK.add(p);
				}
				probUpToK = probUpToK.multiply(pp, alg.getConfig().mathContext);
				gamma = gamma.add(probUpToK, alg.getConfig().mathContext);
			}
		}
		return gamma.doubleValue();
	}

	protected double computeGApproximationByPPMaxBound(BF.SearchMark uMark,
			DirectedEdge uv, BF.SearchMark vMark) throws Exception {
		if (maxDepth < 0)
			calculateMaxDepth();
		BigDecimal gamma = BigDecimal.ZERO;
		if (uv == null)
			gamma = BigDecimal.ONE;
		else {
			PiInterface uPi = providePi(uMark);
			vMark.set(PARENT_PI, uPi);
			int k = poisson.getMaxBound();
			BigDecimal probUpToK = BigDecimal.ZERO;
			for (int i = 0; i < k; i++) {
				BigDecimal p = new BigDecimal(uPi.getProb(i));
				p = p.multiply(new BigDecimal(uCTMC().weight(uv)),
						alg.getConfig().mathContext);
				probUpToK = probUpToK.add(p);
			}
			// BigDecimal pp = new BigDecimal(poisson.getProb(k));
			// probUpToK = probUpToK.multiply(pp, MathContext.DECIMAL128);
			gamma = gamma.add(probUpToK, alg.getConfig().mathContext);
		}
		return gamma.doubleValue();
	}

	protected PiInterface computePseudePi(BF.SearchMark uMark, DirectedEdge uv,
			BF.SearchMark vMark) throws Exception {
		PiInterface uPi = (PiInterface) vMark.get(PARENT_PI);
		StochasticTransition vLoop = (StochasticTransition) ((UniformCTMC) alg
				.getGraph()).createTransition((State) vMark.vertex(),
				(State) vMark.vertex());
		vLoop.setProbOrRate((float) alg.getConfig().uniformRate);
		int depth = (Integer) vMark.get(DEPTH);
		return computePi(uPi, (StochasticTransition) uv, vLoop, depth);
	}

	private void initPoissonProcess() throws Exception {
		assert alg.getProperty() instanceof StochTBoundedUntil;
		double q = alg.getConfig().uniformRate;
		double t = ((StochTBoundedUntil) alg.getProperty()).timeBound();
		double epsilon = alg.getConfig().pruneBound;
		poisson = new PoissonProcess(q, t, epsilon);
		if(alg.getConfig().logLevel>= Config.ALG_LOG_NORMAL) 
			alg.log(this.getClass().getName()+": path length bound = "+poisson.getMaxBound()+", (Poisson Process: q = "+q+", t = "+t+").");
	}

	protected void calculateMaxDepth() throws Exception {
		assert poisson == null && maxDepth < 0;
		initPoissonProcess();
		assert poisson != null;
		maxDepth = poisson.getMaxBound();
		assert maxDepth > 0;
	}
	
	protected PiInterface providePi(BF.SearchMark vMark) throws Exception {
		if (!vMark.has(PI)) {
			computePi(vMark);
		}
		return (PiInterface) vMark.get(PI);
	}

	public PiInterface getParentPi(SearchMark mark) {
		return (PiInterface) mark.get(PARENT_PI);
	}
	
	protected void computePi(BF.SearchMark vMark) throws Exception {
		StochasticTransition uv = (StochasticTransition) alg.getSearchTree()
				.getTreeEdge(vMark);
		PiInterface uPi = (PiInterface) vMark.get(PARENT_PI);
		StochasticTransition vUniformLoop = vMark.has(uCTMC().UNIFORM_LOOP) ? (StochasticTransition) vMark
				.get(uCTMC().UNIFORM_LOOP)
				: null;
		int depth = (Integer) vMark.get(DEPTH);
		PiInterface vPi = computePi(uPi, uv, vUniformLoop, depth);
		vMark.destroy(PARENT_PI);
		vMark.set(PI, vPi);
	}


	protected PiInterface computePi(PiInterface uPi, StochasticTransition uv,
			StochasticTransition vLoop, int depth) throws Exception {
		// StochasticTransition uv = (StochasticTransition)
		// alg.getExploredGraph().getEdgeToOptimalParent(vMark);
		// PiInterface parentProbs = (PiInterface) vMark.get(PARENT_PI);
		float x = uv == null ? 0.0f : uCTMC().weight(uv);
		float y = vLoop != null ? uCTMC().weight(vLoop) : 0.0f;
		// int depth = (Integer)vMark.get(DEPTH);
		PiInterface vPi = createPiTemplate(depth, maxDepth);
		int start = depth;
		if (depth == 0) { // s is the initial state
			vPi.setProb(depth, 1.0f);
			start = 1;
		}

		for (int i = start; i < maxDepth; i++) {
			// Transient probabilies at time point i-1 to be at the state s1 or
			// s2
			float p1 = uv == null ? 0 : uPi.getProb(i - 1);
			float p2 = vPi.getProb(i - 1);
			// float newProb = x * p1;
			// float loopProb = y * p2;
			BigDecimal newProb = (new BigDecimal(x)).multiply(
					new BigDecimal(p1), alg.getConfig().mathContext);
			BigDecimal loopProb = (new BigDecimal(y)).multiply(new BigDecimal(
					p2), alg.getConfig().mathContext);
			BigDecimal totalProb = newProb
					.add(loopProb, alg.getConfig().mathContext);
			float floatTotalProb = totalProb.floatValue();
			assert !Float.isInfinite(floatTotalProb);
			assert !Float.isNaN(floatTotalProb);
//			if(Float.isNaN(floatTotalProb) || Float.isInfinite(floatTotalProb)) {
//				System.out.println("x="+x+", y="+y+", p1="+p1+", p2="+p2+", newProb="+newProb+", loopProb="+loopProb);
//				Registry.getMain().handleWarning("Numerical Over-/Under-flow, probability value "+totalProb+" is converted to float "+floatTotalProb+". This value is replaced by 0.");
//				floatTotalProb = 0.0f;
//			}
			vPi.setProb(i,floatTotalProb);
		}
		return vPi;
	}

	private void plot(PiInterface pi) throws DiProException {
		long id = System.currentTimeMillis();
		String fn = "transients" + id + ".txt";
		try {
			// Write data
			PrintStream fout = new PrintStream(new FileOutputStream(fn));
			fout.println("k \t\t p[k]");
			for (int k = 0; k < maxDepth; k++) {
				float p = pi.getProb(k);
				fout.println(k + " \t\t " + p);
			}
			fout.close();

			// Write gnuplot script
			String pfn = "transients" + id + ".plt";
			fout = new PrintStream(new FileOutputStream(pfn));
			fout
					.println("set term postscript portrait \n"
							+ "set size 1.0,0.5");
			fout.println("set xlabel \"k\"");
			fout.println("set ylabel \"p[k]\"");
			fout.println("set output \"transients" + id + ".eps\"");
			fout.println();
			fout
					.println("plot \""
							+ fn
							+ "\" using 1:2 title \"Transient Probabilities\" with linespoints");
			fout.close();

			// Run gnuplot
			Process proc = Runtime.getRuntime().exec("gnuplot " + pfn);
			proc.waitFor();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void addParentPis(BF.SearchMark mark, BF.SearchMark markToAdd)
	throws DiProException {
		int d = Math.min(mark.depth(), markToAdd.depth());
		PiInterface pi = createPiTemplate(d, maxDepth);
		PiInterface pi1 = (PiInterface) mark.get(PARENT_PI);
		PiInterface pi2 = (PiInterface) markToAdd.get(PARENT_PI);
		for (int i = d; i < maxDepth; i++) {
			pi.setProb(i, pi1.getProb(i) + pi2.getProb(i));
		}
		mark.set(PARENT_PI, pi);
	}
	
	public void removeTermporalAttributes(BF.SearchMark vMark) {
		vMark.destroy(PARENT_PI);
		vMark.destroy(PI);
		assert !vMark.has(PARENT_PI);
		assert !vMark.has(PI);
	}
	
	protected PiInterface createPiTemplate(int begin, int end) {
		return new Pi(begin, end);
	}
	
	private XBFpi alg() {
		return (XBFpi) alg;
	}
	
	private UniformCTMC uCTMC() {
		return (UniformCTMC) alg.getGraph();
	}

}

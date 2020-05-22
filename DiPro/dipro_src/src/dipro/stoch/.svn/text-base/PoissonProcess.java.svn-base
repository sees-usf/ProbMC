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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.MathContext;

import dipro.run.Context;

public class PoissonProcess {

	private double q;
	private double t;
	private double epsilon;

	/** to store exp(-qt) */
	private BigDecimal exp;
	private int max;
	/** to store the probabilites */
	private double[] probs;

	// The last discrete number for which the probability has been computed
	private int m;
	// m!
	private BigDecimal mFac;
	// to store (qt)^m
	private BigDecimal mPow;

	private boolean computed;

	public PoissonProcess(double q, double t, double e) {
		this.q = q;
		this.t = t;
		this.epsilon = Math.max(Float.MIN_VALUE, e);
		assert this.q > 0.0d;
		assert this.t > 0.0d;
		assert this.epsilon > 0.0d;
		// this.exp = Math.pow(Math.E, -q*t);
		// if(exp<= 0.0d) exp = Double.MIN_VALUE;
		this.exp = computeExp();
		// assert this.exp.compareTo(BigDecimal.ZERO) > 1;
		max = computeMaxBound();
		// System.out.println("Poisson Process: q = "+q+", t = "+t+", epsilon =
		// "+epsilon+", e^(-qt) = "+exp+", Max Bound = "+max);
		m = -1;
		mFac = null;
		mPow = null;
		computed = false;
	}

	public void computeProbs() {
		assert !computed;
		assert max >= 0;
		probs = new double[max + 1];
		for (int k = 0; k <= max; k++) {
			double p = computeProb(k);
			probs[k] = p;
		}
		computed = true;
	}

	public void recomputeProbs() {
		assert computed;
		assert max >= 0;
		m = -1;
		mFac = null;
		mPow = null;
		for (int k = 0; k <= max; k++) {
			double p = computeProb(k);
			probs[k] = p;
		}
	}

	public void plot() {
		double id = System.currentTimeMillis();
		String fn = "PP_" + id + ".txt";
		try {
			// Write data
			PrintStream fout = new PrintStream(new FileOutputStream(fn));
			fout.println("k \t\t p[k]");
			for (int k = 0; k <= max; k++) {
				fout.println(k + " \t\t " + probs[k]);
			}
			fout.close();

			// Write gnuplot script
			String pfn = "PP_" + id + ".plt";
			fout = new PrintStream(new FileOutputStream(pfn));
			fout
					.println("set term postscript portrait \n"
							+ "set size 1.0,0.5");
			fout.println("set title \"Poisson Process PP(" + q + ", " + t
					+ ")\"");
			fout.println("set xlabel \"k\"");
			fout.println("set ylabel \"pp(k)\"");
			fout.println("set output \"export/PP_" + id + ".eps\"");
			fout.println();
			fout
					.println("plot \""
							+ fn
							+ "\" using 1:2 title \"Poisson Probabilities\" with linespoints");
			fout.close();

			// Run gnuplot
			Process proc = Runtime.getRuntime().exec("gnuplot " + pfn);
			proc.waitFor();
			(new File(fn)).delete();
			(new File(pfn)).delete();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public int getMaxBound() {
		return max;
	}

	public boolean isComputed() {
		return computed;
	}

	public double getProb(int k) {
		assert computed;
		return probs[k];
	}

	public int getExpectedValue() {
		return (int) Math.round(q * t);
	}

	private int computeMaxBound() {
		// assert context.getProperty() instanceof StochTBoundedUntil;
		// assert context.getGraph() instanceof UniformCTMC;
		// double epsilon = context.getConfig().pruneBound;
		if (epsilon <= Double.MIN_VALUE) {
			// Use a realistic error bound
			epsilon = Float.MIN_NORMAL;
		}
		// double q = context.getConfig().uniformRate;
		// double t = ((StochTBoundedUntil)context.getProperty()).timeBound();

		// Using Algorithm: Book of Stewart, Uniformization (Step 1), page 413
		int k = 0;
		// double psi = 1.0d;
		// double rho = 1.0d;
		// double qt = q*t;
		BigDecimal qt = new BigDecimal(q * t);
		BigDecimal psi = BigDecimal.ONE;
		BigDecimal rho = BigDecimal.ONE;
		// double mu = (1.0d- epsilon)/exp;
		BigDecimal mu = BigDecimal.ONE.subtract(new BigDecimal(epsilon));
		mu = mu.divide(exp, MathContext.DECIMAL128);
		while (rho.compareTo(mu) < 0 & k < Integer.MAX_VALUE - 1) {
			k = k + 1;
			psi = psi.multiply(qt, MathContext.DECIMAL128);
			psi = psi.divide(new BigDecimal(k), MathContext.DECIMAL128);
			rho = rho.add(psi, MathContext.DECIMAL128);
//			 System.out.println("k = "+k+", psi = "+psi+", rho = "+rho);
		}
//		System.out.println("PoissonProcess.computeMaxBound()= "+k);
		return k;
	}

	private double computeProb(int k) {
		assert !computed;
		assert k == 0 || k == m + 1;
		m = k;
		BigDecimal x;
		// The following computation is equivalent to: x = x.pow(k,
		// MathContext.DECIMAL128);
		if (k == 0)
			x = BigDecimal.ONE;
		else {
			x = new BigDecimal(q * t);
			x = x.multiply(mPow, MathContext.DECIMAL128);
		}
		mPow = x;
		// double y = fac(k);
		// BigDecimal y= approxFac(k);
		// The following computation is equivalent to: BigDecimal y = fac(k);
		BigDecimal y;
		if (k == 0)
			y = BigDecimal.ONE;
		else {
			y = mFac.multiply(new BigDecimal(k), MathContext.DECIMAL128);
		}
		mFac = y;
		BigDecimal p = x.divide(y, MathContext.DECIMAL128).multiply(exp,
				MathContext.DECIMAL128);
		// double p = x/y_ * exp;
		// System.out.println("x="+x+", y="+y+", p="+p);
		return p.doubleValue();
	}

	// private long fac(int k) {
	// long f = 1;
	// for(int i=1; i<=k; i++) {
	// f = f*i;
	// System.out.println("f="+f);
	// }
	// return f;
	// }

	// private double approxFac(int k) {
	// double x = 2* Math.PI * k;
	// x = Math.sqrt(x);
	// double y = k/Math.E;
	// y = Math.pow(y, k);
	// double f = x*y;
	// System.out.println("Faculty(k): x="+x+", y="+y+", f="+f);
	// return f;
	// }

	// private BigDecimal fac(int k) {
	// BigDecimal f = BigDecimal.ONE;
	// for(int i=1; i<=k; i++) {
	// f = f.multiply(new BigDecimal(i));
	// System.out.println("f="+f);
	// }
	// return f;
	// }

	// private BigDecimal approxFac(int k) {
	// double d = 2* Math.PI * k;
	// d = Math.sqrt(d);
	// BigDecimal x = new BigDecimal(d);
	// BigDecimal y = new BigDecimal((double)k);
	// y = y.divide(new BigDecimal(Math.E), MathContext.DECIMAL128);
	// // double y = k/Math.E;
	// // y = Math.pow(y, k);
	// y = y.pow(k);
	// BigDecimal f = x.multiply(y, MathContext.DECIMAL128);
	// System.out.println("Faculty(k): x="+x+", y="+y+", f="+f);
	// return f;
	// }

	private BigDecimal computeExp() {
		double qt = q * t;
		int n = (int) qt;
		double x = qt - n;
		double r = Math.pow(Math.E, x);
		BigDecimal bd = new BigDecimal(Math.E);
		bd = bd.pow(n, MathContext.DECIMAL128);
		bd = bd.multiply(new BigDecimal(r), MathContext.DECIMAL128);
		bd = BigDecimal.ONE.divide(bd, MathContext.DECIMAL128);
		return bd;
	}

	
	public static int approximateDiscreteTBound(Context context) throws Exception {
		assert context.getProperty() instanceof StochTBoundedUntil;
		assert context.getGraph() instanceof UniformCTMC 
				|| context.getGraph() instanceof CTMC;
		double epsilon = context.getConfig().pruneBound;
		double q = context.getConfig().uniformRate;
		double t = ((StochTBoundedUntil) context.getProperty()).timeBound();
		assert q > 0.0d;
		assert t > 0.0d;
		PoissonProcess pp = new PoissonProcess(q, t, epsilon);
		return pp.getMaxBound();
	}
	
	public static void main(String[] args) {
		double t = 10;
		double q = 1;
		double e = 1.0E-10;
		while (q < 1.0E10) {
			PoissonProcess pp = new PoissonProcess(q, t, e);
			pp.computeProbs();
			double p = pp.getProb(pp.max);
			System.out.println("q=" + pp.q + ",\t exp=" + pp.exp + ",\t max="
					+ pp.max + ",\t Prob(max)=" + p);
			pp.plot();
			q = q * 10;
		}
	}
}

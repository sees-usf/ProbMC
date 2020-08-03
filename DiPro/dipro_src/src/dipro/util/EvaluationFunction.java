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

package dipro.util;

import dipro.alg.BF;
import dipro.graph.DirectedEdge;
//import dipro.h.pattern.PatternHeuristicLength;

/**
 * @author aljazzar
 */

public class EvaluationFunction {

	protected final Attribute G = new Attribute("G");
	protected final Attribute H = new Attribute("H");
	protected final Attribute DEPTH = new Attribute("DEPTH");
	protected final boolean isLengthBased = false;
	protected BF alg;

	public EvaluationFunction(BF alg) {
		this.alg = alg;
	}

	public double evaluate(BF.SearchMark uMark, DirectedEdge uv,
			BF.SearchMark vMark) throws Exception {
		System.out.println("Evaluate - EvaluationFunction");
		int d = computeDepth(uMark, uv, vMark);
		vMark.set(DEPTH, d);
		if (!alg.getConfig().greedy) {
			System.out.println("evaluate if statement 1 - EvalautionFunction");
			double g = computeG(uMark, uv, vMark);
			if(Double.isInfinite(g) || Double.isNaN(g)) 
				throw new DiProException("Numerical over- or underflow: g-value = "+g);
			vMark.set(G, new Double(g));
		}
		double h = 0.0d;
		if(!alg.getConfig().isProbPatternH)
		{
			h = computeH(vMark);
		}
		else
		{
			h = computeH(uMark,uv, vMark);
		}
		if(Double.isInfinite(h) || Double.isNaN(h)) 
			throw new DiProException("Numerical over- or underflow: h-value = "+h);
		// if(h==0.0d) System.out.println("H == 0");
		vMark.set(H, new Double(h));
		return f(vMark);
	}

	protected double computeG(BF.SearchMark uMark, DirectedEdge uv,
			BF.SearchMark vMark) throws Exception {
		double g = 0.0d;
		if (uv != null) {
			System.out.println("computeG- EvalautionFunction");
			assert uv.source().equals(uMark.vertex());
			g = uMark.g() + alg.getGraph().weight(uv);
		}
		return g;
	}

	
	public boolean isLengthBased()
	{
		return isLengthBased;
	}
	
	protected int computeDepth(BF.SearchMark uMark, DirectedEdge uv, BF.SearchMark vMark) {
		int d = 0;
		if (uv != null) {
			System.out.println("computeDepth - EvalautionFunction");
			assert uv.source().equals(uMark.vertex());
			d = uMark.depth() + 1;
		}
		return d;
	}

	
	public double f(BF.SearchMark vMark) {
		double f;
		if (alg.getConfig().greedy)
			f = h(vMark);
		else
			f = g(vMark) + h(vMark);
		return f;
	}

	public double g(BF.SearchMark vMark) {
		assert vMark.has(G);
		return (Double) vMark.get(G);
	}

	public double h(BF.SearchMark vMark) {
		assert vMark.has(H);
		return (Double) vMark.get(H);
	}

	public int depth(BF.SearchMark vMark) {
		assert vMark.has(DEPTH);
		return (Integer) vMark.get(DEPTH);
	}

	public void setG(BF.SearchMark vMark, double g) {
		vMark.set(G, new Double(g));
	}

	public void setH(BF.SearchMark vMark, double h) {
		vMark.set(H, new Double(h));
	}

	public void setDepth(BF.SearchMark vMark, int d) {
		vMark.set(DEPTH, new Integer(d));
	}

	public double computeH(BF.SearchMark vMark) throws Exception {
		System.out.println("Compute H - EvaluationFUnction");			
		if (alg.getHeuristic() == null)
			return 0.0f;
		return alg.getHeuristic().evaluate(vMark.vertex());
	}

	public double computeH(BF.SearchMark uMark, DirectedEdge uv,
			BF.SearchMark vMark) throws Exception {
		System.out.println("ComputerH - EvaluationFunction");	
		if (alg.getHeuristic() == null)
			return 0.0f;
		if (uv == null)
			return 1.0d;
			
		return alg.getHeuristic().evaluate(uMark.vertex(), vMark.vertex());
			
		
	}
	
	public double computeTraceValue(Trace trace) throws Exception {
		double g = 0f;
		for (int i = 0; i < trace.length() - 1; i++) {
			g = g + alg.getGraph().weight(trace.getEdge(i));
		}
		return g;
	}

}

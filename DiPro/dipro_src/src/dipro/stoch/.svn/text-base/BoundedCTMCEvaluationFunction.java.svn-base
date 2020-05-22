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

import dipro.alg.BF;
import dipro.alg.XBF;
import dipro.graph.DirectedEdge;
import dipro.run.Config;

public class BoundedCTMCEvaluationFunction extends CTMCEvaluationFunction {

	protected PoissonProcess poisson;

	public BoundedCTMCEvaluationFunction(XBF alg) throws Exception {
		super(alg);
		poisson = null;
	}

	protected double computeG(BF.SearchMark uMark, DirectedEdge uv,
			BF.SearchMark vMark) throws Exception {
		double g;
		if (poisson == null) {
			initPoissonProcess();
		}
		if (vMark.depth() > getMaxDepth())
			g = 0.0d;
		else {
			g = super.computeG(uMark, uv, vMark);
		}
		// System.out.println("Depth = "+vDepth+" -> g = "+g);
		return g;
	}

	public int getMaxDepth() {
		assert poisson != null;
		return poisson.getMaxBound();
	}

	private void initPoissonProcess() throws Exception {
		assert alg.getProperty() instanceof StochTBoundedUntil;
		assert (alg.getGraph() instanceof CTMC)
				|| (alg.getGraph() instanceof UniformCTMC);
		double q = alg.getConfig().uniformRate;
		double t = ((StochTBoundedUntil) alg.getProperty()).timeBound();
		double epsilon = alg.getConfig().pruneBound;
		poisson = new PoissonProcess(q, t, epsilon);
		if(alg.getConfig().logLevel >= Config.ALG_LOG_NORMAL) 
			alg.log(this.getClass().getName()+": path length bound = "+poisson.getMaxBound()+", (Poisson Process: q = "+q+", t = "+t+").");
	}
}

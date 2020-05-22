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

import prism.Result;
import dipro.alg.BF;
import dipro.alg.KSPAlgorithm;
import dipro.run.Config;
import dipro.util.KDefaultSolutionCollector;

public class KDTMCSolutionCollector extends KDefaultSolutionCollector {

	protected double solutionProb;
	protected double probabilityUpperBound;
	protected boolean isStrictBound;

	public KDTMCSolutionCollector(KSPAlgorithm alg) throws Exception {
		super(alg);
		solutionProb = 0.0d;
		probabilityUpperBound = -1.0d;
		isStrictBound = false;
	}

	protected void processTrace(BF.SearchMark targetMark) throws Exception {
		if (probabilityUpperBound == -1.0)
			computeProbUpperBound();
		super.processTrace(targetMark);
		solutionProb = solutionProb + targetMark.f();
		boolean enough = false;
		enough = enough || targetMark.f() < alg.getConfig().pruneBound;
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
		}
	}

	protected Config getConfig() {
		return alg.getContext().getConfig();
	}

	protected void computeProbUpperBound() throws Exception {
		assert probabilityUpperBound == -1.0d;
		StochUntil u = (StochUntil) alg.getContext().getProperty();
		if (u.isUpperBounded()) {
			probabilityUpperBound = u.getProbBound();
			isStrictBound = u.isStrictUpperBounded();
		} else {
			Object o = alg.getContext().getModelCheckingResult();
			if (o != null) {
				if (o instanceof Result) {
					Result upperBound = ((Result) o);
					probabilityUpperBound = ((Double)upperBound.getResult()).doubleValue();
					isStrictBound = false;
				}
			} else {
				probabilityUpperBound = 1.0d;
				isStrictBound = true;
			}
		}
	}

	public double getSolutionValue() {
		return solutionProb;
	}

	public void clear() throws Exception {
		solutionProb = 0.0d;
		super.clear();
	}

	public synchronized int computeUsedMemory() {
		int memory = 0;
		/* protected double solutionProb */
		memory = memory + 8;
		/*
		 * Traces and solution graph can be ignored if they are written
		 * immediately into a file on the external memory. In this case one
		 * should remove the next line of code.
		 */
		memory = memory + traceRecorder.getUsedMemory();
		return memory;
	}

}

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

import dipro.run.Context;
import dipro.stoch.BoundedCTMCEvaluationFunction;
import dipro.stoch.BoundedDTMCEvaluationFunction;
import dipro.stoch.CTMC;
import dipro.stoch.CTMCEvaluationFunction;
import dipro.stoch.DTMC;
import dipro.stoch.StochTBoundedUntil;
import dipro.stoch.StochUntil;
import dipro.util.DTMCEvaluationFunction;
import dipro.util.DoubleComparator;
import dipro.util.EvaluationFunction;
import dipro.util.InverseComparator;

public class StochXSearchCreator implements SearchCreator {

	@Override
	public BF createSearch(Context context) throws Exception {
		XBFstoch alg = new XBFstoch();
		Comparator<Double> comparator = new InverseComparator<Double>(
				new DoubleComparator());
		EvaluationFunction evaluationFunction;
		if (context.getProperty() instanceof StochTBoundedUntil) {
			if (context.getGraph() instanceof CTMC) {
				evaluationFunction = new BoundedCTMCEvaluationFunction(alg);
			} else {
				assert context.getGraph().getClass() == DTMC.class;
				evaluationFunction = new BoundedDTMCEvaluationFunction(alg);
			}
		} else {
			assert context.getProperty() instanceof StochUntil;
			if (context.getGraph() instanceof CTMC) {
				evaluationFunction = new CTMCEvaluationFunction(alg);
			} else {
				assert context.getGraph().getClass() == DTMC.class;
				evaluationFunction = new DTMCEvaluationFunction(alg);
			}
		}
		alg.bind(context, comparator, evaluationFunction);
		return alg;
	}

	@Override
	public BF createSearch(Context context, boolean lengthHeuristic)
			throws Exception {
		// TODO Auto-generated method stub
		return createSearch(context);
	}
}

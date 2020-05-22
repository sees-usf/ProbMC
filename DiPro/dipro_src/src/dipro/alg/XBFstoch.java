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
import dipro.util.EvaluationFunction;
import dipro.util.InverseComparator;

/** A specialization of XBF which uses depth-bounded EvaluationFunction. For more details 
 * see XZpi and XUZpi in Aljazzar and Leue TSE 2009 (submitted). 
 * The main difference to XBF is the reopening states also because of depth advantage.
 * @author aljazzar
 */
public class XBFstoch extends XBF {

	protected XBFstoch() {
		super();
	}
	
	public void bind(Context context, Comparator<Double> comparator, EvaluationFunction evaluationFunction) {
		assert comparator instanceof InverseComparator;
		assert evaluationFunction instanceof BoundedDTMCEvaluationFunction
			|| evaluationFunction instanceof BoundedCTMCEvaluationFunction;
		super.bind(context, comparator, evaluationFunction);
	}
	
	protected boolean checkRelaxOpen(SearchMark newSearchMark, SearchMark oldSearchMark) {
		boolean b = super.checkRelaxOpen(newSearchMark, oldSearchMark);
		if(b) return true;
		int newDepth = newSearchMark.depth();
		int oldDepth = oldSearchMark.depth();
		return newDepth < oldDepth;
	}
	
	protected RelaxationInfo relaxOpen(SearchMark newVMark, SearchMark oldVMark) throws Exception {
		int newDepth = newVMark.depth();
		int oldDepth = oldVMark.depth();
		int d = Math.min(newDepth, oldDepth);
		double g = Math.max(newVMark.g(), oldVMark.g());
		evaluationFunction.setDepth(newVMark, d);
		evaluationFunction.setG(newVMark, g);
		return super.relaxOpen(newVMark, oldVMark);
	}
	
	protected RelaxationInfo reopen(SearchMark newVMark, SearchMark oldVMark) throws Exception {
		int newDepth = newVMark.depth();
		int oldDepth = oldVMark.depth();
		int d = Math.min(newDepth, oldDepth);
		double g = Math.max(newVMark.g(), oldVMark.g());
		evaluationFunction.setDepth(newVMark, d);
		evaluationFunction.setG(newVMark, g);
//		evaluationFunction.setDepth(newVMark, Math.min(newDepth, oldDepth));
//		if(newDepth <= oldDepth || property.check(newVMark.vertex())==Safety.TRUE) {
//			double g = newVMark.g() + oldVMark.g();
//			evaluationFunction.setG(newVMark, g);
//		}
		return super.reopen(newVMark, oldVMark);
	}
}

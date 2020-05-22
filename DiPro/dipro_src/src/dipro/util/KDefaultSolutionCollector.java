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

import dipro.alg.KSPAlgorithm;
import dipro.alg.BF.SearchMark;

public class KDefaultSolutionCollector extends DefaultSolutionCollector {

	public KDefaultSolutionCollector(KSPAlgorithm alg) throws Exception {
		super(alg);
	}

	protected Trace constructSolTrace(SearchMark mark) {
		Trace trace = ((KSPAlgorithm) alg).constructTrace(mark);
		/* < ForDebugging >
		 double dist1 = mark.f();
		 double dist2;
		try {
			dist2 = alg.computeTraceValue(trace);
			if(Math.abs(dist1 - dist2)>= 1e-6) {
				System.out.println("halt..");
				System.out.println("f = "+dist1);
				System.out.println("d = "+dist2);
			}
			assert Math.abs(dist1 - dist2)<1e-6;
		} catch (Exception e) {
			e.printStackTrace();
		}
 		/* </ ForDebugging >*/
		return trace;
	}
}
